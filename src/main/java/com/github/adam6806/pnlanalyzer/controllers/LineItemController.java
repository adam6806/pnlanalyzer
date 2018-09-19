package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.domain.LineItem;
import com.github.adam6806.pnlanalyzer.forms.LineItemForm;
import com.github.adam6806.pnlanalyzer.services.LineItemService;
import com.github.adam6806.pnlanalyzer.utility.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class LineItemController {

    private final LineItemService lineItemService;

    @Autowired
    public LineItemController(LineItemService lineItemService) {
        this.lineItemService = lineItemService;
    }

    @RequestMapping(value = "/lineitem/editlineitem", method = RequestMethod.GET)
    public ModelAndView editCompany(@RequestParam Long lineItemId) {
        LineItem lineItem = lineItemService.findLineItemById(lineItemId);
        LineItemForm lineItemForm = new LineItemForm();
        lineItemForm.setCurrencyCredit(lineItem.getCreditString());
        lineItemForm.setCurrencyDebit(lineItem.getDebitString());
        lineItemForm.setLineItemId(lineItemId);
        lineItemForm.setTrialBalanceReportId(lineItem.getTrialBalanceReport().getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("lineItemForm", lineItemForm);
        modelAndView.setViewName("lineitem/editlineitem");
        return modelAndView;
    }

    @RequestMapping(value = "/lineitem/editlineitem", method = RequestMethod.POST)
    public ModelAndView editCompany(@Valid LineItemForm lineItemForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("lineItemForm", lineItemForm);
            modelAndView.setViewName("lineitem/editlineitem");
        } else {
            LineItem lineItem = lineItemService.updateLineItem(lineItemForm.getLineItemId(), lineItemForm.getCurrencyCredit(), lineItemForm.getCurrencyDebit());
            Message message = new Message();
            message.setSuccessMessage("Line Item was edited successfully.");
            redirectAttributes.addFlashAttribute(message);
            modelAndView.setViewName("redirect:/trialbalancereport/lineitem?trialbalancereportId=" + lineItem.getTrialBalanceReport().getId());
        }
        return modelAndView;
    }
}
