package com.github.adam6806.pnlanalyzer.company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class CompanyController {

    @Autowired
    CompanyRepository companyRepository;

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
        modelAndView.setViewName("company/addcompany");
        return modelAndView;
    }

    @PostMapping(value = "/company/addcompany")
    public ModelAndView addCompany(@RequestParam String companyName) {
        Company newCompany = new Company();
        newCompany.setName(companyName);
        companyRepository.save(newCompany);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/company");
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
    public ModelAndView editCompany(@RequestParam String companyName, @RequestParam Long companyId) {
        Company company = companyRepository.findById(companyId).get();
        company.setName(companyName);
        companyRepository.save(company);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/company");
        return modelAndView;
    }
}
