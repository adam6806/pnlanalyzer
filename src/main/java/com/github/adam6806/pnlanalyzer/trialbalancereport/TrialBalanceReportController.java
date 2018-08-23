package com.github.adam6806.pnlanalyzer.trialbalancereport;

import com.github.adam6806.pnlanalyzer.company.Company;
import com.github.adam6806.pnlanalyzer.company.CompanyRepository;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ModelAndView addTrialbalancereport(@RequestParam("trialbalancereportfile") MultipartFile trialbalancereportfile, @RequestParam int month, @RequestParam int year, @RequestParam Long companyId) {

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
}
