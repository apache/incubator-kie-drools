package org.drools.example.api.kiecontainerfromkierepo;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class KieContainerFromKieRepoExampleTest {

    private static final String NL = System.getProperty("line.separator");
    
    @Test
    public void testGo() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        new KieContainerFromKieRepoExample().go(ps);
        ps.close();

        String actual = baos.toString();
        String expected = "" +
                          "Dave: Hello, HAL. Do you read me, HAL?" + NL +
                          "HAL: Dave. I read you." + NL;
        assertEquals(expected, actual);
    }
}
