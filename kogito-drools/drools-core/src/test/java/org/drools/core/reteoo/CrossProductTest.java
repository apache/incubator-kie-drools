/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
import java.util.ArrayList;
import java.util.List;

import org.drools.core.WorkingMemory;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.core.base.ClassObjectType;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.ObjectType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBaseFactory;

public class CrossProductTest {
    private InternalKnowledgePackage pkg;
    private KieSession    ksession;
    private List          values;

    @Before
    public void setUp() throws Exception {
        final ObjectType list1ObjectType = new ClassObjectType( String.class );
        final ObjectType list2ObjectType = new ClassObjectType( String.class );

        final RuleImpl rule = new RuleImpl( "rule-1" );

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

        this.pkg = new KnowledgePackageImpl( "org.drools" );
        this.pkg.addRule( rule );
    }

    @Test
    public void testNotRemoveIdentities() throws Exception {
        // Default is remove identity FALSE
        InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackage( this.pkg );

        this.ksession = kBase.newKieSession();
        this.ksession.insert( "F1" );
        this.ksession.insert( "F2" );
        this.ksession.insert( "F3" );
        this.ksession.insert( "F4" );

        this.ksession.fireAllRules();

        // A full cross product is 16, this is just 12
        System.out.println(values);
        assertEquals( 16,
                      this.values.size() );
    }

    @Test
    public void testRemoveIdentities() throws Exception {
        System.setProperty( "drools.removeIdentities",
                            "true" );
        try {
            InternalKnowledgeBase kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
            kBase.addPackage( this.pkg );

            this.ksession = kBase.newKieSession();
            this.ksession.insert( "F1" );
            this.ksession.insert( "F2" );
            this.ksession.insert( "F3" );
            this.ksession.insert( "F4" );

            this.ksession.fireAllRules();

            // A full cross product is 16, this is just 12
            assertEquals( 12,
                          this.values.size() );
        } finally {
            System.setProperty( "drools.removeIdentities",
                                "false" );
        }
    }

}
