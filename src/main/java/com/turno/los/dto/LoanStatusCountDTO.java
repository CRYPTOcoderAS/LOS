package com.turno.los.dto;

import com.turno.los.domain.enums.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatusCountDTO {
    private LoanStatus status;
    private long count;
} 