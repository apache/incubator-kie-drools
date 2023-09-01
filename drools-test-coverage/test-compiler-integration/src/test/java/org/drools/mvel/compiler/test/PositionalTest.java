package org.drools.mvel.compiler.test;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class PositionalTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PositionalTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with ttestPositional. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testPositional() {

        String drl =
                "import " + Man.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;" +
                "\n" +
                "rule \"To be or not to be\"\n" +
                "when\n" +
                "    $m : Man( \"john\" , 18 , $w ; )\n" +
                "then\n" +
                "    list.add($w); " +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession kSession = kbase.newKieSession();

        java.util.ArrayList list = new ArrayList();
        kSession.setGlobal( "list",
                            list );

        kSession.insert( new Man( "john", 18, 84.2 ) );
        kSession.insert( new Man( "john", 19, 85.2 ) );
        kSession.fireAllRules();

        assertThat(list.contains(84.2)).isTrue();
        assertThat(list.contains(85.2)).isFalse();
    }


    @Test(timeout = 5000)
    public void testPositionalWithNull() {
        // DROOLS-51
        String str =
                "declare Bean\n" +
                "  value : String\n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Bean( null ) );\n" +
                "  insert( \"test\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Bind\"\n" +
                "when\n" +
                "  $s : String(  )\n" +
                "  $b : Bean( null ; )\n" +
                "then\n" +
                "  modify ( $b ) { setValue( $s ); }\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }
}
