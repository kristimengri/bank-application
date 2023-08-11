package com.BankApplication.bankapplication.service.impl;

import com.BankApplication.bankapplication.dto.EmailDetails;

public interface EmailService {

    void sendEmailAlerts(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}
