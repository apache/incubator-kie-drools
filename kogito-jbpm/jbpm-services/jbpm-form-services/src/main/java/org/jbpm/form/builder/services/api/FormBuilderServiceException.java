package org.jbpm.form.builder.services.api;

public class FormBuilderServiceException extends Exception {

    private static final long serialVersionUID = 3248011011993977193L;

    public FormBuilderServiceException() {
        super();
    }

    public FormBuilderServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormBuilderServiceException(String message) {
        super(message);
    }

    public FormBuilderServiceException(Throwable cause) {
        super(cause);
    }
}
