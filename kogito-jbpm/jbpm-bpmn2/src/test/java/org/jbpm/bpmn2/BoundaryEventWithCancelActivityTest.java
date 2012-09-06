package org.jbpm.bpmn2;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgePackage;
import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.bpmn2.JbpmBpmn2TestCase.TestWorkItemHandler;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.junit.Test;

public class BoundaryEventWithCancelActivityTest extends JbpmBpmn2TestCase {

    public void testConditionalBoundaryEventInterrupting() throws Exception {
        KnowledgeBase kbase = readKnowledgeBaseFromDisc("BPMN2-ConditionalBoundaryEventInterrupting.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("ConditionalBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        ksession = restoreSession(ksession, true);
        Person person = new Person();
        person.setName("john");
        ksession.insert(person);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello", "StartSubProcess",
                "Task", "BoundaryEvent", "Goodbye", "EndProcess");
    }
    
    public void testSignalBoundaryEventInterrupting() throws Exception {
        KnowledgeBase kbase = readKnowledgeBaseFromDisc("BPMN2-SignalBoundaryEventInterrupting.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("SignalBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        ksession = restoreSession(ksession, true);
        ksession.signalEvent("MyMessage", null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    private KnowledgeBase readKnowledgeBaseFromDisc(String process) throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(process), ResourceType.BPMN2);
        File packageFile = null;
        // build and store compiled package
        for (KnowledgePackage pkg : kbuilder.getKnowledgePackages()) {
            packageFile = new File(System.getProperty("java.io.tmpdir") + File.separator + pkg.getName() + ".pkg");
            writePackage(pkg, packageFile);

            // store first package only
            break;
        }
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newFileResource(packageFile), ResourceType.PKG);

        return kbuilder.newKnowledgeBase();
    }
    
    private void writePackage(KnowledgePackage kpackage, File p1file)
            throws IOException, FileNotFoundException {
        FileOutputStream out = new FileOutputStream(p1file);
        try {
            DroolsStreamUtils.streamOut(out, kpackage);
        } finally {
            out.close();
        }
    }

}
