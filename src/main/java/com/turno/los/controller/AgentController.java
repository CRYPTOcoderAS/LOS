package com.turno.los.controller;

import com.turno.los.dto.AgentDecisionDTO;
import com.turno.los.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final LoanService loanService;

    @PutMapping("/{agentId}/loans/{loanId}/decision")
    public ResponseEntity<Void> makeDecision(
            @PathVariable Long agentId,
            @PathVariable Long loanId,
            @RequestBody AgentDecisionDTO decisionDTO) {
        loanService.agentDecision(loanId, agentId, decisionDTO.getDecision());
        return ResponseEntity.ok().build();
    }
} 