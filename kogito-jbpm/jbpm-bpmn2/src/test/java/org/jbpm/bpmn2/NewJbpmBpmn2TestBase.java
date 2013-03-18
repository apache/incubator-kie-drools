package org.jbpm.bpmn2;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.drools.core.SessionConfiguration;
import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.bpmn2.xml.*;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.ProcessBaseFactoryService;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Process;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewJbpmBpmn2TestBase extends Assert { 

    protected Logger logger = LoggerFactory.getLogger(NewJbpmBpmn2TestBase.class);
    
    protected KieBase createKnowledgeBase(String process) throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new ProcessBaseFactoryService());
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        ((PackageBuilderConfiguration) conf).initSemanticModules();
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
        // ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        
        // Dump and reread
        XmlProcessReader processReader 
            = new XmlProcessReader(((PackageBuilderConfiguration) conf).getSemanticModules(), getClass().getClassLoader());
        List<Process> processes = processReader.read(SimpleBPMNProcessTest.class.getResourceAsStream("/" + process));
        for (Process p : processes) {
            RuleFlowProcess ruleFlowProcess = (RuleFlowProcess) p;
            String dumpedString = XmlBPMNProcessDumper.INSTANCE.dump(ruleFlowProcess);
            kbuilder.add(ResourceFactory.newReaderResource(new StringReader(dumpedString)), ResourceType.BPMN2);
        }
        // Print errors
        if (!kbuilder.getErrors().isEmpty()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Errors while parsing knowledge base");
        }
        
        // KieBase doesn't have an equivalent (that I know of?)
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return (KieBase) kbase;
    }
    
    protected KieBase createKnowledgeBaseWithoutDumper(String process) throws Exception {
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
                .newKnowledgeBuilderConfiguration();
        ((PackageBuilderConfiguration) conf).initSemanticModules();
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
        ((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNExtensionsSemanticModule());
        // ProcessDialectRegistry.setDialect("XPath", new XPathDialect());

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
        kbuilder.add(
                ResourceFactory.newReaderResource(new InputStreamReader(
                        SimpleBPMNProcessTest.class.getResourceAsStream("/" + process))), 
                ResourceType.BPMN2);
        
        // Print errors
        if (!kbuilder.getErrors().isEmpty()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                logger.error(error.toString());
            }
            throw new IllegalArgumentException("Errors while parsing knowledge base");
        }
        
        // KieBase doesn't have an equivalent (that I know of?)
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        
        return kbase;
    }
    
    protected KieSession createKnowledgeSession(KieBase kbase) {
        Properties defaultProps = new Properties();
        defaultProps.setProperty("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName());
        defaultProps.setProperty("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName());
        SessionConfiguration sessionConfig = new SessionConfiguration(defaultProps);
    
        return kbase.newKieSession(sessionConfig, EnvironmentFactory.newEnvironment());
    }

    protected KieSession restoreSession(KieSession ksession, boolean useCache) {
        return ksession;
    }

    protected KieSession restoreSession(KieSession ksession) {
        return ksession;
    }

    protected void assertProcessInstanceCompleted(long processInstanceId, KieSession ksession) {
        assertNull(ksession.getProcessInstance(processInstanceId));
    }

    protected void assertProcessInstanceAborted(long processInstanceId, KieSession ksession) {
        assertNull(ksession.getProcessInstance(processInstanceId));
    }

    protected void assertProcessInstanceActive(long processInstanceId, KieSession ksession) {
        assertNotNull(ksession.getProcessInstance(processInstanceId));
    }
    
}
