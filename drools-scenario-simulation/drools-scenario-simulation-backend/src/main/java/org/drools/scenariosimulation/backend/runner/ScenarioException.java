package org.drools.scenariosimulation.backend.runner;

public class ScenarioException extends IllegalArgumentException {

    private final boolean failedAssertion;

    public ScenarioException(String message) {
        super(message);
        this.failedAssertion = false;
    }

    public ScenarioException(String message, boolean failedAssertion) {
        super(message);
        this.failedAssertion = failedAssertion;
    }

    public ScenarioException(String message, Throwable cause) {
        super(message, cause);
        this.failedAssertion = false;
    }

    public ScenarioException(Throwable cause) {
        super(cause.getMessage(), cause);
        failedAssertion = false;
    }

    public boolean isFailedAssertion() {
        return failedAssertion;
    }
}
