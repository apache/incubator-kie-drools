package org.drools.vsm;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import junit.framework.TestCase;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseProvider;
import org.drools.SystemEventListenerFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderProvider;
import org.drools.builder.ResourceType;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.vsm.MessageHandler;
import org.drools.vsm.ServiceManagerClient;
import org.drools.vsm.ServiceManagerServer;

public class ServiceManagerTest extends TestCase {
	ServiceManagerServer server;
    ServiceManagerClient client;

    @Override
    protected void setUp() throws Exception {
    	this.server = new ServiceManagerServer();
	    Thread thread = new Thread( server );
	    thread.start();
	    Thread.sleep( 500 );  
	    	
    	this.client = new ServiceManagerClient( "client 1",
    			     							new MessageHandler(SystemEventListenerFactory.getSystemEventListener()) );
    	
      NioSocketConnector connector = new NioSocketConnector();
      SocketAddress address = new InetSocketAddress( "127.0.0.1",
                                                     9123 );
      client.connect( connector,
                      address );
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
        server.stop();
    }
    
    public void testFireAllRules() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello1!!!\" ); \n";
        str += "end \n";
        str += "rule rule2 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello2!!!\" ); \n";
        str += "end \n"  ;      
        
    	KnowledgeBuilderProvider kbuilderFactory = this.client.getKnowledgeBuilderFactory();
    	KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
    	kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
    	
    	if ( kbuilder.hasErrors() ) {
    	    System.out.println( "Errors: " + kbuilder.getErrors() );
    	}
    	
    	KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
    	KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();
    	
    	kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
    	
    	StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    	int fired = ksession.fireAllRules();
    	assertEquals( 2, fired );
    }
    
    public void testExecute() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello1!!!\" ); \n";
        str += "end \n";
        str += "rule rule2 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello2!!!\" ); \n";
        str += "end \n"  ;      
        
        KnowledgeBuilderProvider kbuilderFactory = this.client.getKnowledgeBuilderFactory();
        KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            System.out.println( "Errors: " + kbuilder.getErrors() );
        }
        
        KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
        KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();
        
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        ExecutionResults results = ksession.execute( new FireAllRulesCommand( "fired" ) );
       
        assertEquals( 2, (int ) ( Integer) results.getValue( "fired" ) );
    }   
    
    public void testNamedService() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello1!!!\" ); \n";
        str += "end \n";
        str += "rule rule2 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello2!!!\" ); \n";
        str += "end \n"  ;      
        
        KnowledgeBuilderProvider kbuilderFactory = this.client.getKnowledgeBuilderFactory();
        KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            System.out.println( "Errors: " + kbuilder.getErrors() );
        }
        
        KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
        KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();
        
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        this.client.register( "ksession1", ksession );
        
        ExecutionResults results = this.client.lookup( "ksession1" ).execute( new FireAllRulesCommand( "fired" ) );
       
        assertEquals( 2, (int ) ( Integer) results.getValue( "fired" ) );
    }      

    public void testVsmPipeline() throws Exception {
        String str = "";
        str += "package org.drools \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello1!!!\" ); \n";
        str += "end \n";
        str += "rule rule2 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "then \n";
        str += "    System.out.println( \"hello2!!!\" ); \n";
        str += "end \n"  ;      
        
        KnowledgeBuilderProvider kbuilderFactory = this.client.getKnowledgeBuilderFactory();
        KnowledgeBuilder kbuilder = kbuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            System.out.println( "Errors: " + kbuilder.getErrors() );
        }
        
        KnowledgeBaseProvider kbaseFactory = this.client.getKnowledgeBaseFactory();
        KnowledgeBase kbase = kbaseFactory.newKnowledgeBase();
        
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        this.client.register( "ksession1", ksession );
        
//        ExecutionResults results = this.client.lookup( "ksession1" ).execute( new FireAllRulesCommand( "fired" ) );
//       
//        assertEquals( 2, (int ) ( Integer) results.getValue( "fired" ) );
    } 
}
