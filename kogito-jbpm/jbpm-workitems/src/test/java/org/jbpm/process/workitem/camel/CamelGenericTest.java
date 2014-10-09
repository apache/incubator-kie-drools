package org.jbpm.process.workitem.camel;

import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.junit.Assert;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * Generic endpoint test - using Class endpoint.
 * */
public class CamelGenericTest extends AbstractBaseTest {

    private static boolean called;

    public void testMethod() {
        called = true;
    }

    @Before
    public void setup() {
        called = false;
    }

    @Test
    public void testClass() {
        CamelHandler handler = CamelHandlerFactory.genericHandler("class", "FQCN");

        final WorkItem workItem = new WorkItemImpl();
        workItem.setParameter("FQCN", getClass().getCanonicalName());
        workItem.setParameter("method", "testMethod");

        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem(workItem, manager);

        Assert.assertTrue(called);
    }
}
