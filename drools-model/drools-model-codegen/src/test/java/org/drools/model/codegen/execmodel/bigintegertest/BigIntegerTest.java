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
package org.drools.model.codegen.execmodel.bigintegertest;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.BaseModelTest;
import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class BigIntegerTest extends BaseModelTest {

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerGreaterThan(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + Policy.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "rule \"Set Policy Rate\"\n" +
                        "when\n" +
                        "$customer: Customer( $code: code, rate > 0I )\n" +
                        "$policy: Policy( customer == $code, rate == 0I )\n" +
                        "then\n" +
                        "$policy.setRate(new BigInteger($customer.getRate().toString()));\n" +
                        "update($policy);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigInteger("5"));
        Policy policy = new Policy();
        policy.setCustomer(customer.getCode());
        policy.setRate(new BigInteger("0"));

        ksession.insert(customer);
        ksession.insert(policy);
        ksession.fireAllRules();

        assertThat(policy.getRate().toString()).isEqualTo("5");

    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerCompare(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + Policy.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "rule \"Set Policy Rate\"\n" +
                        "when\n" +
                        "$customer: Customer( $code: code, $cr: rate, $cr.compareTo(new BigInteger(\"0\")) > 0 )\n" +
                        "$policy: Policy( customer == $code, $pr: rate, $pr.compareTo(new BigInteger(\"0\")) == 0 )\n" +
                        "then\n" +
                        "$policy.setRate(new BigInteger($customer.getRate().toString()));\n" +
                        "update($policy);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigInteger("5"));
        Policy policy = new Policy();
        policy.setCustomer(customer.getCode());
        policy.setRate(new BigInteger("0"));

        ksession.insert(customer);
        ksession.insert(policy);
        ksession.fireAllRules();

        assertThat(policy.getRate().toString()).isEqualTo("5");

    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerEqualsToNull(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
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
    public void testBigIntegerNotEqualsToNull(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "rule \"Set Policy Rate\"\n" +
                        "when\n" +
                        "$customer: Customer( rate != value )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigInteger("5"));

        ksession.insert(customer);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerNotEqualsToLiteralNull(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "rule \"Set Policy Rate\"\n" +
                        "when\n" +
                        "$customer: Customer( rate != null )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigInteger("5"));

        ksession.insert(customer);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerNotEqualsToLiteralValue(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "rule \"Set Policy Rate\"\n" +
                        "when\n" +
                        "$customer: Customer( rate != 1I )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigInteger("5"));

        ksession.insert(customer);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @Disabled("Fails with non-exec-model")
    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerGreaterThanNull(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "rule \"Set Policy Rate\"\n" +
                        "when\n" +
                        "Customer( rate > value )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setCode("code1");
        customer.setRate(new BigInteger("5"));

        ksession.insert(customer);
        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerEquals(RUN_TYPE runType) {
        // DROOLS-3527
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "rule R1\n" +
                        "when\n" +
                        "$customer: Customer( rate == 12I )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer customer = new Customer();
        customer.setRate(new BigInteger("12"));

        ksession.insert(customer);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerAdd(RUN_TYPE runType) {
        // RHDM-1635
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "  $customer: Customer( $rate : (rate + 10) == 20 )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer c1 = new Customer();
        c1.setRate(new BigInteger("10"));
        Customer c2 = new Customer();
        c2.setRate(new BigInteger("11"));

        ksession.insert(c1);
        ksession.insert(c2);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerRemainder(RUN_TYPE runType) {
        // RHDM-1635
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "  $customer: Customer( $rate : (rate % 10) == 0 )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Customer c1 = new Customer();
        c1.setRate(new BigInteger("20"));
        Customer c2 = new Customer();
        c2.setRate(new BigInteger("21"));

        ksession.insert(c1);
        ksession.insert(c2);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class Order {
        private BigInteger price;
        private BigInteger taxRate;
        private BigInteger tax;

        public BigInteger getPrice() {
            return price;
        }
        public void setPrice(BigInteger price) {
            this.price = price;
        }
        public BigInteger getTaxRate() {
            return taxRate;
        }
        public void setTaxRate(BigInteger taxRate) {
            this.taxRate = taxRate;
        }
        public BigInteger getTax() {
            return tax;
        }
        public void setTax(BigInteger tax) {
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
    public void testBigIntegerAndStringComparison(RUN_TYPE runType) {
        // DROOLS-6823
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BigIntegerTest.Order.class.getCanonicalName() + ";\n" +
                        "rule R1 dialect \"mvel\" when\n" +
                        "    $o : Order( $price : price )\n" +
                        "    String( this == $price )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BigIntegerTest.Order order = new BigIntegerTest.Order();
        order.setPrice(new BigInteger("300"));
        ksession.insert(order);
        ksession.insert("300");

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testStringAndBigIntegerComparison(RUN_TYPE runType) {
        // DROOLS-6823
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BigIntegerTest.Order.class.getCanonicalName() + ";\n" +
                        "rule R1 dialect \"mvel\" when\n" +
                        "    $s : String()\n" +
                        "    $o : Order( price == $s )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BigIntegerTest.Order order = new BigIntegerTest.Order();
        order.setPrice(new BigInteger("300"));
        ksession.insert(order);
        ksession.insert("300");

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class BiHolder {

        private BigInteger bi1;
        private BigInteger bi2;

        public BiHolder() {
            super();
        }

        public BiHolder(BigInteger bi1, BigInteger bi2) {
            super();
            this.bi1 = bi1;
            this.bi2 = bi2;
        }

        public BigInteger getBi1() {
            return bi1;
        }

        public void setBi1(BigInteger bi1) {
            this.bi1 = bi1;
        }

        public BigInteger getBi2() {
            return bi2;
        }

        public void setBi2(BigInteger bi2) {
            this.bi2 = bi2;
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMultiply(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder(bi2 == bi1 * 10)", "10", "100");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMultiplyWithNegativeValue(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder(bi2 == bi1 * -1)", "10", "-10");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMultiplyWithBindVariable(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder($bi1 : bi1, bi2 == $bi1 * 10)", "10", "100");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMultiplyWithBindVariableWithNegativeValue(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder($bi1 : bi1, bi2 == $bi1 * -1)", "10", "-10");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMultiplyWithBindVariableWithNegativeValueEnclosed(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder($bi1 : bi1, bi2 == $bi1 * (-1))", "10", "-10");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMultiplyWithBindVariableWithNegativeValueEnclosedBoth(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder($bi1 : bi1, bi2 == ($bi1 * -1))", "10", "-10");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMultiplyWithBindVariableWithNegativeValueEnclosedNest(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder($bi1 : bi1, bi2 == ($bi1 * (-1)))", "10", "-10");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testAddWithBindVariable(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder($bi1 : bi1, bi2 == $bi1 + 10)", "10", "20");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSubtractWithBindVariable(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder($bi1 : bi1, bi2 == $bi1 - 10)", "10", "0");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testDivideWithBindVariable(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder($bi1 : bi1, bi2 == $bi1 / 10)", "10", "1");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testModWithBindVariable(RUN_TYPE runType) {
        testBigIntegerArithmeticOperation(runType, "BiHolder($bi1 : bi1, bi2 == $bi1 % 10)", "10", "0");
    }

    private void testBigIntegerArithmeticOperation(RUN_TYPE runType, String pattern, String bi1, String bi2) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BigIntegerTest.BiHolder.class.getCanonicalName() + ";\n" +
                        "rule R1 dialect \"mvel\" when\n" +
                        pattern + "\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BigIntegerTest.BiHolder holder = new BigIntegerTest.BiHolder(new BigInteger(bi1), new BigInteger(bi2));
        ksession.insert(holder);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerLiteralLhsNegative(RUN_TYPE runType) {
        // DROOLS-6596
        String str =
                "package org.drools.modelcompiler.bigintegerss\n" +
                        "import " + BiHolder.class.getCanonicalName() + ";\n" +
                        "rule R1 dialect \"mvel\" when\n" +
                        "    $holder : BiHolder(bi1 > -10I)\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BiHolder holder = new BiHolder();
        holder.setBi1(new BigInteger("10"));
        ksession.insert(holder);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerLiteralRhsNegative(RUN_TYPE runType) {
        // DROOLS-6596
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BiHolder.class.getCanonicalName() + ";\n" +
                        "rule R1 dialect \"mvel\" when\n" +
                        "    $holder : BiHolder()\n" +
                        "then\n" +
                        "    $holder.bi1 = -10I;\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BiHolder holder = new BiHolder();
        ksession.insert(holder);
        ksession.fireAllRules();

        assertThat(holder.getBi1()).isEqualTo(new BigInteger("-10"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigIntegerLiteralWithBinding(RUN_TYPE runType) {
        // DROOLS-6936
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BigIntegerTest.BiHolder.class.getCanonicalName() + ";\n" +
                        "global java.util.List result;\n" +
                        "rule R1 dialect \"mvel\"\n" +
                        "when\n" +
                        "    $holder : BiHolder($bi1 : bi1, $zero : 0I)\n" +
                        "then\n" +
                        "    result.add($zero);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        List<BigInteger> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        BigIntegerTest.BiHolder holder = new BigIntegerTest.BiHolder();
        holder.setBi1(new BigInteger("10"));
        ksession.insert(holder);
        ksession.fireAllRules();

        assertThat(result).containsExactly(new BigInteger("0"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testModifyWithNegativeBigInteger(RUN_TYPE runType) {
        // DROOLS-7324
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BigIntegerTest.BiHolder.class.getCanonicalName() + ";\n" +
                        "global java.util.List result;\n" +
                        "rule R1 dialect \"mvel\" when\n" +
                        "    $bd : BiHolder(bi1 > 5)\n" +
                        "then\n" +
                        "    modify($bd) { bi1 = -1 }\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        List<BigInteger> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        BigIntegerTest.BiHolder holder = new BigIntegerTest.BiHolder();
        holder.setBi1(new BigInteger("10"));
        ksession.insert(holder);
        int fires = ksession.fireAllRules();

        assertThat(fires).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void bigIntegerArithmeticInMethodCallScope(RUN_TYPE runType) {
        // DROOLS-7364
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "global java.util.List result;\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        Customer( $ans : (rate * new BigInteger(\"1000\")).longValue() )\n" +
                        "    then\n" +
                        "        result.add($ans);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        List<Long> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Customer customer = new Customer();
        customer.setRate(new BigInteger("2"));
        ksession.insert(customer);
        ksession.fireAllRules();

        assertThat(result).contains(2000L);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void bigIntegerArithmeticInMethodCallScopeInMethodCallArgument(RUN_TYPE runType) {
        // DROOLS-7364
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "global java.util.List result;\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        Customer( $ans : String.format(\"%,d\", (rate * new BigInteger(\"1000\")).longValue()) )\n" +
                        "    then\n" +
                        "        result.add($ans);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Customer customer = new Customer();
        customer.setRate(new BigInteger("2"));
        ksession.insert(customer);
        ksession.fireAllRules();

        assertThat(result).contains("2,000");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void nonBigIntegerArithmeticInMethodCallScopeInMethodCallArgument(RUN_TYPE runType) {
        // DROOLS-7364
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
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
    public void bigIntegerArithmeticInMethodCallArgumentWithoutEnclosedExpr(RUN_TYPE runType) {
        // DROOLS-7364
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + Customer.class.getCanonicalName() + ";\n" +
                        "import " + BigIntegerTest.Util.class.getCanonicalName() + ";\n" +
                        "import " + BigInteger.class.getCanonicalName() + ";\n" +
                        "global java.util.List result;\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        Customer( $ans : Util.getString(\"%,d\", rate * new BigInteger(\"1000\")) )\n" +
                        "    then\n" +
                        "        result.add($ans);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        Customer customer = new Customer();
        customer.setRate(new BigInteger("2"));
        ksession.insert(customer);
        ksession.fireAllRules();

        assertThat(result).contains("2,000");
    }

    public static class Util {

        public static String getString(String format, BigInteger bd) {
            return String.format("%,d", bd.longValue());
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void bigIntegerCoercionInMethodArgument_shouldNotFailToBuild(RUN_TYPE runType) {
        // KIE-748
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BIFact.class.getCanonicalName() + ";\n" +
                        "import static " + BigIntegerTest.class.getCanonicalName() + ".intToString;\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        BIFact( intToString(value2 - 1) == \"2\" )\n" +
                        "    then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BIFact biFact = new BIFact();
        biFact.setValue2(new BigInteger("3"));

        ksession.insert(biFact);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void bigIntegerCoercionInNestedMethodArgument_shouldNotFailToBuild(RUN_TYPE runType) {
        // KIE-748
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BIFact.class.getCanonicalName() + ";\n" +
                        "import static " + BigIntegerTest.class.getCanonicalName() + ".intToString;\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        BIFact( intToString(value1 * (value2 - 1)) == \"20\" )\n" +
                        "    then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BIFact biFact = new BIFact();
        biFact.setValue1(new BigInteger("10"));
        biFact.setValue2(new BigInteger("3"));

        ksession.insert(biFact);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static String intToString(int value) {
        return Integer.toString(value);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void bindVariableToBigIntegerCoercion2Operands_shouldBindCorrectResult(RUN_TYPE runType) {
        bindVariableToBigIntegerCoercion(runType, "$var : (1000 * value1)");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void bindVariableToBigIntegerCoercion3Operands_shouldBindCorrectResult(RUN_TYPE runType) {
        bindVariableToBigIntegerCoercion(runType, "$var : (100000 * value1 / 100)");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void bindVariableToBigIntegerCoercion3OperandsWithParentheses_shouldBindCorrectResult(RUN_TYPE runType) {
        bindVariableToBigIntegerCoercion(runType, "$var : ((100000 * value1) / 100)");
    }

    private void bindVariableToBigIntegerCoercion(RUN_TYPE runType, String binding) {
        // KIE-775
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BIFact.class.getCanonicalName() + ";\n" +
                        "global java.util.List result;\n" +
                        "rule R1\n" +
                        "    when\n" +
                        "        BIFact( " + binding +  " )\n" +
                        "    then\n" +
                        "        result.add($var);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        List<BigInteger> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        BIFact biFact = new BIFact();
        biFact.setValue1(new BigInteger("80"));

        ksession.insert(biFact);
        ksession.fireAllRules();

        assertThat(result).contains(new BigInteger("80000"));
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void bigIntegerInWithInt_shouldNotFailToBuild(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BIFact.class.getCanonicalName() + ";\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        BIFact( value1 in (100, 200) )\n" +
                        "    then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BIFact biFact = new BIFact();
        biFact.setValue1(new BigInteger("100"));

        ksession.insert(biFact);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void bigIntegerInWithBI_shouldNotFailToBuild(RUN_TYPE runType) {
        String str =
                "package org.drools.modelcompiler.bigintegers\n" +
                        "import " + BIFact.class.getCanonicalName() + ";\n" +
                        "rule \"Rule 1a\"\n" +
                        "    when\n" +
                        "        BIFact( value1 in (100I, 200I) )\n" +
                        "    then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        BIFact biFact = new BIFact();
        biFact.setValue1(new BigInteger("100"));

        ksession.insert(biFact);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }
}
