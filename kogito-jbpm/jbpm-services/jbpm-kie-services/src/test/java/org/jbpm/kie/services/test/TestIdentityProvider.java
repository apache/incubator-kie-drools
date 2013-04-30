package org.jbpm.kie.services.test;

import java.util.Collections;
import java.util.List;

import org.jbpm.kie.services.api.IdentityProvider;

public class TestIdentityProvider implements IdentityProvider {

    public String getName() {
        return "testUser";
    }

    public List<String> getRoles() {
        return Collections.EMPTY_LIST;
    }

}
