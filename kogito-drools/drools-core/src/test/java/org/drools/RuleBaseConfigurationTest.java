/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;

import java.util.Properties;

import junit.framework.TestCase;

import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.RuleBaseConfiguration.LogicalOverride;
import org.drools.RuleBaseConfiguration.SequentialAgenda;
import org.drools.common.ArrayAgendaGroupFactory;
import org.drools.common.PriorityQueueAgendaGroupFactory;
import org.drools.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;

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
        SessionConfiguration cfg = new SessionConfiguration(properties);
        assertEquals(cfg.getWorkItemHandlers().size(), 3);
        assertEquals(cfg.getWorkItemHandlers().get("MyWork").getClass(), SystemOutWorkItemHandler.class);
        assertEquals(cfg.getWorkItemHandlers().get("UIWork").getClass(), SystemOutWorkItemHandler.class);
        assertEquals(cfg.getWorkItemHandlers().get("Log").getClass(), DoNothingWorkItemHandler.class);
    }

}
