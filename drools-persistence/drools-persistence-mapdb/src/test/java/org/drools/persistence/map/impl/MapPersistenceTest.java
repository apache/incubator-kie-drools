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

import org.drools.persistence.mapdb.marshaller.MapDBPlaceholderResolverStrategy;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MapPersistenceTest {

    private static Logger logger = LoggerFactory.getLogger(MapDBPlaceholderResolverStrategy.class);
    
    @Test
    public void createPersistentSession() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        
        KieSession crmPersistentSession = createSession( kbase );
        crmPersistentSession.fireAllRules();

        crmPersistentSession = createSession( kbase );
        Assert.assertNotNull( crmPersistentSession );
    }


    @Test
    public void createPersistentSessionWithRules() {
    	KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();

        String rule = "package org.drools.persistence.map.impl\n";
        rule += "import org.drools.persistence.map.impl.Buddy;\n";
        rule += "rule \"echo2\" \n";
        rule += "dialect \"mvel\"\n";
        rule += "when\n";
        rule += "    $m : Buddy()\n";
        rule += "then\n";
        rule += "    System.out.println(\"buddy inserted\")";
        rule += "end\n";
        kfs.write( "src/main/resources/r1.drl", ResourceFactory.newByteArrayResource( rule.getBytes() ) );
        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();
        if ( kbuilder.getResults().hasMessages(Level.ERROR) ) {
            for ( Message error : kbuilder.getResults().getMessages()) {
                logger.warn( "Error: " + error );
            }
            Assert.fail( "KnowledgeBase did not build" );
        }

        KieBase kbase = KieServices.Factory.get().newKieContainer(kbuilder.getKieModule().getReleaseId()).getKieBase();
        KieSession ksession = createSession( kbase );

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
        
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KieSession crmPersistentSession = createSession(kbase);

        long ksessionId = crmPersistentSession.getIdentifier();
        crmPersistentSession.fireAllRules();

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase);

        Assert.assertEquals(ksessionId, crmPersistentSession.getIdentifier());

        ksessionId = crmPersistentSession.getIdentifier();
        crmPersistentSession.fireAllRules();

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase);

        crmPersistentSession.fireAllRules();

        Assert.assertEquals(initialNumberOfSavedSessions + 1, getSavedSessionsCount());
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
        Assert.assertNotSame( bestBuddy, obtainedBuddy );
        Assert.assertEquals(bestBuddy, obtainedBuddy);

        crmPersistentSession.dispose();
    }

    protected abstract KieSession createSession(KieBase kbase);
    
    protected abstract KieSession disposeAndReloadSession(KieSession crmPersistentSession, KieBase kbase);

    protected abstract long getSavedSessionsCount();
}
