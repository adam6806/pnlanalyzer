package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.domain.LineItem;

public interface LineItemService {

    LineItem findLineItemById(long id);

    LineItem updateLineItem(long id, String credit, String debit);
}
