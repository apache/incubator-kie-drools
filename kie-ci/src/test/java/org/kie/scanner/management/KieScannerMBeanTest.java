/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.scanner.management;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieScanner;
import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.scanner.AbstractKieCiTest;
import org.kie.scanner.KieRepositoryScannerImpl;
import org.kie.scanner.MavenRepository;

import javax.management.ObjectName;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.MavenRepository.getMavenRepository;

public class KieScannerMBeanTest extends AbstractKieCiTest {
    
    private FileManager fileManager;
    private File kPom;

    @Before
    public void setUp() throws Exception {
        MBeanUtils.setMBeanEnabled(true);
        System.setProperty(MBeanUtils.MBEANS_PROPERTY, "enabled");
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        ReleaseId releaseId = KieServices.Factory.get().newReleaseId("org.kie", "scanner-mbean-test", "1.0-SNAPSHOT");
        kPom = createKPom(fileManager, releaseId);
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
        System.setProperty(MBeanUtils.MBEANS_PROPERTY, "");
        MBeanUtils.setMBeanEnabled(false);
    }
    
    @Test
    public void testKScannerMBean() throws Exception {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-mbean-test", "1.0-SNAPSHOT");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, "rule1", "rule2");
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, kPom);

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        KieRepositoryScannerImpl scanner = (KieRepositoryScannerImpl) ks.newKieScanner(kieContainer);
        KieScannerMBeanImpl mBean = (KieScannerMBeanImpl) scanner.getMBean();
        ObjectName mbeanName = mBean.getMBeanName();
        
        // we want to check that the mbean is register in the server and exposing the correct attribute values
        // so we fetch the attributes from the server
        Assert.assertEquals( releaseId.toExternalForm(), MBeanUtils.getAttribute( mbeanName, "ScannerReleaseId") );
        Assert.assertEquals( releaseId.toExternalForm(), MBeanUtils.getAttribute( mbeanName, "CurrentReleaseId") );
        Assert.assertEquals( InternalKieScanner.Status.STOPPED.toString(), MBeanUtils.getAttribute( mbeanName, "Status") );
        
        MBeanUtils.invoke(mbeanName, "start", new Object[] { Long.valueOf(10000) }, new String[] { "long" } );
        
        Assert.assertEquals( InternalKieScanner.Status.RUNNING.toString(), MBeanUtils.getAttribute( mbeanName, "Status") );

        MBeanUtils.invoke(mbeanName, "stop", new Object[] {}, new String[] {} );
        
        Assert.assertEquals( InternalKieScanner.Status.STOPPED.toString(), MBeanUtils.getAttribute( mbeanName, "Status") );
        
        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId, "rule2", "rule3");
        // deploy it on maven
        repository.deployArtifact(releaseId, kJar2, kPom);
        
        MBeanUtils.invoke(mbeanName, "scanNow", new Object[] {}, new String[] {} );
        
        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");
        
        MBeanUtils.invoke(mbeanName, "shutdown", new Object[] {}, new String[] {} );
        
        Assert.assertEquals( InternalKieScanner.Status.SHUTDOWN.toString(), MBeanUtils.getAttribute( mbeanName, "Status") );
        
        ks.getRepository().removeKieModule(releaseId);
    }

    private void checkKSession(KieSession ksession, Object... results) {
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(results.length, list.size());
        for (Object result : results) {
            assertTrue( list.contains( result ) );
        }
    }


}
