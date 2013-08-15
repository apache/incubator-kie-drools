package org.jbpm.test.util;

import org.jbpm.marshalling.util.TestMarshallingUtilsTest;
import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBaseTest {
    
    private static final Logger log = LoggerFactory.getLogger(TestMarshallingUtilsTest.class);

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
