package org.drools.verifier;

import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;

public class EmptyVerifierConfiguration extends VerifierConfigurationImpl {

    public EmptyVerifierConfiguration() {
        //Ensure mandatory bootstrap items have been setup
        verifyingResources.put( ResourceFactory.newClassPathResource("bootstrap-essentials.drl",
                getClass()),
                                ResourceType.DRL );
    }

}
