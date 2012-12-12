package org.drools.cdi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.drools.cdi.example.CDIExamplesTest;
import org.drools.cdi.example.Message;
import org.drools.cdi.example.Message2;
import org.drools.cdi.example.Message2Impl1;
import org.drools.cdi.example.Message2Impl2;
import org.drools.cdi.example.MessageImpl;
import org.drools.cdi.example.MessageProducers;
import org.drools.cdi.example.MessageProducers2;
import org.drools.cdi.example.Msg;
import org.drools.cdi.example.Msg1;
import org.drools.cdi.example.Msg2;
import org.drools.kproject.AbstractKnowledgeTest;
import org.drools.kproject.KPTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.KieBase;
import org.kie.KieServices;
import org.kie.KnowledgeBase;
import org.kie.builder.KieRepository;
import org.kie.cdi.KBase;
import org.kie.cdi.KGAV;
import org.kie.cdi.KSession;
import org.kie.command.KieCommands;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;

import static org.junit.Assert.*;

@RunWith(CDITestRunner.class)
@Ignore
public class CDIGAVTest {
//    public static AbstractKnowledgeTest helper;
//   
//    @Inject
//    @KBase("jar1.KBase1") @KGAV(groupId    = "jar1", 
//                                artifactId = "art1", 
//                                version    = "1.0")
//    private KieBase jar1KBase1v10;
//    
//    @Inject
//    @KBase("jar1.KBase1") @KGAV(groupId    = "jar1", 
//                                artifactId = "art1", 
//                                version    = "1.1")
//    private KieBase jar1KBase1v11;    
//
//    @Inject
//    @KSession("jar1.KSession1")  @KGAV( groupId    = "jar1", 
//                                        artifactId = "art1", 
//                                        version    = "1.0" )
//    private StatelessKieSession kbase1ksession1v10;
//    
//    @Inject
//    @KSession("jar1.KSession1")  @KGAV( groupId    = "jar1", 
//                                        artifactId = "art1", 
//                                        version    = "1.1" )
//    private StatelessKieSession kbase1ksession1v11; 
//    
//    @Inject
//    @KSession("jar1.KSession2")  @KGAV( groupId    = "jar1", 
//                                        artifactId = "art1", 
//                                        version    = "1.0" )
//    private KieSession kbase1ksession2v10;
//    
//    @Inject
//    @KSession("jar1.KSession2")  @KGAV( groupId    = "jar1", 
//                                        artifactId = "art1", 
//                                        version    = "1.1" )
//    private KieSession kbase1ksession2v11;    
//    
//    @BeforeClass
//    public static void beforeClass() {  
//        helper = new  AbstractKnowledgeTest();        
//        try {
//            helper.setUp();
//        } catch ( Exception e ) {
//            e.printStackTrace();     
//            fail( e.getMessage() ); 
//        }
//        try {
//            helper.createKieModule( "jar1", true, "1.0" );
//            helper.createKieModule( "jar1", true, "1.1" );
//        } catch (Exception e) {
//            e.printStackTrace();            
//            fail( "Unable to build dynamic KieModules:\n" + e.toString() );
//        }        
//        
//        CDITestRunner.weld = CDITestRunner.createWeld( CDIGAVTest.class.getName()  );
//        
//        CDITestRunner.container = CDITestRunner.weld.initialize();
//    }
//
//    @AfterClass
//    public static void afterClass() {
//        if ( CDITestRunner.weld != null ) { 
//            CDITestRunner.weld.shutdown();
//         
//            CDITestRunner.weld = null;
//        }
//        if ( CDITestRunner.container != null ) {
//            CDITestRunner.container = null; 
//        }
//        
//        try {
//            helper.tearDown();
//        } catch ( Exception e ) {
//            fail( e.getMessage() );
//        }
//    } 
//
//    @Test
//    public void testDynamicKieBaseGAV() throws IOException, ClassNotFoundException, InterruptedException {
//        assertNotNull( jar1KBase1v10 );
//        assertNotNull( jar1KBase1v11 );
//        
//        KieSession kSession = jar1KBase1v10.newKieSession();
//        List<String> list = new ArrayList<String>();
//        kSession.setGlobal( "list", list );
//        kSession.fireAllRules();
//        
//        assertEquals( 2, list.size() );
//        assertTrue( list.get(0).endsWith( "1.0" ) );
//        assertTrue( list.get(1).endsWith( "1.0" ) );
//
//        
//        kSession = jar1KBase1v11.newKieSession();
//        list = new ArrayList<String>();
//        kSession.setGlobal( "list", list );
//        kSession.fireAllRules();
//
//        assertEquals( 2, list.size() );
//        assertTrue( list.get(0).endsWith( "1.1" ) );
//        assertTrue( list.get(1).endsWith( "1.1" ) );        
//    }
//    
//    @Test
//    public void testDynamicStatelessKieSessionGAV() {
//        KieCommands cmds  = KieServices.Factory.get().getCommands();
//        
//        List<String> list = new ArrayList<String>();
//        kbase1ksession1v10.setGlobal( "list", list );
//        kbase1ksession1v10.execute( cmds.newFireAllRules() );
//        
//        assertEquals( 2, list.size() );
//        assertTrue( list.get(0).endsWith( "1.0" ) );
//        assertTrue( list.get(1).endsWith( "1.0" ) );
//        
//        list = new ArrayList<String>();
//        kbase1ksession1v11.setGlobal( "list", list );
//        kbase1ksession1v11.execute( cmds.newFireAllRules() );
//        
//        assertEquals( 2, list.size() );
//        assertTrue( list.get(0).endsWith( "1.1" ) );
//        assertTrue( list.get(1).endsWith( "1.1" ) );         
//    }    
//
//    @Test
//    public void testDynamicKieSessionGAV() {
//        List<String> list = new ArrayList<String>();
//        kbase1ksession2v10.setGlobal( "list", list );
//        kbase1ksession2v10.fireAllRules();
//        
//        assertEquals( 2, list.size() );
//        assertTrue( list.get(0).endsWith( "1.0" ) );
//        assertTrue( list.get(1).endsWith( "1.0" ) );
//        
//        list = new ArrayList<String>();
//        kbase1ksession2v11.setGlobal( "list", list );
//        kbase1ksession2v11.fireAllRules();
//        
//        assertEquals( 2, list.size() );
//        assertTrue( list.get(0).endsWith( "1.1" ) );
//        assertTrue( list.get(1).endsWith( "1.1" ) );         
//    }


}
