package org.drools.verifier;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

public class VerifierConfigurationImpl implements VerifierConfiguration {

    protected Map<Resource, ResourceType> verifyingResources = new HashMap<>();
    private Map<String, String>           properties         = new HashMap<>();

    public String getProperty(String name) {
        return properties.get( name );
    }

    public boolean setProperty(String name, String value) {
        properties.put( name, value );
        return true;
    }

    public Map<Resource, ResourceType> getVerifyingResources() {
        return verifyingResources;
    }
}
