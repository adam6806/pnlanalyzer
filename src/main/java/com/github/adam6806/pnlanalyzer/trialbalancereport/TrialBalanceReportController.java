package com.github.adam6806.pnlanalyzer.trialbalancereport;

import com.github.adam6806.pnlanalyzer.company.Company;
import com.github.adam6806.pnlanalyzer.company.CompanyRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@Controller
public class TrialBalanceReportController {

    private final TrialBalanceReportRepository trialBalanceReportRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public TrialBalanceReportController(TrialBalanceReportRepository trialBalanceReportRepository, CompanyRepository companyRepository) {
        this.trialBalanceReportRepository = trialBalanceReportRepository;
        this.companyRepository = companyRepository;
    }

    @RequestMapping(value = "/trialbalancereport", method = RequestMethod.GET)
    public ModelAndView getTrialbalancereports() {
        ModelAndView modelAndView = new ModelAndView();
        List<TrialBalanceReport> trialBalanceReports = trialBalanceReportRepository.findAll();
        modelAndView.addObject("trialbalancereports", trialBalanceReports);
        modelAndView.setViewName("trialbalancereport");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport", method = RequestMethod.POST)
    public ModelAndView deleteTrialbalancereport(@RequestParam Long trialbalancereportId) {
        trialBalanceReportRepository.deleteById(trialbalancereportId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/trialbalancereport");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/lineitem", method = RequestMethod.GET)
    public ModelAndView getLineItemsForTRIALBALANCEREPORT(@RequestParam Long trialbalancereportId) {
        ModelAndView modelAndView = new ModelAndView();
        TrialBalanceReport trialBalanceReport = trialBalanceReportRepository.getOne(trialbalancereportId);
        modelAndView.addObject("lineitems", trialBalanceReport.getLineItems());
        modelAndView.setViewName("trialbalancereport/lineitem");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/addtrialbalancereport", method = RequestMethod.GET)
    public ModelAndView getAddTrialbalancereportForm() {
        ModelAndView modelAndView = new ModelAndView();
        List<Company> companies = companyRepository.findAll();
        modelAndView.addObject("companies", companies);
        modelAndView.setViewName("trialbalancereport/addtrialbalancereport");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/addtrialbalancereport", method = RequestMethod.POST)
    public ModelAndView addTrialbalancereport(@RequestParam("trialbalancereportfile") MultipartFile trialbalancereportfile, @RequestParam Long companyId) {

        ModelAndView modelAndView = new ModelAndView();

        try {
            String name = trialbalancereportfile.getOriginalFilename();
            InputStream stream = trialbalancereportfile.getInputStream();
            List<LineItem> lineItems = ExcelParser.parseExcelFile(stream);
            String dateString = lineItems.get(0).getDescription();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yy", Locale.ENGLISH);
            LocalDate dateTime = LocalDate.parse(dateString, formatter);
            lineItems.remove(0);
            TrialBalanceReport trialBalanceReport = new TrialBalanceReport();
            trialBalanceReport.setDate(Date.valueOf(dateTime));
            trialBalanceReport.setLineItems(new HashSet<>(lineItems));
            trialBalanceReport.setName(name);
            trialBalanceReport.setCompany(companyRepository.findById(companyId).get());
            trialBalanceReportRepository.save(trialBalanceReport);
            modelAndView.setViewName("trialbalancereport/lineitem");
            modelAndView.addObject("lineitems", lineItems);
            return modelAndView;
        } catch (IOException | InvalidFormatException ex) {
            modelAndView.setViewName("error");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/trialbalancereport/createjournalentries", method = RequestMethod.GET)
    public ModelAndView createJournalEntries(@RequestParam Long trialbalancereportId) {
        ModelAndView modelAndView = new ModelAndView();
        TrialBalanceReport current = trialBalanceReportRepository.getOne(trialbalancereportId);
        List<TrialBalanceReport> all = trialBalanceReportRepository.findAllByCompany_Id(current.getCompany().getId());
        all.removeIf(trialBalanceReport -> trialBalanceReport.getDate().compareTo(current.getDate()) >= 0);
        modelAndView.addObject("trialbalancereports", all);
        modelAndView.addObject("currentTbrId", current.getId());
        modelAndView.setViewName("trialbalancereport/createjournalentries");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/journalentries", method = RequestMethod.GET)
    public ModelAndView getJournalEntryLineItems(@RequestParam Long prevTbr, @RequestParam Long currentTbr) {
        ModelAndView modelAndView = new ModelAndView();
        List<LineItem> lineItems = createJournalEntryLineItems(prevTbr, currentTbr);
        modelAndView.addObject("lineitems", lineItems);
        modelAndView.addObject("prevTbrId", prevTbr);
        modelAndView.addObject("currentTbrId", currentTbr);
        modelAndView.setViewName("trialbalancereport/lineitem");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/downloadjournalentry", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(@RequestParam Long prevTbr, @RequestParam Long currentTbr) throws IOException {

        TrialBalanceReport current = trialBalanceReportRepository.getOne(currentTbr);
        List<LineItem> calculatedDifference = createJournalEntryLineItems(prevTbr, currentTbr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current.getDate());
        int month = calendar.get(Calendar.MONTH) + 1;
        String fileName = month + "-" + calendar.get(Calendar.YEAR) + "-" + current.getCompany().getName() + ".iif";

        Resource resource = new InputStreamResource(ExcelParser.generateJournalEntries(calculatedDifference, current));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    private List<LineItem> createJournalEntryLineItems(Long prevTbr, Long currentTbr) {

        TrialBalanceReport previous = trialBalanceReportRepository.getOne(prevTbr);
        TrialBalanceReport current = trialBalanceReportRepository.getOne(currentTbr);
        return DifferenceCalculator.calculateDifference(previous.getLineItems(), current.getLineItems());
    }
}
