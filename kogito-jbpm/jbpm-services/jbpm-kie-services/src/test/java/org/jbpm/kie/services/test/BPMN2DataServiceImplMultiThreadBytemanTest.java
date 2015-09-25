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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CyclicBarrier;

import javax.print.attribute.HashAttributeSet;

import org.drools.core.util.IoUtils;
import org.drools.core.xml.ExtensibleXmlParser;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceImpl;
import org.jbpm.kie.services.impl.bpmn2.BPMN2DataServiceSemanticModule;
import org.jbpm.kie.services.impl.bpmn2.DataServiceItemDefinitionHandler;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMUnitConfig(loadDirectory="target/test-classes") // set "debug=true to see debug output
@BMScript(value="byteman/setBPMN2ProcessProvider.btm")
public class BPMN2DataServiceImplMultiThreadBytemanTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BPMN2DataServiceImplMultiThreadBytemanTest.class);

    protected TestDataServiceItemDefinitionHandler testItemDefinitionHandler;

    @Before
    public void prepare() {
        configureServices();

        BPMN2DataServiceSemanticModule module = getDataServicesBPMN2DataServiceSemanticModule();
        testItemDefinitionHandler = new TestDataServiceItemDefinitionHandler(module);
        module.addDataServicesHandler("itemDefinition", testItemDefinitionHandler);
        module.setItemDefinitionHandler(testItemDefinitionHandler);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        close();
    }

    /**
     * This test simulates the following race condition:
     *
     * Thread A: dataservices
     * Thread B: kie builder
     *
     * A. do setup for building the process definition and pause
     * B. Build a process *instance*
     * A. build the process definition
     *
     * The problem is that the data service logic messes with the
     * internal infrastructure used to build a process instance.
     *
     * This test makes sure that non of the modifications to the internal
     * (jbpm-flow) process instance creation infrastructure, modify the
     * process instance.
     * @throws Exception
     */
    @Test
    public void testBuildProcessDefinitionConcurrentWithKieBuilder() throws Exception {
        final List<ProcessDefinition> defs = new ArrayList<ProcessDefinition>();

        byte[] process1 = IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream(
                "/repo/processes/general/customtask.bpmn"));
        final String process1Content = new String(process1, "UTF-8");
        final String SCRIPT_BPMN_PATH = "repo/processes/general/hello.bpmn";

        final CyclicBarrier threadsFinishedBarrier = new CyclicBarrier(3);

        Thread dataServiceThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ProcessDefinition def = bpmn2Service.buildProcessDefinition("test", process1Content, null, true);
                    defs.add(def);
                } finally {
                    waitForTheOtherThreads(threadsFinishedBarrier);
                }
            }
        });

        final Exception [] exceptionHolder = new Exception[1];

        final KnowledgeBuilder [] kbuilderHolder = new KnowledgeBuilder[1];
        Thread kieBuilderThread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    kbuilderHolder[0]
                        = BPMN2DataServiceImplMultiThreadBytemanTest.createKBuilder(SCRIPT_BPMN_PATH, ResourceType.BPMN2);
                } catch( Exception e ) {
                    e.printStackTrace();
                    exceptionHolder[0] = e;
                } finally {
                    letDataServiceThreadFinish();
                    waitForTheOtherThreads(threadsFinishedBarrier);
                }
            }
        });

        String dataServiceThreadName = "dataService";
        dataServiceThread.setName(dataServiceThreadName);
        dataServiceThread.start();
        kieBuilderThread.setName("kieBuilder");
        kieBuilderThread.start();

        waitForTheOtherThreads(threadsFinishedBarrier);

        assertEquals(1, defs.size());

        if( exceptionHolder[0] != null ) {
            fail( "See stacktrace: unable to add resource to KieBuilder: " + exceptionHolder[0].getMessage() );
        }

        Set<String> dataServiceModuleThreads = testItemDefinitionHandler.getUsedByThreads();
        assertEquals( "Only 1 thread should have accessed the data service handlers!",
                      1, dataServiceModuleThreads.size() );
        assertEquals( "Only the data service thread should have accessed the data service handlers!",
                      dataServiceThreadName, dataServiceModuleThreads.iterator().next() );

        // This will throw an exception if the process instance has not been created well
        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("name", "Poppy");
        kbuilderHolder[0].newKnowledgeBase().newKieSession().startProcess("hello", params);
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

    public BPMN2DataServiceSemanticModule getDataServicesBPMN2DataServiceSemanticModule() {
         Field [] fields = BPMN2DataServiceImpl.class.getDeclaredFields();
         Field moduleField = null;
         for( Field field : fields ) {
            if( field.getName().equalsIgnoreCase("module") )  {
               moduleField = field;
               moduleField.setAccessible(true);
               break;
            }
         }

         assertNotNull( "Could not find " + BPMN2DataServiceSemanticModule.class.getSimpleName() + ".MODULE field!", moduleField);
         BPMN2DataServiceSemanticModule module = null;
         try {
            module = (BPMN2DataServiceSemanticModule) moduleField.get(null);
         } catch( Exception e ) {
             e.printStackTrace();
             fail( "Could not get " + BPMN2DataServiceSemanticModule.class.getSimpleName() + ".MODULE field: " + e.getMessage());
         }
         return module;
    }

    protected static final class TestDataServiceItemDefinitionHandler extends DataServiceItemDefinitionHandler {

        public TestDataServiceItemDefinitionHandler(BPMN2DataServiceSemanticModule module) {
            super(module);
        }

        private Set<String> usedByThreads = new CopyOnWriteArraySet<String>();

        @Override
        public Object start( String uri, String localName, Attributes attrs, ExtensibleXmlParser parser ) throws SAXException {
            usedByThreads.add(Thread.currentThread().getName());
            return super.start(uri, localName, attrs, parser);
        }

        public Set<String> getUsedByThreads() {
            return usedByThreads;
        }

    }
}
