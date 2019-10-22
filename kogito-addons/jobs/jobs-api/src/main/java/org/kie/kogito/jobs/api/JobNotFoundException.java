package org.kie.kogito.jobs.api;


public class JobNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -5827356422593810436L;

    public JobNotFoundException(String message) {
        super(message);

    }
}
