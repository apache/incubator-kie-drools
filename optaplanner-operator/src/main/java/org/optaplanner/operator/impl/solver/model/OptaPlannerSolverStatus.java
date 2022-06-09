package org.optaplanner.operator.impl.solver.model;

public final class OptaPlannerSolverStatus {
    private String errorMessage;
    private String inputMessageAddress;
    private String outputMessageAddress;

    public OptaPlannerSolverStatus() {
        // required by Jackson
    }

    private OptaPlannerSolverStatus(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static OptaPlannerSolverStatus success() {
        return new OptaPlannerSolverStatus(null);
    }

    public static OptaPlannerSolverStatus error(Exception exception) {
        return new OptaPlannerSolverStatus(exception.getMessage());
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getInputMessageAddress() {
        return inputMessageAddress;
    }

    public void setInputMessageAddress(String inputMessageAddress) {
        this.inputMessageAddress = inputMessageAddress;
    }

    public String getOutputMessageAddress() {
        return outputMessageAddress;
    }

    public void setOutputMessageAddress(String outputMessageAddress) {
        this.outputMessageAddress = outputMessageAddress;
    }
}
