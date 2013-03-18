package org.drools.compiler.cdi;

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

import org.drools.compiler.kproject.AbstractKnowledgeTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KReleaseId;
import org.kie.runtime.KieSession;

@RunWith(CDITestRunner.class)
public class KieBaseInjectionTest {
    public static AbstractKnowledgeTest helper;  
    
    @Inject
    @KBase("jar1.KBase1") 
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0")
    private KieBase jar1KBase1v10;
    
    @Inject
    @KBase("jar1.KBase1") 
    @KReleaseId(groupId    = "jar1",
                artifactId = "art1", 
                version    = "1.1")
    private KieBase jar1KBase1v11;      
    
    @Inject
    @KBase(value="jar1.KBase1", name="kb1")
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0")
    private KieBase jar1KBase1kb1;

    @Inject  
    @KBase(value="jar1.KBase1", name="kb2")    
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0")
    private KieBase jar1KBase1kb2;    

    @Inject
    @KBase(value="jar1.KBase1", name="kb2")    
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0")
    private KieBase jar1KBase1kb22;      
    
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

        CDITestRunner.weld = CDITestRunner.createWeld( KieBaseInjectionTest.class.getName() );

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
    public void testDynamicKieBaseReleaseId() throws IOException, ClassNotFoundException, InterruptedException {
        assertNotNull( jar1KBase1v10 );
        assertNotNull( jar1KBase1v11 );
        
        KieSession kSession = jar1KBase1v10.newKieSession();
        List<String> list = new ArrayList<String>();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.0" ) );
        assertTrue( list.get(1).endsWith( "1.0" ) );

        
        kSession = jar1KBase1v11.newKieSession();
        list = new ArrayList<String>();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.1" ) );
        assertTrue( list.get(1).endsWith( "1.1" ) );        
    }    
    
    @Test
    public void testNamedKieBases() throws IOException, ClassNotFoundException, InterruptedException {
        assertNotNull(jar1KBase1kb1);
        assertNotNull(jar1KBase1kb2);
        assertNotNull(jar1KBase1kb22);
        
        assertNotSame(jar1KBase1kb1, jar1KBase1kb2);
        assertSame( jar1KBase1kb2, jar1KBase1kb22);
        
        KieSession kSession = jar1KBase1kb1.newKieSession();
        List<String> list = new ArrayList<String>();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.0" ) );
        assertTrue( list.get(1).endsWith( "1.0" ) );

        
        kSession = jar1KBase1kb2.newKieSession();
        list = new ArrayList<String>();
        kSession.setGlobal( "list", list );
        kSession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.0" ) );
        assertTrue( list.get(1).endsWith( "1.0" ) );        
    }   
          
}
