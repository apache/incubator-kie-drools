package org.drools.cdi;

import javax.inject.Inject;

import org.drools.kproject.AbstractKnowledgeTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.KieBase;
import org.kie.builder.KieContainer;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.cdi.KBase;
import org.kie.cdi.KGAV;
import org.kie.cdi.KSession;
import org.kie.command.KieCommands;
import org.kie.io.KieResources;
import org.kie.runtime.KieSession;

import static org.junit.Assert.*;

@RunWith(CDITestRunner.class)
public class KieServicesInjectionTest {
    
    @Inject
    KieServices   sc;

    @Inject
    KieRepository kr;

    @Inject
    KieCommands   cmds;
    
    @Inject
    KieResources  rscs;
    
    @BeforeClass
    public static void beforeClass() {          
        CDITestRunner.weld = CDITestRunner.createWeld( KieServicesInjectionTest.class.getName()  );        
        CDITestRunner.container = CDITestRunner.weld.initialize();
    }

    @AfterClass
    public static void afterClass() {
        if ( CDITestRunner.weld != null ) { 
            CDITestRunner.weld.shutdown();
         
            CDITestRunner.weld = null;
        }
        if ( CDITestRunner.container != null ) {
            CDITestRunner.container = null; 
        }
    }     
    
    @Test
    public void testKieServicesInjection() {
        assertNotNull( sc );
        assertNotNull( sc.getResources().newByteArrayResource( new byte[] {0} ) );
    }

    
    @Test
    public void testKieRepositoryInjection() {
        assertNotNull( kr );
        assertNotNull( kr.getDefaultGAV() );
    }
    
    
    @Test
    public void testKieCommands() {
        assertNotNull( cmds );
        assertNotNull( cmds.newFireAllRules());
    }
    
    
    @Test
    public void testKieResources() {
        assertNotNull( rscs );
        assertNotNull( rscs.newByteArrayResource( new byte[] {0} ) );
    }   
    
    
}
