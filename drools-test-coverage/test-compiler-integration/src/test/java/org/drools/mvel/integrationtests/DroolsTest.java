package org.drools.mvel.integrationtests;

import java.io.Serializable;
import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

@RunWith(Parameterized.class)
public class DroolsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DroolsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }
    private final static int NUM_FACTS = 20;

    private static int       counter;

    public static class Foo implements Serializable {
        private final int id;

        public Foo(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public static class Bar implements Serializable {
        private final int id;

        public Bar(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    @Test
    public void test1() throws Exception {
        String str = "package org.drools.mvel.integrationtests;\n";
        str += "import " + DroolsTest.class.getName() + ";\n";
        str += "import " + DroolsTest.class.getName() + ".Foo;\n";
        str += "import " + DroolsTest.class.getName() + ".Bar;\n";
        str += "rule test\n";
        str += "when\n";
        str += "      Foo($p : id, id < " + Integer.toString( NUM_FACTS ) + ")\n";
        str += "      Bar(id == $p)\n";
        str += "then\n";
        str += "   DroolsTest.incCounter();\n";
        str += "end\n";

        counter = 0;

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession wm = kbase.newKieSession();

        for ( int i = 0; i < NUM_FACTS; i++ ) {
            wm.insert( new Foo( i ) );
            wm.insert( new Bar( i ) );
        }

        wm.fireAllRules();
        System.out.println( counter + ":" + (counter == NUM_FACTS ? "passed" : "failed" ));
    }

    public static void incCounter() {
        ++counter;
    }
}
