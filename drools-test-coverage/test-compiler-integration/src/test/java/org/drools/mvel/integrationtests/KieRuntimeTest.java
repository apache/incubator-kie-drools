package org.drools.mvel.integrationtests;

import java.io.IOException;
import java.util.Collection;

import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.Message;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

@RunWith(Parameterized.class)
public class KieRuntimeTest extends CommonTestMethodBase {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public KieRuntimeTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testKieRuntimeAccess() throws IOException, ClassNotFoundException {
        String str = "";
        str += "package org.drools.mvel.compiler.test\n";
        str += "import " + Message.class.getName() + "\n";
        str += "rule \"Hello World\"\n";
        str += "when\n";
        str += "    Message( )\n";
        str += "then\n";
        str += "    System.out.println( drools.getKieRuntime() );\n";
        str += "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Message( "help" ) );
        ksession.fireAllRules();
        ksession.dispose();
    }

}
