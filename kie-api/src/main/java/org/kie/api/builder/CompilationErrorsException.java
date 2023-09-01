package org.kie.api.builder;

import java.util.List;

public class CompilationErrorsException extends RuntimeException {

    private final List<Message> errorMessages;

    public CompilationErrorsException(List<Message> errorMessages) {
        super("Unable to create KieModule, Errors Existed: " + errorMessages);
        this.errorMessages = errorMessages;
    }

    public List<Message> getErrorMessages() {
        return errorMessages;
    }
}
