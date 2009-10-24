package org.drools.verifier;

import java.util.Map;

import org.drools.PropertiesConfiguration;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;

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
