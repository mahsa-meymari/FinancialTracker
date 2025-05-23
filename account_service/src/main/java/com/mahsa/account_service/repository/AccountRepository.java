package com.mahsa.account_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mahsa.account_service.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
 List<Account> findByUserId(Long userId);
}
