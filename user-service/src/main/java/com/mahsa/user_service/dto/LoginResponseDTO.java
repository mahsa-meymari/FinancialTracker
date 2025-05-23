package com.mahsa.user_service.dto;

public class LoginResponseDTO {
    private String token;
    private Long userId;
    private String username;
    private String message;

    public LoginResponseDTO(){}
    public LoginResponseDTO(String token, Long userId, String username, String message) {
        this.token = token;
        this.userId = userId;
        this.username = username;
        this.message = message;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
        public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
