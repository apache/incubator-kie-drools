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
package org.drools.mvel.compiler.conf;

import org.drools.core.runtime.rule.impl.DefaultConsequenceExceptionHandler;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.conf.BetaRangeIndexOption;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.PrototypesOption;
import org.kie.api.conf.RemoveIdentitiesOption;
import org.kie.api.conf.SequentialOption;
import org.kie.api.runtime.rule.ConsequenceExceptionHandler;
import org.kie.internal.conf.AlphaRangeIndexThresholdOption;
import org.kie.internal.conf.AlphaThresholdOption;
import org.kie.internal.conf.CompositeKeyDepthOption;
import org.kie.internal.conf.ConsequenceExceptionHandlerOption;
import org.kie.internal.conf.IndexLeftBetaMemoryOption;
import org.kie.internal.conf.IndexPrecedenceOption;
import org.kie.internal.conf.IndexRightBetaMemoryOption;
import org.kie.internal.conf.MaxThreadsOption;
import org.kie.internal.conf.ParallelExecutionOption;
import org.kie.internal.conf.SequentialAgendaOption;
import org.kie.internal.conf.ShareAlphaNodesOption;
import org.kie.internal.conf.ShareBetaNodesOption;

import static org.assertj.core.api.Assertions.assertThat;

public class KnowledgeBaseConfigurationTest {

    private KieBaseConfiguration config;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        config = KieServices.Factory.get().newKieBaseConfiguration();
    }

    @Test
    public void testSequentialConfiguration() {
        // setting the option using the type safe method
        config.setOption( SequentialOption.YES );

        // checking the type safe getOption() method
        assertThat(config.getOption(SequentialOption.KEY)).isEqualTo(SequentialOption.YES);
        // checking the string based getProperty() method
        assertThat(config.getProperty(SequentialOption.PROPERTY_NAME)).isEqualTo("true");

        // setting the options using the string based setProperty() method
        config.setProperty( SequentialOption.PROPERTY_NAME,
                            "false" );

        // checking the type safe getOption() method
        assertThat(config.getOption(SequentialOption.KEY)).isEqualTo(SequentialOption.NO);
        // checking the string based getProperty() method
        assertThat(config.getProperty(SequentialOption.PROPERTY_NAME)).isEqualTo("false");
    }
    
    @Test
    public void testRemoveIdentitiesConfiguration() {
        // setting the option using the type safe method
        config.setOption( RemoveIdentitiesOption.YES );

        // checking the type safe getOption() method
        assertThat(config.getOption(RemoveIdentitiesOption.KEY)).isEqualTo(RemoveIdentitiesOption.YES);
        // checking the string based getProperty() method
        assertThat(config.getProperty(RemoveIdentitiesOption.PROPERTY_NAME)).isEqualTo("true");

        // setting the options using the string based setProperty() method
        config.setProperty( RemoveIdentitiesOption.PROPERTY_NAME,
                            "false" );

        // checking the type safe getOption() method
        assertThat(config.getOption(RemoveIdentitiesOption.KEY)).isEqualTo(RemoveIdentitiesOption.NO);
        // checking the string based getProperty() method
        assertThat(config.getProperty(RemoveIdentitiesOption.PROPERTY_NAME)).isEqualTo("false");
    }
    
    @Test
    public void testShareAlphaNodesConfiguration() {
        // setting the option using the type safe method
        config.setOption( ShareAlphaNodesOption.YES );

        // checking the type safe getOption() method
        assertThat(config.getOption(ShareAlphaNodesOption.KEY)).isEqualTo(ShareAlphaNodesOption.YES);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ShareAlphaNodesOption.PROPERTY_NAME)).isEqualTo("true");

        // setting the options using the string based setProperty() method
        config.setProperty( ShareAlphaNodesOption.PROPERTY_NAME,
                            "false" );

        // checking the type safe getOption() method
        assertThat(config.getOption(ShareAlphaNodesOption.KEY)).isEqualTo(ShareAlphaNodesOption.NO);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ShareAlphaNodesOption.PROPERTY_NAME)).isEqualTo("false");
    }
    
    @Test
    public void testShareBetaNodesConfiguration() {
        // setting the option using the type safe method
        config.setOption( ShareBetaNodesOption.YES );

        // checking the type safe getOption() method
        assertThat(config.getOption(ShareBetaNodesOption.KEY)).isEqualTo(ShareBetaNodesOption.YES);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ShareBetaNodesOption.PROPERTY_NAME)).isEqualTo("true");

        // setting the options using the string based setProperty() method
        config.setProperty( ShareBetaNodesOption.PROPERTY_NAME,
                            "false" );

        // checking the type safe getOption() method
        assertThat(config.getOption(ShareBetaNodesOption.KEY)).isEqualTo(ShareBetaNodesOption.NO);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ShareBetaNodesOption.PROPERTY_NAME)).isEqualTo("false");
    }
    
    @Test
    public void testIndexLeftBetaMemoryConfiguration() {
        // setting the option using the type safe method
        config.setOption( IndexLeftBetaMemoryOption.YES );

        // checking the type safe getOption() method
        assertThat(config.getOption(IndexLeftBetaMemoryOption.KEY)).isEqualTo(IndexLeftBetaMemoryOption.YES);
        // checking the string based getProperty() method
        assertThat(config.getProperty(IndexLeftBetaMemoryOption.PROPERTY_NAME)).isEqualTo("true");

        // setting the options using the string based setProperty() method
        config.setProperty( IndexLeftBetaMemoryOption.PROPERTY_NAME,
                            "false" );

        // checking the type safe getOption() method
        assertThat(config.getOption(IndexLeftBetaMemoryOption.KEY)).isEqualTo(IndexLeftBetaMemoryOption.NO);
        // checking the string based getProperty() method
        assertThat(config.getProperty(IndexLeftBetaMemoryOption.PROPERTY_NAME)).isEqualTo("false");
    }
    
    @Test
    public void testIndexRightBetaMemoryConfiguration() {
        // setting the option using the type safe method
        config.setOption( IndexRightBetaMemoryOption.YES );

        // checking the type safe getOption() method
        assertThat(config.getOption(IndexRightBetaMemoryOption.KEY)).isEqualTo(IndexRightBetaMemoryOption.YES);
        // checking the string based getProperty() method
        assertThat(config.getProperty(IndexRightBetaMemoryOption.PROPERTY_NAME)).isEqualTo("true");

        // setting the options using the string based setProperty() method
        config.setProperty( IndexRightBetaMemoryOption.PROPERTY_NAME,
                            "false" );

        // checking the type safe getOption() method
        assertThat(config.getOption(IndexRightBetaMemoryOption.KEY)).isEqualTo(IndexRightBetaMemoryOption.NO);
        // checking the string based getProperty() method
        assertThat(config.getProperty(IndexRightBetaMemoryOption.PROPERTY_NAME)).isEqualTo("false");
    }

    @Test
    public void testIndexPrecedenceConfiguration() {
        // setting the option using the type safe method
        config.setOption( IndexPrecedenceOption.PATTERN_ORDER );

        // checking the type safe getOption() method
        assertThat(config.getOption(IndexPrecedenceOption.KEY)).isEqualTo(IndexPrecedenceOption.PATTERN_ORDER);
        // checking the string based getProperty() method
        assertThat(config.getProperty(IndexPrecedenceOption.PROPERTY_NAME)).isEqualTo("pattern");

        // setting the options using the string based setProperty() method
        config.setProperty( IndexPrecedenceOption.PROPERTY_NAME,
                "equality" );

        // checking the type safe getOption() method
        assertThat(config.getOption(IndexPrecedenceOption.KEY)).isEqualTo(IndexPrecedenceOption.EQUALITY_PRIORITY);
        // checking the string based getProperty() method
        assertThat(config.getProperty(IndexPrecedenceOption.PROPERTY_NAME)).isEqualTo("equality");
    }

    @Test
    public void testAssertBehaviorConfiguration() {
        // setting the option using the type safe method
        config.setOption( EqualityBehaviorOption.EQUALITY );

        // checking the type safe getOption() method
        assertThat(config.getOption(EqualityBehaviorOption.KEY)).isEqualTo(EqualityBehaviorOption.EQUALITY);
        // checking the string based getProperty() method
        assertThat(config.getProperty(EqualityBehaviorOption.PROPERTY_NAME)).isEqualTo("equality");

        // setting the options using the string based setProperty() method
        config.setProperty( EqualityBehaviorOption.PROPERTY_NAME,
                            "identity" );

        // checking the type safe getOption() method
        assertThat(config.getOption(EqualityBehaviorOption.KEY)).isEqualTo(EqualityBehaviorOption.IDENTITY);
        // checking the string based getProperty() method
        assertThat(config.getProperty(EqualityBehaviorOption.PROPERTY_NAME)).isEqualTo("identity");
    }

    @Test
    public void testPrototypesConfiguration() {
        // setting the option using the type safe method
        config.setOption(PrototypesOption.ALLOWED);

        // checking the type safe getOption() method
        assertThat(config.getOption(PrototypesOption.KEY)).isEqualTo(PrototypesOption.ALLOWED);
        // checking the string based getProperty() method
        assertThat(config.getProperty(PrototypesOption.PROPERTY_NAME)).isEqualTo("allowed");

        // setting the options using the string based setProperty() method
        config.setProperty( PrototypesOption.PROPERTY_NAME, "disabled" );

        // checking the type safe getOption() method
        assertThat(config.getOption(PrototypesOption.KEY)).isEqualTo(PrototypesOption.DISABLED);
        // checking the string based getProperty() method
        assertThat(config.getProperty(PrototypesOption.PROPERTY_NAME)).isEqualTo("disabled");
    }
    
    @Test
    public void testSequentialAgendaConfiguration() {
        // setting the option using the type safe method
        config.setOption( SequentialAgendaOption.DYNAMIC );

        // checking the type safe getOption() method
        assertThat(config.getOption(SequentialAgendaOption.KEY)).isEqualTo(SequentialAgendaOption.DYNAMIC);
        // checking the string based getProperty() method
        assertThat(config.getProperty(SequentialAgendaOption.PROPERTY_NAME)).isEqualTo("dynamic");

        // setting the options using the string based setProperty() method
        config.setProperty( SequentialAgendaOption.PROPERTY_NAME,
                            "sequential" );

        // checking the type safe getOption() method
        assertThat(config.getOption(SequentialAgendaOption.KEY)).isEqualTo(SequentialAgendaOption.SEQUENTIAL);
        // checking the string based getProperty() method
        assertThat(config.getProperty(SequentialAgendaOption.PROPERTY_NAME)).isEqualTo("sequential");
    }
    
    @Test
    public void testAlphaThresholdConfiguration() {
        // setting the option using the type safe method
        config.setOption( AlphaThresholdOption.get(5) );

        // checking the type safe getOption() method
        assertThat(config.getOption(AlphaThresholdOption.KEY)).isEqualTo(AlphaThresholdOption.get(5));
        // checking the string based getProperty() method
        assertThat(config.getProperty(AlphaThresholdOption.PROPERTY_NAME)).isEqualTo("5");

        // setting the options using the string based setProperty() method
        config.setProperty( AlphaThresholdOption.PROPERTY_NAME,
                            "7" );

        // checking the type safe getOption() method
        assertThat(config.getOption(AlphaThresholdOption.KEY)).isEqualTo(AlphaThresholdOption.get(7));
        // checking the string based getProperty() method
        assertThat(config.getProperty(AlphaThresholdOption.PROPERTY_NAME)).isEqualTo("7");
    }

    @Test
    public void testAlphaRangeIndexThresholdConfiguration() {
        // setting the option using the type safe method
        config.setOption( AlphaRangeIndexThresholdOption.get(5) );

        // checking the type safe getOption() method
        assertThat(config.getOption(AlphaRangeIndexThresholdOption.KEY)).isEqualTo(AlphaRangeIndexThresholdOption.get(5));
        // checking the string based getProperty() method
        assertThat(config.getProperty(AlphaRangeIndexThresholdOption.PROPERTY_NAME)).isEqualTo("5");

        // setting the options using the string based setProperty() method
        config.setProperty( AlphaRangeIndexThresholdOption.PROPERTY_NAME,
                            "7" );

        // checking the type safe getOption() method
        assertThat(config.getOption(AlphaRangeIndexThresholdOption.KEY)).isEqualTo(AlphaRangeIndexThresholdOption.get(7));
        // checking the string based getProperty() method
        assertThat(config.getProperty(AlphaRangeIndexThresholdOption.PROPERTY_NAME)).isEqualTo("7");

        // If empty, default value is set
        config.setProperty( AlphaRangeIndexThresholdOption.PROPERTY_NAME,
                            "" );

        // checking the type safe getOption() method
        assertThat(config.getOption(AlphaRangeIndexThresholdOption.KEY)).isEqualTo(AlphaRangeIndexThresholdOption.get(AlphaRangeIndexThresholdOption.DEFAULT_VALUE));
        // checking the string based getProperty() method
        assertThat(config.getProperty(AlphaRangeIndexThresholdOption.PROPERTY_NAME)).isEqualTo(String.valueOf(AlphaRangeIndexThresholdOption.DEFAULT_VALUE));
    }

    @Test
    public void testBetaRangeIndexenabledConfiguration() {
        // setting the option using the enum
        config.setOption( BetaRangeIndexOption.ENABLED );

        // checking the type safe getOption() method
        assertThat(config.getOption(BetaRangeIndexOption.KEY)).isEqualTo(BetaRangeIndexOption.ENABLED);
        // checking the string based getProperty() method
        assertThat(config.getProperty(BetaRangeIndexOption.PROPERTY_NAME)).isEqualTo("true");

        // setting the options using the string based setProperty() method
        config.setProperty( BetaRangeIndexOption.PROPERTY_NAME,
                            "false" );

        // checking the type safe getOption() method
        assertThat(config.getOption(BetaRangeIndexOption.KEY)).isEqualTo(BetaRangeIndexOption.DISABLED);
        // checking the string based getProperty() method
        assertThat(config.getProperty(BetaRangeIndexOption.PROPERTY_NAME)).isEqualTo("false");
    }

    @Test
    public void testCompositeKeyDepthConfiguration() {
        // setting the option using the type safe method
        config.setOption( CompositeKeyDepthOption.get(1) );

        // checking the type safe getOption() method
        assertThat(config.getOption(CompositeKeyDepthOption.KEY)).isEqualTo(CompositeKeyDepthOption.get(1));
        // checking the string based getProperty() method
        assertThat(config.getProperty(CompositeKeyDepthOption.PROPERTY_NAME)).isEqualTo("1");

        // setting the options using the string based setProperty() method
        config.setProperty( CompositeKeyDepthOption.PROPERTY_NAME,
                            "2" );

        // checking the type safe getOption() method
        assertThat(config.getOption(CompositeKeyDepthOption.KEY)).isEqualTo(CompositeKeyDepthOption.get(2));
        // checking the string based getProperty() method
        assertThat(config.getProperty(CompositeKeyDepthOption.PROPERTY_NAME)).isEqualTo("2");
    }
    
    @Test
    public void testConsequenceExceptionHandlerConfiguration() {
        Class<? extends ConsequenceExceptionHandler> handler = DefaultConsequenceExceptionHandler.class;
        // setting the option using the type safe method
        config.setOption( ConsequenceExceptionHandlerOption.get(handler) );

        // checking the type safe getOption() method
        assertThat(config.getOption(ConsequenceExceptionHandlerOption.KEY)).isEqualTo(ConsequenceExceptionHandlerOption.get(handler));
        // checking the string based getProperty() method
        assertThat(config.getProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME)).isEqualTo(handler.getName());

        // setting the options using the string based setProperty() method
        config.setProperty( ConsequenceExceptionHandlerOption.PROPERTY_NAME,
                            handler.getName() );

        // checking the type safe getOption() method
        assertThat(config.getOption(ConsequenceExceptionHandlerOption.KEY).getHandler().getName()).isEqualTo(handler.getName());
        // checking the string based getProperty() method
        assertThat(config.getProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME)).isEqualTo(handler.getName());
    }
    
    @Test
    public void testEventProcessingConfiguration() {
        // setting the option using the type safe method
        config.setOption( EventProcessingOption.STREAM );

        // checking the type safe getOption() method
        assertThat(config.getOption(EventProcessingOption.KEY)).isEqualTo(EventProcessingOption.STREAM);
        // checking the string based getProperty() method
        assertThat(config.getProperty(EventProcessingOption.PROPERTY_NAME)).isEqualTo("stream");

        // setting the options using the string based setProperty() method
        config.setProperty( EventProcessingOption.PROPERTY_NAME,
                            "cloud" );

        // checking the type safe getOption() method
        assertThat(config.getOption(EventProcessingOption.KEY)).isEqualTo(EventProcessingOption.CLOUD);
        // checking the string based getProperty() method
        assertThat(config.getProperty(EventProcessingOption.PROPERTY_NAME)).isEqualTo("cloud");
    }
    
    @Test
    public void testMaxThreadsConfiguration() {
        // setting the option using the type safe method
        config.setOption( MaxThreadsOption.get(5) );

        // checking the type safe getOption() method
        assertThat(config.getOption(MaxThreadsOption.KEY)).isEqualTo(MaxThreadsOption.get(5));
        // checking the string based getProperty() method
        assertThat(config.getProperty(MaxThreadsOption.PROPERTY_NAME)).isEqualTo("5");

        // setting the options using the string based setProperty() method
        config.setProperty( MaxThreadsOption.PROPERTY_NAME, "8" );

        // checking the type safe getOption() method
        assertThat(config.getOption(MaxThreadsOption.KEY)).isEqualTo(MaxThreadsOption.get(8));
        // checking the string based getProperty() method
        assertThat(config.getProperty(MaxThreadsOption.PROPERTY_NAME)).isEqualTo("8");
    }
    
    @Test
    public void testParallelExecutionConfiguration() {
        // setting the option using the type safe method
        config.setOption( ParallelExecutionOption.FULLY_PARALLEL );

        // checking the type safe getOption() method
        assertThat(config.getOption(ParallelExecutionOption.KEY)).isEqualTo(ParallelExecutionOption.FULLY_PARALLEL);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ParallelExecutionOption.PROPERTY_NAME)).isEqualTo("fully_parallel");

        // setting the options using the string based setProperty() method
        config.setProperty( ParallelExecutionOption.PROPERTY_NAME,"sequential" );

        // checking the type safe getOption() method
        assertThat(config.getOption(ParallelExecutionOption.KEY)).isEqualTo(ParallelExecutionOption.SEQUENTIAL);
        // checking the string based getProperty() method
        assertThat(config.getProperty(ParallelExecutionOption.PROPERTY_NAME)).isEqualTo("sequential");
    }
    
    @Test
    public void testRulebaseSetUpdateHandler() {
        // this test is to avoid a regression, since update handler was supposed to be disabled in Drools 5.
        // At this moment, we no longer want to expose the update handler API, so, we did not created an Option
        // class for it.
        
        // checking the string based getProperty() method
        assertThat(config.getProperty("drools.ruleBaseUpdateHandler")).isEqualTo("");

        // setting the options using the string based setProperty() method
        config.setProperty( "drools.ruleBaseUpdateHandler",
                            "somethingElse" );

        // checking the string based getProperty() method
        assertThat(config.getProperty("drools.ruleBaseUpdateHandler")).isEqualTo("somethingElse");

        // setting the options using the string based setProperty() method
        config.setProperty( "drools.ruleBaseUpdateHandler",
                            null );

        // checking the string based getProperty() method
        assertThat(config.getProperty("drools.ruleBaseUpdateHandler")).isEqualTo("");

        // setting the options using the string based setProperty() method
        config.setProperty( "drools.ruleBaseUpdateHandler",
                            "" );

        // checking the string based getProperty() method
        assertThat(config.getProperty("drools.ruleBaseUpdateHandler")).isEqualTo("");
    }
    
    
    

}
