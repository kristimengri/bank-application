package com.BankApplication.bankapplication.service.impl;

import com.BankApplication.bankapplication.dto.LoginRequest;
import com.BankApplication.bankapplication.dto.LoginResponse;
import com.BankApplication.bankapplication.entity.User;
import com.BankApplication.bankapplication.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public LoginResponse authenticateUser(LoginRequest loginRequest){
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Authentication successful, generate JWT token
            String jwtToken = generateJwtToken(user);
            return new LoginResponse(jwtToken);
        }

        return null; // Authentication failed
    }

    private String generateJwtToken(User user) {
        // Generate JWT token using user information
        String jwtToken = Jwts.builder()
                .setSubject(user.getEmail())
                // Set other claims if needed
                .signWith(SignatureAlgorithm.HS512, "yourSecretKey")
                .compact();
        return jwtToken;
    }

}
