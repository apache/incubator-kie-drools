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
package org.drools.persistence.map.impl;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.io.ByteArrayResource;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public abstract class MapPersistenceTest {

    private static Logger logger = LoggerFactory.getLogger(JPAPlaceholderResolverStrategy.class);
    
    @Test
    public void createPersistentSession() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        
        KieSession crmPersistentSession = createSession( kbase );
        crmPersistentSession.fireAllRules();

        crmPersistentSession = createSession( kbase );
        assertThat(crmPersistentSession).isNotNull();
    }


    @Test
    public void createPersistentSessionWithRules() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();

        String rule = "package org.drools.persistence.map.impl\n";
        rule += "import org.drools.persistence.map.impl.Buddy;\n";
        rule += "rule \"echo2\" \n";
        rule += "dialect \"mvel\"\n";
        rule += "when\n";
        rule += "    $m : Buddy()\n";
        rule += "then\n";
        rule += "    System.out.println(\"buddy inserted\")";
        rule += "end\n";
        kbuilder.add( new ByteArrayResource( rule.getBytes() ),
                      ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors != null && errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                logger.warn( "Error: " + error.getMessage() );
            }
            fail( "KnowledgeBase did not build" );
        }

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );

        KieSession ksession = createSession( kbase );

        FactHandle buddyFactHandle = ksession.insert( new Buddy() );
        ksession.fireAllRules();

        assertThat(ksession.getObjects().size()).isEqualTo(1);

        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        assertThat(ksession).isNotNull();

        assertThat(ksession.getObjects().size()).isEqualTo(1);

        assertThat(ksession.getObject( buddyFactHandle)).as( "An object can't be retrieved with a FactHandle from a disposed session").isNull();

    }

    @Test
    public void dontCreateMoreSessionsThanNecessary() {
        long initialNumberOfSavedSessions = getSavedSessionsCount();
        
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KieSession crmPersistentSession = createSession(kbase);

        long ksessionId = crmPersistentSession.getIdentifier();
        crmPersistentSession.fireAllRules();

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase);

        assertThat(crmPersistentSession.getIdentifier()).isEqualTo(ksessionId);

        ksessionId = crmPersistentSession.getIdentifier();
        crmPersistentSession.fireAllRules();

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase);

        crmPersistentSession.fireAllRules();

        assertThat(getSavedSessionsCount()).isEqualTo(initialNumberOfSavedSessions + 1);
        crmPersistentSession.dispose();
    }


    @Test
    public void insertObjectIntoKsessionAndRetrieve() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        
        KieSession crmPersistentSession = createSession(kbase);
        Buddy bestBuddy = new Buddy("john");
        crmPersistentSession.insert(bestBuddy);

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase);
        Object obtainedBuddy = crmPersistentSession
                .getObjects().iterator().next();
        assertThat(obtainedBuddy).isNotSameAs(bestBuddy);
        assertThat(obtainedBuddy).isEqualTo(bestBuddy);

        crmPersistentSession.dispose();
    }

    protected abstract KieSession createSession(KieBase kbase);
    
    protected abstract KieSession disposeAndReloadSession(KieSession crmPersistentSession,
                                                                        KieBase kbase);

    protected abstract long getSavedSessionsCount();
}
