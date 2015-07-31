package org.jbpm.casemgmt;

import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

public class CaseMgmtAdHocTest extends JbpmJUnitBaseTestCase {
    
    public CaseMgmtAdHocTest() {
        super(true, true);
    }
    
    @Test
    public void testAdHoc() {
        addWorkItemHandler("Milestone", new SystemOutWorkItemHandler());
        createRuntimeManager("CaseUserTask.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        
        Process[] cases = caseMgmtService.getAvailableCases();
        prettyPrintCases(cases);
        assertEquals(1, cases.length);
        
        ProcessInstance processInstance = runtimeEngine.getKieSession().startProcess("CaseUserTask");
        
        NodeInstanceLog[] nodes = caseMgmtService.getActiveNodes(processInstance.getId());
        prettyPrintActiveNodes(nodes);
        assertEquals(1, nodes.length);
        
        String[] names = caseMgmtService.getAdHocFragmentNames(processInstance.getId());
        prettyPrintNames(names);
        
        caseMgmtService.triggerAdHocFragment(processInstance.getId(), "Hello2");

        nodes = caseMgmtService.getActiveNodes(processInstance.getId());
        prettyPrintActiveNodes(nodes);
        assertEquals(2, nodes.length);
    }
    
    public void prettyPrintCases(Process[] cases) {
        System.out.println("***** Available cases: *****");
        for (Process process: cases) {
            System.out.println(process.getName() + " (id=" + process.getId() + ")");
        }
    }
    
    public void prettyPrintNames(String[] names) {
        System.out.println("***** Ad-hoc fragments: *****");
        for (String name: names) {
            System.out.println(name);
        }
    }
    
    public void prettyPrintActiveNodes(NodeInstanceLog[] nodes) {
        System.out.println("***** Active nodes: *****");
        for (NodeInstanceLog node: nodes) {
            System.out.println(node.getNodeName() + " (id=" + node.getNodeInstanceId() + ")");
        }
    }
    
}
