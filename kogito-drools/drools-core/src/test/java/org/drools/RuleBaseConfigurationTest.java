package org.drools;

import java.util.Properties;

import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.RuleBaseConfiguration.LogicalOverride;
import org.drools.RuleBaseConfiguration.SequentialAgenda;
import org.drools.common.ArrayAgendaGroupFactory;
import org.drools.common.PriorityQueueAgendaGroupFactory;

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
    
    public void testShadowProxy() {
        // check default for rete
        RuleBaseConfiguration cfg = new RuleBaseConfiguration();        
        assertTrue( cfg.isShadowProxy() );

        // check default for sequentail
        Properties properties = new Properties();
        properties.setProperty( "drools.sequential", "true" );
        cfg = new RuleBaseConfiguration(properties);        
        assertFalse(  cfg.isShadowProxy() );
        
        properties = new Properties();
        properties.setProperty( "drools.shadowproxy", "false" );
        cfg = new RuleBaseConfiguration(properties);        
        assertFalse(  cfg.isShadowProxy() );
        
        
        properties = new Properties();
        properties.setProperty( "drools.sequential", "true" );
        properties.setProperty( "drools.shadowproxy", "false" );
        cfg = new RuleBaseConfiguration(properties);        
        assertFalse(  cfg.isShadowProxy() );         
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
    
    public void testAssertBehaviour() {
        Properties properties = new Properties();
        properties.setProperty( "drools.assertBehaviour", "identity" );
        RuleBaseConfiguration cfg = new RuleBaseConfiguration(properties);
        
        assertEquals( AssertBehaviour.IDENTITY, cfg.getAssertBehaviour() );
        
        properties = new Properties();
        properties.setProperty( "drools.assertBehaviour", "equality" );
        cfg = new RuleBaseConfiguration(properties);
        
        assertEquals( AssertBehaviour.EQUALITY, cfg.getAssertBehaviour() );        
    }
    
    public void testLogicalOverride() {
        Properties properties = new Properties();
        properties.setProperty( "drools.logicalOverride", "preserve" );
        RuleBaseConfiguration cfg = new RuleBaseConfiguration(properties);
        
        assertEquals( LogicalOverride.PRESERVE, cfg.getLogicalOverride() );
        
        properties = new Properties();
        properties.setProperty( "drools.logicalOverride", "discard" );
        cfg = new RuleBaseConfiguration(properties);
        
        assertEquals( LogicalOverride.DISCARD, cfg.getLogicalOverride() );        
    }    
    
    public void testSequential() {
        Properties properties = new Properties();
        properties.setProperty( "drools.sequential", "false" );
        RuleBaseConfiguration cfg = new RuleBaseConfiguration(properties);
        
        assertFalse( cfg.isSequential() );
        assertTrue( cfg.getAgendaGroupFactory() instanceof PriorityQueueAgendaGroupFactory );
        
        properties = new Properties();
        properties.setProperty( "drools.sequential.agenda", "sequential" );
        properties.setProperty( "drools.sequential", "true" );
        cfg = new RuleBaseConfiguration(properties);
        
        assertTrue( cfg.isSequential() );
        assertEquals( SequentialAgenda.SEQUENTIAL, cfg.getSequentialAgenda() );
        assertTrue( cfg.getAgendaGroupFactory() instanceof ArrayAgendaGroupFactory );
        
        properties = new Properties();
        properties.setProperty( "drools.sequential.agenda", "dynamic" );
        properties.setProperty( "drools.sequential", "true" );
        cfg = new RuleBaseConfiguration(properties);
        
        assertTrue( cfg.isSequential() );
        assertEquals( SequentialAgenda.DYNAMIC, cfg.getSequentialAgenda() );
        assertTrue( cfg.getAgendaGroupFactory() instanceof PriorityQueueAgendaGroupFactory );
    }    

}
