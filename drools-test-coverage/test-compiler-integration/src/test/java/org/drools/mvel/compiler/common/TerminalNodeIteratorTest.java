package org.drools.mvel.compiler.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.serialization.protobuf.iterators.TerminalNodeIterator;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.util.Iterator;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class TerminalNodeIteratorTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public TerminalNodeIteratorTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testTerminalNodeListener() {
        String str = "package org.kie.test \n" +
                     "\n" +
                     "rule rule1 when\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule2 when\n" +
                     "then\n" +
                     "end\n" +
                     "rule rule3 when\n" +
                     "    Object()" +
                     "then\n" +
                     "end\n" +
                     "rule rule4 when\n" +
                     "    Object()" +
                     "then\n" +
                     "end\n" +
                     "rule rule5 when\n" + // this will result in two terminal nodes
                     "    Object() or\n" +
                     "    Object()\n" +
                     "then\n" +
                     "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);

        List<String> nodes = new ArrayList<String>();
        Iterator it = TerminalNodeIterator.iterator(kbase);
        for ( TerminalNode node = (TerminalNode) it.next(); node != null; node = (TerminalNode) it.next() ) {
            nodes.add( node.getRule().getName());
        }

        assertThat(nodes.size()).isEqualTo(6);
        assertThat(nodes.contains("rule1")).isTrue();
        assertThat(nodes.contains("rule2")).isTrue();
        assertThat(nodes.contains("rule3")).isTrue();
        assertThat(nodes.contains("rule4")).isTrue();
        assertThat(nodes.contains("rule5")).isTrue();

        int first = nodes.indexOf( "rule5" );
        int second = nodes.lastIndexOf( "rule5" );
        assertThat(first != second).isTrue();
    }

}
