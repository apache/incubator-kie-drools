package org.drools.core.reteoo.sequencing;

public enum SignalStatus {
    MATCHED(0),
    UNMATCHED(1),
    FAILED(2);

    private final int status;

    private SignalStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SignalStatus{ status=" + super.toString() + "}";
    }
}
