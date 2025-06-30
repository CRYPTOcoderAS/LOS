package com.turno.los.service;

import com.turno.los.domain.LoanApplication;
import com.turno.los.domain.enums.LoanStatus;
import com.turno.los.dto.LoanStatusCountDTO;
import com.turno.los.dto.TopCustomerDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoanService {

    LoanApplication submitLoanApplication(LoanApplication loanApplication);

    Page<LoanApplication> getLoansByStatus(LoanStatus status, int page, int size);

    List<LoanStatusCountDTO> getLoanStatusCounts();

    void processLoans();

    void agentDecision(Long loanId, Long agentId, String decision);

    List<TopCustomerDTO> getTopCustomers();

    List<LoanApplication> getAllLoansWithAgent();
} 