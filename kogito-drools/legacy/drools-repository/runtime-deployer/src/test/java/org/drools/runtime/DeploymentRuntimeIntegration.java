package org.drools.runtime;

import java.util.Properties;

import junit.framework.TestCase;

/** required that SVN be running somewhere. */
public class DeploymentRuntimeIntegration extends TestCase {
    
    public void testIt() throws Exception {
        
        Properties props = new Properties();
        props.load( this.getClass().getResourceAsStream( "drools-deployment.properties" ) );
        
        DeploymentRuntime deployer = new DeploymentRuntime(props);
        byte[] drl = deployer.getResource();
        assertNotNull(drl);
        
        byte[] drl2 = deployer.getResource();
        
        assertEquals(drl, drl2);
    }
    
}
