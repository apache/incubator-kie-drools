package ${package};

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.jbpm.test.AbstractBaseTest;
import org.junit.Test;
import static org.junit.Assert.*;

public class ${classPrefix}WorkItemHandlerTest extends AbstractBaseTest {

    @Test
    public void testHandler() throws Exception {
        WorkItemImpl workItem = new WorkItemImpl();
        TestWorkItemManager manager = new TestWorkItemManager();

        ${classPrefix}WorkItemHandler handler = new ${classPrefix}WorkItemHandler();
        handler.setLogThrownException(true);
        handler.executeWorkItem(workItem,
                                manager);

        assertNotNull(manager.getResults());
        assertEquals(1,
                     manager.getResults().size());
        assertTrue(manager.getResults().containsKey(workItem.getId()));
    }
}
