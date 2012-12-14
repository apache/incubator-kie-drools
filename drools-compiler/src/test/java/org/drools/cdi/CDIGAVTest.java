package org.drools.cdi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.drools.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.FileManager;
import org.drools.kproject.AbstractKnowledgeTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.KieBase;
import org.kie.KieServices;
import org.kie.cdi.KBase;
import org.kie.cdi.KReleaseId;
import org.kie.cdi.KSession;
import org.kie.command.KieCommands;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;

import static org.junit.Assert.*;

@RunWith(CDITestRunner.class)
public class CDIGAVTest {
    public static AbstractKnowledgeTest helper;
   
    @Inject
    @KBase("jar1.KBase1") @KReleaseId(groupId    = "jar1",
                                      artifactId = "art1", 
                                      version    = "1.0")
    private KieBase jar1KBase1v10;
    
    @Inject
    @KBase("jar1.KBase1") @KReleaseId(groupId    = "jar1",
                                      artifactId = "art1", 
                                      version    = "1.1")
    private KieBase jar1KBase1v11;    

    @Inject
    @KSession("jar1.KSession1")  @KReleaseId( groupId    = "jar1",
                                              artifactId = "art1", 
                                              version    = "1.0" )
    private StatelessKieSession kbase1ksession1v10;
    
    @Inject
    @KSession("jar1.KSession1")  @KReleaseId( groupId    = "jar1",
                                              artifactId = "art1", 
                                              version    = "1.1" )
    private StatelessKieSession kbase1ksession1v11; 
    
    @Inject
    @KSession("jar1.KSession2")  @KReleaseId( groupId    = "jar1",
                                              artifactId = "art1", 
                                              version    = "1.0" )
    private KieSession kbase1ksession2v10;
    
    @Inject
    @KSession("jar1.KSession2")  @KReleaseId( groupId    = "jar1",
                                              artifactId = "art1", 
                                              version    = "1.1" )
    private KieSession kbase1ksession2v11;    
    
    @BeforeClass
    public static void beforeClass() {  
        helper = new  AbstractKnowledgeTest();        
        try {
            helper.setUp();
        } catch ( Exception e ) {
            e.printStackTrace();     
            fail( e.getMessage() ); 
        }
        try {
            helper.createKieModule( "jar1", true, "1.0" );
            helper.createKieModule( "jar1", true, "1.1" );
        } catch (Exception e) {
            e.printStackTrace();            
            fail( "Unable to build dynamic KieModules:\n" + e.toString() );
        }        
        
        CDITestRunner.setUp();             
        
        CDITestRunner.weld = CDITestRunner.createWeld( CDIGAVTest.class.getName()  );
        
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
    public void testDynamicKieBaseGAV() throws IOException, ClassNotFoundException, InterruptedException {
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
    public void testDynamicStatelessKieSessionGAV() {
        KieCommands cmds  = KieServices.Factory.get().getCommands();
        
        List<String> list = new ArrayList<String>();
        kbase1ksession1v10.setGlobal( "list", list );
        kbase1ksession1v10.execute( cmds.newFireAllRules() );
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.0" ) );
        assertTrue( list.get(1).endsWith( "1.0" ) );
        
        list = new ArrayList<String>();
        kbase1ksession1v11.setGlobal( "list", list );
        kbase1ksession1v11.execute( cmds.newFireAllRules() );
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.1" ) );
        assertTrue( list.get(1).endsWith( "1.1" ) );         
    }    

    @Test
    public void testDynamicKieSessionGAV() {
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


}
