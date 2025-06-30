package com.turno.los.controller;

import com.turno.los.dto.TopCustomerDTO;
import com.turno.los.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final LoanService loanService;

    @GetMapping("/top")
    public ResponseEntity<List<TopCustomerDTO>> getTopCustomers() {
        return ResponseEntity.ok(loanService.getTopCustomers());
    }
} 