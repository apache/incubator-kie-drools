package org.drools.verifier.components;

import org.drools.drl.ast.descr.BaseDescr;

public class EntryPoint extends VerifierComponentSource {

    public EntryPoint(BaseDescr descr) {
        super(descr);
    }

    private String entryPointName;

    @Override
    public String getPath() {
        return String.format( "source[@type=%s @entryPointName=%s]",
                              getVerifierComponentType().getType(),
                              getEntryPointName() );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.ENTRY_POINT_DESCR;
    }

    public void setEntryPointName(String entryPointName) {
        this.entryPointName = entryPointName;
    }

    public String getEntryPointName() {
        return entryPointName;
    }
}
