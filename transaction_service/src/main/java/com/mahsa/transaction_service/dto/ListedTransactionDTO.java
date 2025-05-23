package com.mahsa.transaction_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.mahsa.transaction_service.enums.TransactionType;

public class ListedTransactionDTO {
    private Long id;
    private String description;
    private BigDecimal amount;
    private  TransactionType type;
    private LocalDate date;
    private Long accountId;

    public ListedTransactionDTO() {} 
    public ListedTransactionDTO(Long id, String description,BigDecimal amount,TransactionType type, LocalDate date, Long accountId) {
    this.id = id;
    this.description = description;
    this.amount = amount;
    this.type = type;
    this.date = date;
    this.accountId = accountId;
   } 
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Long getAccountId() {
        return accountId;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
