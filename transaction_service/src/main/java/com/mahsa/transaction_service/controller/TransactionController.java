package com.mahsa.transaction_service.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.mahsa.transaction_service.dto.AddTransactionRequestDTO;
import com.mahsa.transaction_service.dto.AddTransactionResponseDTO;
import com.mahsa.transaction_service.dto.ListedTransactionDTO;
import com.mahsa.transaction_service.entity.Transaction;
import com.mahsa.transaction_service.repository.TransactionRepository;

import jakarta.validation.Valid;


@RestController
@RequestMapping("api/transactions")
public class TransactionController {
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;

    //Read user-service URL from application.properties
    @Value("${user.service.url}")
    private String userServiceBaseUrl;

    //Read account-service URL from application.properties
    @Value("${account.service.url}")
    private String accountServiceBaseUrl;

    public TransactionController(TransactionRepository transactionRepository, RestTemplate restTemplate){
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @PostMapping // Maps to POST /api/transactions
    public ResponseEntity<?> addTransaction(
        @RequestHeader(name="X-User-ID") Long userId,
        @RequestHeader(name="X-Account-ID") Long accountId,
        @Valid @RequestBody AddTransactionRequestDTO requestDTO){
        // // --- VALIDATE userId by calling user-service ---
        try {
            String validationUrl = userServiceBaseUrl + "/validate/" + userId;
            // Make the GET request. Expecting a Boolean response.
            ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(validationUrl, Boolean.class);
            Boolean userExists = responseEntity.getBody();
            if(userExists==null || !userExists ){
                    return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error: User with ID " + userId + " does not exist."));
            }   
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error: Could not connect to user service for validation. " + e.getMessage()));
            }
        // // --- END USER VALIDATION ---

        
        // // --- VALIDATE accountId and if it belongs to the user by calling account-service ---
        try {
            String accountValidationUrl = accountServiceBaseUrl + "/validate/" + accountId + "?userId=" + userId;
            // Make the GET request. Expecting a Boolean response.
            ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(accountValidationUrl, Boolean.class);
            Boolean accountValidationResponseEntity = responseEntity.getBody();
            if(accountValidationResponseEntity==null || !accountValidationResponseEntity ){
                    return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error: Account with ID " + accountId + " does not exist or does not belong to you."));
            }   
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error: Could not connect to account service for validation. " + e.getMessage()));
            }
        // // --- END ACCOUNT VALIDATION ---

        /// ----- Other validations -----
        /// @Valid covers the rest of validations
        // if (requestDTO.getAmount() == null || requestDTO.getAmount().compareTo(BigDecimal.ZERO) == 0) { ... }
        // if (requestDTO.getType() == null) { ... }

        // @When @valid validation fails on requestDTO, MethodArgumentNotValidException will be thrown BEFORE entering the method
        /// ----- End of Other validations -----
        
        //Create a transaction with DTO data
        Transaction newTransaction = new Transaction();
        newTransaction.setDescription(requestDTO.getDescription());
        newTransaction.setAmount(requestDTO.getAmount());
        newTransaction.setType(requestDTO.getType());
        newTransaction.setDate(requestDTO.getDate());
        newTransaction.setAccountId(accountId);
        
        //Save the transaction
        Transaction savedTransaction = transactionRepository.save(newTransaction);
        
        // Create responseDTO
        AddTransactionResponseDTO responseDTO = new AddTransactionResponseDTO(
            savedTransaction.getId(),
            savedTransaction.getDescription(),
            savedTransaction.getAmount(),
            savedTransaction.getType(),
            savedTransaction.getDate(),
            savedTransaction.getAccountId()
        );

        //Return a ResponseEntity with HttpStatus.CREATED and the saved transaction (or a DTO/Map without sensitive info if desired).
        return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(responseDTO);   
    }

    @GetMapping // Maps to GET /api/transactions
    public ResponseEntity<?>  getTransactionsByAccountId(
        @RequestHeader(name="X-User-ID") Long userId,
        @RequestHeader(name="X-Account-ID") Long accountId) {
        
        // // --- VALIDATE accountId and if it belongs to the user by calling account-service ---
        try {
            String accountValidationUrl = accountServiceBaseUrl + "/validate/" + accountId + "?userId=" + userId;
            // Make the GET request. Expecting a Boolean response.
            ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(accountValidationUrl, Boolean.class);
            Boolean accountValidationResponseEntity = responseEntity.getBody();
            if(accountValidationResponseEntity==null || !accountValidationResponseEntity ){
                    return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Error: Account with ID " + accountId + " does not exist or does not belong to you."));
            }   
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Error: Could not connect to account service for validation. " + e.getMessage()));
            }
        // // --- END ACCOUNT VALIDATION ---
        
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        List<ListedTransactionDTO> responseDTOs = transactions.stream()
            .map(transaction -> new ListedTransactionDTO(
                    transaction.getId(),
                    transaction.getDescription(),
                    transaction.getAmount(),
                    transaction.getType(),
                    transaction.getDate(),
                    transaction.getAccountId())
                    )
                    .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(responseDTOs);
    }
}