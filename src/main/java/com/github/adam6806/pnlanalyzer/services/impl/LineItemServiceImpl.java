package com.github.adam6806.pnlanalyzer.services.impl;

import com.github.adam6806.pnlanalyzer.domain.LineItem;
import com.github.adam6806.pnlanalyzer.repositories.LineItemRepository;
import com.github.adam6806.pnlanalyzer.services.LineItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

@Service("lineItemService")
@Transactional
public class LineItemServiceImpl implements LineItemService {

    @Autowired
    private LineItemRepository lineItemRepository;

    @Override
    public LineItem findLineItemById(long id) {
        return lineItemRepository.getOne(id);
    }

    @Override
    public LineItem updateLineItem(long id, String credit, String debit) {

        LineItem lineItem = lineItemRepository.getOne(id);
        lineItem.setCredit(formatCurrency(credit));
        lineItem.setDebit(formatCurrency(debit));
        LineItem saved = lineItemRepository.save(lineItem);
        return saved;
    }

    public BigDecimal formatCurrency(String currency) {
        currency = currency.replaceAll("\\$", "");
        currency = currency.replaceAll(",", "");
        return BigDecimal.valueOf(Double.parseDouble(currency));
    }
}
