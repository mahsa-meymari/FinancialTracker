package com.mahsa.account_service.dto;

import java.math.BigDecimal;

import com.mahsa.account_service.enums.AccountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateAccountRequestDTO {
    @NotBlank(message = "name cannot be blank")
    private String name;

    private AccountType type;
    
    @NotNull(message = "Initial balance cannot be null")
    private BigDecimal balance;

    public CreateAccountRequestDTO() {}
    public CreateAccountRequestDTO(String name, AccountType type, BigDecimal balance) {
        this.name = name;
        this.type = type;
        this.balance = balance;
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

}
