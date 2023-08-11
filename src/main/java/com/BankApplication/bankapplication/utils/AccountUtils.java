package com.BankApplication.bankapplication.utils;


import java.time.Year;


public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE = "001";
    public static final String ACCOUNT_EXISTS_MESSAGE = "This user already has an account created.";
    public static final String ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account has been successfully created.";
    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided Account Number does not exists";
    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_SUCCESS = "User Account Found Successfully";
    public static final String ACCOUNT_CREDITED_SUCCESS_CODE = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User Account Credited Successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance";
    public static final String ACCOUNT_DEBITED_SUCCESSFULLY_CODE = "007";
    public static final String ACCOUNT_DEBITED_SUCCESSFULLY_MESSAGE = "Account has been successfully debited";
    public static final String TRANSFER_SUCCESSFUL_CODE = "008";
    public static final String TRANSFER_SUCCESS_MESSAGE = "Transfer successful";
    public static final String INVALID_TOKEN_CODE = "009";
    public static final String INVALID_TOKEN_MESSAGE = "Token Invalid. Authorization failed.";


    /* 2023 + randomSixDigits

    *2023112233

    */
    public static String generateAccountNumber(){
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        //Generate a random number between min and max
        int randNumber = (int) Math.floor(Math.random() * (max - min + 1 ) + min);

        //convert the current and randomNumber to strings, then concatenate

        String year = String.valueOf(currentYear);

        String randomNumber =  String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();

        return year + randomNumber;



    }

}
