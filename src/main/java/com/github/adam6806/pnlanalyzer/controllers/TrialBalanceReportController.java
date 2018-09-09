package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.domain.Company;
import com.github.adam6806.pnlanalyzer.domain.LineItem;
import com.github.adam6806.pnlanalyzer.domain.TrialBalanceReport;
import com.github.adam6806.pnlanalyzer.services.CompanyService;
import com.github.adam6806.pnlanalyzer.services.TrialBalanceReportService;
import com.github.adam6806.pnlanalyzer.utility.Message;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class TrialBalanceReportController {

    private final TrialBalanceReportService trialBalanceReportService;
    private final CompanyService companyService;

    @Autowired
    public TrialBalanceReportController(TrialBalanceReportService trialBalanceReportService, CompanyService companyService) {
        this.trialBalanceReportService = trialBalanceReportService;
        this.companyService = companyService;
    }

    @RequestMapping(value = "/trialbalancereport", method = RequestMethod.GET)
    public ModelAndView getTrialbalancereports(@ModelAttribute Message message) {
        ModelAndView modelAndView = new ModelAndView();
        List<TrialBalanceReport> trialBalanceReports = trialBalanceReportService.findAllTrialBalanceReports();
        modelAndView.addObject("trialbalancereports", trialBalanceReports);
        modelAndView.addObject("message", message);
        modelAndView.setViewName("trialbalancereport");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport", method = RequestMethod.POST)
    public ModelAndView deleteTrialbalancereport(@RequestParam Long trialbalancereportId, RedirectAttributes redirectAttributes) {
        trialBalanceReportService.deleteTrialBalanceReportById(trialbalancereportId);
        ModelAndView modelAndView = new ModelAndView();
        redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("Trial Balance Report was deleted successfully."));
        modelAndView.setViewName("redirect:/trialbalancereport");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/lineitem", method = RequestMethod.GET)
    public ModelAndView getLineItemsForTRIALBALANCEREPORT(@RequestParam Long trialbalancereportId) {
        ModelAndView modelAndView = new ModelAndView();
        TrialBalanceReport trialBalanceReport = trialBalanceReportService.findTrialBalanceReportById(trialbalancereportId);
        modelAndView.addObject("lineitems", trialBalanceReport.getLineItems());
        modelAndView.setViewName("trialbalancereport/lineitem");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/addtrialbalancereport", method = RequestMethod.GET)
    public ModelAndView getAddTrialbalancereportForm() {
        ModelAndView modelAndView = new ModelAndView();
        List<Company> companies = companyService.findAllCompanies();
        modelAndView.addObject("companies", companies);
        modelAndView.setViewName("trialbalancereport/addtrialbalancereport");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/addtrialbalancereport", method = RequestMethod.POST)
    public ModelAndView addTrialbalancereport(@RequestParam("trialbalancereportfile") MultipartFile trialbalancereportfile, @RequestParam Long companyId) {

        ModelAndView modelAndView = new ModelAndView();

        try {
            TrialBalanceReport trialBalanceReport = trialBalanceReportService.createTrialBalanceReport(trialbalancereportfile, companyId);
            modelAndView.setViewName("trialbalancereport/lineitem");
            modelAndView.addObject("lineitems", trialBalanceReport.getLineItems());
            return modelAndView;
        } catch (IOException | InvalidFormatException ex) {
            // TODO Make this return an error message back to the trial balance report page.
            modelAndView.setViewName("error");
            return modelAndView;
        }
    }

    @RequestMapping(value = "/trialbalancereport/createjournalentries", method = RequestMethod.GET)
    public ModelAndView createJournalEntries(@RequestParam Long trialbalancereportId, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        List<TrialBalanceReport> all = trialBalanceReportService.getPreviousTrialBalanceReports(trialbalancereportId);
        if (all.isEmpty()) {
            redirectAttributes.addFlashAttribute(new Message().setErrorMessage("No Trial Balance Reports exist prior to the selected Trial Balance Report for this company."));
            modelAndView.setViewName("redirect:/trialbalancereport");
            return modelAndView;
        }
        modelAndView.addObject("trialbalancereports", all);
        modelAndView.addObject("currentTbrId", trialbalancereportId);
        modelAndView.setViewName("trialbalancereport/createjournalentries");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/journalentries", method = RequestMethod.GET)
    public ModelAndView getJournalEntryLineItems(@RequestParam Long prevTbr, @RequestParam Long currentTbr) {
        ModelAndView modelAndView = new ModelAndView();
        List<LineItem> lineItems = trialBalanceReportService.createJournalEntries(prevTbr, currentTbr);
        modelAndView.addObject("lineitems", lineItems);
        modelAndView.addObject("prevTbrId", prevTbr);
        modelAndView.addObject("currentTbrId", currentTbr);
        modelAndView.setViewName("trialbalancereport/lineitem");
        return modelAndView;
    }

    @RequestMapping(value = "/trialbalancereport/downloadjournalentry", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadFile(@RequestParam Long prevTbr, @RequestParam Long currentTbr) throws IOException {

        return trialBalanceReportService.createJournalEntryFile(prevTbr, currentTbr);
    }
}
