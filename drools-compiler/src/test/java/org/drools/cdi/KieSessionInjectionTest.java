package org.drools.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.drools.kproject.AbstractKnowledgeTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.cdi.KReleaseId;
import org.kie.cdi.KSession;
import org.kie.runtime.KieSession;

@RunWith(CDITestRunner.class)
public class KieSessionInjectionTest {
    public static AbstractKnowledgeTest helper;
    
    @Inject
    @KSession("jar1.KSession2")  
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0" )
    private KieSession kbase1ksession2v10;
    
    @Inject
    @KSession("jar1.KSession2")  
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.1" )
    private KieSession kbase1ksession2v11;      
    
    @Inject
    @KSession(value="jar1.KSession2", name="ks1")    
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0" )
    private KieSession kbase1ksession2ks1;
    
    @Inject
    @KSession(value="jar1.KSession2", name="ks2")  
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0" )
    private KieSession kbase1ksession2ks2  ;  
    
    @Inject
    @KSession(value="jar1.KSession2", name="ks2")  
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0" )
    private KieSession kbase1ksession2ks22;        
    
    @BeforeClass
    public static void beforeClass() {  
        helper = new AbstractKnowledgeTest();
        try {
            helper.setUp();
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        try {
            helper.createKieModule( "jar1", true, "1.0" );
            helper.createKieModule( "jar1", true, "1.1" );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unable to build dynamic KieModules:\n" + e.toString() );
        }

        CDITestRunner.setUp( );

        CDITestRunner.weld = CDITestRunner.createWeld( KieSessionInjectionTest.class.getName() );

        CDITestRunner.container = CDITestRunner.weld.initialize();
    }

    @AfterClass
    public static void afterClass() {
        CDITestRunner.tearDown();
        
        try {
            helper.tearDown();
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
    }     
    
    @Test
    public void testDynamicKieSessionReleaseId() throws IOException, ClassNotFoundException, InterruptedException {
        assertNotNull( kbase1ksession2v10 );
        assertNotNull( kbase1ksession2v10 );
        
        List<String> list = new ArrayList<String>();
        kbase1ksession2v10.setGlobal( "list", list );
        kbase1ksession2v10.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.0" ) );
        assertTrue( list.get(1).endsWith( "1.0" ) );
        
        list = new ArrayList<String>();
        kbase1ksession2v11.setGlobal( "list", list );
        kbase1ksession2v11.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.1" ) );
        assertTrue( list.get(1).endsWith( "1.1" ) );        
    }    
    
    @Test
    public void testNamedKieSessions() throws IOException, ClassNotFoundException, InterruptedException {
        assertNotNull(kbase1ksession2ks1);
        assertNotNull(kbase1ksession2ks2);
        assertNotNull(kbase1ksession2ks22);
        
        assertNotSame(kbase1ksession2ks1, kbase1ksession2ks2);
        assertSame( kbase1ksession2ks2, kbase1ksession2ks22);     
        
        List<String> list = new ArrayList<String>();
        kbase1ksession2ks1.setGlobal( "list", list );
        kbase1ksession2ks1.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.0" ) );
        assertTrue( list.get(1).endsWith( "1.0" ) );
        
        list = new ArrayList<String>();
        kbase1ksession2ks2.setGlobal( "list", list );
        kbase1ksession2ks2.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.0" ) );
        assertTrue( list.get(1).endsWith( "1.0" ) );          
    }   
          
}
