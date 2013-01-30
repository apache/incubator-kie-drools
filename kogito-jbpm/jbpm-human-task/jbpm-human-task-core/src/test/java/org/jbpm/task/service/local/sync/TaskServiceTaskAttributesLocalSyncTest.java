package org.jbpm.task.service.local.sync;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.task.service.base.sync.TaskServiceTaskAttributesBaseSyncTest;
import org.jbpm.task.service.local.LocalTaskService;

public class TaskServiceTaskAttributesLocalSyncTest extends TaskServiceTaskAttributesBaseSyncTest {

    protected EntityManagerFactory createEntityManagerFactory() { 
        return Persistence.createEntityManagerFactory("org.jbpm.task.local");
    }
    
    @Override
    protected void setUp() throws Exception {
        setupJTADataSource();
        super.setUp();
       
        client = new LocalTaskService(taskService);
    }

}
