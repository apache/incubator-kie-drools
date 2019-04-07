/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
