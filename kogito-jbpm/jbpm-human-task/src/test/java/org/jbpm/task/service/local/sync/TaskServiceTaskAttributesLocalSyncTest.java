package org.jbpm.task.service.local.sync;

import org.jbpm.task.service.base.sync.TaskServiceTaskAttributesBaseSyncTest;
import org.jbpm.task.service.local.LocalTaskService;

public class TaskServiceTaskAttributesLocalSyncTest extends TaskServiceTaskAttributesBaseSyncTest {

	@Override
    protected void setUp() throws Exception {
        super.setUp();
       
        client = new LocalTaskService(taskSession);
        
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        
    }
}
