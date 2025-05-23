package com.mahsa.user_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LoginRequestDTO {
    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @NotNull
    private String password;

 
    public LoginRequestDTO(){}
    public LoginRequestDTO(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;  
    }

}
