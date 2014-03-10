package org.drools.example.cdi.cdiexample;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;

import static org.junit.Assert.*;

public class CDIInstanceExampleTest {

    @Test
    public void testGo() {
        Weld w = new Weld();
        WeldContainer wc = w.initialize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        CDIInstanceExample bean = wc.instance().select(CDIInstanceExample.class).get();
        bean.go(ps);

        ps.close();

        String actual = new String(baos.toByteArray());
        String expected = "" +
                          "Dave: Hello, HAL. Do you read me, HAL?\n" +
                          "HAL: Dave. I read you.\n";
        assertEquals(expected, actual);

        w.shutdown();
    }
}
