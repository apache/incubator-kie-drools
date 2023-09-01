package org.drools.compiler;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.fail;

public class CompilerTest {

    @Test
    public void test() throws Exception {
        String drl =
                "rule R when\n" +
                "    $s: String()" +
                "then\n" +
                "end";

        try {
            new KieHelper().addContent( drl, ResourceType.DRL ).build();
            fail("trying to build without drools-mvel on classpath should throw an exception");
        } catch (RuntimeException e) {
            // expected
        }
    }
}
