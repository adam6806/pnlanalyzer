package com.github.adam6806.pnlanalyzer.repositories;

import com.github.adam6806.pnlanalyzer.domain.TrialBalanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrialBalanceReportRepository extends JpaRepository<TrialBalanceReport, Long> {
    List<TrialBalanceReport> findAllByCompany_Id(Long id);
}
