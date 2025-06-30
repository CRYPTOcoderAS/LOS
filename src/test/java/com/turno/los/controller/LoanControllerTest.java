package com.turno.los.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turno.los.domain.LoanApplication;
import com.turno.los.domain.enums.LoanStatus;
import com.turno.los.domain.enums.LoanType;
import com.turno.los.dto.LoanStatusCountDTO;
import com.turno.los.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanController.class)
class LoanControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LoanService loanService;

    private LoanApplication sampleLoan;

    @BeforeEach
    void setUp() {
        sampleLoan = LoanApplication.builder()
                .loanId(1L)
                .customerName("John Doe")
                .customerPhone("1234567890")
                .loanAmount(10000.0)
                .loanType(LoanType.PERSONAL)
                .applicationStatus(LoanStatus.APPLIED)
                .build();
    }

    @Test
    void submitLoan_Success() throws Exception {
        when(loanService.submitLoanApplication(any())).thenReturn(sampleLoan);
        mockMvc.perform(post("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleLoan)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("John Doe"));
    }

    @Test
    void getStatusCounts_Success() throws Exception {
        List<LoanStatusCountDTO> counts = Arrays.asList(new LoanStatusCountDTO(LoanStatus.APPLIED, 2));
        when(loanService.getLoanStatusCounts()).thenReturn(counts);
        mockMvc.perform(get("/api/v1/loans/status-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("APPLIED"));
    }

    @Test
    void getLoansByStatus_Success() throws Exception {
        Page<LoanApplication> page = new PageImpl<>(Collections.singletonList(sampleLoan));
        when(loanService.getLoansByStatus(eq(LoanStatus.APPLIED), anyInt(), anyInt())).thenReturn(page);
        mockMvc.perform(get("/api/v1/loans")
                .param("status", "APPLIED")
                .param("page", "0")
                .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].customerName").value("John Doe"));
    }

    @Test
    void submitLoan_Failure_BadRequest() throws Exception {
        // Missing required field
        LoanApplication invalidLoan = LoanApplication.builder().build();
        when(loanService.submitLoanApplication(any())).thenThrow(new RuntimeException("Validation failed"));
        mockMvc.perform(post("/api/v1/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLoan)))
                .andExpect(status().isInternalServerError());
    }
} 