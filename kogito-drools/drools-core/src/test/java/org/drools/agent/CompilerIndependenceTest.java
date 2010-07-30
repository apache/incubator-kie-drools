/**
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
import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.event.knowledgeagent.AfterChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.AfterChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.AfterResourceProcessedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetAppliedEvent;
import org.drools.event.knowledgeagent.BeforeChangeSetProcessedEvent;
import org.drools.event.knowledgeagent.BeforeResourceProcessedEvent;
import org.drools.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.event.knowledgeagent.KnowledgeBaseUpdatedEvent;
import org.drools.event.knowledgeagent.ResourceCompilationFailedEvent;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.impl.ResourceChangeScannerImpl;

public class CompilerIndependenceTest extends TestCase {

    public void fixmeTest() {
        
    }
// FIXME
//    private final Object lock = new Object();
//    private volatile boolean kbaseUpdated;
//
//    @Override
//    protected void setUp() throws Exception {
//        ((ResourceChangeScannerImpl) ResourceFactory.getResourceChangeScannerService()).reset();
//        ResourceFactory.getResourceChangeNotifierService().start();
//        ResourceFactory.getResourceChangeScannerService().start();
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
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
//        xml += "<change-set xmlns='http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >";
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
//        xml += "<change-set xmlns='http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd'>";
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
//        assertEquals(1, kagent.getKnowledgeBase().getKnowledgePackages().size());
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
//        assertEquals(1, kagent.getKnowledgeBase().getKnowledgePackages().size());
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
//                System.out.println("KBase was updated");
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
