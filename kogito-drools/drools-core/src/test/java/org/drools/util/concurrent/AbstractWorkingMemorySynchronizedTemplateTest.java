package org.drools.util.concurrent;

import junit.framework.TestCase;

import org.drools.DroolsException;
import org.drools.DroolsRuntimeException;
import org.drools.WorkingMemory;
import org.drools.WorkingMemoryTemplate.Callback;
import org.easymock.MockControl;
import org.easymock.container.EasymockContainer;

public class AbstractWorkingMemorySynchronizedTemplateTest extends TestCase {

    protected EasymockContainer mocks = new EasymockContainer();

    protected MockControl controlWorkingMemory = mocks.createControl(WorkingMemory.class);
    protected WorkingMemory mockWorkingMemory = (WorkingMemory) controlWorkingMemory.getMock();

    protected MockControl controlCallback = mocks.createControl(Callback.class);
    protected Callback mockCallback = (Callback) controlCallback.getMock();

    protected AbstractWorkingMemorySynchronizedTemplate getTemplateUnderTest() {
        return new AbstractWorkingMemorySynchronizedTemplate() {
            protected WorkingMemory getWorkingMemory() {
                return mockWorkingMemory;
            }
        };
    }

    private AbstractWorkingMemorySynchronizedTemplate template;

    protected void setUp() throws Exception {
        super.setUp();
        template = getTemplateUnderTest();
    }

    public void testExecute() throws Exception {
        Object expectedResult = new Object();
        controlCallback.expectAndReturn(
                (mockCallback).doInWorkingMemory(mockWorkingMemory), expectedResult);
        mocks.replay();

        Object result = template.execute(mockCallback);

        mocks.verify();
        assertSame(expectedResult, result);
    }

    public void testExecuteDroolsException() throws Exception {
        DroolsException expectedException = new DroolsException();
        controlCallback.expectAndThrow(
                (mockCallback).doInWorkingMemory(mockWorkingMemory), expectedException);
        mocks.replay();

        try {
            template.execute(mockCallback);
            fail("expected DroolsRuntimeException");
        } catch (DroolsRuntimeException e) {
            // expected
        }

        mocks.verify();
    }
}
