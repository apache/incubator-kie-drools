/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.rule;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.drools.core.ClockType;
import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.base.EnabledBoolean;
import org.drools.core.base.SalienceInteger;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.accessor.Salience;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.mvel.expr.MVELSalienceExpression;
import org.junit.Test;
import org.kie.api.KieServices;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleTest {

    @Test
    public void testDateEffective() {
        WorkingMemory wm = (WorkingMemory) KnowledgeBaseFactory.newKnowledgeBase( "x", RuleBaseFactory.newKnowledgeBaseConfiguration()).newKieSession();

        final RuleImpl rule = new RuleImpl( "myrule" );

        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isTrue();

        final Calendar earlier = Calendar.getInstance();
        earlier.setTimeInMillis( 10 );

        rule.setDateEffective( earlier );

        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isTrue();

        final Calendar later = Calendar.getInstance();
        later.setTimeInMillis( later.getTimeInMillis() + 100000000 );

        assertThat(later.after(Calendar.getInstance())).isTrue();

        rule.setDateEffective( later );
        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isFalse();

    }

    @Test
    public void testDateExpires() throws Exception {
        WorkingMemory wm = (WorkingMemory) KnowledgeBaseFactory.newKnowledgeBase("x", RuleBaseFactory.newKnowledgeBaseConfiguration()).newKieSession();
        
        final RuleImpl rule = new RuleImpl( "myrule" );

        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isTrue();

        final Calendar earlier = Calendar.getInstance();
        earlier.setTimeInMillis( 10 );

        rule.setDateExpires( earlier );

        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isFalse();

        final Calendar later = Calendar.getInstance();
        later.setTimeInMillis( later.getTimeInMillis() + 100000000 );

        rule.setDateExpires( later );
        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isTrue();

    }

    @Test
    public void testDateEffectiveExpires() {
        WorkingMemory wm = (WorkingMemory) KnowledgeBaseFactory.newKnowledgeBase("x",RuleBaseFactory.newKnowledgeBaseConfiguration()).newKieSession();
        
        final RuleImpl rule = new RuleImpl( "myrule" );

        final Calendar past = Calendar.getInstance();
        past.setTimeInMillis( 10 );

        final Calendar future = Calendar.getInstance();
        future.setTimeInMillis( future.getTimeInMillis() + 100000000 );

        rule.setDateEffective( past );
        rule.setDateExpires( future );

        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isTrue();

        rule.setDateExpires( past );
        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isFalse();

        rule.setDateExpires( future );
        rule.setDateEffective( future );


        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isFalse();

    }

    @Test
    public void testRuleEnabled() {
        WorkingMemory wm = (WorkingMemory) KnowledgeBaseFactory.newKnowledgeBase("x", RuleBaseFactory.newKnowledgeBaseConfiguration()).newKieSession();
        
        final RuleImpl rule = new RuleImpl( "myrule" );
        rule.setEnabled( EnabledBoolean.ENABLED_FALSE );
        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isFalse();

        final Calendar past = Calendar.getInstance();
        past.setTimeInMillis( 10 );

        rule.setDateEffective( past );
        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isFalse();
        rule.setEnabled( EnabledBoolean.ENABLED_TRUE );

        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isTrue();
    }

    @Test
    public void testTimeMachine() {
        SessionConfiguration conf = KieServices.get().newKieSessionConfiguration().as(SessionConfiguration.KEY);
        conf.setClockType(ClockType.PSEUDO_CLOCK);
        WorkingMemory wm = (WorkingMemory) KnowledgeBaseFactory.newKnowledgeBase("x", RuleBaseFactory.newKnowledgeBaseConfiguration()).newKieSession(conf, null);
        
        final Calendar future = Calendar.getInstance();
        ((PseudoClockScheduler)wm.getSessionClock()).setStartupTime( future.getTimeInMillis() );
        
        final RuleImpl rule = new RuleImpl( "myrule" );
        rule.setEnabled( EnabledBoolean.ENABLED_TRUE );
        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isTrue();

        
        future.setTimeInMillis( future.getTimeInMillis() + 100000000 );
        rule.setDateEffective(future);
        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isFalse();

        ((PseudoClockScheduler)wm.getSessionClock()).advanceTime( 1000000000000L, TimeUnit.MILLISECONDS );

        assertThat(rule.isEffective(null, new RuleTerminalNode().getRequiredDeclarations(), wm)).isTrue();
    }
    
    @Test
    public void testGetSalienceValue() {
    	final RuleImpl rule = new RuleImpl( "myrule" );
    	final int salienceValue = 100;
    	
    	Salience salience = new SalienceInteger(salienceValue);
    	rule.setSalience(salience);

        assertThat(rule.getSalienceValue()).isEqualTo(salienceValue);
        assertThat(rule.isSalienceDynamic()).isFalse();
    }
    
    @Test
    public void testIsSalienceDynamic() {
    	final RuleImpl rule = new RuleImpl( "myrule" );
    	
    	Salience salience = new MVELSalienceExpression();
    	rule.setSalience(salience);

        assertThat(rule.isSalienceDynamic()).isTrue();
    }

}
