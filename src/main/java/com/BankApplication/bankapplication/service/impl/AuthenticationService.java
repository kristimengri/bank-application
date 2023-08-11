package com.BankApplication.bankapplication.service.impl;

import com.BankApplication.bankapplication.dto.LoginRequest;
import com.BankApplication.bankapplication.dto.LoginResponse;

public interface AuthenticationService {

    LoginResponse authenticateUser(LoginRequest loginRequest);
}
