package com.github.adam6806.pnlanalyzer.trialbalancereport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrialBalanceReportRepository extends JpaRepository<TrialBalanceReport, Long> {

}
