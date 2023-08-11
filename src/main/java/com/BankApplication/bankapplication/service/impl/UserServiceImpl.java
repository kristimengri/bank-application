package com.BankApplication.bankapplication.service.impl;

import com.BankApplication.bankapplication.dto.*;
import com.BankApplication.bankapplication.entity.User;
import com.BankApplication.bankapplication.enums.TransactionType;
import com.BankApplication.bankapplication.repository.UserRepository;
import com.BankApplication.bankapplication.utils.AccountUtils;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;


@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    TransactionService transactionService;


    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return authenticationService.authenticateUser(loginRequest);
    }


    @Override
    public BankResponse createAccount(UserRequest userRequest){

        //Creating an account - saving a new user into the db.

        /*

        *Checks if user already have an account.

         */

        if(userRepository.existsByEmail(userRequest.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();

        }


        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .middleName(userRequest.getMiddleName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequest.getEmail())
                .password(userRequest.getPassword())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhone(userRequest.getAlternativePhone())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        String token = generateJwtToken(savedUser);

        //Creating emailDetails Object
        EmailDetails emailDetails = EmailDetails.builder()
                .recipent(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulaions! \nYour account has been successfully created. \nYour Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " +savedUser.getLastName() + " " + savedUser.getMiddleName() + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();

        //Sending an Email Alert
        emailService.sendEmailAlerts(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getMiddleName())
                        .build())
                .jwtToken(token)
                .build();

    }

    // Balance Enquiry, name Enquiry, credit , debit , transfer

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {

        //First check if the provided account number exists in the database

        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());

        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getMiddleName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {

        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExists) {
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }

        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getMiddleName();

    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {

        //checking if the account exists
        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());

        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());

        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));

        userRepository.save(userToCredit);

        //Save the transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType(TransactionType.CREDIT)
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        //Creating emailDetails Object for Email Notification
        EmailDetails emailDetails = EmailDetails.builder()
                .recipent(userToCredit.getEmail())
                .subject("ACCOUNT CREDITED")
                .messageBody("Your account has been credited successfully. \nYour Account Details: \n" +
                        "Account Name: " + userToCredit.getFirstName() + " " +userToCredit.getLastName() + " " + userToCredit.getMiddleName() + "\nAccount Number: " + userToCredit.getAccountNumber() + "\nAccount Balance: " + userToCredit.getAccountBalance())
                .build();

        //Sending an Email Alert
        emailService.sendEmailAlerts(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName() + " " + userToCredit.getMiddleName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        //check first if the account exists
        //check if the amount you intend to withdraw is not more than the current account balance

        boolean isAccountExists = userRepository.existsByAccountNumber(request.getAccountNumber());

        if(!isAccountExists){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());


        //Available Balance
        //First convert the available balance into big integer to perform the comparison
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();

        if( availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        else{

            //We set the new subtracted debit balance and save it into the database for the particular user
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            //Save the transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType(TransactionType.DEBIT)
                    .amount(request.getAmount())
                    .build();

            transactionService.saveTransaction(transactionDto);

            //Creating emailDetails Object for Email Notification
            EmailDetails emailDetails = EmailDetails.builder()
                    .recipent(userToDebit.getEmail())
                    .subject("ACCOUNT DEBITED")
                    .messageBody("Your account has been debited successfully. \nYour Account Details: \n" +
                            "Account Name: " + userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getMiddleName() + "\nAccount Number: " + userToDebit.getAccountNumber() + "\nAccount Balance: " + userToDebit.getAccountBalance())
                    .build();

            //Sending an Email Alert
            emailService.sendEmailAlerts(emailDetails);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESSFULLY_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESSFULLY_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(request.getAccountNumber())
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName() + " " + userToDebit.getMiddleName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }

    }

    @Override
    public BankResponse transfer(TransferRequest request) {

        //Get the account to debit.
        //Check if the amount debiting is not more than the current balance.
        //Debit the account.
        //Get the account to credit.
        //Credit the account.

        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());

        if(!isDestinationAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());

        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUserName = sourceAccountUser.getFirstName() + " " +  sourceAccountUser.getLastName() + " " + sourceAccountUser.getMiddleName();

        userRepository.save(sourceAccountUser);

        //Save the transaction
        TransactionDto transactionDtoDebit = TransactionDto.builder()
                .accountNumber(sourceAccountUser.getAccountNumber())
                .transactionType(TransactionType.DEBIT)
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDtoDebit);

        EmailDetails debitAlerts = EmailDetails.builder()
                .subject("DEBIT ALERT")
                .recipent(sourceAccountUser.getEmail())
                .messageBody("The sum of: " + request.getAmount() + " has been deducted from your account!\nYour current balance is:  " + sourceAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlerts(debitAlerts);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));

//      String recipientUsername = destinationAccountUser.getFirstName() + " " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getMiddleName();

        userRepository.save(destinationAccountUser);

        //Save the transaction
        TransactionDto transactionDtoCredit = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType(TransactionType.CREDIT)
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDtoCredit);

        EmailDetails creditAlerts = EmailDetails.builder()
                .subject("CREDIT ALERT")
                .recipent(destinationAccountUser.getEmail())
                .messageBody("The sum of: " + request.getAmount() + " has been sent to your account from: " + sourceUserName + "\nWith Account Number: " + sourceAccountUser.getAccountNumber() + "\nYour current balance is:  " + destinationAccountUser.getAccountBalance())
                .build();

        emailService.sendEmailAlerts(creditAlerts);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MESSAGE)
                .accountInfo(null)
                .build();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
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
