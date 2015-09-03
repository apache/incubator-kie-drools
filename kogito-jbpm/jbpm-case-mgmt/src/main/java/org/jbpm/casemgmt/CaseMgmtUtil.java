package org.jbpm.casemgmt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.command.impl.GenericCommand;
import org.jbpm.casemgmt.role.Role;
import org.jbpm.casemgmt.role.RoleInstance;
import org.jbpm.casemgmt.role.impl.RoleImpl;
import org.jbpm.casemgmt.role.impl.RoleInstanceImpl;
import org.jbpm.process.instance.impl.ProcessInstanceImpl;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.jbpm.workflow.instance.node.SubProcessNodeInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.NodeInstanceContainer;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.internal.command.Context;

public class CaseMgmtUtil implements CaseMgmtService {
    
    private KieSession ksession;
    private AuditService auditService;
    private TaskService taskService;
    
    public CaseMgmtUtil(RuntimeEngine engine) {
        this.auditService = engine.getAuditService();
        this.ksession = engine.getKieSession();
        this.taskService = engine.getTaskService();
    }
    
    public CaseMgmtUtil(ProcessContext kcontext) {
        this.ksession = (KieSession) kcontext.getKieRuntime();
    }
    
    /**
     ************************** PROCESS INSTANCE DESCRIPTION **************************
     **/
    
    public String getProcessInstanceDescription(long processInstanceId) {
        return ((ProcessInstanceImpl) getProcessInstance(processInstanceId)).getDescription();
    }

    /**
     ************************** ROLES **************************
     **/

    /**
     * Case roles are currently stored as metadata 'customCaseRoles' of the process
     * (a comma-separated list of role names, cardinality ) 
     */
    public Map<String, Role> getCaseRoles(String processId) {
        Process process = ksession.getKieBase().getProcess(processId);
        String roles = (String) process.getMetaData().get("customCaseRoles");
        if (roles == null) {
            return null;
        }
        Map<String, Role> result = new HashMap<String, Role>();
        String[] roleStrings = roles.split(",");
        for (String roleString: roleStrings) {
            String[] ss = roleString.split(":");
            RoleImpl role = new RoleImpl(ss[0]);
            result.put(role.getName(), role);
            if (ss.length > 1) {
                role.setCardinality(Integer.parseInt(ss[1]));
            }
        }
        return result;
    }
    
    public String[] getCaseRoleNames(String processId) {
        List<String> result = new ArrayList<String>();
        for (Role role: getCaseRoles(processId).values()) {
            result.add(role.getName());
        }
        return result.toArray(new String[result.size()]);
    }
    
    /**
     * Case roles instances are currently stored as process instance variable "CaseRoles"
     * (a Map<String, RoleInstance>)
     */
    @SuppressWarnings("unchecked")
    public Map<String, RoleInstance> getCaseRoleInstances(long processInstanceId) {
        ProcessInstance processInstance = getProcessInstance(processInstanceId);
        return (Map<String, RoleInstance>) 
            ((WorkflowProcessInstance) processInstance).getVariable("CaseRoles");
    }
    
    public Map<String, String[]> getCaseRoleInstanceNames(long processInstanceId) {
        Map<String, String[]> result = new HashMap<String, String[]>();
        ProcessInstance processInstance = getProcessInstance(processInstanceId);
        for (String role: getCaseRoleNames(processInstance.getProcessId())) {
            result.put(role, null);
        }
        Map<String, RoleInstance> roleInstances = getCaseRoleInstances(processInstanceId);
        if (roleInstances != null) {
            for (RoleInstance roleInstance: roleInstances.values()) {
                result.put(roleInstance.getRoleName(),
                    ((RoleInstanceImpl) roleInstance).getRoleAssignmentNames());
            }
        }
        return result;
    }
    
    private ProcessInstance getProcessInstance(long processInstanceId) {
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        if (processInstance == null) {
            throw new IllegalArgumentException("Could not find process instance " + processInstanceId);
        }
        return processInstance;
    }
    
    public void addUserToRole(final long processInstanceId, final String roleName, final String userId) {
        ksession.execute(new GenericCommand<Void>() {
            private static final long serialVersionUID = 630L;
            public Void execute(Context context) {
                Map<String, RoleInstance> roleInstances = getCaseRoleInstances(processInstanceId);
                if (roleInstances == null) {
                    roleInstances = new HashMap<String, RoleInstance>();
                    ((WorkflowProcessInstance) getProcessInstance(processInstanceId)).setVariable("CaseRoles", roleInstances);
                }
                RoleInstance roleInstance = roleInstances.get(roleName);
                if (roleInstance == null) {
                    roleInstance = new RoleInstanceImpl(roleName);
                    roleInstances.put(roleName, roleInstance);
                } else {
                    Role role = getCaseRoles(getProcessInstance(processInstanceId).getProcessId()).get(roleName);
                    if (role != null) {
                        Integer cardinality = role.getCardinality();
                        if (cardinality != null && cardinality > 0) {
                            if (cardinality < roleInstance.getRoleAssignments().size() + 1) {
                                throw new IllegalArgumentException("Cannot add more users for role " + roleName 
                                    + ", maximum cardinality " + cardinality + " already reached");
                            }
                        }
                    }
                }
                roleInstance.addRoleAssignment(userId);
                return null;
            }
        });      
    }

    public void setCaseRoleInstance(final long processInstanceId, final String roleName, final String[] userIds) {
        ksession.execute(new GenericCommand<Void>() {
            private static final long serialVersionUID = 630L;
            public Void execute(Context context) {
                Map<String, RoleInstance> roleInstances = getCaseRoleInstances(processInstanceId);
                if (roleInstances == null) {
                    roleInstances = new HashMap<String, RoleInstance>();
                    ((WorkflowProcessInstance) getProcessInstance(processInstanceId)).setVariable("CaseRoles", roleInstances);
                }
                RoleInstance roleInstance = new RoleInstanceImpl(roleName);
                roleInstances.put(roleName, roleInstance);
                for (String userId: userIds) {
                    roleInstance.addRoleAssignment(userId);
                }
                return null;
            }
        });
    }
    
    public static void addUserToRole(ProcessContext kcontext, String roleName, String userId) {
        new CaseMgmtUtil(kcontext)
            .addUserToRole(kcontext.getProcessInstance().getId(), roleName, userId);
    }

    public static String[] getRoleNames(ProcessContext kcontext, String roleName, String userId) {
        return new CaseMgmtUtil(kcontext)
            .getCaseRoleInstanceNames(kcontext.getProcessInstance().getId()).get(roleName);
    }

    public static String getRoleName(ProcessContext kcontext, String roleName, String userId) {
        String[] roles = getRoleNames(kcontext, roleName, userId);
        if (roles.length == 1) {
            return roles[0];
        }
        return null;
    }

    /**
     ************************** NEW CASE **************************
     */
    
    public ProcessInstance startNewCase(String name) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (name != null) {
            params.put("name", name);
        }
        return ksession.startProcess("EmptyCase", params);
    }
    
    public Process[] getAvailableProcesses() {
        Collection<Process> processes = ksession.getKieBase().getProcesses();
        return processes.toArray(new Process[processes.size()]);
    }
    
    public Process[] getAvailableCases() {
        Collection<Process> processes = ksession.getKieBase().getProcesses();
        List<Process> result = new ArrayList<Process>();
        for (Process process: processes) {
            if (((WorkflowProcessImpl) process).isDynamic()) {
                result.add(process);
            }
        }
        return result.toArray(new Process[result.size()]);
    }
    
    /**
     ************************** CASE FILE **************************
     */
    
    public Map<String, Object> getCaseData(long processInstanceId) {
        return ((WorkflowProcessInstanceImpl) getProcessInstance(processInstanceId)).getVariables();
    }
    
    public void setCaseData(final long processInstanceId, final String name, final Object data) {
        ksession.execute(new GenericCommand<Void>() {
            private static final long serialVersionUID = 630L;
            public Void execute(Context context) {
                ((WorkflowProcessInstance) getProcessInstance(processInstanceId)).setVariable(name, data);
                return null;
            }
        });
    }
    
    /**
     ************************** AD-HOC **************************
     **/
    
    public String[] getAdHocFragmentNames(final long processInstanceId) {
        final List<String> result = new ArrayList<String>();
        ksession.execute(new GenericCommand<Void>() {
            private static final long serialVersionUID = 630L;
            public Void execute(Context context) {
                WorkflowProcessInstance processInstance = 
                    (WorkflowProcessInstance) getProcessInstance(processInstanceId);
                org.jbpm.workflow.core.WorkflowProcess process = (org.jbpm.workflow.core.WorkflowProcess)
                    processInstance.getProcess();
                if (process.isDynamic()) {
                    checkAdHoc(process, result);
                }
                checkNodeInstances(processInstance, result);
                return null;
            }
        });
        return result.toArray(new String[result.size()]);
    }
    
    private void checkAdHoc(NodeContainer nodeContainer, List<String> result) {
        for (Node node : nodeContainer.getNodes()) {
            if (node instanceof StartNode) {
                continue;
            }
            if (node.getIncomingConnections().isEmpty()) {
                result.add(node.getName());
            }
        }
    }
    
    private void checkNodeInstances(NodeInstanceContainer nodeInstanceContainer, List<String> result) {
        for (NodeInstance nodeInstance: nodeInstanceContainer.getNodeInstances()) {
            if (nodeInstance instanceof DynamicNodeInstance) {
                checkAdHoc(((DynamicNode) nodeInstance.getNode()), result);
                checkNodeInstances(((DynamicNodeInstance) nodeInstance), result);
            }
        }
    }
    
    public void triggerAdHocFragment(long processInstanceId, String name) {
        ksession.signalEvent(name, null, processInstanceId);
    }
    
    /**
     ************************** DYNAMIC **************************
     **/
    
    public void createDynamicProcess(long processInstanceId, String processId, 
                                     Map<String, Object> parameters) {
        DynamicUtils.addDynamicSubProcess(
            getProcessInstance(processInstanceId), ksession, processId, parameters);
    }

    public void createDynamicHumanTask(long processInstanceId, String taskName,
                                       String actorIds, String groupIds, String comment,
                                       Map<String, Object> parameters) {
        Map<String, Object> workParams = new HashMap<String, Object>();
        if (parameters != null) {
            workParams.putAll(parameters);
        }
        workParams.put("NodeName", taskName);
        workParams.put("TaskName", taskName);
        workParams.put("ActorId", actorIds);
        workParams.put("GroupId", groupIds);
        workParams.put("Comment", comment);
        DynamicUtils.addDynamicWorkItem(
            getProcessInstance(processInstanceId), ksession, "Human Task", workParams);
    }

    public void createDynamicWorkTask(long processInstanceId, String workName,
                                      Map<String, Object> workParams) {
        DynamicUtils.addDynamicWorkItem(
            getProcessInstance(processInstanceId), ksession, workName, workParams);
    }

    /**
     ************************** MILESTONES **************************
     **/
    
    public Map<String, String> getMilestones(String processId) {
        Process process = ksession.getKieBase().getProcess(processId);
        Map<String, String> result = new HashMap<String, String>();
        getMilestones((WorkflowProcess) process, result);
        return result;
    }
    
    private void getMilestones(NodeContainer container, Map<String, String> result) {
        for (Node node: container.getNodes()) {
            if (node instanceof WorkItemNode) {
                if ("Milestone".equals(((WorkItemNode) node).getWork().getName())) {
                    result.put(node.getName(), (String) node.getMetaData().get("UniqueId"));
                }
            }
            if (node instanceof NodeContainer) {
                getMilestones((NodeContainer) node, result);
            }
        }
    }
    
    public String[] getMilestoneNames(String processId) {
        Map<String, String> milestones = getMilestones(processId);
        return milestones.keySet().toArray(new String[milestones.size()]);
    }
    
    public String[] getAchievedMilestones(long processInstanceId) {
        ProcessInstanceLog processInstance = auditService.findProcessInstance(processInstanceId);
        Map<String, String> milestones = getMilestones(processInstance.getProcessId());
        List<? extends NodeInstanceLog> nodeInstances = auditService.findNodeInstances(processInstanceId);
        Map<String, String> nodes = new HashMap<String, String>();
        for (NodeInstanceLog log: nodeInstances) {
            nodes.put(log.getNodeId(), log.getNodeName());
        }
        List<String> result = new ArrayList<String>();
        for (Map.Entry<String, String> entry: milestones.entrySet()) {
            if (nodes.get(entry.getValue()) != null) {
                result.add(entry.getKey());
            }
        }
        return result.toArray(new String[result.size()]);
    }
    
    /**
     ************************** OVERVIEW **************************
     **/
    
    public Task[] getActiveTasks(final long processInstanceId) {
        final List<Long> workItemIds = new ArrayList<Long>();
        ksession.execute(new GenericCommand<Void>() {
            private static final long serialVersionUID = 630L;
            public Void execute(Context context) {
                ProcessInstance processInstance = getProcessInstance(processInstanceId);
                getActiveTasks((WorkflowProcessInstance) processInstance, workItemIds);
                return null;
            }
        });
        List<Task> result = new ArrayList<Task>();
        for (Long workItemId: workItemIds) {
            result.add(taskService.getTaskByWorkItemId(workItemId));
        }
        return result.toArray(new Task[result.size()]);
    }
    
    private void getActiveTasks(NodeInstanceContainer nodeInstanceContainer, List<Long> workItemIds) {
        for (NodeInstance nodeInstance: nodeInstanceContainer.getNodeInstances()) {
            if (nodeInstance instanceof WorkItemNodeInstance) {
                WorkItemNodeInstance workItemNodeInstance = (WorkItemNodeInstance) nodeInstance;
                if ("Human Task".equals(workItemNodeInstance.getWorkItem().getName())) {
                    workItemIds.add(workItemNodeInstance.getWorkItemId());
                }
            } else if (nodeInstance instanceof NodeInstanceContainer) {
                getActiveTasks((NodeInstanceContainer) nodeInstance, workItemIds);
            }
        }
    }

    public ProcessInstance[] getActiveSubProcesses(final long processInstanceId) {
        final List<ProcessInstance> result = new ArrayList<ProcessInstance>();
        ksession.execute(new GenericCommand<Void>() {
            private static final long serialVersionUID = 630L;
            public Void execute(Context context) {
                ProcessInstance processInstance = getProcessInstance(processInstanceId);
                getActiveSubProcesses((WorkflowProcessInstance) processInstance, result);
                return null;
            }
        });
        return result.toArray(new ProcessInstance[result.size()]);
    }
    
    private void getActiveSubProcesses(NodeInstanceContainer nodeInstanceContainer, List<ProcessInstance> result) {
        for (NodeInstance nodeInstance: nodeInstanceContainer.getNodeInstances()) {
            if (nodeInstance instanceof SubProcessNodeInstance) {
                result.add(ksession.getProcessInstance(
                    ((SubProcessNodeInstance) nodeInstance).getProcessInstanceId()));
            } else if (nodeInstance instanceof NodeInstanceContainer) {
                getActiveSubProcesses((NodeInstanceContainer) nodeInstance, result);
            }
        }
    }

    public NodeInstanceLog[] getActiveNodes(final long processInstanceId) {
        final List<Long> nodes = new ArrayList<Long>();
        ksession.execute(new GenericCommand<Void>() {
            private static final long serialVersionUID = 630L;
            public Void execute(Context context) {
                ProcessInstance processInstance = getProcessInstance(processInstanceId);
                getActiveNodes((WorkflowProcessInstance) processInstance, nodes);
                return null;
            }
        });
        Map<String, NodeInstanceLog> logMap = new HashMap<String, NodeInstanceLog>();
        List<? extends NodeInstanceLog> logs = auditService.findNodeInstances(processInstanceId);
        for (NodeInstanceLog log: logs) {
            NodeInstanceLog oldLog = logMap.get(log.getNodeInstanceId());
            if (oldLog != null) {
                if (oldLog.getDate().before(log.getDate())) {
                    continue;
                }
            }
            logMap.put(log.getNodeInstanceId(), log);
        }
        List<NodeInstanceLog> result = new ArrayList<NodeInstanceLog>();
        for (Long node: nodes) {
            result.add(logMap.get(Long.toString(node)));
        }
        return result.toArray(new NodeInstanceLog[result.size()]);
    }
    
    private void getActiveNodes(NodeInstanceContainer nodeInstanceContainer, List<Long> nodes) {
        for (NodeInstance nodeInstance: nodeInstanceContainer.getNodeInstances()) {
            nodes.add(nodeInstance.getId());
        }
    }

}
