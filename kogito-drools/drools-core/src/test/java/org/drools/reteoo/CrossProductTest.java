/**
 * Copyright 2005 JBoss Inc
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

package org.drools.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.base.ClassObjectType;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.WorkingMemory;
import org.drools.spi.Consequence;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ObjectType;

public class CrossProductTest {
    private Package       pkg;
    private WorkingMemory workingMemory;
    private List          values;

    @Before
    public void setUp() throws Exception {
        final ObjectType list1ObjectType = new ClassObjectType( String.class );
        final ObjectType list2ObjectType = new ClassObjectType( String.class );

        final Rule rule = new Rule( "rule-1" );

        final Pattern list1Pattern = new Pattern( 0,
                                               list1ObjectType,
                                               "s1" );
        final Pattern list2Pattern = new Pattern( 1,
                                               list2ObjectType,
                                               "s2" );

        rule.addPattern( list1Pattern );
        rule.addPattern( list2Pattern );

        final Declaration s1Declaration = rule.getDeclaration( "s1" );
        final Declaration s2Declaration = rule.getDeclaration( "s2" );

        this.values = new ArrayList();

        rule.setConsequence( new Consequence() {

            /**
             *
             */
            private static final long serialVersionUID = 510l;

            public void evaluate(final KnowledgeHelper knowledgeHelper,
                                 final WorkingMemory workingMemory) throws Exception {
                final String s1 = (String) knowledgeHelper.get( s1Declaration );
                final String s2 = (String) knowledgeHelper.get( s2Declaration );
                CrossProductTest.this.values.add( new String[]{s1, s2} );
            }

            public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

            }

            public void writeExternal(ObjectOutput out) throws IOException {

            }
            
            public String getName() {
                return "default";
            }            
        } );

        this.pkg = new Package( "org.drools" );
        this.pkg.addRule( rule );
    }

    @Test
    public void testNotRemoveIdentities() throws Exception {
        // Default is remove identity FALSE
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( this.pkg );

        this.workingMemory = ruleBase.newStatefulSession();
        this.workingMemory.insert( "F1" );
        this.workingMemory.insert( "F2" );
        this.workingMemory.insert( "F3" );
        this.workingMemory.insert( "F4" );

        this.workingMemory.fireAllRules();

        // A full cross product is 16, this is just 12
        assertEquals( 16,
                      this.values.size() );
    }

    @Test
    public void testRemoveIdentities() throws Exception {
        System.setProperty( "drools.removeIdentities",
                            "true" );
        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( this.pkg );

        this.workingMemory = ruleBase.newStatefulSession();
        this.workingMemory.insert( "F1" );
        this.workingMemory.insert( "F2" );
        this.workingMemory.insert( "F3" );
        this.workingMemory.insert( "F4" );

        this.workingMemory.fireAllRules();

        // A full cross product is 16, this is just 12
        assertEquals( 12,
                      this.values.size() );
    }

}
