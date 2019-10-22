package org.kie.kogito.jobs.api;


public class InvalidJobException extends RuntimeException {

    private static final long serialVersionUID = -5827356422593810436L;

    public InvalidJobException(String message) {
        super(message);

    }
}
