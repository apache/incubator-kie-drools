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
