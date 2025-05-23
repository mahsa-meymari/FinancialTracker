package com.mahsa.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationRequestDTO {
    @NotBlank(message = "Username cannot be blank")
    @Size(min=3, max=20, message = "Username must be between 3 and 20 characters")
    private String username;
    
    @NotBlank(message = "Password cannot be blank")
    @Size(min=6, max=100, message = "Password must be at least 6 and at most 100 characters")
    private String password;

    public UserRegistrationRequestDTO(){
    }

    public UserRegistrationRequestDTO(String username, String password){
        this.username = username;
        this.password = password;   
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
