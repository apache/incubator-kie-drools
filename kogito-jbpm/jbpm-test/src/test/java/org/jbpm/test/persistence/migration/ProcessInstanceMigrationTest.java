package org.jbpm.test.persistence.migration;


import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstanceUpgrader;

import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.junit.Before;

import org.junit.Test;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.WorkflowProcess;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;

public class ProcessInstanceMigrationTest extends JbpmJUnitBaseTestCase {

    public ProcessInstanceMigrationTest() {
        super(true, true);
    }

    private RuntimeManager manager;
    private RuntimeEngine engine;
    private KieSession ksession;
    private TaskService taskService;
    private BitronixTransactionManager transactionManager;

    @Before
    public void init() throws Exception {
        manager = createRuntimeManager("migration/sample.bpmn2", "migration/sample2.bpmn2",
                "migration/BPMN2-ProcessVersion-3.bpmn2", "migration/BPMN2-ProcessVersion-4.bpmn2",
                "migration/sample3.bpmn2", "migration/sample4.bpmn2");
        engine = manager.getRuntimeEngine(null);
        ksession = engine.getKieSession();
        taskService = engine.getTaskService();

        transactionManager = TransactionManagerServices.getTransactionManager();
        transactionManager.setTransactionTimeout(3600); // longer timeout
                                                        // for a debugger
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testProcessInstanceMigration() throws Exception {
        ProcessInstance p = ksession.startProcess("com.sample.bpmn.migration");
        long pid = p.getId();

        assertEquals("com.sample.bpmn.migration", ksession.getProcessInstance(pid).getProcessId());

        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(list);
        assertEquals(1, list.size());

        // upgrade to version to of the process
        UpgradeCommand c = new UpgradeCommand(pid, null, "com.sample.bpmn.migration2");
        ksession.execute(c);

        TaskSummary task = list.get(0);
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        // in second version of the process second user task is for mary while
        // for first version it's for john
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        assertNotNull(list);
        assertEquals(1, list.size());

        assertEquals("com.sample.bpmn.migration2", ksession.getProcessInstance(pid).getProcessId());

        EntityManager em = getEmf().createEntityManager();
        Query query = em.createQuery(
                        "select p from ProcessInstanceInfo p where p.processInstanceId = :pid")
                .setParameter("pid", pid);
        List<ProcessInstanceInfo> found = query.getResultList();

        assertNotNull(found);
        assertEquals(1, found.size());

        ProcessInstanceInfo instance = found.get(0);
        assertEquals("com.sample.bpmn.migration2", instance.getProcessId());

        Thread.sleep(400);

        manager.disposeRuntimeEngine(engine);
    }
    
    @Test
    public void testProcessInstanceMigrationWithGatewaysAndSameUniqueId() throws Exception {
            
        PersistenceWorkItemHandler handler = new PersistenceWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 5);
        
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("com.sample.BPMN2ProcessVersion3", params);
        long pid = processInstance.getId();
        
        assertEquals(1, processInstance.getNodeInstances().size());
        
        Map<String, Long> mapping = new HashMap<String, Long>();
        mapping.put(
                String.valueOf(getNodeId(ksession, "com.sample.BPMN2ProcessVersion3", "UserTask")),
                getNodeId(ksession, "com.sample.BPMN2ProcessVersion4", "StartTask"));
    
        
        // upgrade to version to of the process
        UpgradeCommand c = new UpgradeCommand(pid, mapping, "com.sample.BPMN2ProcessVersion4");
        ksession.execute(c);
    
        assertEquals(1, processInstance.getNodeInstances().size());
        
        NodeInstance current = processInstance.getNodeInstances().iterator().next();
                
        handler.completeWorkItem(((WorkItemNodeInstance)current).getWorkItem(), ksession.getWorkItemManager());
        
        handler.completeWorkItem(handler.getWorkItem("FirstPath"), ksession.getWorkItemManager());
        handler.completeWorkItem(handler.getWorkItem("SecondPath"), ksession.getWorkItemManager());
        
        // process instance should be null == completed
        assertNull(ksession.getProcessInstance(pid));
        // check variable value if it get updated
        List<? extends VariableInstanceLog> vars = engine.getAuditService().findVariableInstances(pid, "x");
        assertNotNull(vars);
        assertEquals(2, vars.size());
        assertEquals("10", vars.get(1).getValue());
        
        manager.disposeRuntimeEngine(engine);
        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testProcessInstanceMigrationExplicit() throws Exception {
        ProcessInstance p = ksession.startProcess("com.sample.bpmn.migration");
        long pid = p.getId();

        assertEquals("com.sample.bpmn.migration", ksession.getProcessInstance(pid).getProcessId());

        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(list);
        assertEquals(1, list.size());
        
        assertEquals(ProcessInstance.STATE_ACTIVE, p.getState());
        Map<String, Long> mapping = new HashMap<String, Long>();
        mapping.put(
            String.valueOf(getNodeId(((RuleFlowProcess)ksession.getKieBase().getProcess("com.sample.bpmn.migration")).getNodes(), "Task 1")),
            getNodeId(((RuleFlowProcess)ksession.getKieBase().getProcess("com.sample.bpmn.migration2")).getNodes(), "Task 2"));

        // upgrade to version to of the process
        UpgradeCommand c = new UpgradeCommand(pid, mapping, "com.sample.bpmn.migration2");
        ksession.execute(c);
        
        assertEquals("com.sample.bpmn.migration2", ksession.getProcessInstance(pid).getProcessId());

        // in second version of the process second user task is for mary while
        // for first version it's for john
        list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(list);
        assertEquals(1, list.size());

        assertEquals("com.sample.bpmn.migration2", ksession.getProcessInstance(pid).getProcessId());

        EntityManager em = getEmf().createEntityManager();
        Query query = em.createQuery(
                        "select p from ProcessInstanceInfo p where p.processInstanceId = :pid")
                .setParameter("pid", pid);
        List<ProcessInstanceInfo> found = query.getResultList();

        assertNotNull(found);
        assertEquals(1, found.size());

        ProcessInstanceInfo instance = found.get(0);
        assertEquals("com.sample.bpmn.migration2", instance.getProcessId());

        Thread.sleep(400);

        manager.disposeRuntimeEngine(engine);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testProcessInstanceMigrationExplicitSubprocesses() throws Exception {
        
        PersistenceWorkItemHandler handler = new PersistenceWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("com.sample.bpmn.migration3");
        long pid = processInstance.getId();

        assertEquals("com.sample.bpmn.migration3", ksession.getProcessInstance(pid).getProcessId());
        
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        Map<String, String> mapping = new HashMap<String, String>();
        mapping.put("ForJohn", "ForMary");

        ExplicitUpgradeCommand c = new ExplicitUpgradeCommand(pid, mapping, "com.sample.bpmn.migration4");
        ksession.execute(c);
        
        assertEquals("com.sample.bpmn.migration4", ksession.getProcessInstance(pid).getProcessId());
        
        handler.completeWorkItem(handler.getWorkItem("ForJohn"), ksession.getWorkItemManager());
        handler.completeWorkItem(handler.getWorkItem("ForBill"), ksession.getWorkItemManager());
        
        EntityManager em = getEmf().createEntityManager();
        Query query = em.createQuery(
                        "select p from ProcessInstanceInfo p where p.processInstanceId = :pid")
                .setParameter("pid", pid);
        List<ProcessInstanceInfo> found = query.getResultList();

        assertNotNull(found);
        assertEquals(0, found.size()); //process is completed


        Thread.sleep(400);

        manager.disposeRuntimeEngine(engine);
    }
    
    private static Long getNodeId(Node[] nodes, String nodeName) {
        for(Node node : nodes) {
            if(node.getName().compareTo(nodeName) == 0) {
                return node.getId();
            }
        }
        
        throw new IllegalArgumentException("No node with name " + nodeName);
    }
    
    private static Long getNodeId(KieSession kSession, String processId, String nodeName) {
        Node[] nodes = ((WorkflowProcess) kSession.getKieBase().getProcess(processId)).getNodes();
        
        for(Node node : nodes) {
            if(node.getName().compareTo(nodeName) == 0) {
                return node.getId();
            }
        }
        
        throw new IllegalArgumentException("NO node with name " + nodeName);
    }
    
    private class PersistenceWorkItemHandler implements WorkItemHandler {
    
        private List<WorkItem> workItems = new ArrayList<WorkItem>();
        
        
        public WorkItem getWorkItem(String nodeName) {
            for(WorkItem item : workItems) {
                if(((String) item.getParameter("NodeName")).compareTo(nodeName) == 0) {
                    return item;
                }
            }
            
            return null;
        }
        
        public void completeWorkItem(WorkItem workItem, WorkItemManager manager) {
            manager.completeWorkItem(workItem.getId(), null);
            workItems.remove(workItem);
        }
        
        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            workItems.add(workItem);
        }
        
        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            workItems.remove(workItem);
            manager.abortWorkItem(workItem.getId());
        }
    }

    private static class UpgradeCommand implements GenericCommand<Object> {

        private static final long serialVersionUID = -626809842544969669L;

        private long pid;
        private Map<String, Long> mapping;
        private String toProcessId;

        private UpgradeCommand(long pid, Map<String, Long> mapping, String toProcessId) {
            this.pid = pid;
            this.mapping = mapping;
            this.toProcessId = toProcessId;
        }

        public Object execute(org.kie.internal.command.Context arg0) {
            KieSession ksession = ((KnowledgeCommandContext) arg0).getKieSession();

            WorkflowProcessInstanceUpgrader.upgradeProcessInstance(ksession,
                    pid, toProcessId, mapping);

            return null;

        }
    }

    private static class ExplicitUpgradeCommand implements GenericCommand<Object> {
        private static final long serialVersionUID = 8673518721648293556L;
        
        private long pid;
        private Map<String, String> mapping;
        private String toProcessId;

        private ExplicitUpgradeCommand(long pid, Map<String, String> mapping, String toProcessId) {
            this.pid = pid;
            this.mapping = mapping;
            this.toProcessId = toProcessId;
        }

        public Object execute(org.kie.internal.command.Context arg0) {
            KieSession ksession = ((KnowledgeCommandContext) arg0).getKieSession();

            WorkflowProcessInstanceUpgrader.upgradeProcessInstanceByNodeNames(
                    ksession,
                    pid,
                    toProcessId,
                    mapping);

            return null;

        }
    }
}
