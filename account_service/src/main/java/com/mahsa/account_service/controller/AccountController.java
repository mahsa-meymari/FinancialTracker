package com.mahsa.account_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.mahsa.account_service.dto.AccountResponseDTO;
import com.mahsa.account_service.dto.CreateAccountRequestDTO;
import com.mahsa.account_service.entity.Account;
import com.mahsa.account_service.repository.AccountRepository;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final RestTemplate restTemplate;
    private final AccountRepository accountRepository;

    //Read user-service URL from application.properties
    @Value("${user.service.url}")
    private String userServiceBaseUrl;
    
    public AccountController(RestTemplate restTemplate, AccountRepository accountRepository) {
        this.restTemplate = restTemplate;
        this.accountRepository = accountRepository;
    }

    @PostMapping
    public ResponseEntity<?> addAccount(
        @RequestHeader(name="X-User-Id") Long userId,
        @Valid @RequestBody CreateAccountRequestDTO requestDTO) {
        //-------- userId Validation --------
        if (userId == null) { //Check for null userId from header
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Error: X-User-ID header is required."));
        }
        try {
            String validationUrl = userServiceBaseUrl + "/validate/" + userId;
            ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(validationUrl, Boolean.class);
            Boolean userExists = responseEntity.getBody();
            if(userExists == null || !userExists){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", "Error: User with ID " + userId + " does not exist."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("message", "Error: Could not connect to user service for validation. "));
        }
        //------- End of userId Validation


        Account newAccount = new Account();
        newAccount.setBalance(requestDTO.getBalance());
        newAccount.setName(requestDTO.getName());
        newAccount.setType(requestDTO.getType());
        newAccount.setUserId(userId);

        Account savedAccount = accountRepository.save(newAccount);

        AccountResponseDTO responseDTO = new AccountResponseDTO(
            savedAccount.getId(),
            savedAccount.getName(),
            savedAccount.getType(),
            savedAccount.getBalance(),
            savedAccount.getUserId());


        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> listAccountsForUser(
        @RequestHeader(name = "X-User-ID") Long userId) {
            // no need to check if userId is not null as we didn't specify @RequestHeader(required=false)
            // if the header is missing Spring would return 400 before this method is called.
            
            List<Account> userAccounts = accountRepository.findByUserId(userId);
            List<AccountResponseDTO> responseDTOs = userAccounts.stream().map(account -> new AccountResponseDTO(
                account.getId(),
                account.getName(),
                account.getType(),
                account.getBalance(),
                account.getUserId())).collect(Collectors.toList());

            // Shorthand for ResponseEntity.status(HttpStatus.OK).body(responseDTOs);
            return ResponseEntity.ok(responseDTOs);
    }
    

    //GET http://localhost:8082/api/accounts/validate/{accountId}?userId={userId}
    @GetMapping("/validate/{accountId}")
    public ResponseEntity<Boolean> validateAccountExistsAndBelongsToUser(
        @PathVariable Long accountId,
        @RequestParam Long userId) {

            Optional<Account> accountOptional = accountRepository.findById(accountId);
                if (accountOptional.isEmpty()) {
                // Account with the given accountId does not exist at all
                return ResponseEntity.ok(false);
                }
                Account account = accountOptional.get();

                // Check if the found account's does belong to the specified user
                if (!account.getUserId().equals(userId)) { // Use .equals() for comparing Long objects
                    return ResponseEntity.ok(false);
                }
            return ResponseEntity.ok(true);

    }
    
}
