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
package org.drools.compiler.integrationtests;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.ancompiler.CompiledNetwork;
import org.drools.core.reteoo.CompositeObjectSinkAdapter;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSinkPropagator;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.util.index.AlphaRangeIndex;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.MyComparable;
import org.drools.testcoverage.common.model.MyComparableHolder;
import org.drools.testcoverage.common.model.Order;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.Primitives;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.drools.util.DateUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.AlphaRangeIndexThresholdOption;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class AlphaNodeRangeIndexingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    private static final String BASIC_DRL =
            "package org.drools.compiler.test\n" +
                                            "import " + Person.class.getCanonicalName() + "\n" +
                                            "rule test1\n when\n" +
                                            "   Person( age >= 18 )\n" +
                                            "then\n end\n" +
                                            "rule test2\n when\n" +
                                            "   Person( age < 25 )\n" +
                                            "then\n end\n" +
                                            "rule test3\n when\n" +
                                            "   Person( age > 8 )\n" +
                                            "then\n end\n" +
                                            "rule test4\n when\n" +
                                            "   Person( age < 60 )\n" +
                                            "then\n end\n" +
                                            "rule test5\n when\n" +
                                            "   Person( age > 12 )\n" +
                                            "then\n end\n" +
                                            "rule test6\n when\n" +
                                            "   Person( age <= 4 )\n" +
                                            "then\n end\n";

    public AlphaNodeRangeIndexingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        //System.setProperty("alphanetworkCompilerEnabled", "true");
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testInteger() {
        final String drl = BASIC_DRL;

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Person.class, 6, 6, 0, 6); // sinksLength = 6, sinkAdapterSize = 6, rangeIndexableSinks is null, Size of RangeIndexed nodes = 6

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(5);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(3);
    }

    private KieBase createKieBaseWithRangeIndexThresholdValue(String drl, int rangeIndexThresholdValue) {
        final KieModule kieModule = KieUtil.getKieModuleFromDrls("indexing-test", kieBaseTestConfiguration, drl);
        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBaseConfiguration kieBaseConfiguration = kieBaseTestConfiguration.getKieBaseConfiguration();
        kieBaseConfiguration.setOption(AlphaRangeIndexThresholdOption.get(rangeIndexThresholdValue)); // for test convenience. Default value is AlphaRangeIndexThresholdOption.DEFAULT_VALUE
        return kieContainer.newKieBase(kieBaseConfiguration);
    }

    private void assertSinks(KieBase kbase, Class<?> factClass, int sinksLength, int sinkAdapterSize, int rangeIndexableSinksSize, int rangeIndexSize) {
        final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, factClass);
        assertThat(otn).isNotNull();

        ObjectSinkPropagator objectSinkPropagator = otn.getObjectSinkPropagator();
        if (this.kieBaseTestConfiguration.useAlphaNetworkCompiler()) {
            objectSinkPropagator = ((CompiledNetwork) objectSinkPropagator).getOriginalSinkPropagator();
        }
        CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) objectSinkPropagator;

        ObjectSink[] sinks = sinkAdapter.getSinks();
        assertThat(sinks.length).isEqualTo(sinksLength);
        assertThat(sinkAdapter.size()).isEqualTo(sinkAdapterSize);
        if (rangeIndexableSinksSize == 0) {
            assertThat(sinkAdapter.getRangeIndexableSinks()).isNull();
        } else {
            assertThat(sinkAdapter.getRangeIndexableSinks().size()).isEqualTo(rangeIndexableSinksSize);
        }
        if (rangeIndexSize == 0) {
            assertThat(sinkAdapter.getRangeIndexMap()).isNull();
        } else {
            long count = sinkAdapter.getRangeIndexMap().values().stream().mapToLong(index -> index.getAllValues().size()).sum();
            assertThat(count).isEqualTo(rangeIndexSize);
        }
    }

    @Test
    public void testNoMatch() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age < 20 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age <= 25 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age < 30 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( age >= 40 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( age > 45 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Person( age >= 50 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Person.class, 6, 6, 0, 6);

        ksession.insert(new Person("John", 30));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(0);

        ksession.insert(new Person("Paul", 40));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    @Test
    public void testDouble() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Order.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Order( total >= 18.0 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Order( total < 25.0 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Order( total > 8.0 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Order( total < 60.0 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Order( total > 12.0 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Order( total <= 4.0 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Order.class, 6, 6, 0, 6);

        Order o1 = new Order();
        o1.setTotal(18.0);
        ksession.insert(o1);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(5);

        Order o2 = new Order();
        o2.setTotal(60.0);
        ksession.insert(o2);
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(3);
    }

    @Test
    public void testString() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( name >= \"Ann\" )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( name < \"Bob\" )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( name > \"Kent\" )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( name < \"Steve\" )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( name > \"John\" )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Person( name <= \"Paul\" )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Person.class, 6, 6, 0, 6);

        ksession.insert(new Person("John"));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(3);

        ksession.insert(new Person("Paul"));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(5);
    }

    @Test
    public void testBigDecimal() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Primitives.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Primitives( bigDecimal >= 18.0 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Primitives( bigDecimal < 25.0 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Primitives( bigDecimal > 8.0 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Primitives( bigDecimal < 60.0 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Primitives( bigDecimal > 12.0 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Primitives( bigDecimal <= 4.0 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Primitives.class, 6, 6, 0, 6);

        Primitives p1 = new Primitives();
        p1.setBigDecimal(new BigDecimal("18.0"));
        ksession.insert(p1);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(5);

        Primitives p2 = new Primitives();
        p2.setBigDecimal(new BigDecimal("60.0"));
        ksession.insert(p2);
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(3);
    }

    @Test
    public void testNull() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Primitives.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Primitives( bigDecimal >= null )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Primitives( bigDecimal < 25.0 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Primitives( bigDecimal > 8.0 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Primitives( bigDecimal < 60.0 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Primitives( bigDecimal > 12.0 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Primitives( bigDecimal <= 4.0 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Primitives.class, 6, 6, 0, 5); // [bigDecimal >= null]  is in OtherSinks

        Primitives p1 = new Primitives();
        p1.setBigDecimal(new BigDecimal("18.0"));
        ksession.insert(p1);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(4);

        Primitives p2 = new Primitives();
        p2.setBigDecimal(null);
        ksession.insert(p2);
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(0); // bigDecimal >= null is false
    }

    @Test
    public void testEmpty() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( name >= \"Ann\" )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( name < \"Bob\" )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( name > \"\" )\n" + // this is comparable. Smallest
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( name < \"Steve\" )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( name > \"John\" )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Person( name <= \"Paul\" )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Person.class, 6, 6, 0, 6);

        ksession.insert(new Person("John"));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(4);

        ksession.insert(new Person(""));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(3);
    }

    @Test
    public void testDate() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Order.class.getCanonicalName() + "\n" +
                           "global java.util.List results;\n" +
                           "rule test1\n when\n" +
                           "   Order( date >= \"01-Oct-2020\" )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test2\n when\n" +
                           "   Order( date < \"01-Nov-2020\" )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test3\n when\n" +
                           "   Order( date > \"01-Oct-2010\" )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test4\n when\n" +
                           "   Order( date < \"01-Oct-2030\" )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test5\n when\n" +
                           "   Order( date > \"02-Oct-2020\" )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test6\n when\n" +
                           "   Order( date <= \"02-Apr-2020\" )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Order.class, 6, 6, 0, 6);

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Order o1 = new Order();
        o1.setDate(DateUtils.parseDate("01-Oct-2020"));
        ksession.insert(o1);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(4);
        assertThat(results).containsOnly("test1", "test2", "test3", "test4");

        results.clear();
        Order o2 = new Order();
        o2.setDate(DateUtils.parseDate("31-Dec-2010"));
        ksession.insert(o2);
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(4);
        assertThat(results).containsOnly("test2", "test3", "test4", "test6");
    }

    @Test
    public void testUnderThreshold() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Person.class, 2, 2, 2, 0);

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    @Test
    public void testSurroundingRange() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 0 && < 20 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age >= 20 && < 40 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age >= 40 && < 60 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Person.class, 3, 3, 0, 3);

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(0);
    }

    @Test
    public void testRemoveObjectSink() {
        final String drl = BASIC_DRL;

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession1 = kbase.newKieSession();

        ksession1.insert(new Person("John", 18));
        int fired1 = ksession1.fireAllRules();
        assertThat(fired1).isEqualTo(5);

        ksession1.insert(new Person("Paul", 60));
        fired1 = ksession1.fireAllRules();
        assertThat(fired1).isEqualTo(3);
        ksession1.dispose();

        // remove 2 rules
        kbase.removeRule("org.drools.compiler.test", "test2");
        kbase.removeRule("org.drools.compiler.test", "test3");

        if (this.kieBaseTestConfiguration.useAlphaNetworkCompiler()) {
            // after removeRule, ANC is not recreated
            return;
        }

        assertSinks(kbase, Person.class, 4, 4, 0, 4); // still above threshold

        final KieSession ksession2 = kbase.newKieSession();

        ksession2.insert(new Person("John", 18));
        int fired2 = ksession2.fireAllRules();
        assertThat(fired2).isEqualTo(3);

        ksession2.insert(new Person("Paul", 60));
        fired2 = ksession2.fireAllRules();
        assertThat(fired2).isEqualTo(2);
        ksession2.dispose();

        // remove 2 more rules
        kbase.removeRule("org.drools.compiler.test", "test4");
        kbase.removeRule("org.drools.compiler.test", "test5");

        assertSinks(kbase, Person.class, 2, 2, 2, 0); // now under threshold so put back from rangeIndex

        final KieSession ksession3 = kbase.newKieSession();

        ksession3.insert(new Person("John", 18));
        int fired3 = ksession3.fireAllRules();
        assertThat(fired3).isEqualTo(1);

        ksession3.insert(new Person("Paul", 60));
        fired3 = ksession3.fireAllRules();
        assertThat(fired3).isEqualTo(1);
        ksession3.dispose();
    }

    @Test
    public void testCustomComparable() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + MyComparableHolder.class.getCanonicalName() + "\n" +
                           "import " + MyComparable.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   MyComparableHolder( myComparable >= MyComparable.ABC )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   MyComparableHolder( myComparable < MyComparable.DEF )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   MyComparableHolder( myComparable > MyComparable.GHI )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   MyComparableHolder( myComparable < MyComparable.JKL )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   MyComparableHolder( myComparable > MyComparable.MNO )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   MyComparableHolder( myComparable <= MyComparable.PQR )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        // Doesn't support Object type for range index. See CompositeObjectSinkAdapter.isRangeIndexable()
        assertSinks(kbase, MyComparableHolder.class, 6, 6, 0, 0);

        MyComparable abc = new MyComparable("ABC", 1);
        ksession.insert(new MyComparableHolder(abc));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(4);

        MyComparable jkl = new MyComparable("JKL", 10);
        ksession.insert(new MyComparableHolder(jkl));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(3);
    }

    @Test
    public void testNestedProps() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( address.number >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( address.number < 25 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( address.number > 8 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( address.number < 60 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( address.number > 12 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Person( address.number <= 4 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        // Doesn't support nested prop for range index. See CompositeObjectSinkAdapter.isRangeIndexable()
        assertSinks(kbase, Person.class, 6, 6, 0, 0);

        Person person1 = new Person("John", 18);
        person1.setAddress(new Address("ABC street", 18, "London"));
        ksession.insert(person1);
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(5);

        Person person2 = new Person("Paul", 60);
        person2.setAddress(new Address("XYZ street", 60, "London"));
        ksession.insert(person2);
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(3);
    }

    @Test
    public void testMultipleProps() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( age < 60 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( age > 12 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Person( age <= 4 )\n" +
                           "then\n end\n" +
                           "rule test7\n when\n" +
                           "   Person( name >= \"Ann\" )\n" +
                           "then\n end\n" +
                           "rule test8\n when\n" +
                           "   Person( name < \"Bob\" )\n" +
                           "then\n end\n" +
                           "rule test9\n when\n" +
                           "   Person( name > \"Kent\" )\n" +
                           "then\n end\n" +
                           "rule test10\n when\n" +
                           "   Person( name < \"Steve\" )\n" +
                           "then\n end\n" +
                           "rule test11\n when\n" +
                           "   Person( name > \"John\" )\n" +
                           "then\n end\n" +
                           "rule test12\n when\n" +
                           "   Person( name <= \"Paul\" )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, Person.class);
        assertThat(otn).isNotNull();
        ObjectSinkPropagator objectSinkPropagator = otn.getObjectSinkPropagator();
        if (this.kieBaseTestConfiguration.useAlphaNetworkCompiler()) {
            objectSinkPropagator = ((CompiledNetwork) objectSinkPropagator).getOriginalSinkPropagator();
        }
        CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) objectSinkPropagator;
        ObjectSink[] sinks = sinkAdapter.getSinks();
        assertThat(sinks.length).isEqualTo(12);
        assertThat(sinkAdapter.size()).isEqualTo(12);
        assertThat(sinkAdapter.getRangeIndexableSinks()).isNull();
        Collection<AlphaRangeIndex> values = sinkAdapter.getRangeIndexMap().values();
        assertThat(values.size()).isEqualTo(2);
        for (AlphaRangeIndex alphaRangeIndex : values) {
            assertThat(alphaRangeIndex.size()).isEqualTo(6); // a tree for "age" has 6 nodes. a tree for "name" has 6 nodes
        }

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(8);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(8);
    }

    @Test
    public void testModify() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( age < 60 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( age > 12 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   $p : Person( age <= 4 )\n" +
                           "then\n" +
                           "  modify($p) { setAge(90) }\n" +
                           "end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Person.class, 6, 6, 0, 6);

        ksession.insert(new Person("John", 0));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(6);

    }

    @Test
    public void testIncrementalCompilation() {

        // 2 rules under threshold
        final String drl1 =
                "package org.drools.compiler.test\n" +
                            "import " + Person.class.getCanonicalName() + "\n" +
                            "rule test1\n when\n" +
                            "   Person( age >= 18 )\n" +
                            "then\n end\n" +
                            "rule test2\n when\n" +
                            "   Person( age < 25 )\n" +
                            "then\n end\n";

        // 6 rules over threshold
        final String drl2 =
                "package org.drools.compiler.test\n" +
                            "import " + Person.class.getCanonicalName() + "\n" +
                            "rule test1\n when\n" +
                            "   Person( age >= 18 )\n" +
                            "then\n end\n" +
                            "rule test2\n when\n" +
                            "   Person( age < 25 )\n" +
                            "then\n end\n" +
                            "rule test3\n when\n" +
                            "   Person( age > 8 )\n" +
                            "then\n end\n" +
                            "rule test4\n when\n" +
                            "   Person( age < 60 )\n" +
                            "then\n end\n" +
                            "rule test5\n when\n" +
                            "   Person( age > 12 )\n" +
                            "then\n end\n" +
                            "rule test6\n when\n" +
                            "   Person( age <= 4 )\n" +
                            "then\n end\n";

        // 2 rules under threshold
        final String drl3 =
                "package org.drools.compiler.test\n" +
                            "import " + Person.class.getCanonicalName() + "\n" +
                            "rule test5\n when\n" +
                            "   Person( age > 12 )\n" +
                            "then\n end\n" +
                            "rule test6\n when\n" +
                            "   Person( age <= 4 )\n" +
                            "then\n end\n";

        final KieServices ks = KieServices.Factory.get();

        // Create an in-memory jar for version 1.0.0
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, drl1);

        // Create a session and fire rules
        final KieContainer kc = ks.newKieContainer(releaseId1);
        KieBaseConfiguration kieBaseConfiguration = kieBaseTestConfiguration.getKieBaseConfiguration();
        kieBaseConfiguration.setOption(AlphaRangeIndexThresholdOption.get(3)); // Set 3 for test convenience.
        KieBase kbase = kc.newKieBase(kieBaseConfiguration);
        KieSession ksession = kbase.newKieSession();

        assertSinks(ksession.getKieBase(), Person.class, 2, 2, 2, 0);

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);
        ksession.dispose();

        // Create a new jar for version 1.1.0
        final ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade", "1.1.0");
        KieUtil.getKieModuleFromDrls(releaseId2, kieBaseTestConfiguration, drl2);

        // try to update the container to version 1.1.0
        kc.updateToVersion(releaseId2);

        // create and use a new session
        kieBaseConfiguration = kieBaseTestConfiguration.getKieBaseConfiguration();
        kieBaseConfiguration.setOption(AlphaRangeIndexThresholdOption.get(3)); // Set 3 for test convenience.
        kbase = kc.newKieBase(kieBaseConfiguration);
        ksession = kbase.newKieSession();

        assertSinks(ksession.getKieBase(), Person.class, 6, 6, 0, 6); // now fully indexed

        ksession.insert(new Person("Paul", 18));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(5);
        ksession.dispose();

        // Create a new jar for version 1.2.0
        final ReleaseId releaseId3 = ks.newReleaseId("org.kie", "test-upgrade", "1.2.0");
        KieUtil.getKieModuleFromDrls(releaseId3, kieBaseTestConfiguration, drl3);

        // try to update the container to version 1.2.0
        kc.updateToVersion(releaseId3);

        // create and use a new session
        kieBaseConfiguration = kieBaseTestConfiguration.getKieBaseConfiguration();
        kieBaseConfiguration.setOption(AlphaRangeIndexThresholdOption.get(3)); // Set 3 for test convenience.
        kbase = kc.newKieBase(kieBaseConfiguration);
        ksession = kbase.newKieSession();

        assertSinks(ksession.getKieBase(), Person.class, 2, 2, 2, 0); // under threshold so back to rangeIndexableSinks

        ksession.insert(new Person("George", 18));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
        ksession.dispose();
    }

    @Test
    public void testDefaultThreshold() {
        // Assuming AlphaRangeIndexThresholdOption.DEFAULT_VALUE == 9
        final String drl = "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( age < 60 )\n" +
                           "then\n end\n" +
                           "rule test5\n when\n" +
                           "   Person( age > 12 )\n" +
                           "then\n end\n" +
                           "rule test6\n when\n" +
                           "   Person( age <= 4 )\n" +
                           "then\n end\n" +
                           "rule test7\n when\n" +
                           "   Person( age < 1 )\n" +
                           "then\n end\n" +
                           "rule test8\n when\n" +
                           "   Person( age > 99 )\n" +
                           "then\n end\n" +
                           "rule test9\n when\n" +
                           "   Person( age <= 2 )\n" +
                           "then\n end\n";

        final KieModule kieModule = KieUtil.getKieModuleFromDrls("indexing-test", kieBaseTestConfiguration, drl);
        final KieContainer kieContainer = KieServices.get().newKieContainer(kieModule.getReleaseId());
        final KieBase kbase = kieContainer.getKieBase();

        assertSinks(kbase, Person.class, 9, 9, 0, 9); // indexed

        kbase.removeRule("org.drools.compiler.test", "test9");

        if (this.kieBaseTestConfiguration.useAlphaNetworkCompiler()) {
            // after removeRule, ANC is not recreated
            return;
        }

        assertSinks(kbase, Person.class, 8, 8, 8, 0); // under threshold so not indexed

        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(5);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(3);
    }

    @Test
    public void testMixedRangeHashAndOther() {
        final String drl = "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "global java.util.List results;\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 25 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test4\n when\n" +
                           "   Person( age == 60 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test5\n when\n" +
                           "   Person( age == 12 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test6\n when\n" +
                           "   Person( age == 4 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n" +
                           "rule test7\n when\n" +
                           "   Person( age != 18 )\n" +
                           "then\n" +
                           "   results.add(drools.getRule().getName());" +
                           "end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        final ObjectTypeNode otn = KieUtil.getObjectTypeNode(kbase, Person.class);
        assertThat(otn).isNotNull();
        ObjectSinkPropagator objectSinkPropagator = otn.getObjectSinkPropagator();
        if (this.kieBaseTestConfiguration.useAlphaNetworkCompiler()) {
            objectSinkPropagator = ((CompiledNetwork) objectSinkPropagator).getOriginalSinkPropagator();
        }
        CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) objectSinkPropagator;
        ObjectSink[] sinks = sinkAdapter.getSinks();
        assertThat(sinks.length).isEqualTo(7);
        assertThat(sinkAdapter.size()).isEqualTo(7);
        assertThat(sinkAdapter.getRangeIndexableSinks()).isNull();
        assertThat(sinkAdapter.getRangeIndexMap().entrySet().iterator().next().getValue().size()).isEqualTo(3);
        assertThat(sinkAdapter.getHashedSinkMap().size()).isEqualTo(3);
        assertThat(sinkAdapter.getOtherSinks().size()).isEqualTo(1);

        List<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        ksession.insert(new Person("John", 18));
        ksession.fireAllRules();
        assertThat(results).containsOnly("test1", "test2", "test3");
        results.clear();

        ksession.insert(new Person("Paul", 60));
        ksession.fireAllRules();
        assertThat(results).containsOnly("test1", "test3", "test4", "test7");
    }

    @Ignore("No need to test. Fails with standard-drl")
    @Test
    public void testCoercionStringToNumber() {
        final String drl =
                "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( name >= 20 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( name < 40 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( name > 50 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Person.class, 3, 3, 0, 0);

        ksession.insert(new Person("30"));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);

        ksession.insert(new Person("10"));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    @Test
    public void testDifferentNumberOfDigitsInDecimal() {
        // DROOLS-6313
        checkDifferentNumberOfDigitsInDecimal("10");
        checkDifferentNumberOfDigitsInDecimal("10.00");
        checkDifferentNumberOfDigitsInDecimal("10B");
    }

    private void checkDifferentNumberOfDigitsInDecimal(String value) {
        String drl =
                "import " + Factor.class.getCanonicalName() + ";\n" +
                     "rule R1 when\n" +
                     "    Factor( factorAmt > " + value + " )\n" +
                     "then end\n" +
                     "rule R2 when\n" +
                     "    Factor( factorAmt > 0.0, factorAmt <= 1.0 )\n" +
                     "then end\n" +
                     "rule R3 when\n" +
                     "    Factor( factorAmt > 1.0, factorAmt <= 3.0 )\n" +
                     "then end\n" +
                     "rule R4 when\n" +
                     "    Factor( factorAmt > 3.0, factorAmt <= 6.0 )\n" +
                     "then end\n" +
                     "rule R5 when\n" +
                     "    Factor( factorAmt > 6.0, factorAmt <= 10.0 )\n" +
                     "then end\n" +
                     "rule R6 when\n" +
                     "    Factor( factorAmt > 10.0 )\n" +
                     "then end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);

        final KieSession ksession = kbase.newKieSession();
        ksession.insert(new Factor(25.0));
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    public static class Factor {

        private final double factorAmt;

        public Factor(double factorAmt) {
            this.factorAmt = factorAmt;
        }

        public double getFactorAmt() {
            return factorAmt;
        }
    }

    @Test
    public void testIntegerWithStaticMethodAddedBeforeThreshold() {
        final String drl = "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "import " + StaticUtil.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age < StaticUtil.getThirty() )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 30 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n";

        final KieBase kbase;

            kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
            final KieSession ksession = kbase.newKieSession();
            assertSinks(kbase, Person.class, 4, 4, 0, 3); // "age < StaticUtil.getThirty()" is not range indexable

            ksession.insert(new Person("John", 18));
            int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(4);

            ksession.insert(new Person("Paul", 60));
            fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);
    }

    @Test
    public void testIntegerWithStaticMethodAddedAfterThreshold() {
        final String drl = "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "import " + StaticUtil.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 30 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( age < StaticUtil.getThirty() )\n" +
                           "then\n end\n";

        final KieBase kbase;

        kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();
        assertSinks(kbase, Person.class, 4, 4, 0, 3); // "age < StaticUtil.getThirty()" is not range indexable

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(4);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);
    }

    public static class StaticUtil {

        public static int getThirty() {
            return 30;
        }
    }

    @Test
    public void testSharedAlpha() {
        final String drl = "package org.drools.compiler.test\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "rule test1\n when\n" +
                           "   Person( age >= 18 )\n" +
                           "then\n end\n" +
                           "rule test2\n when\n" +
                           "   Person( age < 30 )\n" +
                           "then\n end\n" +
                           "rule test3\n when\n" +
                           "   Person( age > 8 )\n" +
                           "then\n end\n" +
                           "rule test4\n when\n" +
                           "   Person( age < 30 )\n" +
                           "then\n end\n";

        final KieBase kbase = createKieBaseWithRangeIndexThresholdValue(drl, 3);
        final KieSession ksession = kbase.newKieSession();

        assertSinks(kbase, Person.class, 3, 3, 0, 3); // sinksLength = 3, sinkAdapterSize = 3, rangeIndexableSinks is null, Size of RangeIndexed nodes = 3

        ksession.insert(new Person("John", 18));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(4);

        ksession.insert(new Person("Paul", 60));
        fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);
    }
}
