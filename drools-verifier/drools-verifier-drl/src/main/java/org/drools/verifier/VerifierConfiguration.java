package org.drools.verifier;

import java.util.Map;

import org.kie.api.PropertiesConfiguration;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

public interface VerifierConfiguration
    extends
    PropertiesConfiguration {

    /**
     * Add external analyzing rules to verifier.
     * 
     * @param resource
     *            the Resource to add
     * @param type
     *            the resource type
     */
    Map<Resource, ResourceType> getVerifyingResources();

}
