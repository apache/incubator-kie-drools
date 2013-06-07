package org.jbpm.examples.exceptions;

import org.junit.Test;

public class ExceptionHandlingExamplesTest {

    @Test
    public void testScriptException() { 
        ScriptTaskExceptionExample.runExample();
    }
    
    @Test
    public void testWithError() { 
        ExceptionHandlingErrorExample.runExample();
    }
    
    @Test
    public void testWithSignal() { 
        ExceptionHandlingSignalExample.runExample();
    }
    
}
