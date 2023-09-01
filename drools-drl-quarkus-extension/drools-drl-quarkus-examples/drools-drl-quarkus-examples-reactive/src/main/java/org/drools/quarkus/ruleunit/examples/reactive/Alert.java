package org.drools.quarkus.ruleunit.examples.reactive;

public class Alert {

    private String severity;
    private String message;

    public Alert() {
    }

    public Alert(String severity, String message) {
        super();
        this.severity = severity;
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Alert [severity=" + severity + ", message=" + message + "]";
    }

}
