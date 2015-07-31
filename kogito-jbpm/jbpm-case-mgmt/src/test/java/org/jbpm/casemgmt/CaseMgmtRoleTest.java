package org.jbpm.casemgmt;

import java.util.Map;

import org.jbpm.casemgmt.role.Role;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

public class CaseMgmtRoleTest extends JbpmJUnitBaseTestCase {
    
    public CaseMgmtRoleTest() {
        super(true, true);
    }
    
    @Test
    public void testRoles() {
        createRuntimeManager("CaseUserTask.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        
        Map<String, Role> roles = caseMgmtService.getCaseRoles("CaseUserTask");
        prettyPrintRoles(roles);
        assertEquals(3, roles.size());
        
        ProcessInstance processInstance = runtimeEngine.getKieSession().startProcess("CaseUserTask");
        
        Map<String, String[]> roleInstances = caseMgmtService.getCaseRoleInstanceNames(processInstance.getId());
        prettyPrintRoleInstances(roleInstances);
        
        caseMgmtService.addUserToRole(processInstance.getId(), "contact", "mauricio");
        caseMgmtService.addUserToRole(processInstance.getId(), "contact", "tihomir");
        
        caseMgmtService.addUserToRole(processInstance.getId(), "participant", "marco");
        caseMgmtService.addUserToRole(processInstance.getId(), "participant", "maciej");
        caseMgmtService.addUserToRole(processInstance.getId(), "participant", "jeremy");
        
        roleInstances = caseMgmtService.getCaseRoleInstanceNames(processInstance.getId());
        prettyPrintRoleInstances(roleInstances);
        assertEquals(3, roleInstances.size());
        
        try {
            caseMgmtService.addUserToRole(processInstance.getId(), "contact", "marco");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void prettyPrintRoles(Map<String, Role> roles) {
        System.out.println("***** Case roles: *****");
        for (Role role: roles.values()) {
            System.out.print(role.getName());
            Integer cardinality = role.getCardinality();
            if (cardinality != null) {
                System.out.print(" (" + cardinality + ")");
            }
            System.out.println();
        }
    }
    
    public void prettyPrintRoleInstances(Map<String, String[]> roleInstances) {
        System.out.println("***** Case role instances: *****");
        for (Map.Entry<String, String[]> entry: roleInstances.entrySet()) {
            System.out.print(entry.getKey() + " = ");
            String[] roleNames = entry.getValue();
            if (roleNames != null) {
                for (int i = 0; i < roleNames.length; i++) {
                    System.out.print(roleNames[i]);
                    if (i != roleNames.length - 1) {
                        System.out.print(",");
                    }
                }
            }
            System.out.println();
        }
    }

}
