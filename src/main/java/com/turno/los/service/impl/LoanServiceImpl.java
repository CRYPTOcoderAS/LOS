package com.turno.los.service.impl;

import com.turno.los.domain.LoanApplication;
import com.turno.los.domain.User;
import com.turno.los.domain.enums.LoanStatus;
import com.turno.los.dto.LoanStatusCountDTO;
import com.turno.los.dto.TopCustomerDTO;
import com.turno.los.repository.LoanApplicationRepository;
import com.turno.los.repository.UserRepository;
import com.turno.los.service.LoanService;
import com.turno.los.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public LoanApplication submitLoanApplication(LoanApplication loanApplication) {
        loanApplication.setApplicationStatus(LoanStatus.APPLIED);
        return loanApplicationRepository.save(loanApplication);
    }

    @Override
    public Page<LoanApplication> getLoansByStatus(LoanStatus status, int page, int size) {
        return loanApplicationRepository.findByApplicationStatus(status, PageRequest.of(page, size));
    }

    @Override
    public List<LoanStatusCountDTO> getLoanStatusCounts() {
        return loanApplicationRepository.countByStatus();
    }

    @Override
    public void processLoans() {
        List<LoanApplication> appliedLoans = loanApplicationRepository.findByApplicationStatus(LoanStatus.APPLIED);
        appliedLoans.forEach(this::processSingleLoan);
    }

    @Async("taskExecutor")
    @Transactional
    public void processSingleLoan(LoanApplication loan) {
        try {
            loan.setApplicationStatus(LoanStatus.PROCESSING);
            loanApplicationRepository.save(loan);
            log.info("Processing loan: {}", loan.getLoanId());
            Thread.sleep(25000);
            Random random = new Random();
            int decision = random.nextInt(3);
            switch (decision) {
                case 0:
                    loan.setApplicationStatus(LoanStatus.APPROVED_BY_SYSTEM);
                    notificationService.sendSmsToCustomer(loan);
                    break;
                case 1:
                    loan.setApplicationStatus(LoanStatus.REJECTED_BY_SYSTEM);
                    notificationService.sendSmsToCustomer(loan);
                    break;
                case 2:
                    loan.setApplicationStatus(LoanStatus.UNDER_REVIEW);
                    assignToAgent(loan);
                    break;
            }
            loanApplicationRepository.save(loan);
            log.info("Processed loan: {} with status: {}", loan.getLoanId(), loan.getApplicationStatus());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Loan processing was interrupted for loan: {}", loan.getLoanId(), e);
        }
    }

    private void assignToAgent(LoanApplication loan) {
        List<User> agents = userRepository.findAllAgents();
        if (!agents.isEmpty()) {
            User agent = agents.get(new Random().nextInt(agents.size()));
            loan.setAgent(agent);
            notificationService.sendPushNotificationToAgent(agent, loan);
        } else {
            log.warn("No agents available to assign loan: {}", loan.getLoanId());
        }
    }

    @Override
    @Transactional
    public void agentDecision(Long loanId, Long agentId, String decision) {
        LoanApplication loan = loanApplicationRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        if (loan.getAgent() == null) {
            throw new RuntimeException("Loan is not assigned to any agent yet. Wait for the background processor to assign an agent.");
        }
        if (!loan.getAgent().getId().equals(agentId)) {
            throw new RuntimeException("Agent not authorized to make a decision on this loan");
        }
        if ("APPROVE".equalsIgnoreCase(decision)) {
            loan.setApplicationStatus(LoanStatus.APPROVED_BY_AGENT);
        } else if ("REJECT".equalsIgnoreCase(decision)) {
            loan.setApplicationStatus(LoanStatus.REJECTED_BY_AGENT);
        } else {
            throw new RuntimeException("Invalid decision");
        }
        notificationService.sendSmsToCustomer(loan);
        loanApplicationRepository.save(loan);
    }

    @Override
    public List<TopCustomerDTO> getTopCustomers() {
        return loanApplicationRepository.findTopCustomers(PageRequest.of(0, 3));
    }

    @Override
    public List<LoanApplication> getAllLoansWithAgent() {
        return loanApplicationRepository.findAll();
    }
} 