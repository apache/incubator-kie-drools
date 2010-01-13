package org.drools.verifier;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.drools.builder.ResourceType;
import org.drools.io.Resource;

public class VerifierConfigurationImpl
    implements
    VerifierConfiguration {

    protected Map<Resource, ResourceType> verifyingResources              = new HashMap<Resource, ResourceType>();
    private Map<String, String>           properties                      = new HashMap<String, String>();
    protected final List<String>          verifyingScopes                 = new ArrayList<String>();

    private boolean                       acceptRuleWithoutVerifyingScope = true;

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

    public List<String> getVerifyingScopes() {
        return verifyingScopes;
    }

    public void addVerifyingScopes(String scope) {
        this.verifyingScopes.add( scope );
    }

    public boolean acceptRulesWithoutVerifiyingScope() {
        return this.acceptRuleWithoutVerifyingScope;
    }

    public void setAcceptRulesWithoutVerifiyingScope(boolean accept) {
        acceptRuleWithoutVerifyingScope = accept;
    }

}
