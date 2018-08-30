package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.entities.Company;
import com.github.adam6806.pnlanalyzer.repositories.CompanyRepository;
import com.github.adam6806.pnlanalyzer.utility.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public ModelAndView getCompanies(@ModelAttribute Message message) {
        List<Company> companies = companyRepository.findAll();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("company");
        modelAndView.addObject("companies", companies);
        modelAndView.addObject("message", message);
        return modelAndView;
    }

    @RequestMapping(value = "/company", method = RequestMethod.POST)
    public ModelAndView deleteCompany(@RequestParam Long companyId, RedirectAttributes redirectAttributes) {
        companyRepository.deleteById(companyId);

        ModelAndView modelAndView = new ModelAndView();
        Message message = new Message();
        message.setSuccessMessage("Company was deleted successfully.");
        redirectAttributes.addFlashAttribute(message);
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

    @RequestMapping(value = "/company/addcompany", method = RequestMethod.POST)
    public ModelAndView addCompany(@Valid Company company, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("company", company);
            modelAndView.setViewName("company/addcompany");
        } else {
            companyRepository.save(company);
            redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("Company was added successfully."));
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

    @RequestMapping(value = "/company/editcompany", method = RequestMethod.POST)
    public ModelAndView editCompany(@Valid Company company, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("company", company);
            modelAndView.setViewName("company/editcompany");
        } else {
            companyRepository.save(company);
            Message message = new Message();
            message.setSuccessMessage("Company was edited successfully.");
            redirectAttributes.addFlashAttribute(message);
            modelAndView.setViewName("redirect:/company");
        }
        return modelAndView;
    }
}
