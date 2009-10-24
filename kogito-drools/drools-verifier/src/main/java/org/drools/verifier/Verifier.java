package org.drools.verifier;

import java.util.List;

import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.verifier.data.VerifierReport;

public interface Verifier {

    /**
     * Add resource that is verified.
     * 
     * @param descr
     */
    public void addResourcesToVerify(Resource resource,
                                     ResourceType type);

    /**
     * TODO: Something like this, takes a look at the objects and finds out
     */
    // public void addObjectModel();

    /**
     * 
     * This will run the verifier.
     * 
     * @return true if everything worked.
     */
    public boolean fireAnalysis();

    public VerifierReport getResult();

    public boolean hasErrors();

    public List<VerifierError> getErrors();

    public void dispose();

}