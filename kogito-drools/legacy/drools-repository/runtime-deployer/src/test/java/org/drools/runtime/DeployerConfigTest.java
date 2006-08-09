package org.drools.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

public class DeployerConfigTest extends TestCase {

    public void testBasicConfig() throws Exception {
        InputStream stream = this.getClass().getResourceAsStream( "drools-deployment.properties" );
        DeployerConfig conf = new TestDeployerConfig(stream);
        assertEquals("svn", conf.getType());
        assertTrue(conf.getClient() instanceof MockSVNClient);
        assertEquals("drools/rules/michael.drl", conf.getResourceURI());
        assertEquals(10, conf.getRefreshIntervalSeconds());
    }
    
    static class TestDeployerConfig extends DeployerConfig {

        public TestDeployerConfig(InputStream config) throws IOException {
            super( config );
            
        }

        RepositoryClient getSVNClient(String hostUrl,
                                      String user,
                                      String password,
                                      String status) {
            
            return new MockSVNClient(hostUrl, user, password, status); 
        }


        
    }
    
    static class MockSVNClient implements RepositoryClient {

        public MockSVNClient(String repoUrl,
                             String user,
                             String password,
                             String status) {

        }

        public ResourceVersion getResource(String resourceURI) {
            return null;
        }

        public boolean hasResourceChanged(long latestVersionNumber,
                                          String resourceUri) {
            // TODO Auto-generated method stub
            return false;
        }
        
    }
    
}
