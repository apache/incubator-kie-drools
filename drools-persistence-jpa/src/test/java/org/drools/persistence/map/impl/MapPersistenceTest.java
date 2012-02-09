/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.map.impl;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ByteArrayResource;
import org.drools.persistence.VariablePersistenceUnitTest;
import org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MapPersistenceTest extends VariablePersistenceUnitTest {

    private static Logger logger = LoggerFactory.getLogger(JPAPlaceholderResolverStrategy.class);
    
    @Test
    public void createPersistentSession() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        
        StatefulKnowledgeSession crmPersistentSession = createSession( kbase );
        crmPersistentSession.fireAllRules();

        crmPersistentSession = createSession( kbase );
        Assert.assertNotNull( crmPersistentSession );
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
            Assert.fail( "KnowledgeBase did not build" );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = createSession( kbase );

        FactHandle buddyFactHandle = ksession.insert( new Buddy() );
        ksession.fireAllRules();

        Assert.assertEquals( 1,
                             ksession.getObjects().size() );

        ksession = disposeAndReloadSession( ksession,
                                            kbase );

        Assert.assertNotNull( ksession );

        Assert.assertEquals( 1,
                             ksession.getObjects().size() );

        Assert.assertNull( "An object can't be retrieved with a FactHandle from a disposed session",
                           ksession.getObject( buddyFactHandle ) );

    }

    @Test
    public void dontCreateMoreSessionsThanNecessary() {
        long initialNumberOfSavedSessions = getSavedSessionsCount();
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        StatefulKnowledgeSession crmPersistentSession = createSession(kbase);

        long ksessionId = crmPersistentSession.getId();
        crmPersistentSession.fireAllRules();

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase);

        Assert.assertEquals(ksessionId, crmPersistentSession.getId());

        ksessionId = crmPersistentSession.getId();
        crmPersistentSession.fireAllRules();

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase);

        crmPersistentSession.fireAllRules();

        Assert.assertEquals(initialNumberOfSavedSessions + 1, getSavedSessionsCount());
        crmPersistentSession.dispose();
    }


    @Test
    public void insertObjectIntoKsessionAndRetrieve() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        
        StatefulKnowledgeSession crmPersistentSession = createSession(kbase);
        Buddy bestBuddy = new Buddy("john");
        crmPersistentSession.insert(bestBuddy);

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase);
        Object obtainedBuddy = crmPersistentSession
                .getObjects().iterator().next();
        Assert.assertNotSame( bestBuddy, obtainedBuddy );
        Assert.assertEquals(bestBuddy, obtainedBuddy);

        crmPersistentSession.dispose();
    }

    protected abstract StatefulKnowledgeSession createSession(KnowledgeBase kbase);
    
    protected abstract StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession crmPersistentSession,
                                                                        KnowledgeBase kbase);

    protected abstract long getSavedSessionsCount();
}
