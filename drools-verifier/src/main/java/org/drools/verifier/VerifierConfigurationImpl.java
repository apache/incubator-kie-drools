package org.drools.verifier;

import java.util.HashMap;
import java.util.Map;

import org.drools.builder.ResourceType;
import org.drools.io.Resource;

public class VerifierConfigurationImpl
    implements
    VerifierConfiguration {

    protected Map<Resource, ResourceType> verifyingResources = new HashMap<Resource, ResourceType>();
    private Map<String, String>           properties         = new HashMap<String, String>();

    public String getProperty(String name) {
        return properties.get( name );
    }

    public void setProperty(String name,
                            String value) {
        properties.put( name,
                        value );
    }

    public Map<Resource, ResourceType> getVerifyingResources() {
        return verifyingResources;
    }

}
