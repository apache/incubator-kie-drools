package org.optaplanner.examples.taskassigning.domain;

public enum Priority {
    MINOR,
    MAJOR,
    CRITICAL;

    public String getLabel() {
        switch (this) {
            case MINOR:
                return "Minor priority";
            case MAJOR:
                return "Major priority";
            case CRITICAL:
                return "Critical priority";
            default:
                throw new IllegalStateException("The priority (" + this + ") is not implemented.");
        }
    }

}
