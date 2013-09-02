package org.jbpm.test.util;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBaseTest {
    
    private static final Logger log = LoggerFactory.getLogger(AbstractBaseTest.class);

    protected boolean useLocking;
    
    @BeforeClass
    public static void configure() { 
        BpmnDebugPrintStream.interceptSysOutSysErr();
    }
    
    @AfterClass
    public static void reset() { 
        BpmnDebugPrintStream.resetInterceptSysOutSysErr();
    }
}
