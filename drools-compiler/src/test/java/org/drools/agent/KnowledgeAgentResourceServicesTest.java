package org.drools.agent;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.conf.NewInstanceOption;
import org.drools.agent.conf.UseKnowledgeBaseClassloaderOption;
import org.drools.agent.impl.PrintStreamSystemEventListener;
import org.drools.conf.EventProcessingOption;
import org.drools.event.knowledgeagent.AfterResourceProcessedEvent;
import org.drools.event.rule.DebugKnowledgeAgentEventListener;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class KnowledgeAgentResourceServicesTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public File kFolder;

    private StatefulKnowledgeSession knowledgeSession;
    private KnowledgeAgent kagent;
    private CountingEventListener listener;

    @Before
    public void setup() {
        ResourceChangeScannerConfiguration conf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        conf.setProperty( "drools.resource.scanner.interval", "1" );
        ResourceFactory.getResourceChangeScannerService().configure(conf);

        ResourceFactory.getResourceChangeNotifierService().start();
        ResourceFactory.getResourceChangeScannerService().start();


        KnowledgeBaseConfiguration kbaseConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        kbaseConf.setOption( EventProcessingOption.STREAM );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbaseConf );

        KnowledgeAgentConfiguration agentConf= KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        agentConf.setProperty( NewInstanceOption.PROPERTY_NAME, "false" );
        agentConf.setProperty( UseKnowledgeBaseClassloaderOption.PROPERTY_NAME, "true" );

        kagent = KnowledgeAgentFactory.newKnowledgeAgent( "MyAgent", kbase, agentConf );
        listener = new CountingEventListener();
        kagent.addEventListener( listener );

        kFolder = folder.newFolder( "knowledge" );
        String xml = "";
        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
        xml += "    <add> ";
        xml += "        <resource source='file:" + kFolder.getAbsolutePath() + "' type='DRL' />";
        xml += "    </add> ";
        xml += "</change-set>";


        kagent.applyChangeSet( ResourceFactory.newByteArrayResource( xml.getBytes() ) );

        knowledgeSession = kagent.getKnowledgeBase().newStatefulKnowledgeSession( );
        knowledgeSession.fireAllRules();

    }


    @Test
    public void testSequence() throws InterruptedException {

        Thread.sleep( 1500 );

        try {
            File f = new File( kFolder.getPath() + File.separator + "know1.drl" );
            FileOutputStream fos = new FileOutputStream( f );
            fos.write( drl1.getBytes() );
            fos.flush();
            fos.close();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }

        Thread.sleep( 1500 );

        knowledgeSession.fireAllRules();

        try {
            File f1 = new File( kFolder.getPath() + File.separator + "know4.drl" );
            FileOutputStream fos1 = new FileOutputStream( f1 );
            fos1.write( drl2.getBytes() );
            fos1.flush();
            fos1.close();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }

        Thread.sleep( 1500 );

        knowledgeSession.fireAllRules();

        assertEquals( 1, knowledgeSession.getObjects().size() );

    }



    @Test
    public void testSequenceWithRemoval() throws InterruptedException {

        Thread.sleep( 1500 );

        try {
            File f = new File( kFolder.getPath() + File.separator + "know1.drl" );
            FileOutputStream fos = new FileOutputStream( f );
            fos.write( drl1.getBytes() );
            fos.flush();
            fos.close();

            Thread.sleep( 1500 );

            knowledgeSession.fireAllRules();

            File f2 = new File( kFolder.getPath() + File.separator + "know2.drl" );
            FileOutputStream fos2 = new FileOutputStream( f2 );
            fos2.write( drl1.getBytes() );
            fos2.flush();
            fos2.close();

            Thread.sleep( 1500 );

            knowledgeSession.fireAllRules();

            f.delete();

            Thread.sleep( 1500 );

            knowledgeSession.fireAllRules();

            f2.delete();

            Thread.sleep( 1500 );

            knowledgeSession.fireAllRules();

        } catch ( IOException ioe ) {
            ioe.printStackTrace();
        }

        assertEquals( 3, listener.additions );
        assertEquals( 2, listener.removals );
        assertEquals( 0, listener.modifications );

    }



    @After
    public void tearDown() {
        knowledgeSession.dispose();
        kagent.dispose();
        ResourceFactory.getResourceChangeScannerService().stop();
        ResourceFactory.getResourceChangeNotifierService().stop();
    }


    String drl1 = "package org.drools.test\n" +
                  "\n" +
                  "declare Foo\n" +
                  "\t@role( event )\n" +
                  "end\n" +
                  "";

    String drl2 = "package org.drools.test\n" +
                  "\n" +
                  "\n" +
                  "declare Bar \n" +
                  "end \n" +
                  "" +
                  "rule \"Rule\"\n" +
                  "  when \n" +
                  "  then\n" +
                  "    insert( new Foo() ); \n" +
                  "end";


    private class CountingEventListener extends DebugKnowledgeAgentEventListener {
        public int additions;
        public int removals;
        public int modifications;

        public void afterResourceProcessed( AfterResourceProcessedEvent event ) {
            switch ( event.getStatus() ) {
                case RESOURCE_ADDED: additions++; break;
                case RESOURCE_REMOVED: removals++; break;
                case RESOURCE_MODIFIED: modifications++; break;
            }
            super.afterResourceProcessed( event );
        }
    }
}
