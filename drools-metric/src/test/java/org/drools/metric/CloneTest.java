package org.drools.metric;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class CloneTest extends AbstractMetricTest {

    @Test
    public void testComplexEval() {
        String drl =
                "rule R1 when\n" +
                     "    $s : String()\n" +
                     "    Integer()\n" +
                     "    not( ( eval($s.length() < 2) and (eval(true) or eval(false))))\n" +
                     "then \n" +
                     "end\n";

        KieSession kieSession = new KieHelper().addContent(drl, ResourceType.DRL)
                                               .build().newKieSession();

        kieSession.insert(42);
        kieSession.insert("test");
        assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }
}
