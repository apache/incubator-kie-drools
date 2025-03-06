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

import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeDeclarationTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testRecursiveDeclaration(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String rule = "package org.drools.compiler\n" +
                      "declare Node\n" +
                      "    value: String\n" +
                      "    parent: Node\n" +
                      "end\n" +
                      "rule R1 when\n" +
                      "   $parent: Node( value == \"parent\" )\n" +
                      "   $child: Node( $value : value, parent == $parent )\n" +
                      "then\n" +
                      "   System.out.println( $value );\n" +
                      "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule);
        KieSession ksession = kbase.newKieSession();

        FactType nodeType = kbase.getFactType( "org.drools.compiler", "Node" );
        Object parent = nodeType.newInstance();
        nodeType.set( parent, "value", "parent" );
        ksession.insert( parent );

        Object child = nodeType.newInstance();
        nodeType.set( child, "value", "child" );
        nodeType.set( child, "parent", parent );
        ksession.insert( child );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testCircularDeclaration(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        String rule = "package org.drools.mvel.compiler.test\n" +
                      "declare FactA\n" +
                      "    fieldB: FactB\n" +
                      "end\n" +
                      "declare FactB\n" +
                      "    fieldA: FactA\n" +
                      "end\n" +
                      "rule R1 when\n" +
                      "   $fieldA : FactA( $fieldB : fieldB )\n" +
                      "   FactB( this == $fieldB, fieldA == $fieldA )\n" +
                      "then\n" +
                      "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule);
        KieSession ksession = kbase.newKieSession();

        FactType aType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactA" );
        Object a = aType.newInstance();
        FactType bType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactB" );
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        bType.set( b, "fieldA", a );
        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testCircularDeclarationWithExtension(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        // DROOLS-640
        String drl = "package org.drools.mvel.compiler.test\n" +
                     "declare FactA\n" +
                     "    fieldB: FactB\n" +
                     "end\n" +
                     "declare FactB extends FactA end\n" +
                     "rule R1 when\n" +
                     "   $a : FactA( )\n" +
                     "   $b : FactB( this == $a.fieldB )\n" +
                     "then\n" +
                     "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        FactType aType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactA" );
        FactType bType = kbase.getFactType( "org.drools.mvel.compiler.test", "FactB" );
        Object a = aType.newInstance();
        Object b = bType.newInstance();
        aType.set( a, "fieldB", b );
        ksession.insert( a );
        ksession.insert( b );

        int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }
}
