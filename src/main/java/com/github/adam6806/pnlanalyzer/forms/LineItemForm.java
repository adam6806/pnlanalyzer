package com.github.adam6806.pnlanalyzer.forms;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class LineItemForm {

    private static final String REGEX_MESSAGE = "*Currency must take one of the following forms: 1234 1,234 1234.12 1,234.12 $1234 $1,234 $1234.12 $1,234.12";

    @NotEmpty(message = "*Please provide the currencyCredit value.")
    @Pattern(regexp = "^\\$?([0-9]{1,3},([0-9]{3},)*[0-9]{3}|[0-9]+)(\\.[0-9][0-9])?$", message = REGEX_MESSAGE)
    private String currencyCredit;

    @NotEmpty(message = "*Please provide the currencyDebit value.")
    @Pattern(regexp = "^\\$?([0-9]{1,3},([0-9]{3},)*[0-9]{3}|[0-9]+)(\\.[0-9][0-9])?$", message = REGEX_MESSAGE)
    private String currencyDebit;

    private long lineItemId;
    private long trialBalanceReportId;
}
