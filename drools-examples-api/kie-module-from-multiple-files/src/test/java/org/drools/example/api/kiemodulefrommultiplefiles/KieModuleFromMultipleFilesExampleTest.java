package org.drools.example.api.kiemodulefrommultiplefiles;

import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class KieModuleFromMultipleFilesExampleTest {

    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testGo() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new KieModuleFromMultipleFilesExample().go(ps);
        ps.close();

        String actual = new String(baos.toByteArray());
        String expected = "" +
                          "Dave: Hello, HAL. Do you read me, HAL?" + NL +
                          "HAL: Dave. I read you." + NL +
                          "Dave: Open the pod bay doors, HAL." + NL +
                          "HAL: I'm sorry, Dave. I'm afraid I can't do that." + NL;
        assertEquals(expected, actual);
    }
}
