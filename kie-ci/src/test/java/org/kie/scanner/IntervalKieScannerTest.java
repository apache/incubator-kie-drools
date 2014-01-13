package org.kie.scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

public class IntervalKieScannerTest extends AbstractKieCiTest {
    
    private final int TOLERANCE = 200;
    private final int INTERVAL = 500;
    
    private FileManager fileManager;
    
    private KieServices kieServices;
    
    private MavenRepository repository;
    
    @Before
    public void init() {
        
        this.fileManager = new FileManager();
        
        this.repository = getMavenRepository();
        
        this.kieServices = KieServices.Factory.get();
    }
    
    @Ignore
    @Test
    public void test() throws IOException, InterruptedException {
        
        Long rule2Time= null;
                
        ReleaseId releaseId = kieServices.newReleaseId("org.kie.test", "start-scanner-test", "1.0-SNAPSHOT");
        
        File pomFile = createKPom(releaseId);

        InternalKieModule iKieModule1 = createKieJar(kieServices, releaseId, "rule1");
        
        repository.deployArtifact(releaseId, iKieModule1, pomFile);
        
        KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        
        KieScanner scanner = kieServices.newKieScanner(kieContainer);
        
        scanner.start(INTERVAL);
        
        Long zeroTime = System.currentTimeMillis();
        
        InternalKieModule iKieModule2 = createKieJar(kieServices, releaseId, "rule2");
        repository.deployArtifact(releaseId, iKieModule2, pomFile);
        
        for (long i = 450; i < 1000; i += 50) {
            Thread.sleep(getSleepTime(i, zeroTime));
            
            if (rule2Time == null) {
                if (getResult(kieContainer).equals("rule2")) {
                    rule2Time = i;
                } 
            } else {
                assertEquals("rule2", getResult(kieContainer));
            }
        }

        assertTrue("There is " + (rule2Time - INTERVAL) + "ms delay of KieScanner.", rule2Time < (INTERVAL + TOLERANCE));
    }

    private File createKPom(ReleaseId releaseId) throws IOException {
        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, getPom(releaseId));
        return pomFile;
    }
    
    private String getResult(KieContainer kieContainer) {
        
        KieSession kieSession;
        
        kieSession = kieContainer.newKieSession("KSession1");
       
        List<String> list = new ArrayList<String>();
        kieSession.setGlobal("list", list);
        kieSession.fireAllRules();
        kieSession.dispose();
        
        assertEquals(1, list.size());
        
        return list.get(0);
    }
    
    private Long getSleepTime(Long requiredTimeFromZeroTime, Long zeroTime) {
        
        Long finalTime = requiredTimeFromZeroTime - (System.currentTimeMillis() - zeroTime); 
        
        return finalTime > 0 ? finalTime : 0; 
    }
}
