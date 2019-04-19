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

package org.jbpm.bpmn2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.core.SessionConfiguration;
import org.drools.core.audit.WorkingMemoryInMemoryLogger;
import org.drools.core.audit.event.LogEvent;
import org.drools.core.audit.event.RuleFlowLogEvent;
import org.drools.core.audit.event.RuleFlowNodeLogEvent;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.core.util.MVELSafeHelper;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message.Level;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;
import org.kie.api.io.Resource;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.conf.ForceEagerActivationOption;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Base test case for the jbpm-bpmn2 module.
 */
public abstract class JbpmBpmn2TestCase {
    private static final Logger log = LoggerFactory.getLogger(JbpmBpmn2TestCase.class);
   
    protected WorkingMemoryInMemoryLogger logger;
    
    @Rule
    public Timeout globalTimeout = Timeout.seconds(3000);

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            log.info(" >>> {} <<<", description.getMethodName());

            try {
                String methodName = description.getMethodName();
                int i = methodName.indexOf("[");
                if (i > 0) {
                    methodName = methodName.substring(0, i);
                }
                Method method = description.getTestClass().getMethod(methodName);                
            } catch (Exception ex) {
                // ignore
            }
        };

        protected void finished(Description description) {
            log.info("Finished {}", description);
        };
    };

    public JbpmBpmn2TestCase() {
        
    }

    @After
    public void clear() {
        clearHistory();
    }

    protected KieBase createKnowledgeBase(String... process) throws Exception {
        List<Resource> resources = new ArrayList<Resource>();
        for (int i = 0; i < process.length; ++i) {
            resources.addAll(buildAndDumpBPMN2Process(process[i]));
        }
        return createKnowledgeBaseFromResources(resources.toArray(new Resource[resources.size()]));
    }

    protected KieBase createKnowledgeBaseWithoutDumper(String... process) throws Exception {
        Resource[] resources = new Resource[process.length];
        for (int i = 0; i < process.length; ++i) {
            String p = process[i];
            resources[i] = (ResourceFactory.newClassPathResource(p));
        }
        return createKnowledgeBaseFromResources(resources);
    }
    
    // Important to test this since persistence relies on this
    protected List<Resource> buildAndDumpBPMN2Process(String process) throws SAXException, IOException { 
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        ((KnowledgeBuilderConfigurationImpl) conf).initSemanticModules();
        ((KnowledgeBuilderConfigurationImpl) conf).addSemanticModule(new BPMNSemanticModule());
        ((KnowledgeBuilderConfigurationImpl) conf).addSemanticModule(new BPMNDISemanticModule());
        ((KnowledgeBuilderConfigurationImpl) conf).addSemanticModule(new BPMNExtensionsSemanticModule());
        
        Resource classpathResource = ResourceFactory.newClassPathResource(process);
        // Dump and reread
        XmlProcessReader processReader 
            = new XmlProcessReader(((KnowledgeBuilderConfigurationImpl) conf).getSemanticModules(), getClass().getClassLoader());
        List<Process> processes = processReader.read(this.getClass().getResourceAsStream("/" + process));
        List<Resource> resources = new ArrayList<Resource>();
        for (Process p : processes) {
            RuleFlowProcess ruleFlowProcess = (RuleFlowProcess) p;
            String dumpedString = XmlBPMNProcessDumper.INSTANCE.dump(ruleFlowProcess);
            Resource resource = ResourceFactory.newReaderResource(new StringReader(dumpedString));
            resource.setSourcePath(classpathResource.getSourcePath());
            resource.setTargetPath(classpathResource.getTargetPath());
            resources.add(resource);
        }
        return resources;
    }
    
    protected KieBase createKnowledgeBaseFromResources(Resource... process)
            throws Exception {

        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        if (process.length > 0) {
            KieFileSystem kfs = ks.newKieFileSystem();

            for (Resource p : process) {
                kfs.write(p);
            }

            KieBuilder kb = ks.newKieBuilder(kfs);

            kb.buildAll(); // kieModule is automatically deployed to KieRepository
                           // if successfully built.

            if (kb.getResults().hasMessages(Level.ERROR)) {
                throw new RuntimeException("Build Errors:\n"
                        + kb.getResults().toString());
            }
        }

        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        return kContainer.getKieBase();
    }
    
    protected KieBase createKnowledgeBaseFromDisc(String process) throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieFileSystem kfs = ks.newKieFileSystem();
            
        Resource res = ResourceFactory.newClassPathResource(process);
        kfs.write(res);

        KieBuilder kb = ks.newKieBuilder(kfs);

        kb.buildAll(); // kieModule is automatically deployed to KieRepository
                       // if successfully built.

        if (kb.getResults().hasMessages(Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n"
                    + kb.getResults().toString());
        }

        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        KieBase kbase =  kContainer.getKieBase();
        
        File packageFile = null;
        for (KiePackage pkg : kbase.getKiePackages() ) {
            packageFile = new File(System.getProperty("java.io.tmpdir") + File.separator + pkg.getName()+".pkg");
            packageFile.deleteOnExit();
            FileOutputStream out = new FileOutputStream(packageFile);
            try {
                DroolsStreamUtils.streamOut(out, pkg);
            } finally {
                out.close();
            }
            
            // store first package only
            break;
        }
        
        kfs.delete(res.getSourcePath());
        kfs.write(ResourceFactory.newFileResource(packageFile));

        kb = ks.newKieBuilder(kfs);
        kb.buildAll(); // kieModule is automatically deployed to KieRepository
                       // if successfully built.

        if (kb.getResults().hasMessages(Level.ERROR)) {
            throw new RuntimeException("Build Errors:\n"
                    + kb.getResults().toString());
        }
        
        kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        kbase =  kContainer.getKieBase();
        
        return kbase;
        
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KieBase kbase)
            throws Exception {
        return createKnowledgeSession(kbase, null, null);
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KieBase kbase,
            Environment env) throws Exception {
        return createKnowledgeSession(kbase, null, env);
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KieBase kbase,
            KieSessionConfiguration conf, Environment env) throws Exception {
        StatefulKnowledgeSession result;
        if (conf == null) {
            conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        }
       
        if (env == null) {
            env = EnvironmentFactory.newEnvironment();
        }

        Properties defaultProps = new Properties();
        defaultProps.setProperty("drools.processSignalManagerFactory",
                DefaultSignalManagerFactory.class.getName());
        defaultProps.setProperty("drools.processInstanceManagerFactory",
                DefaultProcessInstanceManagerFactory.class.getName());
        conf = SessionConfiguration.newInstance(defaultProps);
        conf.setOption(ForceEagerActivationOption.YES);
        result = (StatefulKnowledgeSession) kbase.newKieSession(conf, env);
        logger = new WorkingMemoryInMemoryLogger(result);
        
        return result;
    }

    protected StatefulKnowledgeSession createKnowledgeSession(String... process)
            throws Exception {
        KieBase kbase = createKnowledgeBase(process);
        return createKnowledgeSession(kbase);
    }
    
    protected KieSession restoreSession(KieSession ksession, boolean noCache) {
        
        return ksession;
        
    }

    protected KieSession restoreSession(KieSession ksession) {
        return ksession;
    }

    protected StatefulKnowledgeSession restoreSession(StatefulKnowledgeSession ksession) {
        return ksession;
    }


    public void assertProcessInstanceCompleted(ProcessInstance processInstance) {
        assertTrue("Process instance has not been completed.", assertProcessInstanceState(ProcessInstance.STATE_COMPLETED, processInstance));
    }

    public void assertProcessInstanceAborted(ProcessInstance processInstance) {
        assertTrue("Process instance has not been aborted.", assertProcessInstanceState(ProcessInstance.STATE_ABORTED, processInstance));
    }

    public void assertProcessInstanceActive(ProcessInstance processInstance) {
        assertTrue("Process instance is not active.", assertProcessInstanceState(ProcessInstance.STATE_ACTIVE, processInstance)
                || assertProcessInstanceState(ProcessInstance.STATE_PENDING, processInstance));
    }

    public void assertProcessInstanceFinished(ProcessInstance processInstance,
            KieSession ksession) {
        assertNull("Process instance has not been finished.", ksession.getProcessInstance(processInstance.getId()));
    }

    public void assertNodeActive(long processInstanceId, KieSession ksession,
            String... name) {
        List<String> names = new ArrayList<String>();
        for (String n : name) {
            names.add(n);
        }
        ProcessInstance processInstance = ksession
                .getProcessInstance(processInstanceId);
        if (processInstance instanceof WorkflowProcessInstance) {            
            assertNodeActive((WorkflowProcessInstance) processInstance, names);            
        }
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) not active: " + s);
        }
    }

    private void assertNodeActive(NodeInstanceContainer container,
            List<String> names) {
        for (NodeInstance nodeInstance : container.getNodeInstances()) {
            String nodeName = nodeInstance.getNodeName();
            if (names.contains(nodeName)) {
                names.remove(nodeName);
            }
            if (nodeInstance instanceof NodeInstanceContainer) {
                assertNodeActive((NodeInstanceContainer) nodeInstance, names);
            }
        }
    }

    public void assertNodeTriggered(long processInstanceId, String... nodeNames) {
        List<String> names = getNotTriggeredNodes(processInstanceId, nodeNames);
        if (!names.isEmpty()) {
            String s = names.get(0);
            for(int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) not executed: " + s);
        }
    }

    public void assertNotNodeTriggered(long processInstanceId,
            String... nodeNames) {
        List<String> names = getNotTriggeredNodes(processInstanceId, nodeNames);
        assertTrue(Arrays.equals(names.toArray(), nodeNames));
    }
    
    public int getNumberOfNodeTriggered(long processInstanceId,
            String node) {
        int counter = 0;
        
        for (LogEvent event : logger.getLogEvents()) {
            if (event instanceof RuleFlowNodeLogEvent) {
                String nodeName = ((RuleFlowNodeLogEvent) event).getNodeName();
                if (node.equals(nodeName)) {
                    counter++;
                }
            }
        }
    
        return counter;
    }
    
    public int getNumberOfProcessInstances(String processId) {
        int counter = 0;      
        LogEvent [] events = logger.getLogEvents().toArray(new LogEvent[0]);
        for (LogEvent event : events ) { 
            if (event.getType() == LogEvent.BEFORE_RULEFLOW_CREATED) {
                if(((RuleFlowLogEvent) event).getProcessId().equals(processId)) {
                    counter++;                    
                }
            }
        }
        
        return counter;
    }
    
    protected boolean assertProcessInstanceState(int state, ProcessInstance processInstance) {
        
        return processInstance.getState() == state;         
    }

    private List<String> getNotTriggeredNodes(long processInstanceId,
            String... nodeNames) {
        List<String> names = new ArrayList<String>();
        for (String nodeName : nodeNames) {
            names.add(nodeName);
        }
        
        for (LogEvent event : logger.getLogEvents()) {
            if (event instanceof RuleFlowNodeLogEvent) {
                String nodeName = ((RuleFlowNodeLogEvent) event)
                        .getNodeName();
                if (names.contains(nodeName)) {
                    names.remove(nodeName);
                }
            }
        }
        
        return names;
    }
    
    protected List<String> getCompletedNodes(long processInstanceId) { 
        List<String> names = new ArrayList<String>();
        
        for (LogEvent event : logger.getLogEvents()) {
            if (event instanceof RuleFlowNodeLogEvent) {
                if( event.getType() == 27 ) { 
                    names.add(((RuleFlowNodeLogEvent) event).getNodeId());
                }
            }
        }
    
        return names;
    }

    protected void clearHistory() {
        
        if (logger != null) {
            logger.clear();
        }
    
    }

    public void assertProcessVarExists(ProcessInstance process,
            String... processVarNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName : processVarNames) {
            names.add(nodeName);
        }

        for (String pvar : instance.getVariables().keySet()) {
            if (names.contains(pvar)) {
                names.remove(pvar);
            }
        }

        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Process Variable(s) do not exist: " + s);
        }

    }
    
    public String getProcessVarValue(ProcessInstance processInstance, String varName) {
        String actualValue = null;        
        Object value = ((WorkflowProcessInstanceImpl) processInstance).getVariable(varName);
        if (value != null) {
            actualValue = value.toString();
        }
    
        return actualValue;
    }
    
    public void assertProcessVarValue(ProcessInstance processInstance, String varName, Object varValue) {
        String actualValue = getProcessVarValue(processInstance, varName);
        assertEquals("Variable " + varName + " value misatch!",  varValue, actualValue );
    }

    public void assertNodeExists(ProcessInstance process, String... nodeNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName : nodeNames) {
            names.add(nodeName);
        }

        for (Node node : instance.getNodeContainer().getNodes()) {
            if (names.contains(node.getName())) {
                names.remove(node.getName());
            }
        }

        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) do not exist: " + s);
        }
    }

    public void assertNumOfIncommingConnections(ProcessInstance process,
            String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for (Node node : instance.getNodeContainer().getNodes()) {
            if (node.getName().equals(nodeName)) {
                if (node.getIncomingConnections().size() != num) {
                    fail("Expected incomming connections: " + num + " - found "
                            + node.getIncomingConnections().size());
                } else {
                    break;
                }
            }
        }
    }

    public void assertNumOfOutgoingConnections(ProcessInstance process,
            String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for (Node node : instance.getNodeContainer().getNodes()) {
            if (node.getName().equals(nodeName)) {
                if (node.getOutgoingConnections().size() != num) {
                    fail("Expected outgoing connections: " + num + " - found "
                            + node.getOutgoingConnections().size());
                } else {
                    break;
                }
            }
        }
    }

    public void assertVersionEquals(ProcessInstance process, String version) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if (!instance.getWorkflowProcess().getVersion().equals(version)) {
            fail("Expected version: " + version + " - found "
                    + instance.getWorkflowProcess().getVersion());
        }
    }

    public void assertProcessNameEquals(ProcessInstance process, String name) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if (!instance.getWorkflowProcess().getName().equals(name)) {
            fail("Expected name: " + name + " - found "
                    + instance.getWorkflowProcess().getName());
        }
    }

    public void assertPackageNameEquals(ProcessInstance process,
            String packageName) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if (!instance.getWorkflowProcess().getPackageName().equals(packageName)) {
            fail("Expected package name: " + packageName + " - found "
                    + instance.getWorkflowProcess().getPackageName());
        }
    }

    public Object eval(Reader reader, Map vars) {
        try {
            return eval(toString(reader), vars);
        } catch (IOException e) {
            throw new RuntimeException("Exception Thrown", e);
        }
    }

    private String toString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder(1024);
        int charValue;

        while ((charValue = reader.read()) != -1) {
            sb.append((char) charValue);
        }
        return sb.toString();
    }

    public Object eval(String str, Map vars) {

        ParserContext context = new ParserContext();
        context.addPackageImport("org.jbpm.task");
        context.addPackageImport("org.jbpm.task.service");
        context.addPackageImport("org.jbpm.task.query");
        context.addPackageImport("java.util");

        vars.put("now", new Date());
        return MVELSafeHelper.getEvaluator().executeExpression(MVEL.compileExpression(str, context),
                vars);
    }
    
    protected void assertProcessInstanceCompleted(long processInstanceId, KieSession ksession) {
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        assertNull("Process instance has not completed.", processInstance);
    }

    protected void assertProcessInstanceAborted(long processInstanceId, KieSession ksession) {
        assertNull(ksession.getProcessInstance(processInstanceId));
    }

    protected void assertProcessInstanceActive(long processInstanceId, KieSession ksession) {
        assertNotNull(ksession.getProcessInstance(processInstanceId));
    }

}
