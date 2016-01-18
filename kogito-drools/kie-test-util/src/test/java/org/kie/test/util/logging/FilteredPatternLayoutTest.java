/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.test.util.logging;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilteredPatternLayoutTest {

    private static ByteArrayOutputStream baos;
    private static PrintStream originalOut;

    @BeforeClass
    public static void setup() {
        baos = new ByteArrayOutputStream();
        PrintStream loggingStream = new PrintStream(baos);
        originalOut = System.out;
        System.setOut(loggingStream);
    }

    @AfterClass
    public static void cleanup() {
        System.setOut(originalOut);
    }

    @Test
    public void layoutTest() {
        Logger logger = LoggerFactory.getLogger(FilteredPatternLayoutTest.class);

        String msg = "logger test exception";
        logger.warn(msg, new RuntimeException(msg));

        String [] filtered  = {
                "sun.reflect",
                "org.junit",
                "org.eclipse.jdt.internal"
        };
        String output = new String(baos.toByteArray());
        for( String pkgName : filtered ) {
            assertFalse( "Package name in logging: " + pkgName, output.contains(pkgName) );
        }
    }

}
