package org.jbpm.casemgmt;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.casemgmt.role.Role;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class CaseMgmtDescriptionTest extends JbpmJUnitBaseTestCase {
    
    public CaseMgmtDescriptionTest() {
        super(true, true);
    }
    
    @Test
    public void testDescription() {
        createRuntimeManager("CaseUserTask.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "'My example case'");
        ProcessInstance processInstance = runtimeEngine.getKieSession().startProcess("CaseUserTask", params);
        
        String description = caseMgmtService.getProcessInstanceDescription(processInstance.getId());
        System.out.println("Process instance description: " + description);
        assertEquals("Case 'My example case'", description);
    }
    
}
