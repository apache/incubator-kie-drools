package org.drools.runtime;

import java.util.Properties;

/**
 * This is the runtime deployment component for loading rule resources.
 * 
 * This is an optional component useful for people who want live, hot deployment of rule assets.
 * (you can of course implement your own deployment infrastructure similar to this to suit your needs).
 * 
 * This supports the extraction of rules from a specified repository, and caching of rule assets until they are 
 * "refreshed" according to the rules you configure.
 * For instance, you may only want rule assets to be deployed when their status is set to "production", so 
 * that the runtime is running on approved versions of an asset, rather then the latest version only.
 *  
 * At present this only works with single URI identified resources.
 *  
 * @author Michael Neale
 */
public class DeploymentRuntime {
    
    private RepositoryClient client;
    private String resourceUri;
    private ExpiringResourceCopy localCopy;
    private long latestVersionNumber;
    
    /** 
     * A properties configuration, like drools-deployment.properties example
     * One instance of this, maps to one managed rule resource.
     * Updates should only happen when requested, when timeout has expired the repository does in fact
     * have a new version.
     */
    public DeploymentRuntime(Properties properties) {
        DeployerConfig config = new DeployerConfig(properties);
        client = config.getClient();
        this.resourceUri = config.getResourceURI();
        this.localCopy = new ExpiringResourceCopy(config.getRefreshIntervalSeconds());
        localCopy.setData( load() );
    }



    private byte[] load() {
        ResourceVersion version = client.getResource( resourceUri );
        this.latestVersionNumber = version.versionNumber;
        return version.data;
    }


    /**
     * Return the resource, refreshing it if necessary.
     * TODO: make it autorefresh? (really need to pass in a delegate to update the rulebase here...).
     */
    public byte[] getResource() {
        if (localCopy.isExpired()) {
            synchronized ( this ) {
                if (client.hasResourceChanged(latestVersionNumber, resourceUri)) {
                    localCopy.setData( load() );
                }
            }
        }
        return localCopy.getData();
    }

}
