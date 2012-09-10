package org.drools.agent;

import org.drools.ChangeSet;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Message;
import org.drools.builder.*;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EqualityKey;
import org.drools.common.LogicalDependency;
import org.drools.common.SimpleLogicalDependency;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.core.util.*;
import org.drools.definition.KnowledgePackage;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.io.impl.FileSystemResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * JBRULES 3543 KnowledgeAgent: Doesn't remove insertLogical() Facts correctly on incremental build
 */
public class KnowledgeAgentIncrementalWithTMSTest extends BaseKnowledgeAgentTest {

    private KnowledgeAgentConfiguration agentConfig;
    private KnowledgeAgent kAgent;
    private StatefulKnowledgeSession ksession;
    private File res;
    private FileManager fileManager = new FileManager();



    @Test
    public void testKnowledgeAgentIncrementalWithTMS() {
        try {
            createRuleResource();

            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            KnowledgeAgent kAgent = createKAgent( kbase, false );

            ChangeSetHelperImpl cs = new ChangeSetHelperImpl();
            FileSystemResource f = (FileSystemResource) ResourceFactory.newFileResource(res);
            f.setResourceType( ResourceType.DRL );
            cs.addNewResource( f );
            kAgent.applyChangeSet( cs.getChangeSet() );

            ksession = kAgent.getKnowledgeBase().newStatefulKnowledgeSession();

            TruthMaintenanceSystem tms = ((StatefulKnowledgeSessionImpl)ksession).session.getTruthMaintenanceSystem();


            Message message = new Message( );
            message.setMessage("Hello World");
            message.setStatus(Message.HELLO);
            FactHandle fact = ksession.insert(message);

            Message message2 = new Message();
            message2.setMessage("Hello World 2");
            message2.setStatus(Message.HELLO);
            FactHandle fact2 = ksession.insert(message2);

            Message message3 = new Message();
            message3.setMessage("Hello World 3");
            message3.setStatus(Message.HELLO);
            FactHandle fact3 = ksession.insert(message3);

            ksession.fireAllRules();

            assertEquals( 12, ksession.getObjects().size() );

            ChangeSetHelperImpl cs2 = new ChangeSetHelperImpl();
            cs2.addRemovedResource( ResourceFactory.newFileResource( res ) );

            kAgent.applyChangeSet( cs2.getChangeSet() );

            KnowledgePackage kp = kAgent.getKnowledgeBase().getKnowledgePackage( "com.sample" );
            assertEquals(0, kp.getRules().size());

            ksession.fireAllRules();

            assertEquals(3, ksession.getObjects().size() );

            //logger.close();

            ksession.dispose();
            kAgent.dispose();
        } catch ( Throwable t ) {
            t.printStackTrace();
            fail( t.getMessage() );
        }

    }

    private void reportDependencies( TruthMaintenanceSystem tms, int expectedNum ) {
        ObjectHashMap jhm = tms.getJustifiedMap();
        for ( Object o : ksession.getObjects() ) {
            EqualityKey key = tms.get( o );
            if ( Object.class.equals(o.getClass()) ) {
                assertEquals( EqualityKey.JUSTIFIED, key.getStatus() );
                DefaultFactHandle handle = (DefaultFactHandle) ksession.getFactHandle( o );
                List<LogicalDependency> justifiers = collectJustifiers( handle, jhm );

                assertEquals( expectedNum, justifiers.size() );

                System.out.println( "-------------------------------");
                System.out.println( "Justified Object " + o );
                System.out.println( "\t\t " + justifiers );
                System.out.println( "-------------------------------");

            } else if ( Message.class.equals( o.getClass() ) ) {
                assertEquals( EqualityKey.STATED, key.getStatus() );
            }
        }
    }

    private List<LogicalDependency> collectJustifiers(DefaultFactHandle handle, ObjectHashMap jhm) {
        List<LogicalDependency> justifiers = new ArrayList<LogicalDependency>();
        LinkedList entryList = (LinkedList) jhm.get( handle.getId() );
        Iterator sub = entryList.iterator();
        LinkedListEntry lle;
        while ( ( lle = (LinkedListEntry) sub.next() ) != null ) {
            LogicalDependency dep = (LogicalDependency) lle.getObject();
            justifiers.add( dep );
        }

        return justifiers;
    }




    private void createRuleResource() {
        String ruleString = "package com.sample\n" +
                " \n" +
                "import org.drools.Message;\n" +
                " \n" +
                "rule \"Hello World\"\n" +
                "    when\n" +
                "        Message()\n" +
                "        Message(status == Message.HELLO)\n" +
                "    then\n" +
                "        insertLogical( new Object() );\n" +
                "end";
        try {
            res = fileManager.write( "rule.drl", ruleString );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeRuleResource() {
        fileManager.deleteFile(res);
    }


}