/*
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

package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.Otherwise;
import org.drools.core.WorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.TestBean;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBaseFactory;

/**
 * This tests the "otherwise" feature.
 */
public class OtherwiseTest {
    
    @Test
    public void testOneRuleFiringNoOtherwise() throws Exception {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase)KnowledgeBaseFactory.newKnowledgeBase();

        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "Miss Manners" );
        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        final RuleImpl rule1 = getRule( "rule1" );
        pkg.addRule( rule1 );

        final RuleImpl ruleOtherwise = getOtherwise( "rule2" );
        pkg.addRule( ruleOtherwise );

        kBase.addPackage( pkg );

        KieSession ksession = kBase.newStatefulKnowledgeSession();
        ksession.insert( new TestBean() );
        ksession.fireAllRules();

        assertTrue( ((MockConsequence) rule1.getConsequence()).fired );
        assertFalse( ((MockConsequence) ruleOtherwise.getConsequence()).fired );
    }

    @Test
    public void testTwoRulesFiringNoOtherwise() throws Exception {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase)KnowledgeBaseFactory.newKnowledgeBase();

        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "Miss Manners" );
        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        final RuleImpl rule1 = getRule( "rule1" );
        pkg.addRule( rule1 );
        final RuleImpl rule2 = getRule( "rule2" );
        pkg.addRule( rule2 );

        final RuleImpl ruleOtherwise = getOtherwise( "ruleOtherwise" );
        pkg.addRule( ruleOtherwise );

        kBase.addPackage( pkg );

        KieSession ksession = kBase.newStatefulKnowledgeSession();
        ksession.insert( new TestBean() );
        ksession.fireAllRules();

        assertFalse( ((MockConsequence) ruleOtherwise.getConsequence()).fired );
        assertTrue( ((MockConsequence) rule1.getConsequence()).fired );
        assertTrue( ((MockConsequence) rule2.getConsequence()).fired );

    }

    /**
     * @TODO: this is a future to be implemented in the future
     * @throws Exception
     */
    @Test @Ignore
    public void testOtherwiseFiringWithOneRule() throws Exception {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase)KnowledgeBaseFactory.newKnowledgeBase();

        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "Miss Manners" );
        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        final RuleImpl rule1 = getRule( "rule1" );
        pkg.addRule( rule1 );

        final RuleImpl ruleOtherwise = getOtherwise( "rule2" );
        pkg.addRule( ruleOtherwise );

        kBase.addPackage( pkg );

        KieSession ksession = kBase.newStatefulKnowledgeSession();

        ksession.fireAllRules();

        assertFalse( ((MockConsequence) rule1.getConsequence()).fired );
        assertTrue( ((MockConsequence) ruleOtherwise.getConsequence()).fired );

    }

    /**
     * @TODO: this is a future to be implemented in the future
     * @throws Exception
     */
    @Test @Ignore
    public void testOtherwiseFiringMultipleRules() throws Exception {
        InternalKnowledgeBase kBase = (InternalKnowledgeBase)KnowledgeBaseFactory.newKnowledgeBase();

        InternalKnowledgePackage pkg = new KnowledgePackageImpl( "Miss Manners" );
        pkg.setClassFieldAccessorCache( new ClassFieldAccessorCache( Thread.currentThread().getContextClassLoader() ) );
        final RuleImpl rule1 = getRule( "rule1" );
        pkg.addRule( rule1 );
        final RuleImpl rule2 = getRule( "rule2" );
        pkg.addRule( rule2 );

        final RuleImpl ruleOtherwise1 = getOtherwise( "other1" );
        pkg.addRule( ruleOtherwise1 );
        final RuleImpl ruleOtherwise2 = getOtherwise( "other2" );
        pkg.addRule( ruleOtherwise2 );

        kBase.addPackage( pkg );

        KieSession ksession = kBase.newStatefulKnowledgeSession();

        ksession.fireAllRules();

        assertFalse( ((MockConsequence) rule1.getConsequence()).fired );
        assertFalse( ((MockConsequence) rule2.getConsequence()).fired );
        assertTrue( ((MockConsequence) ruleOtherwise1.getConsequence()).fired );
        assertTrue( ((MockConsequence) ruleOtherwise2.getConsequence()).fired );

    }

    private RuleImpl getOtherwise(final String name) {
        final RuleImpl rule = new RuleImpl( name );
        final Pattern pat = new Pattern( 0,
                                         new ClassObjectType( Otherwise.class) );
        rule.addPattern( pat );
        rule.setConsequence( new MockConsequence() );
        return rule;
    }

    private RuleImpl getRule(final String name) {
        final RuleImpl rule = new RuleImpl( name );

        final Pattern pat = new Pattern( 0,
                                         new ClassObjectType( TestBean.class ) );

        rule.addPattern( pat );
        rule.setConsequence( new MockConsequence() );

        return rule;
    }

    static class MockConsequence
        implements
        Consequence {

        public boolean fired = false;

        public void evaluate(final KnowledgeHelper knowledgeHelper,
                             final WorkingMemory workingMemory) throws Exception {
            this.fired = true;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

        }

        public void writeExternal(ObjectOutput out) throws IOException {

        }
        
        public String getName() {
            return "default";
        }
    }

}
