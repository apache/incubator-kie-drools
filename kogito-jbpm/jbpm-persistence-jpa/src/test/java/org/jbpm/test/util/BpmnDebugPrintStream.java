package org.jbpm.test.util;

import java.io.OutputStream;

import org.jbpm.process.instance.impl.util.LoggingPrintStream;

public class BpmnDebugPrintStream extends LoggingPrintStream {

    public BpmnDebugPrintStream(OutputStream outputStream, boolean isError) {
        super(outputStream, isError);
    }

    protected void log(String s) { 
        StackTraceElement [] trace = Thread.currentThread().getStackTrace();
        boolean debug = trace[4].getMethodName().matches("(action|invoke)\\d+");
        if (isError) {
            logger.error(s);
        } else {
            if( debug ) { 
                logger.debug(s);
            } else { 
                logger.info(s);
            }
                
        }
    }
    
    public static void interceptSysOutSysErr() {
        System.setOut(new BpmnDebugPrintStream(System.out, false));
        System.setErr(new BpmnDebugPrintStream(System.err, true));
    }
}
