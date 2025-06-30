package com.turno.los.controller;

import com.turno.los.domain.LoanApplication;
import com.turno.los.domain.enums.LoanStatus;
import com.turno.los.dto.LoanStatusCountDTO;
import com.turno.los.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanApplication> submitLoan(@RequestBody LoanApplication loanApplication) {
        LoanApplication submittedLoan = loanService.submitLoanApplication(loanApplication);
        return ResponseEntity.ok(submittedLoan);
    }

    @GetMapping("/status-count")
    public ResponseEntity<List<LoanStatusCountDTO>> getStatusCounts() {
        return ResponseEntity.ok(loanService.getLoanStatusCounts());
    }

    @GetMapping
    public ResponseEntity<Page<LoanApplication>> getLoansByStatus(
            @RequestParam("status") LoanStatus status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(loanService.getLoansByStatus(status, page, size));
    }

    @GetMapping("/assigned")
    public ResponseEntity<List<LoanApplication>> getAssignedLoans() {
        return ResponseEntity.ok(loanService.getAllLoansWithAgent());
    }
} 