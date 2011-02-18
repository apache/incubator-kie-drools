package org.acme.insurance;

/**
 * This is a simple fact class to mark something as approved.
 *
 */
public class Approve {

    private String reason;

    public Approve(final String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

}
