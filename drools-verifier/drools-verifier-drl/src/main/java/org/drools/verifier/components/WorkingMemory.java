package org.drools.verifier.components;

import org.drools.drl.ast.descr.BaseDescr;

public class WorkingMemory extends VerifierComponentSource {

  
    public WorkingMemory() {
        super(new BaseDescr());
    }
  
    @Override
    public String getPath() {
        return String.format( "source[@type=%s]",
                              getVerifierComponentType().getType() );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.WORKING_MEMORY;
    }

}
