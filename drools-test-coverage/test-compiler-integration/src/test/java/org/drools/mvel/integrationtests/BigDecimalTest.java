package org.drools.mvel.integrationtests;

import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;
import org.drools.mvel.integrationtests.facts.FactWithDouble;
import org.drools.mvel.integrationtests.facts.FactWithFloat;
import org.drools.mvel.integrationtests.facts.FactWithInteger;
import org.drools.mvel.integrationtests.facts.FactWithLong;
import org.drools.mvel.integrationtests.facts.FactWithShort;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import java.math.BigDecimal;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class BigDecimalTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public BigDecimalTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testBigDecimalAssignmentToInt() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithInteger;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $integerFact: FactWithInteger() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $integerFact.intValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithInteger factWithInteger = new FactWithInteger(10);
            ksession.insert(factWithInteger);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithInteger.getIntValue()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalAssignmentToIntegerBoxed() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithInteger;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $integerFact: FactWithInteger() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $integerFact.integerValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithInteger factWithInteger = new FactWithInteger(10);
            ksession.insert(factWithInteger);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithInteger.getIntegerValue()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalAssignmentToLong() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithLong;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $longFact: FactWithLong() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $longFact.longValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithLong factWithLong = new FactWithLong(10L);
            ksession.insert(factWithLong);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithLong.getLongValue()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalAssignmentToLongBoxed() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithLong;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $longFact: FactWithLong() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $longFact.longObjectValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithLong factWithLong = new FactWithLong(10L);
            ksession.insert(factWithLong);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithLong.getLongObjectValue()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalAssignmentToShort() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithShort;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $shortFact: FactWithShort() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $shortFact.shortValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithShort factWithShort = new FactWithShort((short) 10);
            ksession.insert(factWithShort);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithShort.getShortValue()).isEqualTo((short) 1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalAssignmentToShortBoxed() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithShort;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $shortFact: FactWithShort() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $shortFact.shortObjectValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithShort factWithShort = new FactWithShort((short) 10);
            ksession.insert(factWithShort);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithShort.getShortObjectValue()).isEqualTo((short) 1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalAssignmentToDouble() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithDouble;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $doubleFact: FactWithDouble() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $doubleFact.doubleValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithDouble factWithDouble = new FactWithDouble( 10.2);
            ksession.insert(factWithDouble);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithDouble.getDoubleValue()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalAssignmentToDoubleBoxed() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithDouble;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $doubleFact: FactWithDouble() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $doubleFact.doubleObjectValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithDouble factWithDouble = new FactWithDouble( 10.2);
            ksession.insert(factWithDouble);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithDouble.getDoubleObjectValue()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalAssignmentToFloat() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithFloat;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $floatFact: FactWithFloat() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $floatFact.floatValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithFloat factWithFloat = new FactWithFloat( 10.2f);
            ksession.insert(factWithFloat);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithFloat.getFloatValue()).isEqualTo(1f);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testBigDecimalAssignmentToFloatBoxed() {
        String drl = "import org.drools.mvel.integrationtests.facts.FactWithFloat;\n "
                + "import org.drools.mvel.integrationtests.facts.FactWithBigDecimal;\n "
                + "rule testRule\n "
                + "dialect \"mvel\"\n "
                + "when\n "
                + "    $floatFact: FactWithFloat() \n "
                + "    $bigDecimalFact: FactWithBigDecimal() \n "
                + "then\n "
                + "    $floatFact.floatObjectValue = $bigDecimalFact.bigDecimalValue \n "
                + "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactWithFloat factWithFloat = new FactWithFloat( 10.2f);
            ksession.insert(factWithFloat);
            final FactWithBigDecimal factWithBigDecimal = new FactWithBigDecimal(BigDecimal.ONE);
            ksession.insert(factWithBigDecimal);
            ksession.fireAllRules();
            assertThat(factWithFloat.getFloatObjectValue()).isEqualTo(1f);
        } finally {
            ksession.dispose();
        }
    }
}
