package org.jbpm.test.util;

import org.jbpm.process.instance.impl.util.LoggingPrintStream;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBaseTest {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            logger.info("Starting {}", description.getMethodName());
        }

        protected void finished(Description description) {
            logger.info("Finished {}", description);
        }
    };
    
    @After
    public void cleanup() {
    	EntityManagerFactoryManager.get().clear();
    }
    
    @BeforeClass
    public static void configure() { 
        LoggingPrintStream.interceptSysOutSysErr();
    }
    
    @AfterClass
    public static void reset() { 
        LoggingPrintStream.resetInterceptSysOutSysErr();
    }
}
