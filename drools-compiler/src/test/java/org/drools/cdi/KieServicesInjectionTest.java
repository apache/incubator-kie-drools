package org.drools.cdi;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.builder.KieContainer;
import org.kie.builder.KieRepository;
import org.kie.builder.KieServices;
import org.kie.cdi.KBase;
import org.kie.cdi.KGAV;
import org.kie.cdi.KSession;
import org.kie.command.KieCommands;
import org.kie.io.KieResources;
import org.kie.runtime.KieBase;
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
    
//    @Inject @KGAV(groupId="", artifactId="")
//    KieContainer kr2;
//
//    @Inject @KGAV(groupId="", artifactId="", version="")
//    KieContainer kr3;
//
//    @Inject @KBase("xxx")
//    KieBase kb1;
//    
//    @Inject @KBase("xxx") @KGAV(groupId="", artifactId="")
//    KieBase kb2;
//    
//    @Inject @KBase("xxx") @KGAV(groupId="", artifactId="", version="")
//    KieBase kb3;
//
//    
//    @Inject @KSession("xxx")
//    KieSession ks1;
//    
//    @Inject @KSession("xxx") @KGAV(groupId="", artifactId="")
//    KieSession ks2;
//    
//    @Inject @KSession("xxx") @KGAV(groupId="", artifactId="", version="")
//    KieSession ks3;
        
    
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
