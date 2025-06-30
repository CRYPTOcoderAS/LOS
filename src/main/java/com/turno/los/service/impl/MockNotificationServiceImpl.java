package com.turno.los.service.impl;

import com.turno.los.domain.LoanApplication;
import com.turno.los.domain.User;
import com.turno.los.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockNotificationServiceImpl implements NotificationService {

    @Override
    public void sendPushNotificationToAgent(User agent, LoanApplication loanApplication) {
        log.info("Sending push notification to agent: {} for loan: {}", agent.getName(), loanApplication.getLoanId());
        if (agent.getManager() != null) {
            log.info("Sending push notification to manager: {} for loan: {}", agent.getManager().getName(), loanApplication.getLoanId());
        }
    }

    @Override
    public void sendSmsToCustomer(LoanApplication loanApplication) {
        log.info("Sending SMS to customer: {} for loan: {}", loanApplication.getCustomerName(), loanApplication.getLoanId());
    }
} 