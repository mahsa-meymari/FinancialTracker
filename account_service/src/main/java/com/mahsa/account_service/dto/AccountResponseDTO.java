package com.mahsa.account_service.dto;

import java.math.BigDecimal;

import com.mahsa.account_service.enums.AccountType;

public class AccountResponseDTO {
    private Long id;
    private String name;
    private AccountType type;
    private BigDecimal balance;
    private Long userId;

    public AccountResponseDTO() {
    }
    
    public AccountResponseDTO(Long id, String name, AccountType type, BigDecimal balance, Long userId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.balance = balance;
        this.userId = userId;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public AccountType getType() {
        return type;
    }
    public void setType(AccountType type) {
        this.type = type;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }


}
