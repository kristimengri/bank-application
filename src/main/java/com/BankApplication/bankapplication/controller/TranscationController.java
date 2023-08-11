package com.BankApplication.bankapplication.controller;

import com.BankApplication.bankapplication.entity.Transaction;
import com.BankApplication.bankapplication.enums.TransactionType;
import com.BankApplication.bankapplication.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TranscationController {

    private BankStatement bankStatement;

    @GetMapping
    public List<Transaction> generateBankStatement (
            @RequestParam String accountNumber,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) TransactionType transactionType
            ) throws DocumentException, FileNotFoundException {
        return bankStatement.generateStatement(accountNumber, startDate, endDate , transactionType);
    }
}
