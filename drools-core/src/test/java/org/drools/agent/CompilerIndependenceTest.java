/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.agent;


import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.kie.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.kie.event.knowledgeagent.AfterResourceProcessedEvent;
import org.kie.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.kie.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.kie.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.kie.event.knowledgeagent.KnowledgeAgentEventListener;
import org.kie.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.kie.event.knowledgeagent.ResourceCompilationFailedEvent;
import org.kie.io.ResourceChangeScannerConfiguration;
import org.kie.io.ResourceFactory;

import static org.junit.Assert.*;

import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;

public class CompilerIndependenceTest {

    @Test @Ignore
    public void testFixme() {
        
    }
// FIXME
//    private final Object lock = new Object();
//    private volatile boolean kbaseUpdated;
//
//    @Before
//    public void setUp() throws Exception {
//        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();
//        ResourceFactory.getResourceChangeNotifierService().start();
//        ResourceFactory.getResourceChangeScannerService().start();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        ResourceFactory.getResourceChangeNotifierService().stop();
//        ResourceFactory.getResourceChangeScannerService().stop();
//        ((ResourceChangeNotifierImpl) ResourceFactory.getResourceChangeNotifierService()).reset();
//        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();
//    }
//
//    public void testDRL() throws Exception {
//
//        
//        String xml = "";
//        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
//        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
//        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
//        xml += "    <add> ";
//        xml += "        <resource source='classpath:rules.drl' type='DRL' />";
//        xml += "    </add> ";
//        xml += "</change-set>";
//
//
//        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//        KnowledgeAgent kagent = this.createKAgent(kbase,true);
//
//        try{
//            kagent.applyChangeSet(ResourceFactory.newByteArrayResource(xml.getBytes()));
//
//            fail("The agent shouldn't be able to compile the resource!");
//        } catch(IllegalArgumentException ex){
//
//        }
//
//        kagent.dispose();
//
//    }
//
//    public void testPKG() throws Exception {
//
//
//        String xml = "";
//        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'";
//        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'";
//        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
//        xml += "    <add> ";
//        xml += "        <resource source='classpath:pkg/mortgages.pkg' type='PKG' />";
//        xml += "    </add> ";
//        xml += "</change-set>";
//
//
//        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//        KnowledgeAgent kagent = this.createKAgent(kbase,true);
//
//        kagent.applyChangeSet(ResourceFactory.newByteArrayResource(xml.getBytes()));
//        this.kbaseUpdated = false;
//
//        assertEquals(1, kagent.getKieBase().getKnowledgePackages().size());
//
//        Thread.sleep(2000);
//
//        ClassPathResource cpResource = (ClassPathResource)ResourceFactory.newClassPathResource("pkg/mortgages.pkg");
//
//        File f = new File(cpResource.getURL().getFile());
//        assertTrue(f.exists());
//        
//        long t = System.currentTimeMillis() ;
//        int count = 0;
//        boolean success = false;
//        while ( !(success = f.setLastModified(t)) && count < 10 ) {
//            count++;
//            System.gc();
//            Thread.sleep( 100 );
//        }
//        
//        if ( !success) {
//            fail( "Unable to setLastModified" );
//        }
//
//        this.waitUntilKBaseUpdate();
//        assertEquals(1, kagent.getKieBase().getKnowledgePackages().size());
//
//        kagent.dispose();
//
//    }
//
//    private KnowledgeAgent createKAgent(KnowledgeBase kbase, boolean newInstance) {
//        ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
//        sconf.setProperty("drools.resource.scanner.interval", "2");
//        ResourceFactory.getResourceChangeScannerService().configure(sconf);
//
//        KnowledgeAgentConfiguration aconf = KnowledgeAgentFactory.newKnowledgeAgentConfiguration();
//        aconf.setProperty("drools.agent.scanDirectories", "true");
//        aconf.setProperty("drools.agent.scanResources", "true");
//        // Testing incremental build here
//        aconf.setProperty("drools.agent.newInstance", ""+newInstance);
//
//
//        KnowledgeAgent kagent = KnowledgeAgentFactory.newKnowledgeAgent(
//                "test agent", kbase, aconf);
//        
//        kagent.addEventListener(new KnowledgeAgentEventListener() {
//
//            public void beforeChangeSetApplied(BeforeChangeSetAppliedEvent event) {
//            }
//
//            public void afterChangeSetApplied(AfterChangeSetAppliedEvent event) {
//            }
//
//            public void beforeChangeSetProcessed(BeforeChangeSetProcessedEvent event) {
//            }
//
//            public void afterChangeSetProcessed(AfterChangeSetProcessedEvent event) {
//            }
//
//            public void beforeResourceProcessed(BeforeResourceProcessedEvent event) {
//            }
//
//            public void afterResourceProcessed(AfterResourceProcessedEvent event) {
//            }
//
//            public void knowledgeBaseUpdated(KnowledgeBaseUpdatedEvent event) {
//                System.out.println("KieBaseModel was updated");
//                synchronized (lock) {
//                    kbaseUpdated = true;
//                    lock.notifyAll();
//                }
//            }
//
//            public void resourceCompilationFailed(ResourceCompilationFailedEvent event) {
//            }
//        });
//
//        assertEquals("test agent", kagent.getName());
//
//        return kagent;
//    }
//
//    private void waitUntilKBaseUpdate() {
//        synchronized (lock) {
//            while (!kbaseUpdated) {
//                try {
//                    lock.wait();
//                } catch (InterruptedException e) {
//                }
//                System.out.println("Waking up!");
//            }
//            kbaseUpdated = false;
//        }
//    }

}
