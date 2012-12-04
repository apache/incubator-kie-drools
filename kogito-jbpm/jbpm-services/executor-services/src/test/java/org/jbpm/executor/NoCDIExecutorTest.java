/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.executor;

import org.junit.After;
import org.junit.Before;

/**
 *
 * @author salaboy
 */
public class NoCDIExecutorTest extends BasicExecutorBaseTest{
    
    
    public NoCDIExecutorTest() {
    }


    @Before
    public void setUp() {
        executor = ExecutorModule.getInstance().getExecutorServiceEntryPoint();
        executor.setThreadPoolSize(1);
        executor.setInterval(3);
        executor.init();
    }

    @After
    public void tearDown() {
        executor.clearAllRequests();
        executor.clearAllErrors();
        executor.destroy();
    }

   
}
