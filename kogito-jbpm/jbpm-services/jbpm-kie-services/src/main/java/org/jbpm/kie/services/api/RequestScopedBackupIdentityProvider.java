package org.jbpm.kie.services.api;

/**
 * This class is a "backup" version of the IdentityProvider, for 
 * situations in which the IdentityProvider CDI proxy is not available. 
 * </p>
 * The "normal" implementation of the IdentityProvider expects a 
 * HTTP request context object to be available, but the code may be used
 * in situations where there is not an HTTP request or session present.
 * </p>
 * In those situations, a request scoped implementation of this interface
 * may be used in order to provide the appropriate information about the 
 * user.
 */
public interface RequestScopedBackupIdentityProvider {

    String getName();
    
}
