/*
 * Copyright 2015 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

import org.drools.core.util.IoUtils;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMUnitConfig(loadDirectory="target/test-classes") // set "debug=true to see debug output
@BMScript(value="byteman/setBPMN2ProcessProvider.btm")
public class BPMN2DataServiceImplMultiThreadBytemanTest extends AbstractKieServicesBaseTest {

    @Before
    public void prepare() {
        configureServices();
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        close();
    }

    @Test
    public void testBuildProcessDefinitionConcurrentWithKieBuilder() throws Exception {

        final List<ProcessDefinition> defs = new ArrayList<ProcessDefinition>();

        byte[] process1 = IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream(
                "/repo/processes/general/customtask.bpmn"));
        final String HUMAN_TASK_BPMN_PATH = "repo/processes/general/humanTask.bpmn";

        final String process1Content = new String(process1, "UTF-8");

        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);
        
        Thread dataServiceThread = new Thread(new Runnable() {

            @Override
            public void run() {
                ProcessDefinition def = bpmn2Service.buildProcessDefinition("test", process1Content, null, true);
                defs.add(def);
                
                waitForTheOtherThreads(threadsFinishedBarrier); 
            }
        });

        final Exception [] exceptionHolder = new Exception[1];
        
        Thread kieBuilderThread = new Thread(new Runnable() {

            @Override
            public void run() {
               
                try { 
                    BPMN2DataServiceImplMultiThreadBytemanTest.createKBuilder(HUMAN_TASK_BPMN_PATH, ResourceType.BPMN2);
                } catch( Exception e ) { 
                    e.printStackTrace();
                    exceptionHolder[0] = e;
                } finally { 
                    letDataServiceThreadFinish();
                    waitForTheOtherThreads(threadsFinishedBarrier);
                }
            }
        });

        dataServiceThread.start();
        kieBuilderThread.start();

        waitForTheOtherThreads(threadsFinishedBarrier); 
        
        assertEquals(1, defs.size());
        
        if( exceptionHolder[0] != null ) { 
            fail( "See stacktrace: unable to add resource to KieBuilder: " + exceptionHolder[0].getMessage() );
        }
    }

    public static void letDataServiceThreadFinish() { 
        // placeholder 
    }
    
    static KnowledgeBuilder createKBuilder( String resource, ResourceType resourceType ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(resource), resourceType);
        if( kbuilder.hasErrors() ) {
            int errors = kbuilder.getErrors().size();
            if( errors > 0 ) {
                System.out.println("Found " + errors + " errors");
                for( KnowledgeBuilderError error : kbuilder.getErrors() ) {
                    System.out.println(error.getMessage());
                }
            }
            throw new IllegalArgumentException("Application process definition has errors, see log for more details");
        }
        return kbuilder;
    }
}
