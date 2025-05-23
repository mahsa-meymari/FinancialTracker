package com.mahsa.transaction_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.mahsa.transaction_service.enums.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AddTransactionRequestDTO {
    @Size(max=200, message = "Description can be at most 255 characters")
    private String description;

    @NotNull(message = "amount cannot be Null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than or equal to 0")
    private BigDecimal amount;
    
    @NotNull(message = "Transaction type cannot be null")
    private  TransactionType type;
    
    private LocalDate date;
    
    public AddTransactionRequestDTO() {} 
    public AddTransactionRequestDTO(String description,BigDecimal amount,TransactionType type, LocalDate date, Long userId) {
    this.description = description;
    this.amount = amount;
    this.type = type;
    this.date = date;
   } 

   public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
