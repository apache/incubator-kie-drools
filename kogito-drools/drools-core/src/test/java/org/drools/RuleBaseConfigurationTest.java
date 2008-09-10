package org.drools;

import java.util.Properties;

import junit.framework.TestCase;

import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.RuleBaseConfiguration.LogicalOverride;
import org.drools.RuleBaseConfiguration.SequentialAgenda;
import org.drools.common.ArrayAgendaGroupFactory;
import org.drools.common.PriorityQueueAgendaGroupFactory;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.process.instance.impl.demo.UIWorkItemHandler;

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
    
    public void testWorkItemHandlers() {
    	Properties properties = new Properties();
        properties.setProperty( "drools.workItemHandlers", "WorkItemHandlers1.conf WorkItemHandlers2.conf" );
        RuleBaseConfiguration cfg = new RuleBaseConfiguration(properties);
        assertEquals(cfg.getWorkItemHandlers().size(), 3);
        assertEquals(cfg.getWorkItemHandlers().get("MyWork").getClass(), SystemOutWorkItemHandler.class);
        assertEquals(cfg.getWorkItemHandlers().get("UIWork").getClass(), UIWorkItemHandler.class);
    }

}
