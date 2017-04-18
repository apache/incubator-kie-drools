/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.test.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.TestTimedOutException;

public class TestStatusListener extends RunListener {
    
    private static BufferedWriter writer;
    static {
        try {
            writer = Files.newBufferedWriter(
                    Paths.get("./target/testStatusListener"
                                + "." + ManagementFactory.getRuntimeMXBean().getName().replaceAll("\\W+", "")
                                + "." + System.nanoTime()
                                + ".log"));
        } catch (IOException e) {
            new RuntimeException( "TestStatusListener unable to open writer for logging to file test status updates.", e );
        }
    }
    
    private static synchronized void write(String method, Description description) throws IOException {
        writer.write(method);
        writer.write("\t");
        writer.write(description.getClassName());
        writer.write(".");
        if ( description.getMethodName() != null ) { writer.write(description.getMethodName()); }
        writer.newLine();
        writer.flush();
    }
    
    private static synchronized void write(String method, Failure failure) throws IOException {
        writer.write(method);
        writer.write("\t");
        if ( failure.getMessage() != null ) { writer.write(failure.getMessage()); }
        writer.newLine();
        writer.flush();
    }
    
    private static synchronized void write(String method, Result result) throws IOException {
        writer.write(method);
        writer.write("\t");
        writer.write(result.toString());
        writer.newLine();
        writer.flush();
    }
    
    private static synchronized void writeThreadDump() throws IOException {
        ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo ti : threadMxBean.dumpAllThreads(true, true)) {
            writer.write( ti.toString() );
            if ( ti.getStackTrace().length > 8 ) { writer.write("full stacktrace:\n"); writer.write(fullStackTrace(ti)); writer.write("\n"); }
        }
        writer.newLine();
        writer.flush();
    }
    
    private static String fullStackTrace(ThreadInfo ti) {
        StringBuilder sb = new StringBuilder();
        Stream.of( ti.getStackTrace() )
            .forEach( ste -> {
                sb.append(" ");
                sb.append(ste.toString());
                sb.append("\n");
                Stream.of( ti.getLockedMonitors() )
                    .filter( m -> ste.equals(m.getLockedStackFrame()) )
                    .forEach( lm -> {
                        sb.append("  (locked: ");
                        sb.append(lm);
                        sb.append(" )\n");
                    });
            });
        return sb.toString();
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        write("testRunStarted", description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        write("testRunFinished", result);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        write("testStarted", description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        write("testFinished", description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        if ( failure.getException() instanceof TestTimedOutException ) {
            writeThreadDump();
        }
        write("testFailure", failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        try {
            write("testAssumptionFailure", failure);
        } catch (IOException e) {
            // can't do anything at this point to report it to log file
            e.printStackTrace();
        }
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        write("testIgnored", description);
    }
    
}
