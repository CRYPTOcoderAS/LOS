package com.turno.los.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanProcessingScheduler {

    private final LoanService loanService;

    @Scheduled(fixedRate = 30000)
    public void scheduleLoanProcessing() {
        log.info("Starting scheduled loan processing...");
        loanService.processLoans();
    }
} 