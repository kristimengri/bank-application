package com.BankApplication.bankapplication.controller;


import com.BankApplication.bankapplication.dto.*;
import com.BankApplication.bankapplication.entity.User;
import com.BankApplication.bankapplication.repository.UserRepository;
import com.BankApplication.bankapplication.service.impl.UserService;
import com.BankApplication.bankapplication.utils.AccountUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    private Boolean validateTokenEnquiry(User user, String token, EnquiryRequest request) {
        String secretKey = "yourSecretKey";
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.replace("Bearer ", "")).getBody();
        String username = claims.getSubject();
        user = userRepository.findByAccountNumber(request.getAccountNumber());
        if (username != null && username.equals(user.getEmail())) {
            return true;
        } else {
            // Token is invalid or expired
            return false;
        }
    }

    private Boolean validateTokenCreditDebit(User user, String token, CreditDebitRequest request) {
        String secretKey = "yourSecretKey";
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.replace("Bearer ", "")).getBody();
        String username = claims.getSubject();
        user = userRepository.findByAccountNumber(request.getAccountNumber());
        if (username != null && username.equals(user.getEmail())) {
            return true;
        } else {
            // Token is invalid or expired
            return false;
        }
    }

    @Operation(
            summary = "Creating a New User Account",
            description = "Creating a new user and assigning an account Id"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PostMapping
    public BankResponse createAccount(
            @RequestBody UserRequest userRequest
    ) {
        return userService.createAccount(userRequest);
    }



    @Operation(
            summary = "Balance Enquiry",
            description = "Given an account number, check the user amount."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("/balanceEnquiry")
    public BankResponse balanceEnquiry(
            @RequestBody EnquiryRequest request,
            @RequestHeader("Authorization") String token,
            User user
    ) {

        if (validateTokenEnquiry(user, token, request))
            return userService.balanceEnquiry(request);
        else {
            // Token is invalid or expired
            return BankResponse.builder()
                    .responseCode(AccountUtils.INVALID_TOKEN_CODE)
                    .responseMessage(AccountUtils.INVALID_TOKEN_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }


    @GetMapping("/nameEnquiry")
    public String nameEnquiry(
            @RequestBody EnquiryRequest request,
            @RequestHeader("Authorization") String token,
            User user
    ) {
        if (validateTokenEnquiry(user, token, request))
            return userService.nameEnquiry(request);
        else {
            // Token is invalid or expired
            return AccountUtils.INVALID_TOKEN_MESSAGE;
        }
    }


    //Works as a patch method for the credit amount
    @PostMapping("/credit")
    public BankResponse creditAmount(
            @RequestBody CreditDebitRequest request,
            @RequestHeader("Authorization") String token,
            User user
    ) {
        if (validateTokenCreditDebit(user, token, request))
            return userService.creditAccount(request);
        else {
            // Token is invalid or expired
            return BankResponse.builder()
                    .responseCode(AccountUtils.INVALID_TOKEN_CODE)
                    .responseMessage(AccountUtils.INVALID_TOKEN_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    //Works as a patch method for the debit amount
    @PostMapping("/debit")
    public BankResponse debitAmount(
            @RequestBody CreditDebitRequest request,
            @RequestHeader("Authorization") String token,
            User user
    ) {
        if (validateTokenCreditDebit(user, token, request))
            return userService.debitAccount(request);
        else {
            // Token is invalid or expired
            return BankResponse.builder()
                    .responseCode(AccountUtils.INVALID_TOKEN_CODE)
                    .responseMessage(AccountUtils.INVALID_TOKEN_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }


    @PostMapping("/transfer")
    public BankResponse transfer(
            @RequestBody TransferRequest request,
            @RequestHeader("Authorization") String token,
            User user
    ) {
        String secretKey = "yourSecretKey";
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.replace("Bearer ", "")).getBody();
        String username = claims.getSubject();
        user = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (username != null && username.equals(user.getEmail())) {
            return userService.transfer(request);
        } else {
            // Token is invalid or expired
            return BankResponse.builder()
                    .responseCode(AccountUtils.INVALID_TOKEN_CODE)
                    .responseMessage(AccountUtils.INVALID_TOKEN_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public BankResponse deleteUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody LoginRequest request
    ) {
            Optional<User> user = userRepository.findById(id);
            String secretKey = "yourSecretKey";
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.replace("Bearer ", "")).getBody();
            String username = claims.getSubject();
            if (username != null && username.equals(request.getEmail()) && user.get().getPassword().equals(request.getPassword())) {
                userService.deleteUser(id);
                return null;
            }

        else{
            return BankResponse.builder()
                    .responseCode(AccountUtils.INVALID_TOKEN_CODE)
                    .responseMessage(AccountUtils.INVALID_TOKEN_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
    }
}
