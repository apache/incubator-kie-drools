/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.kie.services.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.api.DeploymentIdResolver;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.objects.OtherPerson;
import org.jbpm.kie.test.objects.Person;
import org.jbpm.kie.test.objects.Thing;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.process.CaseData;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;


public class BPMN2DataServicesReferencesTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DeploymentIdResolver.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    private DeploymentUnit deploymentUnit;
    private String deploymentId;

    private boolean loadBusinesRuleProcesses = true;
    private static final String PROC_ID_BUSINESS_RULE =          "org.jbpm.kie.test.references.business";
    private static final String RULE_BUSINESS_RULE_PROCESS =     "org.jbpm.kie.test.references.rules";

    private boolean loadCallActivityProcesses = true;
    private static final String PROC_ID_CALL_ACTIVITY =          "org.jbpm.kie.test.references.parent";
    private static final String PROC_ID_CALL_ACTIVITY_BY_NAME =  "org.jbpm.kie.test.references.parent.name";
    private static final String PROC_ID_ACTIVITY_CALLED =        "org.jbpm.kie.test.references.subprocess";

    private boolean loadGlobalImportProcesses = true;
    private static final String PROC_ID_GLOBAL =                 "org.jbpm.kie.test.references.global";
    private static final String PROC_ID_IMPORT =                 "org.jbpm.kie.test.references.import";

    private boolean loadJavaMvelScriptProcesses = true;
    private static final String PROC_ID_JAVA_SCRIPT_QUALIFIED_CLASS = "org.jbpm.kie.test.references.qualified.java";
    private static final String PROC_ID_JAVA_THING_SCRIPT_QUALIFIED_CLASS = "org.jbpm.kie.test.references.qualified.java.thing";
    private static final String PROC_ID_MVEL_SCRIPT_QUALIFIED_CLASS = "org.jbpm.kie.test.references.qualified.mvel";
    private static final String PROC_ID_RULE_SCRIPT_QUALIFIED_CLASS = "org.jbpm.kie.test.references.qualified.drools";

    private boolean loadSignalGlobalInfoProcess = true;
    private static final String PROC_ID_SIGNAL = "org.jbpm.signal";

    @Before
    public void prepare() {
    	configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> resources = new ArrayList<String>();

        // extension
        if( loadGlobalImportProcesses ) {
            resources.add("repo/processes/references/importWithScriptTask.bpmn2");
            resources.add("repo/processes/references/globalBasedOnEntryExitScriptProcess.bpmn2");
        }

        // call activity (referenced sub processes)
        if( loadCallActivityProcesses ) {
            resources.add("repo/processes/references/callActivityByNameParent.bpmn2");
            resources.add("repo/processes/references/callActivityParent.bpmn2");
            resources.add("repo/processes/references/callActivitySubProcess.bpmn2");
        }

        // item def, business rules
        if( loadBusinesRuleProcesses ) {
            resources.add("repo/processes/references/businessRuleTask.bpmn2");
            resources.add("repo/processes/references/businessRuleTask.drl");
        }

        // qualified class in script
        if( loadJavaMvelScriptProcesses ) {
            resources.add("repo/processes/references/javaScriptTaskWithQualifiedClass.bpmn2");
            resources.add("repo/processes/references/javaScriptTaskWithQualifiedClassItemDefinition.bpmn2");
            resources.add("repo/processes/references/mvelScriptTaskWithQualifiedClass.bpmn2");
            resources.add("repo/processes/references/rulesScriptTaskWithQualifiedClass.bpmn2");
        }

        // qualified class in script
        if( loadSignalGlobalInfoProcess ) {
            resources.add("repo/processes/general/signal.bpmn");
        }

        InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();

        FileOutputStream fs = null;
        try {
            fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
        } catch (Exception e) {
            fail( "Unable to write pom.xml to filesystem: " + e.getMessage());
        }

        try {
            fs.close();
        } catch (Exception e) {
           logger.info("Unable to close fileystem used to write pom.xml" );
        }

        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        // check
        assertNotNull(deploymentService);

        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        deploymentId = deploymentUnit.getIdentifier();

        procInstIds.clear();
    }

    private Set<Long> procInstIds = new HashSet<>();

    @After
    public void cleanup() {
        for( long procInstId : procInstIds ) {
            try {
                processService.abortProcessInstance(procInstId);
            } catch( Exception e ) {
                // ignore if it fails..
            }
        }
    	cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        close();
    }

    @SafeVarargs
    private final long startProcess(String deploymentId, String processId, Map<String, Object>... params) {
        Long procInstId;
        if( params != null && params.length > 0 ) {
            procInstId = processService.startProcess(deploymentId, processId, params[0]);
        } else {
            procInstId = processService.startProcess(deploymentId, processId);
        }
        procInstIds.add(procInstId);
        return procInstId;
    }

    @Test
    public void testImport() throws IOException {
        Assume.assumeTrue("Skip import/global tests", loadGlobalImportProcesses);
        String processId = PROC_ID_IMPORT;

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        assertNotNull(procDef);

        // check that process runs
        Person person = new Person();
        person.setName("Max Rockatansky");
        person.setId(1979l);
        person.setTime(3l);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", person);
        Long procId = startProcess(deploymentId, processId, params);

        ProcessInstance procInst = processService.getProcessInstance(procId);
        assertNull(procInst);

        // verify process information
        Collection<String> refClasses = bpmn2Service.getJavaClasses(deploymentId, processId);
        assertNotNull( "Null set of referenced classes", refClasses );
        assertFalse( "Empty set of referenced classes", refClasses.isEmpty() );
        assertEquals( "Number referenced classes", 3, refClasses.size() );
        String [] expected = {
                              Person.class.getCanonicalName(),
                              CaseAssignment.class.getCanonicalName(),
                              CaseData.class.getCanonicalName()
                      };
        assertArrayEquals( "Imported class in process", expected, refClasses.toArray(new String [refClasses.size()]) );
    }

    @Test
    public void testGlobal() throws IOException {
        Assume.assumeTrue("Skip import/global tests", loadGlobalImportProcesses);
        String processId = PROC_ID_GLOBAL;

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        assertNotNull(procDef);

        // check that process runs
        Person person = new Person();
        person.setName("Max Rockatansky");
        person.setId(1979l);
        person.setTime(3l);

        KieSession ksession = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getKieSession();
        ksession.setGlobal("person", person);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new SystemOutWorkItemHandler());

        Long procId = startProcess(deploymentId, processId);
        ProcessInstance procInst = processService.getProcessInstance(procId);
        assertNull(procInst);
        String log = person.getLog();
        assertFalse( "Empty log" , log == null || log.trim().isEmpty() );
        assertEquals( "Empty log" , log.split(":").length, 4);

        // verify process information
        Collection<String> refClasses = bpmn2Service.getJavaClasses(deploymentId, processId);
        assertNotNull( "Null set of referenced classes", refClasses );
        assertFalse( "Empty set of referenced classes", refClasses.isEmpty() );
        assertEquals( "Number referenced classes", 3, refClasses.size() );
        String [] expected = {
                              Person.class.getCanonicalName(),
                              CaseAssignment.class.getCanonicalName(),
                              CaseData.class.getCanonicalName()
                      };
        assertArrayEquals( "Imported class in process", expected, refClasses.toArray(new String [refClasses.size()]) );
    }

    @Test
    public void testCallActivityByName() throws IOException {
        Assume.assumeTrue("Skip call activity tests", loadCallActivityProcesses);
        String processId = PROC_ID_CALL_ACTIVITY_BY_NAME;

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        assertNotNull(procDef);

        // run process (to verify that it works)
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        Long procId = startProcess(deploymentId, processId, params);
        ProcessInstance procInst = processService.getProcessInstance(procId);

        assertNull(procInst);

        AuditService auditService
            = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getAuditService();
        List<? extends VariableInstanceLog> logs = auditService.findVariableInstances(procId);

        boolean foundY = false;
        for( VariableInstanceLog log: logs ) {
           if( log.getVariableId().equals("y") && log.getValue().equals("new value") ) {
              foundY = true;
           }
        }
        assertTrue( "Parent process did not call sub process", foundY);

        // check information about process
        Collection<String> refProcesses = bpmn2Service.getReusableSubProcesses(deploymentId, processId);
        assertNotNull( "Null set of referenced processes", refProcesses );
        assertFalse( "Empty set of referenced processes", refProcesses.isEmpty() );
        assertEquals( "Number referenced processes", 1, refProcesses.size() );
        assertEquals( "Imported class in processes", PROC_ID_ACTIVITY_CALLED, refProcesses.iterator().next() );
    }

    @Test
    public void testCallActivity() throws IOException {
        Assume.assumeTrue("Skip call activity tests", loadCallActivityProcesses);
        String processId = PROC_ID_CALL_ACTIVITY;

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        assertNotNull(procDef);

        // run process (to verify that it works)
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        Long procId = startProcess(deploymentId, processId, params);
        ProcessInstance procInst = processService.getProcessInstance(procId);

        assertNull(procInst);

        AuditService auditService
            = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getAuditService();
        List<? extends VariableInstanceLog> logs = auditService.findVariableInstances(procId);

        boolean foundY = false;
        for( VariableInstanceLog log: logs ) {
           if( log.getVariableId().equals("y") && log.getValue().equals("new value") ) {
              foundY = true;
           }
        }
        assertTrue( "Parent process did not call sub process", foundY);

        // check information about process
        Collection<String> refProcesses = bpmn2Service.getReusableSubProcesses(deploymentId, processId);
        assertNotNull( "Null set of referenced processes", refProcesses );
        assertFalse( "Empty set of referenced processes", refProcesses.isEmpty() );
        assertEquals( "Number referenced processes", 1, refProcesses.size() );
        assertEquals( "Imported class in processes", PROC_ID_ACTIVITY_CALLED, refProcesses.iterator().next() );
    }

    @Test
    public void testBusinessRuleTask() throws IOException {
        Assume.assumeTrue("Skip business rule tests", loadBusinesRuleProcesses);
        String processId = PROC_ID_BUSINESS_RULE;

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        assertNotNull(procDef);

        // check that process runs
        Person person = new Person();
        person.setName("Max Rockatansky");
        person.setId(1979l);
        person.setTime(3l);

        KieSession ksession = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getKieSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        Long procId = startProcess(deploymentId, processId);

        String ruleOutput = "Executed";
        assertEquals("Global did not contain output from rule!", 1, list.size());
        assertEquals("Global did not contain correct output of rule!", ruleOutput, list.get(0));

        ProcessInstance procInst = processService.getProcessInstance(procId);
        assertNull( "Process instance did not complete!", procInst );

        // check information about process
        Collection<String> refRules = bpmn2Service.getRuleSets(deploymentId, processId);
        assertNotNull( "Null set of imported rules", refRules );
        assertFalse( "Empty set of imported rules", refRules.isEmpty() );
        assertEquals( "Number imported rules", 1, refRules.size() );
        assertEquals( "Name of imported ruleset", RULE_BUSINESS_RULE_PROCESS, refRules.iterator().next() );

        refRules = procDef.getReferencedRules();
        assertNotNull( "Null set of imported rules", refRules );
        assertFalse( "Empty set of imported rules", refRules.isEmpty() );
        assertEquals( "Number imported rules", 1, refRules.size() );
        assertEquals( "Name of imported ruleset", RULE_BUSINESS_RULE_PROCESS, refRules.iterator().next() );
    }


    @Test
    public void testJavaScriptWithQualifiedClass() throws IOException {
        runScriptTest(PROC_ID_JAVA_SCRIPT_QUALIFIED_CLASS);
    }

    @Test
    public void testJavaThingScriptWithQualifiedClass() throws IOException {
        runScriptTest(PROC_ID_JAVA_THING_SCRIPT_QUALIFIED_CLASS);
    }

    @Test
    public void testMvelScriptWithQualifiedClass() throws IOException {
        runScriptTest(PROC_ID_MVEL_SCRIPT_QUALIFIED_CLASS);
    }

    private void runScriptTest(String processId) {
        Assume.assumeTrue("Skip script/expr tests", loadJavaMvelScriptProcesses);

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        assertNotNull(procDef);

        // check that process runs
        Person person = new Person();
        person.setName("Max Rockatansky");
        person.setId(1979l);
        person.setTime(3l);

        Map<String, Object> params = new HashMap<String, Object>(1);
        params.put("person", person);
        Long procId = startProcess(deploymentId, processId, params);

        assertNull( "Process instance did not complete" , processService.getProcessInstance(procId));
        assertTrue( "Script did not modify variable!", person.getLog().startsWith("Hello") );

        Collection<String> javaClasses = bpmn2Service.getJavaClasses(deploymentId, processId);
        assertNotNull( "Null set of java classes", javaClasses );
        assertFalse( "Empty set of java classes", javaClasses.isEmpty() );

        assertEquals( "Number java classes", 6, javaClasses.size() );
        String [] expected = {
                "java.lang.Object",
                Person.class.getCanonicalName(),
                OtherPerson.class.getCanonicalName(),
                Thing.class.getCanonicalName(),
                CaseAssignment.class.getCanonicalName(),
                CaseData.class.getCanonicalName()
        };
        Set<String> expectedClasses = new HashSet<String>(Arrays.asList(expected));
        for( String className : javaClasses ) {
            assertTrue( "Class name is not qualified: " + className, className.contains(".") );
            assertTrue( "Unexpected class: " + className, expectedClasses.remove(className) );
        }
        if( ! expectedClasses.isEmpty() ) {
            fail( "Expected class not found to be referenced: " + expectedClasses.iterator().next() );
        }
    }

    @Test
    @Ignore // TODO!
    public void testDroolsScriptWithQualifiedClass() throws Exception {
        Assume.assumeTrue("Skip script/expr tests", loadJavaMvelScriptProcesses);
        String processId = PROC_ID_RULE_SCRIPT_QUALIFIED_CLASS;

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        assertNotNull(procDef);

        // check that process runs
        Person person = new Person();
        person.setName("Max");
        person.setId(1979l);
        person.setTime(3l);

        KieSession ksession = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getKieSession();
        ksession.insert(person);
        ksession.insert(new Thing());
        ksession.insert(new OtherPerson(person));
        ksession.insert(person.getName());
        ksession.insert(person.getId());

        Long procId = startProcess(deploymentId, processId);

        assertNull( "Process instance did not complete:" , processService.getProcessInstance(procId));

        Collection<String> javaClasses = bpmn2Service.getJavaClasses(deploymentId, processId);
        assertNotNull( "Null set of java classes", javaClasses );
        assertFalse( "Empty set of java classes", javaClasses.isEmpty() );

        assertEquals( "Number java classes", 4, javaClasses.size() );
        String [] expected = {
                "java.lang.Object",
                Person.class.getCanonicalName(),
                OtherPerson.class.getCanonicalName(),
                Thing.class.getCanonicalName()
        };
        Set<String> expectedClasses = new HashSet<String>(Arrays.asList(expected));
        for( String className : javaClasses ) {
            assertTrue( "Class name is not qualified: " + className, className.contains(".") );
            assertTrue( "Unexpected class: " + className, expectedClasses.remove(className) );
        }
        if( ! expectedClasses.isEmpty() ) {
            fail( "Expected class not found to be referenced: " + expectedClasses.iterator().next() );
        }
    }


    @Test
    public void testSignalsAndGlobals() throws IOException {
        Assume.assumeTrue("Skip signal/global tests", loadSignalGlobalInfoProcess);
        String processId = PROC_ID_SIGNAL;

        // check that process starts
        KieSession ksession = deploymentService.getRuntimeManager(deploymentId).getRuntimeEngine(null).getKieSession();
        ksession.setGlobal("person", new Person());
        long procInstId = startProcess(deploymentId, processId);

        ProcessDefinition procDef = bpmn2Service.getProcessDefinition(deploymentId, processId);
        assertNotNull(procDef);

        // check information about process
        assertNotNull( "Null signals list", procDef.getSignals() );
        assertFalse( "Empty signals list", procDef.getSignals().isEmpty() );
        assertEquals( "Unexpected signal", "MySignal", procDef.getSignals().iterator().next() );

        Collection<String> globalNames = procDef.getGlobals();
        assertNotNull( "Null globals list", globalNames );
        assertFalse( "Empty globals list", globalNames.isEmpty() );
        assertEquals( "globals list size", 2, globalNames.size() );
        for( String globalName : globalNames ) {
            assertTrue( "Unexpected global: " + globalName,
                    "person".equals(globalName) || "name".equals(globalName) );
        }

        // cleanup
        processService.abortProcessInstance(procInstId);
    }
}