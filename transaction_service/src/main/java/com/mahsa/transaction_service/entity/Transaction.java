package com.mahsa.transaction_service.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.mahsa.transaction_service.enums.TransactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //Long vs long:
    
    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal amount; //BigDecimal for amount instead of float. Floats and doubles can cause precision errors with financial calculations.

    @Column
    @Enumerated(EnumType.STRING)
    private  TransactionType type;

    @Column(nullable = true)
    private LocalDate date;

    @Column(nullable = false)
    private Long accountId;

   public Transaction() {}

   // constructor
   public Transaction(String description, BigDecimal amount, TransactionType type, LocalDate date, Long accountId) {
       this.description = description;
       this.amount = amount;
       this.type = type;
       this.accountId = accountId;
       this.date = date;
   }

    public void setType(TransactionType type) {
        this.type = type;
    }
    
    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
