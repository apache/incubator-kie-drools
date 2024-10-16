/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.bigdecimaltest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.BaseModelTest2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class BigDecimalTest extends BaseModelTest2 {

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalGreaterThan(RUN_TYPE runType) {
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

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));
        Policy policy = new Policy();
        policy.setCustomer(customer.getCode());
        policy.setRate(new BigDecimal("0.0"));

        ksession.insert(customer);
        ksession.insert(policy);
        ksession.fireAllRules();

        assertThat(policy.getRate().toString()).isEqualTo("0.5");

    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalCompare(RUN_TYPE runType) {
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

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));
        Policy policy = new Policy();
        policy.setCustomer(customer.getCode());
        policy.setRate(new BigDecimal("0.0"));

        ksession.insert(customer);
        ksession.insert(policy);
        ksession.fireAllRules();

        assertThat(policy.getRate().toString()).isEqualTo("0.5");

    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalEqualsToNull(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "$customer: Customer( rate == value )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();

        ksession.insert(customer);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalNotEqualsToNull(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "$customer: Customer( rate != value )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));

        ksession.insert(customer);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalNotEqualsToLiteralNull(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "$customer: Customer( rate != null )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));

        ksession.insert(customer);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalNotEqualsToLiteralValue(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "$customer: Customer( rate != 1I )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));

        ksession.insert(customer);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalGreaterThanNull(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule \"Set Policy Rate\"\n" +
                "when\n" +
                "Customer( rate > value )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigDecimal("0.5"));

        ksession.insert(customer);
        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalEquals(RUN_TYPE runType) {
        // DROOLS-3527
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "rule R1\n" +
                        "when\n" +
                        "$customer: Customer( rate == 12.111B )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setRate(new BigDecimal("12.111"));

        ksession.insert(customer);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalAdd(RUN_TYPE runType) {
        // RHDM-1635
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $customer: Customer( $rate : (rate + 10) == 20 )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Customer c1 = new Customer();
        c1.setRate(new BigDecimal("10"));
        Customer c2 = new Customer();
        c2.setRate(new BigDecimal("11"));

        ksession.insert(c1);
        ksession.insert(c2);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalRemainder(RUN_TYPE runType) {
        // RHDM-1635
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Customer.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $customer: Customer( $rate : (rate % 10) == 0 )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Customer c1 = new Customer();
        c1.setRate(new BigDecimal("20"));
        Customer c2 = new Customer();
        c2.setRate(new BigDecimal("21"));

        ksession.insert(c1);
        ksession.insert(c2);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class Order {
        private BigDecimal price;
        private BigDecimal taxRate;
        private BigDecimal tax;

        public BigDecimal getPrice() {
            return price;
        }
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
        public BigDecimal getTaxRate() {
            return taxRate;
        }
        public void setTaxRate(BigDecimal taxRate) {
            this.taxRate = taxRate;
        }
        public BigDecimal getTax() {
            return tax;
        }
        public void setTax(BigDecimal tax) {
            this.tax= tax;
        }

        public String toString() {
            return this.getClass().getName() + "[" +
                    "price=" + price + "," +
                    "taxRate=" + taxRate + "," +
                    "tax=" + tax + "]";
        }
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNonTerminatingDecimalExpansion(RUN_TYPE runType) {
        // DROOLS-6804
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + Order.class.getCanonicalName() + ";\n" +
                        "rule R1 dialect \"mvel\" when\n" +
                        "    $o : Order( $taxRate : taxRate, $price : price )\n" +
                        "then\n" +
                        "    $o.setTax($price - ($price / ($taxRate + 1)));\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Order order = new Order();
        order.setPrice(new BigDecimal("100000000"));
        order.setTaxRate(new BigDecimal("0.1"));
        ksession.insert(order);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(order.getTax()).isEqualTo(new BigDecimal("9090909.09090909090909090909090909"));
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalAndStringComparison(RUN_TYPE runType) {
        // DROOLS-6823
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Order.class.getCanonicalName() + ";\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "    $o : Order( $price : price )\n" +
                "    String( this == $price )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Order order = new Order();
        order.setPrice(new BigDecimal(300));
        ksession.insert(order);
        ksession.insert("300");

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testStringAndBigDecimalComparison(RUN_TYPE runType) {
        // DROOLS-6823
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + Order.class.getCanonicalName() + ";\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "    $s : String()\n" +
                "    $o : Order( price == $s )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Order order = new Order();
        order.setPrice(new BigDecimal(300));
        ksession.insert(order);
        ksession.insert("300");

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class BdHolder {

        private BigDecimal bd1;
        private BigDecimal bd2;

        public BdHolder() {
            super();
        }

        public BdHolder(BigDecimal bd1, BigDecimal bd2) {
            super();
            this.bd1 = bd1;
            this.bd2 = bd2;
        }

        public BigDecimal getBd1() {
            return bd1;
        }

        public void setBd1(BigDecimal bd1) {
            this.bd1 = bd1;
        }

        public BigDecimal getBd2() {
            return bd2;
        }

        public void setBd2(BigDecimal bd2) {
            this.bd2 = bd2;
        }
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultiply(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder(bd2 == bd1 * 10)", "10", "100");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultiplyWithNegativeValue(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder(bd2 == bd1 * -1)", "10", "-10");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultiplyWithBindVariable(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder($bd1 : bd1, bd2 == $bd1 * 10)", "10", "100");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultiplyWithBindVariableWithNegativeValue(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder($bd1 : bd1, bd2 == $bd1 * -1)", "10", "-10");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultiplyWithBindVariableWithNegativeValueEnclosed(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder($bd1 : bd1, bd2 == $bd1 * (-1))", "10", "-10");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultiplyWithBindVariableWithNegativeValueEnclosedBoth(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder($bd1 : bd1, bd2 == ($bd1 * -1))", "10", "-10");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultiplyWithBindVariableWithNegativeValueEnclosedNest(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder($bd1 : bd1, bd2 == ($bd1 * (-1)))", "10", "-10");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testAddWithBindVariable(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder($bd1 : bd1, bd2 == $bd1 + 10)", "10", "20");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testSubtractWithBindVariable(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder($bd1 : bd1, bd2 == $bd1 - 10)", "10", "0");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testDivideWithBindVariable(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder($bd1 : bd1, bd2 == $bd1 / 10)", "10", "1");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testModWithBindVariable(RUN_TYPE runType) {
        testBigDecimalArithmeticOperation(runType, "BdHolder($bd1 : bd1, bd2 == $bd1 % 10)", "10", "0");
    }

    private void testBigDecimalArithmeticOperation(RUN_TYPE runType, String pattern, String bd1, String bd2) {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + BdHolder.class.getCanonicalName() + ";\n" +
                     "rule R1 dialect \"mvel\" when\n" +
                     pattern + "\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        BdHolder holder = new BdHolder(new BigDecimal(bd1), new BigDecimal(bd2));
        ksession.insert(holder);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalLiteralLhsNegative(RUN_TYPE runType) {
        // DROOLS-6596
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + BdHolder.class.getCanonicalName() + ";\n" +
                     "rule R1 dialect \"mvel\" when\n" +
                     "    $holder : BdHolder(bd1 > -10.5B)\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        BdHolder holder = new BdHolder();
        holder.setBd1(new BigDecimal("10"));
        ksession.insert(holder);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalLiteralRhsNegative(RUN_TYPE runType) {
        // DROOLS-6596
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + BdHolder.class.getCanonicalName() + ";\n" +
                     "rule R1 dialect \"mvel\" when\n" +
                     "    $holder : BdHolder()\n" +
                     "then\n" +
                     "    $holder.bd1 = -10.5B;\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        BdHolder holder = new BdHolder();
        ksession.insert(holder);
        ksession.fireAllRules();

        assertThat(holder.getBd1()).isEqualTo(new BigDecimal("-10.5"));
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBigDecimalLiteralWithBinding(RUN_TYPE runType) {
        // DROOLS-6936
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + BdHolder.class.getCanonicalName() + ";\n" +
                        "global java.util.List result;\n" +
                        "rule R1 dialect \"mvel\"\n" +
                        "when\n" +
                        "    $holder : BdHolder($bd1 : bd1, $zero : 0B)\n" +
                        "then\n" +
                        "    result.add($zero);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        List<BigDecimal> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        BdHolder holder = new BdHolder();
        holder.setBd1(new BigDecimal("10"));
        ksession.insert(holder);
        ksession.fireAllRules();

        assertThat(result).containsExactly(new BigDecimal("0"));
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testModifyWithNegativeBigDecimal(RUN_TYPE runType) {
        // DROOLS-7324
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                "import " + BdHolder.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "rule R1 dialect \"mvel\" when\n" +
                "    $bd : BdHolder(bd1 > 5)\n" +
                "then\n" +
                "    modify($bd) { bd1 = -1 }\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);
        List<BigDecimal> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        BdHolder holder = new BdHolder();
        holder.setBd1(new BigDecimal("10"));
        ksession.insert(holder);
        int fires = ksession.fireAllRules();

        assertThat(fires).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bigDecimalArithmeticInMethodCallScope(RUN_TYPE runType) {
        // DROOLS-7364
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + Customer.class.getCanonicalName() + ";\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "global java.util.List result;\n" +
                     "rule \"Rule 1a\"\n" +
                     "    when\n" +
                     "        Customer( $ans : (rate * new BigDecimal(\"1000\")).longValue() )\n" +
                     "    then\n" +
                     "        result.add($ans);\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);
        List<Long> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Customer customer = new Customer();
        customer.setRate(new BigDecimal("2"));
        ksession.insert(customer);
        ksession.fireAllRules();

        assertThat(result).contains(2000L);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bigDecimalArithmeticInMethodCallScopeInMethodCallArgument(RUN_TYPE runType) {
        // DROOLS-7364
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + Customer.class.getCanonicalName() + ";\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "global java.util.List result;\n" +
                     "rule \"Rule 1a\"\n" +
                     "    when\n" +
                     "        Customer( $ans : String.format(\"%,d\", (rate * new BigDecimal(\"1000\")).longValue()) )\n" +
                     "    then\n" +
                     "        result.add($ans);\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Customer customer = new Customer();
        customer.setRate(new BigDecimal("2"));
        ksession.insert(customer);
        ksession.fireAllRules();

        assertThat(result).contains("2,000");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void nonBigDecimalArithmeticInMethodCallScopeInMethodCallArgument(RUN_TYPE runType) {
        // DROOLS-7364
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + Customer.class.getCanonicalName() + ";\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "global java.util.List result;\n" +
                     "rule \"Rule 1a\"\n" +
                     "    when\n" +
                     "        Customer( $ans : String.format(\"%,d\", (longValue * Long.valueOf(1000L)).longValue()) )\n" +
                     "    then\n" +
                     "        result.add($ans);\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Customer customer = new Customer();
        customer.setLongValue(2L);
        ksession.insert(customer);
        ksession.fireAllRules();

        assertThat(result).contains("2,000");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bigDecimalArithmeticInMethodCallArgumentWithoutEnclosedExpr(RUN_TYPE runType) {
        // DROOLS-7364
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + Customer.class.getCanonicalName() + ";\n" +
                     "import " + Util.class.getCanonicalName() + ";\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "global java.util.List result;\n" +
                     "rule \"Rule 1a\"\n" +
                     "    when\n" +
                     "        Customer( $ans : Util.getString(\"%,d\", rate * new BigDecimal(\"1000\")) )\n" +
                     "    then\n" +
                     "        result.add($ans);\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Customer customer = new Customer();
        customer.setRate(new BigDecimal("2"));
        ksession.insert(customer);
        ksession.fireAllRules();

        assertThat(result).contains("2,000");
    }

    public static class Util {

        public static String getString(String format, BigDecimal bd) {
            return String.format("%,d", bd.longValue());
        }
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bigDecimalEqualityWithDifferentScale_shouldBeEqual(RUN_TYPE runType) {
        // DROOLS-7414
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + Customer.class.getCanonicalName() + ";\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "global java.util.List result;\n" +
                     "rule \"Rule 1a\"\n" +
                     "    when\n" +
                     "        Customer( $rate : rate == new BigDecimal(\"1.0\") )\n" +
                     "    then\n" +
                     "        result.add($rate);\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);
        List<BigDecimal> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Customer customerTwoScale = new Customer();
        customerTwoScale.setRate(new BigDecimal("1.00"));

        ksession.insert(customerTwoScale);
        ksession.fireAllRules();

        // BigDecimal("1.0") and BigDecimal("1.00") are considered as equal because exec-model uses EvaluationUtil.equals() which is based on compareTo()
        assertThat(result).contains(new BigDecimal("1.00"));
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bigDecimalCoercionInMethodArgument_shouldNotFailToBuild(RUN_TYPE runType) {
        // KIE-748
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + BDFact.class.getCanonicalName() + ";\n" +
                        "import static " + BigDecimalTest.class.getCanonicalName() + ".intToString;\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        BDFact( intToString(value2 - 1) == \"2\" )\n" +
                        "    then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BDFact bdFact = new BDFact();
        bdFact.setValue2(new BigDecimal("3"));

        ksession.insert(bdFact);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bigDecimalCoercionInNestedMethodArgument_shouldNotFailToBuild(RUN_TYPE runType) {
        // KIE-748
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + BDFact.class.getCanonicalName() + ";\n" +
                     "import static " + BigDecimalTest.class.getCanonicalName() + ".intToString;\n" +
                     "rule \"Rule 1a\"\n" +
                     "    when\n" +
                     "        BDFact( intToString(value1 * (value2 - 1)) == \"20\" )\n" +
                     "    then\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        BDFact bdFact = new BDFact();
        bdFact.setValue1(new BigDecimal("10"));
        bdFact.setValue2(new BigDecimal("3"));

        ksession.insert(bdFact);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static String intToString(int value) {
        return Integer.toString(value);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bindVariableToBigDecimalCoercion2Operands_shouldBindCorrectResult(RUN_TYPE runType) {
        bindVariableToBigDecimalCoercion(runType, "$var : (1000 * value1)");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bindVariableToBigDecimalCoercion3Operands_shouldBindCorrectResult(RUN_TYPE runType) {
        bindVariableToBigDecimalCoercion(runType, "$var : (100000 * value1 / 100)");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bindVariableToBigDecimalCoercion3OperandsWithParentheses_shouldBindCorrectResult(RUN_TYPE runType) {
        bindVariableToBigDecimalCoercion(runType, "$var : ((100000 * value1) / 100)");
    }

    private void bindVariableToBigDecimalCoercion(RUN_TYPE runType, String binding) {
        // KIE-775
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + BDFact.class.getCanonicalName() + ";\n" +
                        "global java.util.List result;\n" +
                        "rule R1\n" +
                        "    when\n" +
                        "        BDFact( " + binding +  " )\n" +
                        "    then\n" +
                        "        result.add($var);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        List<BigDecimal> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        BDFact bdFact = new BDFact();
        bdFact.setValue1(new BigDecimal("80"));

        ksession.insert(bdFact);
        ksession.fireAllRules();

        assertThat(result).contains(new BigDecimal("80000"));
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bigDecimalInWithInt_shouldNotFailToBuild(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + BDFact.class.getCanonicalName() + ";\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        BDFact( value1 in (100, 200) )\n" +
                        "    then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BDFact bdFact = new BDFact();
        bdFact.setValue1(new BigDecimal("100"));

        ksession.insert(bdFact);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void bigDecimalInWithBD_shouldNotFailToBuild(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                        "import " + BDFact.class.getCanonicalName() + ";\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        BDFact( value1 in (100B, 200B) )\n" +
                        "    then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BDFact bdFact = new BDFact();
        bdFact.setValue1(new BigDecimal("100"));

        ksession.insert(bdFact);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }
}
