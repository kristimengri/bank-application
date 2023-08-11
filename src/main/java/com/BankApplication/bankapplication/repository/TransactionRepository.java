package com.BankApplication.bankapplication.repository;

import com.BankApplication.bankapplication.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction,String> {
}
