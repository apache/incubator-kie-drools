package org.drools.modelcompiler.bigdecimaltest;

import java.math.BigDecimal;

import org.drools.modelcompiler.BaseModelTest;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;

public class BigDecimalTest extends BaseModelTest {

    public BigDecimalTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testBigDecimalGreaterThan() {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + Policy.class.getCanonicalName() + ";\n" +
                        "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                        "rule \"Set Policy Rate\"\n" +
                        "when\n" +
                        "$customer: Customer( $code: code, rate > 0.0B )\n" +
                        "$policy: Policy( customer == $code, rate == 0.0B )\n" +
                        "then\n" +
                        "$policy.setRate(new BigDecimal($customer.getRate().toString()));\n" +
                        "update($policy);\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));
        Policy policy = new Policy();
        policy.setCustomer(customer.getCode());
        policy.setRate(new BigDecimal("0.0"));

        ksession.insert(customer);
        ksession.insert(policy);
        ksession.fireAllRules();

        assertEquals("0.5", policy.getRate().toString());

    }

    @Test
    public void testBigDecimalCompare() {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + Policy.class.getCanonicalName() + ";\n" +
                        "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                        "rule \"Set Policy Rate\"\n" +
                        "when\n" +
                        "$customer: Customer( $code: code, $cr: rate, $cr.compareTo(new BigDecimal(\"0.0\")) > 0 )\n" +
                        "$policy: Policy( customer == $code, $pr: rate, $pr.compareTo(new BigDecimal(\"0.0\")) == 0 )\n" +
                        "then\n" +
                        "$policy.setRate(new BigDecimal($customer.getRate().toString()));\n" +
                        "update($policy);\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));
        Policy policy = new Policy();
        policy.setCustomer(customer.getCode());
        policy.setRate(new BigDecimal("0.0"));

        ksession.insert(customer);
        ksession.insert(policy);
        ksession.fireAllRules();

        assertEquals("0.5", policy.getRate().toString());

    }

    @Test
    public void testBigDecimalEqualsToNull() {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "$customer: Customer( rate == value )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();

        ksession.insert(customer);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testBigDecimalNotEqualsToNull() {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "$customer: Customer( rate != value )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));

        ksession.insert(customer);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testBigDecimalNotEqualsToLiteralNull() {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "$customer: Customer( rate != null )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));

        ksession.insert(customer);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testBigDecimalNotEqualsToLiteralValue() {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "$customer: Customer( rate != 1I )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));

        ksession.insert(customer);
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testBigDecimalGreaterThanNull() {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "Customer( rate > value )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));

        ksession.insert(customer);
        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testBigDecimalEquals() {
        // DROOLS-3527
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "rule R1\n" +
                        "when\n" +
                        "$customer: Customer( rate == 12.111B )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        Customer customer = new Customer();
        customer.setRate(new BigDecimal("12.111"));

        ksession.insert(customer);

        assertEquals(1, ksession.fireAllRules());

    }

    @Test
    public void testBigDecimalAdd() {
        // RHDM-1635
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $customer: Customer( $rate : (rate + 10) == 20 )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Customer c1 = new Customer();
        c1.setRate(new BigDecimal("10"));
        Customer c2 = new Customer();
        c2.setRate(new BigDecimal("11"));

        ksession.insert(c1);
        ksession.insert(c2);

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testBigDecimalRemainder() {
        // RHDM-1635
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $customer: Customer( $rate : (rate % 10) == 0 )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(str);

        Customer c1 = new Customer();
        c1.setRate(new BigDecimal("20"));
        Customer c2 = new Customer();
        c2.setRate(new BigDecimal("21"));

        ksession.insert(c1);
        ksession.insert(c2);

        assertEquals(1, ksession.fireAllRules());
    }
}
