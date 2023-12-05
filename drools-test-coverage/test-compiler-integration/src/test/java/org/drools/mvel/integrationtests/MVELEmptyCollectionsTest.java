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
package org.drools.mvel.integrationtests;

import org.drools.base.base.ClassObjectType;
import org.drools.base.rule.constraint.AlphaNodeFieldConstraint;
import org.drools.core.reteoo.AlphaNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.MVELConstraint;
import org.drools.mvel.accessors.ClassFieldReader;
import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Cheesery;
import org.drools.mvel.compiler.FactA;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.TestEnum;
import org.drools.mvel.expr.MVELDebugHandler;
import org.drools.mvel.extractors.MVELObjectClassFieldReader;
import org.drools.mvel.integrationtests.facts.FactWithList;
import org.drools.mvel.integrationtests.facts.FactWithMap;
import org.drools.mvel.integrationtests.facts.FactWithObject;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.drools.util.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class MVELEmptyCollectionsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MVELEmptyCollectionsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        // Some of these fail without executable model, so test only executable model.
        return TestParametersUtil.getKieBaseCloudOnlyExecModelConfiguration();
    }

    @Test
    public void testEmptyListAsMethodParameter() {
        final String drl =
                "import " + FactWithList.class.getCanonicalName() + "; \n" +
                "rule \"test\"\n" +
                "dialect \"mvel\" \n" +
                "when\n" +
                "    $p: FactWithList()\n" +
                "then\n" +
                "    $p.setItems([]); \n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        final FactWithList f = new FactWithList("testString");
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(f.getItems()).hasSize(0);
    }

    @Test
    public void testEmptyListAsConstructorParameter() {
        final String drl =
                "import " + FactWithList.class.getCanonicalName() + "; \n" +
                        "import " + FactWithObject.class.getCanonicalName() + "; \n" +
                        "rule \"test\"\n" +
                        "dialect \"mvel\" \n" +
                        "when\n" +
                        "    $p: FactWithObject()\n" +
                        "then\n" +
                        "    $p.setObjectValue(new FactWithList([])); \n" +
                        "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        final FactWithObject f = new FactWithObject(null);
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(f.getObjectValue()).isInstanceOf(FactWithList.class);
    }

    @Test
    public void testEmptyMapAsMethodParameter() {
        final String drl =
                "import " + FactWithMap.class.getCanonicalName() + "; \n" +
                        "rule \"test\"\n" +
                        "dialect \"mvel\" \n" +
                        "when\n" +
                        "    $p: FactWithMap()\n" +
                        "then\n" +
                        "    $p.setItemsMap([]); \n" +
                        "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        final FactWithMap f = new FactWithMap(1, "testString");
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(f.getItemsMap()).hasSize(0);
    }

    @Test
    public void testEmptyMapAsConstructorParameter() {
        final String drl =
                "import " + FactWithMap.class.getCanonicalName() + "; \n" +
                        "import " + FactWithObject.class.getCanonicalName() + "; \n" +
                        "rule \"test\"\n" +
                        "dialect \"mvel\" \n" +
                        "when\n" +
                        "    $p: FactWithObject()\n" +
                        "then\n" +
                        "    $p.setObjectValue(new FactWithMap([])); \n" +
                        "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();
        final FactWithObject f = new FactWithObject(null);
        ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(f.getObjectValue()).isNotNull().isInstanceOf(FactWithMap.class);
    }
}
