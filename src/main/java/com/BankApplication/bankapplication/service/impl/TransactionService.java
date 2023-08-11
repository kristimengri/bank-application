package com.BankApplication.bankapplication.service.impl;

import com.BankApplication.bankapplication.dto.TransactionDto;
import com.BankApplication.bankapplication.entity.Transaction;

public interface TransactionService {

    void saveTransaction(TransactionDto transactionDto);
}
