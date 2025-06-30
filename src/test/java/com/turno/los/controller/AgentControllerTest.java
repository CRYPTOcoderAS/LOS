package com.turno.los.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.turno.los.dto.AgentDecisionDTO;
import com.turno.los.service.LoanService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgentController.class)
class AgentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LoanService loanService;

    @Test
    void makeDecision_Success() throws Exception {
        AgentDecisionDTO dto = new AgentDecisionDTO();
        dto.setDecision("APPROVE");
        mockMvc.perform(put("/api/v1/agents/1/loans/2/decision")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void makeDecision_Failure_InvalidDecision() throws Exception {
        AgentDecisionDTO dto = new AgentDecisionDTO();
        dto.setDecision("INVALID");
        doThrow(new RuntimeException("Invalid decision")).when(loanService).agentDecision(anyLong(), anyLong(), eq("INVALID"));
        mockMvc.perform(put("/api/v1/agents/1/loans/2/decision")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError());
    }
} 