package org.drools;

import java.util.Properties;

import org.drools.RuleBaseConfiguration.AssertBehaviour;

import junit.framework.TestCase;

public class RuleBaseConfigurationTest extends TestCase {

    public void testSystemProperties() {
        RuleBaseConfiguration cfg = new RuleBaseConfiguration();
        assertEquals( AssertBehaviour.IDENTITY,
                      cfg.getAssertBehaviour() );

        System.setProperty( "drools.assertBehaviour",
                            "EQUALITY" );
        cfg = new RuleBaseConfiguration();
        assertEquals( AssertBehaviour.EQUALITY,
                      cfg.getAssertBehaviour() );
        
        System.getProperties().remove( "drools.assertBehaviour" );        
    }

    public void testProgrammaticPropertiesFile() {
        RuleBaseConfiguration cfg = new RuleBaseConfiguration();
        assertEquals( true,
                      cfg.isIndexLeftBetaMemory() );

        Properties properties = new Properties();
        properties.setProperty( "drools.indexLeftBetaMemory",
                                "false" );
        cfg = new RuleBaseConfiguration( properties );

        assertEquals( false,
                      cfg.isIndexLeftBetaMemory() );
        
        System.getProperties().remove( "drools.indexLeftBetaMemory" );        
    }
    
    public void testShadowProxyExcludes() {
        RuleBaseConfiguration cfg = new RuleBaseConfiguration();
        
        Properties properties = new Properties();
        properties.setProperty( "drools.shadowProxyExcludes", "java.util.List java.util.Map java.lang.reflect.*" );
        
        cfg = new RuleBaseConfiguration( properties );
        
        assertFalse( cfg.isShadowed( "java.util.List" ) );
        assertFalse( cfg.isShadowed( "java.util.Map" ) );
        assertTrue( cfg.isShadowed( "java.util.HashMap" ) );
        
        assertFalse( cfg.isShadowed( "java.lang.reflect.Method" ) );
        
        assertTrue( cfg.isShadowed( "java.lang.String" ) );
    }

}
