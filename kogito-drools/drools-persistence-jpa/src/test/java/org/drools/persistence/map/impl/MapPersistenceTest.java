package org.drools.persistence.map.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.impl.ByteArrayResource;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.persistence.map.AbstractStorage;
import org.drools.persistence.map.AbstractStorageEnvironmentBuilder;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Assert;
import org.junit.Test;

public class MapPersistenceTest {

    @Test
    public void createPersistentSession() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        AbstractStorage storage = new AbstractStorage() {

            public void saveOrUpdate(SessionInfo storedObject) {
                System.out.println( "saving" );
            }

            public SessionInfo findSessionInfo(Long id) {
                System.out.println( "finding" );
                return null;
            }
        };
        
        StatefulKnowledgeSession crmPersistentSession = createSession( kbase,
                                                                       storage );
        crmPersistentSession.fireAllRules();

        crmPersistentSession = createSession( kbase,
                                              storage );
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
                System.out.println( "Error: " + error.getMessage() );
            }
            Assert.fail( "KnowledgeBase did not build" );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        AbstractStorage storage = getSimpleStorage();
        
        StatefulKnowledgeSession ksession = createSession( kbase,
                                                           storage );

        FactHandle buddyFactHandle = ksession.insert( new Buddy() );
        ksession.fireAllRules();

        Assert.assertEquals( 1,
                             ksession.getObjects().size() );

        ksession = disposeAndReloadSession( ksession,
                                            kbase,
                                            storage );

        Assert.assertNotNull( ksession );

        Assert.assertEquals( 1,
                             ksession.getObjects().size() );

        Assert.assertNull( "An object can't be retrieved with a FactHandle from a disposed session",
                           ksession.getObject( buddyFactHandle ) );

    }

    @Test
    public void dontCreateMoreSessionsThanNecessary() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        final Map<Long, SessionInfo> savedEntities = new HashMap<Long, SessionInfo>();

        AbstractStorage storage = new AbstractStorage() {

            public void saveOrUpdate(SessionInfo storedObject) {
                storedObject.update();
                savedEntities.put( storedObject.getId(), storedObject );
            }

            public SessionInfo findSessionInfo(Long id) {
                return savedEntities.get( id );
            }
        };

        StatefulKnowledgeSession crmPersistentSession = createSession(kbase, storage);

        long ksessionId = crmPersistentSession.getId();
        crmPersistentSession.fireAllRules();

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase, storage);

        Assert.assertEquals(ksessionId, crmPersistentSession.getId());

        ksessionId = crmPersistentSession.getId();
        crmPersistentSession.fireAllRules();

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase, storage);

        crmPersistentSession.fireAllRules();

        Assert.assertEquals(1, savedEntities.size());
        crmPersistentSession.dispose();
    }

    @Test
    public void insertObjectIntoKsessionAndRetrieve() {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        AbstractStorage storage = getSimpleStorage();
        
        StatefulKnowledgeSession crmPersistentSession = createSession(kbase, storage);
        Buddy bestBuddy = new Buddy("john");
        crmPersistentSession.insert(bestBuddy);

        crmPersistentSession = disposeAndReloadSession(crmPersistentSession, kbase, storage);
        Object obtainedBuddy = crmPersistentSession
                .getObjects().iterator().next();
        Assert.assertNotSame( bestBuddy, obtainedBuddy );
        Assert.assertEquals(bestBuddy, obtainedBuddy);

        crmPersistentSession.dispose();
    }

    private AbstractStorage getSimpleStorage() {
        return new AbstractStorage() {

            private Map<Long, SessionInfo> savedEntities = new HashMap<Long, SessionInfo>();
            
            public SessionInfo findSessionInfo(Long id) {
                return savedEntities.get( id );
            }

            public void saveOrUpdate(SessionInfo storedObject) {
                storedObject.update();
                savedEntities.put( storedObject.getId(), storedObject );
            }
        };
    }

    private StatefulKnowledgeSession createSession(KnowledgeBase kbase,
                                                   AbstractStorage storage) {
        
        AbstractStorageEnvironmentBuilder envBuilder = new AbstractStorageEnvironmentBuilder( storage );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        //FIXME temporary usage of this constants
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 envBuilder.getTransactionManager() );
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 envBuilder.getPersistenceContextManager() );

        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase,
                                                                null,
                                                                env );
    }

    private StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession,
                                                             KnowledgeBase kbase,
                                                             AbstractStorage storage) {
        long sessionId = ksession.getId();
        ksession.dispose();
        AbstractStorageEnvironmentBuilder envBuilder = new AbstractStorageEnvironmentBuilder( storage );
        Environment env = KnowledgeBaseFactory.newEnvironment();
        //FIXME temporary usage of this constants
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 envBuilder.getTransactionManager() );
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY,
                 envBuilder.getPersistenceContextManager() );
        
        return JPAKnowledgeService.loadStatefulKnowledgeSession( sessionId, kbase, null, env );
    }

}
