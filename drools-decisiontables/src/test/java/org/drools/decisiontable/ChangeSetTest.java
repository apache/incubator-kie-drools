/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.decisiontable;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeSetTest {
    
    @Test
    public void testIntegration() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "changeset1Test.xml", getClass()), ResourceType.CHANGE_SET );
        assertThat(kbuilder.hasErrors()).isFalse();
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        
        ksession.insert( new Cheese( "cheddar",
                                    42 ) );
        ksession.insert( new Person( "michael",
                                    "stilton",
                                    25 ) );
        
        ksession.fireAllRules();
        ksession.dispose();

        assertThat(list.size()).isEqualTo(3);

        assertThat(list.get(0)).isEqualTo("Young man cheddar");

        assertThat(list.get(1)).isEqualTo("rule1");
        assertThat(list.get(2)).isEqualTo("rule2");
    }

    @Test
    public void multipleSheets() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "multipleSheetsChangeSet.xml", getClass()), ResourceType.CHANGE_SET );
        assertThat(kbuilder.hasErrors()).isFalse();
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        KieSession ksession = kbase.newKieSession();
        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( new Cheese( "cheddar",
                                    42 ) );
        ksession.insert( new Person( "michael",
                                    "stilton",
                                    25 ) );
        ksession.insert( new Person( "Jane",
                                    "stilton",
                                    55 ) );

        ksession.fireAllRules();
        ksession.dispose();

        assertThat(list.size()).isEqualTo(2);

        assertThat(list.contains("Young man cheddar")).isTrue();
        assertThat(list.contains("Jane eats cheddar")).isTrue();
    }

    @Test
    public void testCSV() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "changeSetTestCSV.xml", getClass()), ResourceType.CHANGE_SET );
        assertThat(kbuilder.hasErrors()).isFalse();
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        assertThat(kbase.getKiePackages().size()).isEqualTo(1);
        assertThat(kbase.getKiePackages().iterator().next().getRules().size()).isEqualTo(3);
    }
}
