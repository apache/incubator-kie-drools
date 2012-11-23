package org.droolsjbpm.services.test;

import java.util.Collections;
import java.util.List;

import org.droolsjbpm.services.api.IdentityProvider;

public class TestIdentityProvider implements IdentityProvider {

    @Override
    public String getName() {
        return "testUser";
    }

    @Override
    public List<String> getRoles() {
        
        return Collections.EMPTY_LIST;
    }

}
