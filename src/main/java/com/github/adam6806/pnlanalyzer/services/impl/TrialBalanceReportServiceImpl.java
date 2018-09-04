package com.github.adam6806.pnlanalyzer.services.impl;

import com.github.adam6806.pnlanalyzer.entities.LineItem;
import com.github.adam6806.pnlanalyzer.entities.TrialBalanceReport;
import com.github.adam6806.pnlanalyzer.repositories.CompanyRepository;
import com.github.adam6806.pnlanalyzer.repositories.TrialBalanceReportRepository;
import com.github.adam6806.pnlanalyzer.services.DifferenceCalculatorService;
import com.github.adam6806.pnlanalyzer.services.ExcelParserService;
import com.github.adam6806.pnlanalyzer.services.TrialBalanceReportService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service("trialBalanceReportService")
@Transactional
public class TrialBalanceReportServiceImpl implements TrialBalanceReportService {

    private final TrialBalanceReportRepository trialBalanceReportRepository;
    private final CompanyRepository companyRepository;
    private final DifferenceCalculatorService differenceCalculatorService;
    private final ExcelParserService excelParserService;

    @Autowired
    public TrialBalanceReportServiceImpl(TrialBalanceReportRepository trialBalanceReportRepository, CompanyRepository companyRepository, DifferenceCalculatorService differenceCalculatorService, ExcelParserService excelParserService) {
        this.trialBalanceReportRepository = trialBalanceReportRepository;
        this.companyRepository = companyRepository;
        this.differenceCalculatorService = differenceCalculatorService;
        this.excelParserService = excelParserService;
    }

    @Override
    public List<TrialBalanceReport> findAllTrialBalanceReports() {
        return trialBalanceReportRepository.findAll();
    }

    @Override
    public TrialBalanceReport findTrialBalanceReportById(Long id) {
        return trialBalanceReportRepository.getOne(id);
    }

    @Override
    public void deleteTrialBalanceReportById(Long id) {
        trialBalanceReportRepository.deleteById(id);
    }

    @Override
    public TrialBalanceReport createTrialBalanceReport(MultipartFile file, Long companyId) throws IOException, InvalidFormatException {
        String name = file.getOriginalFilename();
        InputStream stream = file.getInputStream();
        List<LineItem> lineItems = excelParserService.parseExcelFile(stream);
        String dateString = lineItems.get(0).getDescription();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yy", Locale.ENGLISH);
        LocalDate dateTime = LocalDate.parse(dateString, formatter);
        lineItems.remove(0);
        TrialBalanceReport trialBalanceReport = new TrialBalanceReport();
        trialBalanceReport.setDate(Date.valueOf(dateTime));
        trialBalanceReport.setLineItems(new HashSet<>(lineItems));
        trialBalanceReport.setName(name);
        trialBalanceReport.setCompany(companyRepository.getOne(companyId));
        trialBalanceReportRepository.save(trialBalanceReport);
        return trialBalanceReport;
    }

    @Override
    public List<TrialBalanceReport> getPreviousTrialBalanceReports(Long currentTrialBalanceReportId) {
        TrialBalanceReport current = trialBalanceReportRepository.getOne(currentTrialBalanceReportId);
        List<TrialBalanceReport> all = trialBalanceReportRepository.findAllByCompany_Id(current.getCompany().getId());
        all.removeIf(trialBalanceReport -> trialBalanceReport.getDate().compareTo(current.getDate()) >= 0);
        Comparator<TrialBalanceReport> comparator = Comparator.comparing(TrialBalanceReport::getDate);
        all.sort(comparator.reversed());
        return all;
    }

    @Override
    public List<LineItem> createJournalEntries(Long previousTrialBalanceReportId, Long currentTrialBalanceReportId) {
        return createJournalEntryLineItems(previousTrialBalanceReportId, currentTrialBalanceReportId);

    }

    @Override
    public ResponseEntity<Resource> createJournalEntryFile(Long previousTrialBalanceReportId, Long currentTrialBalanceReportId) throws IOException {
        TrialBalanceReport current = trialBalanceReportRepository.getOne(currentTrialBalanceReportId);
        List<LineItem> journalEntries = createJournalEntries(previousTrialBalanceReportId, currentTrialBalanceReportId);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current.getDate());
        int month = calendar.get(Calendar.MONTH) + 1;
        String fileName = month + "-" + calendar.get(Calendar.YEAR) + "-" + current.getCompany().getName() + "-TBRResult.iif";
        Resource resource = new InputStreamResource(excelParserService.generateJournalEntries(journalEntries, current));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    private List<LineItem> createJournalEntryLineItems(Long prevTbr, Long currentTbr) {

        TrialBalanceReport previous = trialBalanceReportRepository.getOne(prevTbr);
        TrialBalanceReport current = trialBalanceReportRepository.getOne(currentTbr);
        return differenceCalculatorService.calculateDifference(previous.getLineItems(), current.getLineItems());
    }
}
