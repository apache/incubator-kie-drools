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
import javax.inject.Named;

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
public class CDINamedTest {
//    public static AbstractKnowledgeTest helper;
//    
//    @Inject
//    @KBase("jar1.KBase1")
//    @Named("kb1")
//    @KGAV(groupId    = "jar1", 
//          artifactId = "art1", 
//          version    = "1.0")
//    private KieBase jar1KBase1kb1;
//
//    @Inject
//    @KBase("jar1.KBase1")
//    @Named("kb2")
//    @KGAV(groupId    = "jar1", 
//          artifactId = "art1", 
//          version    = "1.0")
//    private KieBase jar1KBase1kb2;    
//
//    @Inject
//    @KBase("jar1.KBase1")
//    @Named("kb2")
//    @KGAV(groupId    = "jar1", 
//          artifactId = "art1", 
//          version    = "1.0")
//    private KieBase jar1KBase1kb22;   
//    
//    @Inject
//    @KSession("jar1.KSession1")  
//    @KGAV( groupId    = "jar1", 
//           artifactId = "art1", 
//           version    = "1.0" )
//    @Named("sks1")    
//    private StatelessKieSession kbase1ksession1sks1;
//    
//    @Inject
//    @KSession("jar1.KSession1")  
//    @KGAV( groupId    = "jar1", 
//           artifactId = "art1", 
//           version    = "1.0" )
//    @Named("sks2")    
//    private StatelessKieSession kbase1ksession1sks2  ;  
//    
//    @Inject
//    @KSession("jar1.KSession1")  
//    @KGAV( groupId    = "jar1", 
//           artifactId = "art1", 
//           version    = "1.0" )
//    @Named("sks2")      
//    private StatelessKieSession kbase1ksession1sks22;
//    
//    @Inject
//    @KSession("jar1.KSession2")  
//    @KGAV( groupId    = "jar1", 
//           artifactId = "art1", 
//           version    = "1.0" )
//    @Named("ks1")    
//    private KieSession kbase1ksession2ks1;
//    
//    @Inject
//    @KSession("jar1.KSession2")  
//    @KGAV( groupId    = "jar1", 
//           artifactId = "art1", 
//           version    = "1.0" )
//    @Named("ks2")    
//    private KieSession kbase1ksession2ks2  ;  
//    
//    @Inject
//    @KSession("jar1.KSession2")  
//    @KGAV( groupId    = "jar1", 
//           artifactId = "art1", 
//           version    = "1.0" )
//    @Named("ks2")      
//    private KieSession kbase1ksession2ks22;    
//    
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
//        CDITestRunner.weld = CDITestRunner.createWeld( CDINamedTest.class.getName()  );
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
//    public void testNamedKieBases() throws IOException, ClassNotFoundException, InterruptedException {
//        assertNotNull(jar1KBase1kb1);
//        assertNotNull(jar1KBase1kb2);
//        assertNotNull(jar1KBase1kb22);
//        
//        assertNotSame(jar1KBase1kb1, jar1KBase1kb2);
//        assertSame( jar1KBase1kb2, jar1KBase1kb22);      
//    }
//    
//    @Test
//    public void testNamedStatelessKieSessionGAV() {
//        assertNotNull(kbase1ksession1sks1);
//        assertNotNull(kbase1ksession1sks2);
//        assertNotNull(kbase1ksession1sks22);
//        
//        assertNotSame(kbase1ksession1sks1, kbase1ksession1sks2);
//        assertSame( kbase1ksession1sks2, kbase1ksession1sks22);        
//    }
//    
//    @Test
//    public void testNamedKieSessionGAV() {
//        assertNotNull(kbase1ksession2ks1);
//        assertNotNull(kbase1ksession2ks2);
//        assertNotNull(kbase1ksession2ks22);
//        
//        assertNotSame(kbase1ksession2ks1, kbase1ksession2ks2);
//        assertSame( kbase1ksession2ks2, kbase1ksession2ks22);        
//    }     

}
