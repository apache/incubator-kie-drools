/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.scanner.management;

import java.util.ArrayList;
import java.util.List;
import javax.management.ObjectName;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieScanner;
import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.scanner.AbstractKieCiTest;
import org.kie.scanner.KieMavenRepository;
import org.kie.scanner.KieRepositoryScannerImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class KieScannerMBeanTest extends AbstractKieCiTest {
    
    private FileManager fileManager;

    @Before
    public void setUp() throws Exception {
        MBeanUtils.setMBeanEnabled(true);
        System.setProperty(MBeanUtils.MBEANS_PROPERTY, "enabled");
        this.fileManager = new FileManager();
        this.fileManager.setUp();
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

        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kJar1, createKPom(fileManager, releaseId));

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        checkKSession(ksession, "rule1", "rule2");

        KieRepositoryScannerImpl scanner = (KieRepositoryScannerImpl) ks.newKieScanner(kieContainer);
        KieScannerMBeanImpl mBean = (KieScannerMBeanImpl) scanner.getMBean();
        ObjectName mbeanName = mBean.getMBeanName();

        // we want to check that the mbean is register in the server and exposing the correct attribute values
        // so we fetch the attributes from the server
        assertThat(MBeanUtils.getAttribute(mbeanName, "ScannerReleaseId")).isEqualTo(releaseId.toExternalForm());
        assertThat(MBeanUtils.getAttribute(mbeanName, "CurrentReleaseId")).isEqualTo(releaseId.toExternalForm());
        assertThat(MBeanUtils.getAttribute(mbeanName, "Status")).isEqualTo(InternalKieScanner.Status.STOPPED.toString());
        
        MBeanUtils.invoke(mbeanName, "start", new Object[] { Long.valueOf(10000) }, new String[] { "long" } );

        assertThat(MBeanUtils.getAttribute(mbeanName, "Status")).isEqualTo(InternalKieScanner.Status.RUNNING.toString());

        MBeanUtils.invoke(mbeanName, "stop", new Object[] {}, new String[] {} );

        assertThat(MBeanUtils.getAttribute(mbeanName, "Status")).isEqualTo(InternalKieScanner.Status.STOPPED.toString());
        
        // create a new kjar
        InternalKieModule kJar2 = createKieJar(ks, releaseId, "rule2", "rule3");
        // deploy it on maven
        repository.installArtifact(releaseId, kJar2, createKPom(fileManager, releaseId));
        
        MBeanUtils.invoke(mbeanName, "scanNow", new Object[] {}, new String[] {} );
        
        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        checkKSession(ksession2, "rule2", "rule3");
        
        MBeanUtils.invoke(mbeanName, "shutdown", new Object[] {}, new String[] {} );

        assertThat(MBeanUtils.getAttribute(mbeanName, "Status")).isEqualTo(InternalKieScanner.Status.SHUTDOWN.toString());
        
        ks.getRepository().removeKieModule(releaseId);
    }

    protected void checkKSession(KieSession ksession, Object... results) {
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();
        ksession.dispose();

        assertThat(list.size()).isEqualTo(results.length);
        for (Object result : results) {
            assertThat(list.contains(result)).isTrue();
        }
    }


}
