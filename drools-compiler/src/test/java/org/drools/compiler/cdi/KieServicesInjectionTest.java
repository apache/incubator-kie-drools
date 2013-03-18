package org.drools.compiler.cdi;

import javax.inject.Inject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.command.KieCommands;
import org.kie.io.KieResources;

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
        CDITestRunner.setUp();
        CDITestRunner.weld = CDITestRunner.createWeld( KieServicesInjectionTest.class.getName()  );        
        CDITestRunner.container = CDITestRunner.weld.initialize();
    }


    @AfterClass
    public static void afterClass() {
        CDITestRunner.tearDown();
    }   
    
    @Test
    public void testKieServicesInjection() {
        assertNotNull( sc );
        assertNotNull( sc.getResources().newByteArrayResource( new byte[] {0} ) );
    }

    
    @Test
    public void testKieRepositoryInjection() {
        assertNotNull( kr );
        assertNotNull( kr.getDefaultReleaseId() );
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
