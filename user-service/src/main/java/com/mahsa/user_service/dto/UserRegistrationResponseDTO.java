package com.mahsa.user_service.dto;

//This class will represent the data that the server sends back to the client after a successful user registration.
public class UserRegistrationResponseDTO {
    private Long id;
    private String username;


    public UserRegistrationResponseDTO(){}
    public UserRegistrationResponseDTO(Long id, String username){
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    
}
