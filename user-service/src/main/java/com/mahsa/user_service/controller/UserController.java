package com.mahsa.user_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mahsa.user_service.dto.LoginRequestDTO;
import com.mahsa.user_service.dto.LoginResponseDTO;
import com.mahsa.user_service.dto.UserRegistrationRequestDTO;
import com.mahsa.user_service.dto.UserRegistrationResponseDTO;
import com.mahsa.user_service.entity.User;
import com.mahsa.user_service.repository.UserRepository;
import com.mahsa.user_service.security.JwtUtil;

import jakarta.validation.Valid;

import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/users")
public class UserController {
    
    //inject jwtUtil, userRepository and passwordEncoder beans
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public UserController(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }



    // --- Registration Endpoint ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequestDTO requestDTO) {
        //Check if username already exists
        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error: Username is already taken!"));
        }

        // Create a new User entity from the DTO with username and hashed password
        User newUser = new User();
        newUser.setUsername(requestDTO.getUsername());
        newUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));

        //TODO: Add more validation (password strength, email format if you add email etc.)

        //Save the user in the the database
        User savedUser = userRepository.save(newUser);

        UserRegistrationResponseDTO userResponseDTO = new UserRegistrationResponseDTO(
            savedUser.getId(),
            savedUser.getUsername()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }


    // --- Login Endpoint ---
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        // check if the username exists
        Optional<User> existingUser = userRepository.findByUsername(loginRequestDTO.getUsername());
        if (existingUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new LoginResponseDTO(null,null,null,"Invalid username or password"));
        }

        //check if the hashed password matches
        User user = existingUser.get();
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new LoginResponseDTO(null,null, null, "invalid username or password"));
        }

        // --- User is authenticated, generate JWT ---
        final String token = jwtUtil.generateToken(user);
        final LoginResponseDTO responseDTO = new LoginResponseDTO(
            token,
            user.getId(),
            user.getUsername(),
            "successful Login");

        return ResponseEntity.ok(responseDTO);
    }


    @GetMapping("/validate/{userId}")
    public ResponseEntity<Boolean> validateUserExists(@PathVariable Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (exists) {
        return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }

    
}