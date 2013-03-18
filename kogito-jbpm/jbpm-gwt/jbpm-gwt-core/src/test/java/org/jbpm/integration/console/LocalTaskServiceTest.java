package org.jbpm.integration.console;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.bpm.console.client.model.TaskRef;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.StatefulKnowledgeSession;
import org.kie.api.runtime.process.NodeInstance;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class LocalTaskServiceTest {

    private static PoolingDataSource pds;
    @BeforeClass
    public static void setup() {
        
        pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-local-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", "sa");
        pds.getDriverProperties().put("password", "");
        pds.getDriverProperties().put("url", "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        pds.getDriverProperties().put("driverClassName", "org.h2.Driver");
        pds.init();

        System.setProperty("jbpm.conf.dir", "./src/test/resources/local");
        System.setProperty("jbpm.console.directory","./src/test/resources");
    }
    
    @AfterClass
    public static void tearDown() {
        pds.close();
        System.clearProperty("jbpm.conf.dir");
        System.clearProperty("jbpm.console.directory");
    }
    
    @Test 
    public void testNewInstance() throws Exception {
       
        StatefulKnowledgeSession session = StatefulKnowledgeSessionUtil.getStatefulKnowledgeSession();
        JPAProcessInstanceDbLog.setEnvironment(session.getEnvironment());
        TaskManagement taskMgmt = new TaskManagement();
        Map<String, Object> params = new HashMap<String, Object>();
        
        String definitionId = "UserTaskLocal";
        
        ProcessInstanceLog processInstance = CommandDelegate.startProcess(definitionId, params);
        Collection<NodeInstance> activeNodes = CommandDelegate.getActiveNodeInstances(processInstance.getId());
        assertNotNull(activeNodes);
        
        
        
        
        List<TaskRef> tasks = taskMgmt.getAssignedTasks("Tony Stark");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        try {
            taskMgmt.completeTask(tasks.get(0).getId(), new HashMap(), "Tony Stark");
            fail("Should fail");
        } catch (Exception e) {
        
        }
        
        
        tasks = taskMgmt.getAssignedTasks("Tony Stark");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        // abort process instance to clean up
        session.abortProcessInstance(processInstance.getProcessInstanceId());
    }
}
