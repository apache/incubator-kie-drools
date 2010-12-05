package org.drools.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.drools.KnowledgeBase;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.FileManager;
import org.drools.core.util.IoUtils;
import org.drools.core.util.StringUtils;
import org.drools.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.AfterResourceProcessedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.drools.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.drools.event.knowledgeagent.ResourceCompilationFailedEvent;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ResourceHandler;

import junit.framework.TestCase;

public abstract class BaseKnowledgeAgentTest extends TestCase {
    FileManager     fileManager;
    Server           server;    
    ResourceChangeScannerImpl scanner;

    @Override
    protected void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();

        ResourceFactory.getResourceChangeNotifierService().start();
        
        // we don't start the scanner, as we call it manually;
        this.scanner = (ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService();

        this.server = new Server( IoUtils.findPort() );
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase( fileManager.getRootDirectory().getPath() );

        this.server.setHandler( resourceHandler );

        this.server.start();
    }
    
    @Override
    protected void tearDown() throws Exception {
        fileManager.tearDown();
        ResourceFactory.getResourceChangeNotifierService().stop();
        ((ResourceChangeNotifierImpl) ResourceFactory.getResourceChangeNotifierService()).reset();
        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();

        server.stop();
    } 
    


    public int getPort() {
        return this.server.getConnectors()[0].getLocalPort();
    }
    

    public void scan(KnowledgeAgent kagent) {
        // Calls the Resource Scanner and sets up a listener and a latch so we can wait until it's finished processing, instead of using timers
        final CountDownLatch latch = new CountDownLatch( 1 );
        
        KnowledgeAgentEventListener l = new KnowledgeAgentEventListener() {
            
            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
            }
            
            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
            }
            
            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
            }
            
            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {                              
            }
            
            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
            }
            
            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
            }
            
            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
            }
            
            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                latch.countDown();
            }
        };        
        
        kagent.addEventListener( l );
        
        this.scanner.scan();
        
        try {
            latch.await( 10, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( "Unable to wait for latch countdown", e);
        }
        
        if ( latch.getCount() > 0 ) {            
            throw new RuntimeException( "Event for KnowlegeBase update, due to scan, was never received" );
        }
        
        kagent.removeEventListener( l );
    }
    
    void applyChangeSet(KnowledgeAgent kagent, String xml) {
        // Calls the Resource Scanner and sets up a listener and a latch so we can wait until it's finished processing, instead of using timers
        final CountDownLatch latch = new CountDownLatch( 1 );
        
        KnowledgeAgentEventListener l = new KnowledgeAgentEventListener() {
            
            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
            }
            
            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
            }
            
            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
            }
            
            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {                              
            }
            
            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
            }
            
            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
            }
            
            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
            }
            
            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                latch.countDown();
            }
        };        
        
        kagent.addEventListener( l );
        
        kagent.applyChangeSet( ResourceFactory.newByteArrayResource( xml.getBytes() ) );
        
        try {
            latch.await( 10, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( "Unable to wait for latch countdown", e);
        }
        
        if ( latch.getCount() > 0 ) {            
            throw new RuntimeException( "Event for KnowlegeBase update, due to scan, was never received" );
        }
        
        kagent.removeEventListener( l );        
    }
    
    void applyChangeSet(KnowledgeAgent kagent, Resource r) {
        // Calls the Resource Scanner and sets up a listener and a latch so we can wait until it's finished processing, instead of using timers
        final CountDownLatch latch = new CountDownLatch( 1 );
        
        KnowledgeAgentEventListener l = new KnowledgeAgentEventListener() {
            
            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
            }
            
            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
            }
            
            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
            }
            
            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {                              
            }
            
            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
            }
            
            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
            }
            
            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
            }
            
            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
                latch.countDown();
            }
        };        
        
        kagent.addEventListener( l );
        
        kagent.applyChangeSet( r );
        
        try {
            latch.await( 10, TimeUnit.SECONDS );
        } catch ( InterruptedException e ) {
            throw new RuntimeException( "Unable to wait for latch countdown", e);
        }
        
        if ( latch.getCount() > 0 ) {            
            throw new RuntimeException( "Event for KnowlegeBase update, due to scan, was never received" );
        }
        
        kagent.removeEventListener( l );        
    }   
    

    public static void writePackage(Object pkg,
                                     File p1file )throws IOException, FileNotFoundException {
        if ( p1file.exists() ) {
            // we want to make sure there is a time difference for lastModified and lastRead checks as Linux and http often round to seconds
            // http://saloon.javaranch.com/cgi-bin/ubb/ultimatebb.cgi?ubb=get_topic&f=1&t=019789
            try {
                Thread.sleep( 1000 );
            } catch (Exception e) {
                throw new RuntimeException( "Unable to sleep" );
            }            
        }
        FileOutputStream out = new FileOutputStream( p1file );
        try {
            DroolsStreamUtils.streamOut( out,
                                         pkg );
        } finally {
            out.close();
        }
    }

    public KnowledgeAgent createKAgent(KnowledgeBase kbase) {
        return createKAgent( kbase, true );
    }
    public KnowledgeAgent createKAgent(KnowledgeBase kbase, boolean newInsatnce) {
        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
        aconf.setProperty( "drools.agent.scanDirectories",
                           "true" );
        aconf.setProperty( "drools.agent.scanResources",
                           "true" );
        aconf.setProperty( "drools.agent.newInstance",
                           Boolean.toString( newInsatnce ) );

        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent("test agent",
                                                                         kbase,
                                                                         aconf );

        assertEquals( "test agent",
                      kagent.getName() );

        return kagent;
    }
    
    
    public String createVersionedRule(String packageName, String ruleName, String attribute, String version) {        
        StringBuilder rule = new StringBuilder();
        if ( StringUtils.isEmpty( packageName ) ) {
            rule.append( "package org.drools.test\n" );
        } else {
            rule.append( "package " );
            rule.append( packageName );
            rule.append( "\n" );
        }
        rule.append( "global java.util.List list\n" );
        rule.append( "rule " );
        rule.append( ruleName );
        rule.append( "\n" );
        if ( !StringUtils.isEmpty( attribute ) ) {
            rule.append( attribute +"\n" );    
        }
        rule.append( "when\n" );
        rule.append( "then\n" );
        if ( StringUtils.isEmpty( version ) ) {
            rule.append( "list.add( drools.getRule().getName() );\n" );
        } else {
            rule.append("list.add( drools.getRule().getName()+\"-V" + version + "\");\n");
        }
        rule.append( "end\n" );

        return rule.toString();       
    }    
    
    public String createVersionedRule(String ruleName, String version) {
        return createVersionedRule( null, ruleName, null, version );
    }

    public String createDefaultRule(String name) {
        return createDefaultRule( name,
                                  null );
    }

    public String createDefaultRule(String ruleName,
                                    String packageName) {
        return createVersionedRule( null, ruleName, null, null );
    }  
    
    public String createAttributeRule(String ruleName,
                                      String attribute) {
        return createVersionedRule( null, ruleName, attribute, null );
    }     
}
