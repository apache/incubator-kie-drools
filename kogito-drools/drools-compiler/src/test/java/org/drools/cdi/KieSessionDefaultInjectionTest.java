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
import javax.inject.Named;

import org.drools.kproject.AbstractKnowledgeTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.KieBase;
import org.kie.cdi.KBase;
import org.kie.cdi.KReleaseId;
import org.kie.runtime.KieSession;

@RunWith(CDITestRunner.class)
public class KieSessionDefaultInjectionTest {
    public static AbstractKnowledgeTest helper;
    
    @Inject
    private KieSession defaultClassPathKSession;

    @Inject
    @KReleaseId( groupId    = "jar1",
                 artifactId = "art1", 
                 version    = "1.0")    
    private KieSession defaultDynamicKSession;            
    
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
            helper.createKieModule( "jar2", true, "2.0" );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( "Unable to build dynamic KieModules:\n" + e.toString() );
        }

        CDITestRunner.setUp( helper.getFileManager().newFile( "jar2-2.0.jar" ) );
        CDITestRunner.setUp( );

        CDITestRunner.weld = CDITestRunner.createWeld( KieSessionDefaultInjectionTest.class.getName() );

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
    public void tessDefaultClassPathKBase() throws IOException, ClassNotFoundException, InterruptedException {
        assertNotNull( defaultClassPathKSession );
        
        List<String> list = new ArrayList<String>();
        defaultClassPathKSession.setGlobal( "list", list );
        defaultClassPathKSession.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "2.0" ) );
        assertTrue( list.get(1).endsWith( "2.0" ) );        
    }
    
    @Test    
    public void tessDefaultDynamicKBase() throws IOException, ClassNotFoundException, InterruptedException {
        assertNotNull( defaultDynamicKSession );
                
        List<String> list = new ArrayList<String>();
        defaultDynamicKSession.setGlobal( "list", list );
        defaultDynamicKSession .fireAllRules();
        
        assertEquals( 2, list.size() );
        assertTrue( list.get(0).endsWith( "1.0" ) );
        assertTrue( list.get(1).endsWith( "1.0" ) );        
    }  
          
}
