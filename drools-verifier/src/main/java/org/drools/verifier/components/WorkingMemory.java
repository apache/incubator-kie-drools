package org.drools.verifier.components;

/**
 * 
 * @author Toni Rikkola
 */
public class WorkingMemory extends VerifierComponentSource {

    @Override
    public String getPath() {
        return String.format( "source[@type=%s]",
                              getVerifierComponentType().getType() );
    }

    public VerifierComponentType getVerifierComponentType() {
        return VerifierComponentType.WORKING_MEMORY;
    }

}
