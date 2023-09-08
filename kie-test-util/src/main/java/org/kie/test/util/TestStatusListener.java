/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.test.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runners.model.TestTimedOutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestStatusListener extends RunListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestStatusListener.class);

    private final BufferedWriter writer;

    public TestStatusListener() {
        try {
            this.writer = Files.newBufferedWriter(
                    Paths.get("./target/testStatusListener"
                                      + "." + ManagementFactory.getRuntimeMXBean().getName().replaceAll("\\W+", "")
                                      + "." + System.nanoTime()
                                      + ".log"));
        } catch (final IOException e) {
            throw new RuntimeException( "TestStatusListener unable to open writer for logging to file test status updates.", e );
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (writer != null) {
            writer.close();
        }
    }

    private synchronized void write(final String method, final Description description) throws IOException {
        writer.write(method);
        writer.write("\t");
        writer.write(description.getClassName());
        writer.write(".");
        if ( description.getMethodName() != null ) { writer.write(description.getMethodName()); }
        writer.newLine();
        writer.flush();
    }
    
    private synchronized void write(final String method, final Failure failure) throws IOException {
        writer.write(method);
        writer.write("\t");
        if ( failure.getMessage() != null ) { writer.write(failure.getMessage()); }
        writer.newLine();
        writer.flush();
    }
    
    private synchronized void write(final String method, final Result result) throws IOException {
        writer.write(method);
        writer.write("\t");
        writer.write(result.toString());
        writer.newLine();
        writer.flush();
    }
    
    private synchronized void writeThreadDump() throws IOException {
        final ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
        for (final ThreadInfo ti : threadMxBean.dumpAllThreads(true, true)) {
            writer.write( ti.toString() );
            if ( ti.getStackTrace().length > 8 ) { writer.write("full stacktrace:\n"); writer.write(fullStackTrace(ti)); writer.write("\n"); }
        }
        writer.newLine();
        writer.flush();
    }
    
    private static String fullStackTrace(final ThreadInfo ti) {
        final StringBuilder sb = new StringBuilder();
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
    public void testRunStarted(final Description description) throws Exception {
        write("testRunStarted", description);
    }

    @Override
    public void testRunFinished(final Result result) throws Exception {
        write("testRunFinished", result);
    }

    @Override
    public void testStarted(final Description description) throws Exception {
        write("testStarted", description);
    }

    @Override
    public void testFinished(final Description description) throws Exception {
        write("testFinished", description);
    }

    @Override
    public void testFailure(final Failure failure) throws Exception {
        if ( failure.getException() instanceof TestTimedOutException ) {
            writeThreadDump();
        }
        write("testFailure", failure);
    }

    @Override
    public void testAssumptionFailure(final Failure failure) {
        try {
            write("testAssumptionFailure", failure);
        } catch (final IOException e) {
            // can't do anything at this point to report it to log file
            LOG.error("Exception", e);
        }
    }

    @Override
    public void testIgnored(final Description description) throws Exception {
        write("testIgnored", description);
    }
    
}
