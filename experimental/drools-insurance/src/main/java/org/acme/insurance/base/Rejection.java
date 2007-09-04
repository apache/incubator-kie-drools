package org.acme.insurance.base;

public class Rejection {

    private String reason;

    public Rejection(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
