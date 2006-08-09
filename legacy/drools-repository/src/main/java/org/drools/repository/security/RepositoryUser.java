package org.drools.repository.security;

import java.security.Principal;

/** Simple wrapper for user identity for the repository to enforce ACLs */
public class RepositoryUser
    implements
    Principal {

    private String userId;
    
    public RepositoryUser(String userId) {
        this.userId = userId;    
    }
    
    RepositoryUser() {}
    
    public String getName() {
        return userId;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    void setUserId(String id) {
        this.userId = id;
    }

}
