package org.drools.runtime;

/**
 * Implementors of this interface are responsible for getting resources out of the repository.
 * Configuration is repo specific.
 * 
 * @author Michael Neale
 *
 */
public interface RepositoryClient {

    /**
     * @param resourceURI Enough information to get the resource.
     */
    public ResourceVersion getResource(String resourceURI);

    /**
     * Return true if the resource has changed, and needs to be reloaded.
     */
    public boolean hasResourceChanged(long latestVersionNumber,
                                      String resourceUri);
    
}
