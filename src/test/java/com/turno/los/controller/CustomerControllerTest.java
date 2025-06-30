package com.turno.los.controller;

import com.turno.los.dto.TopCustomerDTO;
import com.turno.los.service.LoanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LoanService loanService;

    @Test
    void getTopCustomers_Success() throws Exception {
        TopCustomerDTO dto = new TopCustomerDTO("John Doe", 3);
        when(loanService.getTopCustomers()).thenReturn(Arrays.asList(dto));
        mockMvc.perform(get("/api/v1/customers/top")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("John Doe"))
                .andExpect(jsonPath("$[0].approvedCount").value(3));
    }

    @Test
    void getTopCustomers_Empty() throws Exception {
        when(loanService.getTopCustomers()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/v1/customers/top")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
} 