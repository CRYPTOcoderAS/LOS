package com.turno.los.service;

import com.turno.los.domain.LoanApplication;
import com.turno.los.domain.User;

public interface NotificationService {
    void sendPushNotificationToAgent(User agent, LoanApplication loanApplication);
    void sendSmsToCustomer(LoanApplication loanApplication);
} 