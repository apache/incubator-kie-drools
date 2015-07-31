package org.jbpm.casemgmt;

import java.util.Map;

import org.jbpm.casemgmt.role.Role;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Task;

public interface CaseMgmtService {
    
    /** Process instance description */
    
    String getProcessInstanceDescription(long processInstanceId);
    
    /** Roles **/
    
    Map<String, Role> getCaseRoles(String processId);
    
    String[] getCaseRoleNames(String processId);
    
    Map<String, String[]> getCaseRoleInstanceNames(long processInstanceId);
    
    void addUserToRole(long processInstanceId, String roleName, String userId);
    
    void setCaseRoleInstance(long processInstanceId, String roleName, String[] userIds);
    
    /** New Case **/
    
    ProcessInstance startNewCase(String name);
    
    Process[] getAvailableCases();
    
    Process[] getAvailableProcesses();
    
    /** Case File **/
    
    Map<String, Object> getCaseData(long processInstanceId);
    
    void setCaseData(long processInstanceId, String name, Object data);
    
    /** Ad-hoc **/
    
    String[] getAdHocFragmentNames(long processInstanceId);
    
    void triggerAdHocFragment(long processInstanceId, String name);
    
    /** Dynamic **/
    
    void createDynamicProcess(long processInstanceId, String processId, 
                              Map<String, Object> parameters);
    
    void createDynamicHumanTask(long processInstanceId, String taskName,
                                String actorIds, String groupIds, String comment,
                                Map<String, Object> parameters);
    
    void createDynamicWorkTask(long processInstanceId, String workName,
                               Map<String, Object> workParams);
    
    /** Milestones **/
    
    Map<String, String> getMilestones(String processId);
    
    String[] getMilestoneNames(String processId);
    
    String[] getAchievedMilestones(long processInstanceId);
    
    /** Overview **/
    
    Task[] getActiveTasks(long processInstanceId);
    
    ProcessInstance[] getActiveSubProcesses(long processInstanceId);
    
    NodeInstanceLog[] getActiveNodes(final long processInstanceId);

}
