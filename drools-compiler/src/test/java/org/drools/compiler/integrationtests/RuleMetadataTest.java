/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.rule.ConsequenceMetaData;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;

public class RuleMetadataTest extends CommonTestMethodBase {

    @Test
    public void testModify() {
        String rule1 = "modify( $a ) { setA( 20 ), setB( $bb ) }";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertEquals(ConsequenceMetaData.Statement.Type.MODIFY, statment.getType());
        assertEquals("org.drools.A", statment.getFactClassName());

        assertEquals(2, statment.getFields().size());
        ConsequenceMetaData.Field field1 = statment.getFields().get(0);
        assertEquals("a", field1.getName());
        assertEquals("20", field1.getValue());
        assertTrue(field1.isLiteral());
        ConsequenceMetaData.Field field2 = statment.getFields().get(1);
        assertEquals("b", field2.getName());
        assertEquals("$bb", field2.getValue());
        assertFalse(field2.isLiteral());
    }

    @Test
    public void testModify2() {
        String rule1 = "modify( $a ) { setC( $bc ) };\n modify( $b ) { c = \"Hello\" };";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertEquals(2, consequenceMetaData.getStatements().size());

        ConsequenceMetaData.Statement statment1 = consequenceMetaData.getStatements().get(0);
        assertEquals(ConsequenceMetaData.Statement.Type.MODIFY, statment1.getType());
        assertEquals("org.drools.A", statment1.getFactClassName());
        assertEquals(1, statment1.getFields().size());
        ConsequenceMetaData.Field field1 = statment1.getFields().get(0);
        assertEquals("c", field1.getName());
        assertEquals("$bc", field1.getValue());
        assertFalse(field1.isLiteral());

        ConsequenceMetaData.Statement statment2 = consequenceMetaData.getStatements().get(1);
        assertEquals(ConsequenceMetaData.Statement.Type.MODIFY, statment2.getType());
        assertEquals( RuleMetadataTest.B.class.getName(), statment2.getFactClassName());
        assertEquals(1, statment2.getFields().size());
        ConsequenceMetaData.Field field2 = statment2.getFields().get(0);
        assertEquals("c", field2.getName());
        assertEquals("\"Hello\"", field2.getValue());
        assertTrue(field2.isLiteral());
    }

    @Test
    public void testRetract() {
        String rule1 = "retract( $b );";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertEquals(1, consequenceMetaData.getStatements().size());

        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertEquals(ConsequenceMetaData.Statement.Type.RETRACT, statment.getType());
        assertEquals(RuleMetadataTest.B.class.getName(), statment.getFactClassName());
    }

    @Test
    public void testRetractWithFunction() {
        String rule1 = "retract( getA($a) );";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertEquals(1, consequenceMetaData.getStatements().size());

        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertEquals(ConsequenceMetaData.Statement.Type.RETRACT, statment.getType());
        assertEquals("org.drools.A", statment.getFactClassName());
    }

    @Test
    public void testUpdate() {
        String rule1 = "$a.setA( 20 );\n $a.setB( $bb );\n update( $a );";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertEquals(ConsequenceMetaData.Statement.Type.MODIFY, statment.getType());
        assertEquals("org.drools.A", statment.getFactClassName());

        assertEquals(2, statment.getFields().size());
        ConsequenceMetaData.Field field1 = statment.getFields().get(0);
        assertEquals("a", field1.getName());
        assertEquals("20", field1.getValue());
        assertTrue(field1.isLiteral());
        ConsequenceMetaData.Field field2 = statment.getFields().get(1);
        assertEquals("b", field2.getName());
        assertEquals("$bb", field2.getValue());
        assertFalse(field2.isLiteral());
    }

    @Test
    public void testUpdate2() {
        String rule1 = "$a.setC( $bc );\n $b.c = \"Hello\";\n update( $a );\n update( $b );";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertEquals(2, consequenceMetaData.getStatements().size());

        ConsequenceMetaData.Statement statment1 = consequenceMetaData.getStatements().get(0);
        assertEquals(ConsequenceMetaData.Statement.Type.MODIFY, statment1.getType());
        assertEquals("org.drools.A", statment1.getFactClassName());
        assertEquals(1, statment1.getFields().size());
        ConsequenceMetaData.Field field1 = statment1.getFields().get(0);
        assertEquals("c", field1.getName());
        assertEquals("$bc", field1.getValue());
        assertFalse(field1.isLiteral());

        ConsequenceMetaData.Statement statment2 = consequenceMetaData.getStatements().get(1);
        assertEquals(ConsequenceMetaData.Statement.Type.MODIFY, statment2.getType());
        assertEquals(RuleMetadataTest.B.class.getName(), statment2.getFactClassName());
        assertEquals(1, statment2.getFields().size());
        ConsequenceMetaData.Field field2 = statment2.getFields().get(0);
        assertEquals("c", field2.getName());
        assertEquals("\"Hello\"", field2.getValue());
        assertTrue(field2.isLiteral());
    }

    @Test
    public void testInsert() {
        String rule1 = "insert( new A(1, $bb, \"3\") );";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertEquals(ConsequenceMetaData.Statement.Type.INSERT, statment.getType());
        assertEquals("org.drools.A", statment.getFactClassName());

        assertEquals(3, statment.getFields().size());
        ConsequenceMetaData.Field field1 = statment.getFields().get(0);
        assertEquals("a", field1.getName());
        assertEquals("1", field1.getValue());
        assertTrue(field1.isLiteral());
        ConsequenceMetaData.Field field2 = statment.getFields().get(1);
        assertEquals("b", field2.getName());
        assertEquals("$bb", field2.getValue());
        assertFalse(field2.isLiteral());
        ConsequenceMetaData.Field field3 = statment.getFields().get(2);
        assertEquals("c", field3.getName());
        assertEquals("\"3\"", field3.getValue());
        assertTrue(field3.isLiteral());
    }

    @Test
    public void testInsert2() {
        String rule1 = "insert( new B(1, $ab) );";
        KnowledgeBase kbase = getKnowledgeBase(rule1);
        RuleImpl rule = getRule(kbase, "R0");

        ConsequenceMetaData consequenceMetaData = rule.getConsequenceMetaData();
        assertEquals(1, consequenceMetaData.getStatements().size());
        ConsequenceMetaData.Statement statment = consequenceMetaData.getStatements().get(0);
        assertEquals(ConsequenceMetaData.Statement.Type.INSERT, statment.getType());
        assertEquals(RuleMetadataTest.B.class.getName(), statment.getFactClassName());
    }

    private KnowledgeBase getKnowledgeBase(String... consequences) {
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

        return loadKnowledgeBaseFromString( rule );
    }

    private RuleImpl getRule(KnowledgeBase kbase, String ruleName) {
        return ((KnowledgeBaseImpl)kbase).getPackage("org.drools").getRule(ruleName);
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
