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
package org.drools.testcoverage.regression;

import java.util.Collection;

import org.drools.testcoverage.common.KieSessionTest;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class EqualityKeyOverrideTest extends KieSessionTest {

    private static final int ANY_NUMBER = 42;

    private static final String DRL =
            "package org.drools.testcoverage.regression;\n" +
            "declare Superclass\n" +
            "end\n" +
            "declare Subclass extends Superclass\n" +
            "end\n" +
            "rule insertSubclass\n" +
            "    when\n" +
            "        String()\n" +
            "    then\n" +
            "        insertLogical(new Subclass());\n" +
            "end\n" +
            "rule insertSuperclass\n" +
            "    when\n" +
            "        Integer()\n" +
            "    then\n" +
            "        insertLogical(new Superclass());\n" +
            "end\n";

    public EqualityKeyOverrideTest(final KieBaseTestConfiguration kieBaseTestConfiguration,
                                   final KieSessionTestConfiguration kieSessionTestConfiguration) {
        super(kieBaseTestConfiguration, kieSessionTestConfiguration);
    }

    @Parameterized.Parameters(name = "{1}" + " (from " + "{0}" + ")")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseAndStatefulKieSessionConfigurations();
    }

    @Test
    public void testEqualityOverride() {
        KieSession ksession =  session.getStateful();

        FactHandle string = ksession.insert("testString");
        ksession.fireAllRules();

        assertThat(ksession.getObjects().size()).isEqualTo(2);

        ksession.insert(ANY_NUMBER);
        ksession.fireAllRules();

        assertThat(ksession.getObjects().size()).isEqualTo(4);

        ksession.delete(string);
        ksession.fireAllRules();
        assertThat(ksession.getObjects().size()).isEqualTo(2);
    }

    @Override
    protected Resource[] createResources() {
        return KieUtil.createResources(DRL);
    }
}
