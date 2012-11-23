package org.droolsjbpm.services.api;

import java.util.List;

public interface IdentityProvider {

    String getName();
    
    List<String> getRoles();
}
