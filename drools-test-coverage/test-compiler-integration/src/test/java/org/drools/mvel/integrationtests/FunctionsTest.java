package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.mvel.compiler.Cheese;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class FunctionsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public FunctionsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFunction() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_FunctionInConsequence.drl");
        KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list",
                            list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        ksession.insert( stilton );

        ksession.fireAllRules();

        assertThat(((List<Integer>) ksession.getGlobal("list")).get(0)).isEqualTo(new Integer( 5 ));
    }

    @Test
    public void testFunctionException() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_FunctionException.drl");
        KieSession ksession = kbase.newKieSession();

        final Cheese brie = new Cheese( "brie",
                                        12 );
        ksession.insert( brie );

        try {
            ksession.fireAllRules();
            fail( "Should throw an Exception from the Function" );
        } catch ( final Exception e ) {
            assertThat(e.getCause().getMessage()).contains("this should throw an exception");
        }
    }

    @Test
    public void testFunctionWithPrimitives() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration, "test_FunctionWithPrimitives.drl");
        KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list",
                                 list );

        final Cheese stilton = new Cheese( "stilton",
                                           5 );
        ksession.insert( stilton );

        ksession.fireAllRules();

        assertThat(list.get(0)).isEqualTo(new Integer( 10 ));
    }
    
    @Test
    public void testFunctionCallingFunctionWithEclipse() throws Exception {
        Resource[] resources = KieUtil.createResources("test_functionCallingFunction.drl", this.getClass());
        Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put("drools.dialect.java.compiler", "ECLIPSE");
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromResources("test", kieBaseTestConfiguration,
                                                                 kieModuleConfigurationProperties,
                                                                 resources);
        final KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "results",
                            list );

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);

        assertThat(list.get(0).intValue()).isEqualTo(12);
    }

    @Test
    public void testJBRULES3117() {
        String str = "package org.kie\n" +
                     "function boolean isOutOfRange( Object value, int lower ) { return true; }\n" + 
                     "function boolean isNotContainedInt( Object value, int[] values ) { return true; }\n" +
                     "rule R1\n" +
                     "when\n" +
                     "then\n" +
                     "    boolean x = isOutOfRange( Integer.MAX_VALUE, 1 );\n" +
                     "    boolean y = isNotContainedInt( Integer.MAX_VALUE, new int[] { 1, 2, 3 } );\n" +
                     "end\n";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }
}
