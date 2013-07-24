package org.jbpm.test.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractBaseTest {
    
    protected static AtomicInteger uniqueIdGen = new AtomicInteger(0);
    
    @BeforeClass
    public static void configure() { 
        LoggingPrintStream.interceptSysOutSysErr();
    }
    
    @AfterClass
    public static void reset() { 
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }
}
