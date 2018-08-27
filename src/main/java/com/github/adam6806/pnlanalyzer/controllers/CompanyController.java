package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.entities.Company;
import com.github.adam6806.pnlanalyzer.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;

@Controller
public class CompanyController {

    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @RequestMapping(value = "/company", method = RequestMethod.GET)
    public ModelAndView getCompanies() {
        List<Company> companies = companyRepository.findAll();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("company");
        modelAndView.addObject("companies", companies);
        return modelAndView;
    }

    @PostMapping(value = "/company")
    public ModelAndView deleteCompany(@RequestParam Long companyId) {
        companyRepository.deleteById(companyId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/company");
        return modelAndView;
    }

    @RequestMapping(value = "/company/addcompany", method = RequestMethod.GET)
    public ModelAndView addCompany() {
        ModelAndView modelAndView = new ModelAndView();
        Company company = new Company();
        modelAndView.addObject("company", company);
        modelAndView.setViewName("company/addcompany");
        return modelAndView;
    }

    @PostMapping(value = "/company/addcompany")
    public ModelAndView addCompany(@Valid Company company, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("company", company);
            modelAndView.setViewName("/company/addcompany");
        } else {
            companyRepository.save(company);
            modelAndView.setViewName("redirect:/company");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/company/editcompany", method = RequestMethod.GET)
    public ModelAndView editCompany(@RequestParam Long companyId) {
        Company company = companyRepository.findById(companyId).get();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("company", company);
        modelAndView.setViewName("company/editcompany");
        return modelAndView;
    }

    @PostMapping(value = "/company/editcompany")
    public ModelAndView editCompany(@Valid Company company, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("company", company);
            modelAndView.setViewName("/company/editcompany");
        } else {
            companyRepository.save(company);
            modelAndView.setViewName("redirect:/company");
        }
        return modelAndView;
    }
}
