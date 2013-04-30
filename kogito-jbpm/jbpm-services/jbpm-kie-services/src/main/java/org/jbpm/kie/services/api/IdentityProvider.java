package org.jbpm.kie.services.api;

import java.util.List;

public interface IdentityProvider {

    String getName();
    
    List<String> getRoles();
}
