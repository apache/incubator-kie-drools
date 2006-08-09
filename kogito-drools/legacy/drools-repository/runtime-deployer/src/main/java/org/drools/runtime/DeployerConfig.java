package org.drools.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This holds the configuration of the Deployer.
 * 
 * @author Michael Neale
 */
class DeployerConfig {

    private Properties properties;

    /**
     * @param config a properties file with the relevant info.
     * @throws IOException If it is unable to load the config (it just aint going to work).
     */
    public DeployerConfig(InputStream config) throws IOException {
        this.properties = new Properties();
        this.properties.load( config );
    }

    public DeployerConfig(Properties props) {
        this.properties = props;
    }
    
    public String getType() {
        return properties.getProperty( "deployer.type", "svn");
    }
    
    /** Based on the configuration, get the repo client */
    public RepositoryClient getClient() {
        if (getType().equals( "svn" )) {
            String hostUrl = this.properties.getProperty( "svn.url" );
            String user = this.properties.getProperty( "svn.user", "" );
            String password = this.properties.getProperty( "svn.password", "" );
            String status = this.properties.getProperty( "resource.status" );
            return getSVNClient( hostUrl,
                          user,
                          password,
                          status );
            
        } else {
            throw new UnsupportedOperationException("Sorry, can't do that type of repo just yet.");
        }
    }

    RepositoryClient getSVNClient(String hostUrl,
                                   String user,
                                   String password,
                                   String status) {
        return new SVNClient(hostUrl, user, password, status);
    }
    
    /** The resource URI we wil be loading */
    public String getResourceURI() {
        return this.properties.getProperty( "resource.uri" );
    }

    public int getRefreshIntervalSeconds() {
        return Integer.parseInt( this.properties.getProperty( "refresh.interval", "100000" ));
    }
    
    
    
}
