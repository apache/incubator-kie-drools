package org.drools.verifier;

import java.util.List;
import java.util.jar.JarInputStream;

import org.drools.verifier.builder.ScopesAgendaFilter;
import org.drools.verifier.data.VerifierReport;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public interface Verifier {

    /**
     * Add resource that is verified.
     * 
     * @param descr
     */
    public void addResourcesToVerify(Resource resource,
                                     ResourceType type);
    
    public void addResourcesToVerify(Resource resource,
            ResourceType type, ResourceConfiguration config);

    /**
     * Give model info optionally as a jar. This way verifier doesn't have to figure out the field types.
     */
    public void addObjectModel(JarInputStream jar);

    public void flushKnowledgeSession();

    /**
     * 
     * This will run the verifier.
     * 
     * @return true if everything worked.
     */
    public boolean fireAnalysis();

    public boolean fireAnalysis(ScopesAgendaFilter scopesAgendaFilter);

    public VerifierReport getResult();

    public boolean hasErrors();

    public List<VerifierError> getErrors();

    public void dispose();

}
