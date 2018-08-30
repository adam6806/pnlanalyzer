package com.github.adam6806.pnlanalyzer.utility;

public class Message {

    private String successMessage;
    private String errorMessage;

    public String getSuccessMessage() {
        return successMessage;
    }

    public Message setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Message setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }
}
