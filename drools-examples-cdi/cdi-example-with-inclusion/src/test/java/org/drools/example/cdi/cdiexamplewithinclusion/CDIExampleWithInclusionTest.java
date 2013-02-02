package org.drools.example.cdi.cdiexamplewithinclusion;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class CDIExampleWithInclusionTest {

    @Test
    public void testGo() {
        Weld w = new Weld();
        WeldContainer wc = w.initialize();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);

        CDIExampleWithInclusion bean = wc.instance().select(CDIExampleWithInclusion.class).get();
        bean.go(ps);

        ps.close();

        String actual = new String(baos.toByteArray());
        String expected = "" +
                          "Dave: Hello, HAL. Do you read me, HAL?\n" +
                          "HAL: Dave. I read you.\n" +
                          "Dave: Open the pod bay doors, HAL.\n" +
                          "HAL: I'm sorry, Dave. I'm afraid I can't do that.\n";
        assertEquals(expected, actual);

        w.shutdown();
    }
}
