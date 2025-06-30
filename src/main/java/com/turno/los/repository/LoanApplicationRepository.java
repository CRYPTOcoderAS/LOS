package com.turno.los.repository;

import com.turno.los.domain.LoanApplication;
import com.turno.los.domain.enums.LoanStatus;
import com.turno.los.dto.LoanStatusCountDTO;
import com.turno.los.dto.TopCustomerDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {

    List<LoanApplication> findByApplicationStatus(LoanStatus status);

    Page<LoanApplication> findByApplicationStatus(LoanStatus status, Pageable pageable);

    @Query("SELECT new com.turno.los.dto.LoanStatusCountDTO(l.applicationStatus, count(l)) FROM LoanApplication l GROUP BY l.applicationStatus")
    List<LoanStatusCountDTO> countByStatus();

    @Query("SELECT new com.turno.los.dto.TopCustomerDTO(l.customerName, count(l)) FROM LoanApplication l WHERE l.applicationStatus IN ('APPROVED_BY_SYSTEM', 'APPROVED_BY_AGENT') GROUP BY l.customerName ORDER BY count(l) DESC")
    List<TopCustomerDTO> findTopCustomers(Pageable pageable);
} 