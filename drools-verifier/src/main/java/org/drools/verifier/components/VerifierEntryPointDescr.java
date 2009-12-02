package org.drools.verifier.components;


public class VerifierEntryPointDescr extends Source {

    private String entryId;

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.ENTRY_POINT_DESCR;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    public String getEntryId() {
        return entryId;
    }
}
