package org.drools.modelcompiler.bigdecimaltest;

import java.math.BigDecimal;

import org.drools.modelcompiler.BaseModelTest;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

public class BigDecimalTest extends BaseModelTest {

    public BigDecimalTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    @Ignore
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
    @Ignore
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
}
