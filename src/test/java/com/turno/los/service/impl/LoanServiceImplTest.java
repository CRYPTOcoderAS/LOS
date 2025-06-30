package com.turno.los.service.impl;

import com.turno.los.domain.LoanApplication;
import com.turno.los.domain.User;
import com.turno.los.domain.enums.LoanStatus;
import com.turno.los.domain.enums.LoanType;
import com.turno.los.dto.LoanStatusCountDTO;
import com.turno.los.repository.LoanApplicationRepository;
import com.turno.los.repository.UserRepository;
import com.turno.los.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoanServiceImplTest {
    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LoanServiceImpl loanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSubmitLoanApplication() {
        LoanApplication loan = LoanApplication.builder()
                .customerName("Test User")
                .customerPhone("1234567890")
                .loanAmount(10000.0)
                .loanType(LoanType.PERSONAL)
                .build();
        when(loanApplicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        LoanApplication result = loanService.submitLoanApplication(loan);
        assertEquals(LoanStatus.APPLIED, result.getApplicationStatus());
        verify(loanApplicationRepository).save(result);
    }

    @Test
    void testGetLoansByStatus() {
        LoanApplication loan = new LoanApplication();
        Page<LoanApplication> page = new PageImpl<>(Collections.singletonList(loan));
        when(loanApplicationRepository.findByApplicationStatus(eq(LoanStatus.APPLIED), any(PageRequest.class))).thenReturn(page);
        Page<LoanApplication> result = loanService.getLoansByStatus(LoanStatus.APPLIED, 0, 1);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetLoanStatusCounts() {
        List<LoanStatusCountDTO> counts = Arrays.asList(new LoanStatusCountDTO(LoanStatus.APPLIED, 2));
        when(loanApplicationRepository.countByStatus()).thenReturn(counts);
        List<LoanStatusCountDTO> result = loanService.getLoanStatusCounts();
        assertEquals(1, result.size());
        assertEquals(LoanStatus.APPLIED, result.get(0).getStatus());
    }

    @Test
    void testAgentDecisionApprove() {
        User agent = User.builder().id(1L).name("Agent").build();
        LoanApplication loan = LoanApplication.builder().loanId(2L).agent(agent).applicationStatus(LoanStatus.UNDER_REVIEW).build();
        when(loanApplicationRepository.findById(2L)).thenReturn(Optional.of(loan));
        when(loanApplicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        loanService.agentDecision(2L, 1L, "APPROVE");
        assertEquals(LoanStatus.APPROVED_BY_AGENT, loan.getApplicationStatus());
        verify(notificationService).sendSmsToCustomer(loan);
    }

    @Test
    void testAgentDecisionReject() {
        User agent = User.builder().id(1L).name("Agent").build();
        LoanApplication loan = LoanApplication.builder().loanId(2L).agent(agent).applicationStatus(LoanStatus.UNDER_REVIEW).build();
        when(loanApplicationRepository.findById(2L)).thenReturn(Optional.of(loan));
        when(loanApplicationRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        loanService.agentDecision(2L, 1L, "REJECT");
        assertEquals(LoanStatus.REJECTED_BY_AGENT, loan.getApplicationStatus());
        verify(notificationService).sendSmsToCustomer(loan);
    }

    @Test
    void testAgentDecisionInvalidAgent() {
        User agent = User.builder().id(1L).name("Agent").build();
        LoanApplication loan = LoanApplication.builder().loanId(2L).agent(agent).applicationStatus(LoanStatus.UNDER_REVIEW).build();
        when(loanApplicationRepository.findById(2L)).thenReturn(Optional.of(loan));
        Exception ex = assertThrows(RuntimeException.class, () -> loanService.agentDecision(2L, 99L, "APPROVE"));
        assertTrue(ex.getMessage().contains("Agent not authorized"));
    }

    @Test
    void testAgentDecisionInvalidDecision() {
        User agent = User.builder().id(1L).name("Agent").build();
        LoanApplication loan = LoanApplication.builder().loanId(2L).agent(agent).applicationStatus(LoanStatus.UNDER_REVIEW).build();
        when(loanApplicationRepository.findById(2L)).thenReturn(Optional.of(loan));
        Exception ex = assertThrows(RuntimeException.class, () -> loanService.agentDecision(2L, 1L, "INVALID"));
        assertTrue(ex.getMessage().contains("Invalid decision"));
    }
} 