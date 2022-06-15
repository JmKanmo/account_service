package com.zerobase.account_service.repository;

import com.zerobase.account_service.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Transaction findByTransactionId(String transactionId);
}
