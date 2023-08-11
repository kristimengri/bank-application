package com.BankApplication.bankapplication.service.impl;

import com.BankApplication.bankapplication.dto.*;
import org.springframework.stereotype.Repository;

public interface UserService {

    LoginResponse login(LoginRequest loginRequest);
    BankResponse createAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse debitAccount(CreditDebitRequest request);
    BankResponse transfer(TransferRequest request);
    void deleteUser(Long id);
}
