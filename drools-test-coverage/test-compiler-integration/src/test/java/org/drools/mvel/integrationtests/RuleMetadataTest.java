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

import java.util.Collection;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalRuleBase;
import org.drools.base.rule.ConsequenceMetaData;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class RuleMetadataTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RuleMetadataTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs. This test may not be necessary for exec-model
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testModify() {
        String rule1 = "modify( $a ) { setA( 20 ), setB( $bb ) }";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertThat(consequenceMetaData.getStatements().size()).isEqualTo(1);
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertThat(statment.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.MODIFY);
        assertThat(statment.getFactClassName()).isEqualTo("org.drools.A");

        assertThat(statment.getFields().size()).isEqualTo(2);
        ConsequenceMetaData.Field field1 = statment.getFields().get(0);
        assertThat(field1.getName()).isEqualTo("a");
        assertThat(field1.getValue()).isEqualTo("20");
        assertThat(field1.isLiteral()).isTrue();
        ConsequenceMetaData.Field field2 = statment.getFields().get(1);
        assertThat(field2.getName()).isEqualTo("b");
        assertThat(field2.getValue()).isEqualTo("$bb");
        assertThat(field2.isLiteral()).isFalse();
    }

    @Test
    public void testModify2() {
        String rule1 = "modify( $a ) { setC( $bc ) };\n modify( $b ) { c = \"Hello\" };";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertThat(consequenceMetaData.getStatements().size()).isEqualTo(2);

        ConsequenceMetaData.Statement statment1 = consequenceMetaData.getStatements().get(0);
        assertThat(statment1.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.MODIFY);
        assertThat(statment1.getFactClassName()).isEqualTo("org.drools.A");
        assertThat(statment1.getFields().size()).isEqualTo(1);
        ConsequenceMetaData.Field field1 = statment1.getFields().get(0);
        assertThat(field1.getName()).isEqualTo("c");
        assertThat(field1.getValue()).isEqualTo("$bc");
        assertThat(field1.isLiteral()).isFalse();

        ConsequenceMetaData.Statement statment2 = consequenceMetaData.getStatements().get(1);
        assertThat(statment2.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.MODIFY);
        assertThat(statment2.getFactClassName()).isEqualTo(RuleMetadataTest.B.class.getName());
        assertThat(statment2.getFields().size()).isEqualTo(1);
        ConsequenceMetaData.Field field2 = statment2.getFields().get(0);
        assertThat(field2.getName()).isEqualTo("c");
        assertThat(field2.getValue()).isEqualTo("\"Hello\"");
        assertThat(field2.isLiteral()).isTrue();
    }

    @Test
    public void testRetract() {
        String rule1 = "retract( $b );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertThat(consequenceMetaData.getStatements().size()).isEqualTo(1);

        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertThat(statment.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.RETRACT);
        assertThat(statment.getFactClassName()).isEqualTo(RuleMetadataTest.B.class.getName());
    }

    @Test
    public void testRetractWithFunction() {
        String rule1 = "retract( getA($a) );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertThat(consequenceMetaData.getStatements().size()).isEqualTo(1);

        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertThat(statment.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.RETRACT);
        assertThat(statment.getFactClassName()).isEqualTo("org.drools.A");
    }

    @Test
    public void testUpdate() {
        String rule1 = "$a.setA( 20 );\n $a.setB( $bb );\n update( $a );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertThat(consequenceMetaData.getStatements().size()).isEqualTo(1);
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertThat(statment.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.MODIFY);
        assertThat(statment.getFactClassName()).isEqualTo("org.drools.A");

        assertThat(statment.getFields().size()).isEqualTo(2);
        ConsequenceMetaData.Field field1 = statment.getFields().get(0);
        assertThat(field1.getName()).isEqualTo("a");
        assertThat(field1.getValue()).isEqualTo("20");
        assertThat(field1.isLiteral()).isTrue();
        ConsequenceMetaData.Field field2 = statment.getFields().get(1);
        assertThat(field2.getName()).isEqualTo("b");
        assertThat(field2.getValue()).isEqualTo("$bb");
        assertThat(field2.isLiteral()).isFalse();
    }

    @Test
    public void testUpdate2() {
        String rule1 = "$a.setC( $bc );\n $b.c = \"Hello\";\n update( $a );\n update( $b );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertThat(consequenceMetaData.getStatements().size()).isEqualTo(2);

        ConsequenceMetaData.Statement statment1 = consequenceMetaData.getStatements().get(0);
        assertThat(statment1.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.MODIFY);
        assertThat(statment1.getFactClassName()).isEqualTo("org.drools.A");
        assertThat(statment1.getFields().size()).isEqualTo(1);
        ConsequenceMetaData.Field field1 = statment1.getFields().get(0);
        assertThat(field1.getName()).isEqualTo("c");
        assertThat(field1.getValue()).isEqualTo("$bc");
        assertThat(field1.isLiteral()).isFalse();

        ConsequenceMetaData.Statement statment2 = consequenceMetaData.getStatements().get(1);
        assertThat(statment2.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.MODIFY);
        assertThat(statment2.getFactClassName()).isEqualTo(RuleMetadataTest.B.class.getName());
        assertThat(statment2.getFields().size()).isEqualTo(1);
        ConsequenceMetaData.Field field2 = statment2.getFields().get(0);
        assertThat(field2.getName()).isEqualTo("c");
        assertThat(field2.getValue()).isEqualTo("\"Hello\"");
        assertThat(field2.isLiteral()).isTrue();
    }

    @Test
    public void testInsert() {
        String rule1 = "insert( new A(1, $bb, \"3\") );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertThat(consequenceMetaData.getStatements().size()).isEqualTo(1);
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertThat(statment.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.INSERT);
        assertThat(statment.getFactClassName()).isEqualTo("org.drools.A");

        assertThat(statment.getFields().size()).isEqualTo(3);
        ConsequenceMetaData.Field field1 = statment.getFields().get(0);
        assertThat(field1.getName()).isEqualTo("a");
        assertThat(field1.getValue()).isEqualTo("1");
        assertThat(field1.isLiteral()).isTrue();
        ConsequenceMetaData.Field field2 = statment.getFields().get(1);
        assertThat(field2.getName()).isEqualTo("b");
        assertThat(field2.getValue()).isEqualTo("$bb");
        assertThat(field2.isLiteral()).isFalse();
        ConsequenceMetaData.Field field3 = statment.getFields().get(2);
        assertThat(field3.getName()).isEqualTo("c");
        assertThat(field3.getValue()).isEqualTo("\"3\"");
        assertThat(field3.isLiteral()).isTrue();
    }

    @Test
    public void testInsert2() {
        String rule1 = "insert( new B(1, $ab) );";
        KieBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertThat(consequenceMetaData.getStatements().size()).isEqualTo(1);
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertThat(statment.getType()).isEqualTo(ConsequenceMetaData.Statement.Type.INSERT);
        assertThat(statment.getFactClassName()).isEqualTo(RuleMetadataTest.B.class.getName());
    }

    private KieBase getKnowledgeBase(String... consequences) {
        String rule = "package org.drools\n" +
                "import " + RuleMetadataTest.B.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "function A getA(A a) {\n" +
                "    return a;\n" +
                "}" +
                "declare A\n" +
                "    a : int\n" +
                "    b : int\n" +
                "    c : String\n" +
                "end\n";

        int i = 0;
        for ( String str : consequences ) {
            rule += "rule R" + (i++) + "\n" +
                    "when\n" +
                    "   $a : A( $aa : a, $ab : b, $ac : c )\n" +
                    "   $b : B( $ba : a, $bb : b, $bc : c )\n" +
                    "then\n" +
                    str +
                    "\nend\n";
        }

        return KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule);
    }

    private RuleImpl getRule(KieBase kbase, String ruleName) {
        return ((InternalRuleBase)kbase).getPackage("org.drools").getRule(ruleName);
    }

    public static class B {
        public int a;
        public int b;
        public String c;

        public B() { }

        public B(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public String getC() {
            return c;
        }

        public void setC(String c) {
            this.c = c;
        }
    }
}
