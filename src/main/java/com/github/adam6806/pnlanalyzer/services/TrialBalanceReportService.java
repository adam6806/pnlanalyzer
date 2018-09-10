package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.domain.LineItem;
import com.github.adam6806.pnlanalyzer.domain.TrialBalanceReport;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TrialBalanceReportService {

    List<TrialBalanceReport> findAllTrialBalanceReports();

    TrialBalanceReport findTrialBalanceReportById(Long id);

    void deleteTrialBalanceReportById(Long id);

    TrialBalanceReport createTrialBalanceReport(MultipartFile file, Long companyId) throws IOException, InvalidFormatException;

    List<TrialBalanceReport> getPreviousTrialBalanceReports(Long currentTrialBalanceReportId);

    List<LineItem> createJournalEntries(Long previousTrialBalanceReportId, Long currentTrialBalanceReportId);

    ResponseEntity<Resource> createJournalEntryFile(Long previousTrialBalanceReportId, Long currentTrialBalanceReportId) throws IOException;
}
