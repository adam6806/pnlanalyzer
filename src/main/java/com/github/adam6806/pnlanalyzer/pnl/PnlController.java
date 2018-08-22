package com.github.adam6806.pnlanalyzer.pnl;

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
public class PnlController {

    private final PnlRepository pnlRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public PnlController(PnlRepository pnlRepository, CompanyRepository companyRepository) {
        this.pnlRepository = pnlRepository;
        this.companyRepository = companyRepository;
    }

    @RequestMapping(value = "/pnl", method = RequestMethod.GET)
    public ModelAndView getPnls() {
        ModelAndView modelAndView = new ModelAndView();
        List<Pnl> pnls = pnlRepository.findAll();
        modelAndView.addObject("pnls", pnls);
        modelAndView.setViewName("pnl");
        return modelAndView;
    }

    @RequestMapping(value = "/pnl", method = RequestMethod.POST)
    public ModelAndView deletePnl(@RequestParam Long pnlId) {
        pnlRepository.deleteById(pnlId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/pnl");
        return modelAndView;
    }

    @RequestMapping(value = "/pnl/lineitem", method = RequestMethod.GET)
    public ModelAndView getLineItemsForPNL(@RequestParam Long pnlId) {
        ModelAndView modelAndView = new ModelAndView();
        Pnl pnl = pnlRepository.getOne(pnlId);
        modelAndView.addObject("lineitems", pnl.getLineItems());
        modelAndView.setViewName("pnl/lineitem");
        return modelAndView;
    }

    @RequestMapping(value = "/pnl/addpnl", method = RequestMethod.GET)
    public ModelAndView getAddPnlForm() {
        ModelAndView modelAndView = new ModelAndView();
        List<Company> companies = companyRepository.findAll();
        modelAndView.addObject("companies", companies);
        modelAndView.setViewName("pnl/addpnl");
        return modelAndView;
    }

    @RequestMapping(value = "/pnl/addpnl", method = RequestMethod.POST)
    public ModelAndView addPnl(@RequestParam("pnlfile") MultipartFile pnlfile, @RequestParam int month, @RequestParam int year, @RequestParam Long companyId) {

        ModelAndView modelAndView = new ModelAndView();

        try {
            String name = pnlfile.getOriginalFilename();
            InputStream stream = pnlfile.getInputStream();
            List<LineItem> lineItems = ExcelParser.parseExcelFile(stream);
            String dateString = lineItems.get(0).getDescription();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yy", Locale.ENGLISH);
            LocalDate dateTime = LocalDate.parse(dateString, formatter);
            lineItems.remove(0);
            Pnl pnl = new Pnl();
            pnl.setDate(Date.valueOf(dateTime));
            pnl.setLineItems(new HashSet<>(lineItems));
            pnl.setName(name);
            pnl.setCompany(companyRepository.findById(companyId).get());
            pnlRepository.save(pnl);
            modelAndView.setViewName("pnl/lineitem");
            modelAndView.addObject("lineitems", lineItems);
            return modelAndView;
        } catch (IOException | InvalidFormatException ex) {
            modelAndView.setViewName("error");
            return modelAndView;
        }
    }
}
