package org.drools.example.api.namedkiesession;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class NamedKieSessionExampleTest {
    private static final String NL = System.getProperty("line.separator");

    @Test
    public void testGo() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new NamedKieSessionExample().go(ps);
        ps.close();

        String actual = new String(baos.toByteArray());
        String expected = "" +
                "Dave: Hello, HAL. Do you read me, HAL?" + NL +
                "HAL: Dave. I read you." + NL;
        assertEquals(expected, actual);
    }
}
