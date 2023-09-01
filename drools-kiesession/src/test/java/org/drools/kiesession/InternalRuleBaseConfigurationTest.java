/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.kiesession;

import java.util.Properties;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.RuleBaseConfiguration.AssertBehaviour;
import org.drools.core.RuleBaseConfiguration.SequentialAgenda;
import org.drools.core.common.PriorityQueueAgendaGroupFactory;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.junit.Test;
import org.kie.internal.conf.CompositeConfiguration;
import org.kie.internal.utils.ChainedProperties;

import static org.assertj.core.api.Assertions.assertThat;

public class InternalRuleBaseConfigurationTest {
    public RuleBaseConfiguration getRuleBaseConfiguration(Properties props) {
        RuleBaseConfiguration brconf = new RuleBaseConfiguration(new CompositeConfiguration<>(ChainedProperties.getChainedProperties(null).addProperties(props), null));

        return brconf;
    }

    @Test
    public void testSystemProperties() {
        RuleBaseConfiguration cfg = getRuleBaseConfiguration(null);
        assertThat(cfg.getAssertBehaviour()).isEqualTo(AssertBehaviour.IDENTITY);

        System.setProperty( "drools.equalityBehavior",
                            "EQUALITY" );
        cfg = getRuleBaseConfiguration(null);
        assertThat(cfg.getAssertBehaviour()).isEqualTo(AssertBehaviour.EQUALITY);
        
        System.getProperties().remove( "drools.equalityBehavior" );
    }

    @Test
    public void testProgrammaticPropertiesFile() {
        RuleBaseConfiguration cfg = getRuleBaseConfiguration(null);
        assertThat(cfg.isIndexLeftBetaMemory()).isEqualTo(true);

        Properties properties = new Properties();
        properties.setProperty( "drools.indexLeftBetaMemory",
                                "false" );
        cfg = getRuleBaseConfiguration(properties);

        assertThat(cfg.isIndexLeftBetaMemory()).isEqualTo(false);
        
        System.getProperties().remove( "drools.indexLeftBetaMemory" );
    }
    
    @Test
    public void testAssertBehaviour() {
        Properties properties = new Properties();
        properties.setProperty( "drools.equalityBehavior", "identity" );
        RuleBaseConfiguration cfg = getRuleBaseConfiguration(properties);

        assertThat(cfg.getAssertBehaviour()).isEqualTo(AssertBehaviour.IDENTITY);
        
        properties = new Properties();
        properties.setProperty( "drools.equalityBehavior", "equality" );
        cfg = getRuleBaseConfiguration(properties);

        assertThat(cfg.getAssertBehaviour()).isEqualTo(AssertBehaviour.EQUALITY);
    }
    
    
    @Test
    public void testSequential() {
        Properties properties = new Properties();
        properties.setProperty( "drools.sequential", "false" );
        RuleBaseConfiguration cfg = getRuleBaseConfiguration(properties);

        assertThat(cfg.isSequential()).isFalse();
        assertThat(RuntimeComponentFactory.get().getAgendaGroupFactory() instanceof PriorityQueueAgendaGroupFactory).isTrue();
        
        properties = new Properties();
        properties.setProperty( "drools.sequential.agenda", "sequential" );
        properties.setProperty( "drools.sequential", "true" );
        cfg = getRuleBaseConfiguration(properties);

        assertThat(cfg.isSequential()).isTrue();
        assertThat(cfg.getSequentialAgenda()).isEqualTo(SequentialAgenda.SEQUENTIAL);
        
        properties = new Properties();
        properties.setProperty( "drools.sequential.agenda", "dynamic" );
        properties.setProperty( "drools.sequential", "true" );
        cfg = getRuleBaseConfiguration(properties);

        assertThat(cfg.isSequential()).isTrue();
        assertThat(cfg.getSequentialAgenda()).isEqualTo(SequentialAgenda.DYNAMIC);
        assertThat(RuntimeComponentFactory.get().getAgendaGroupFactory() instanceof PriorityQueueAgendaGroupFactory).isTrue();
    }
}
