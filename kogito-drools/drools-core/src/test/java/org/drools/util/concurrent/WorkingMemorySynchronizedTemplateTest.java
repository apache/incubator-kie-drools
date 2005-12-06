package org.drools.util.concurrent;


public class WorkingMemorySynchronizedTemplateTest extends AbstractWorkingMemorySynchronizedTemplateTest {

    protected AbstractWorkingMemorySynchronizedTemplate getTemplateUnderTest() {
        return new WorkingMemorySynchronizedTemplate(mockWorkingMemory);
    }

}
