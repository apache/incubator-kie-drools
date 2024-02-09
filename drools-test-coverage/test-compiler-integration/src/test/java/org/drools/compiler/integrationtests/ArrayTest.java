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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.testcoverage.common.model.Primitives;
import org.drools.testcoverage.common.model.TestParam;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ArrayTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ArrayTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testEqualsOnIntArray() {

        final String drl =
            "package org.drools.compiler;\n" +
            "import " + Primitives.class.getCanonicalName() + ";\n" +
            "global java.util.List list;\n" +
            "rule \"contains in array\"\n" +
            "     salience 10\n" +
            "     when\n" +
            "         Primitives( primitiveIntArray[0] == 1 )\n" +
            "     then\n" +
            "        list.add( \"ok1\" );\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Primitives p1 = new Primitives();
            p1.setPrimitiveIntArray(new int[]{1, 2});
            ksession.insert(p1);

            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testContainsBooleanArray() {

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Boolean", false, "true"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, true, new Boolean[]{true, false});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveBooleanArray(new boolean[]{true, false});
            p1.setBooleanPrimitive(false);

            final Primitives p2 = new Primitives();
            p2.setPrimitiveBooleanArray(new boolean[]{true, false});
            p2.setBooleanPrimitive(true);

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testNotContainsBooleanArray() {
        final String drl =
            "package org.drools.compiler;\n" +
            "import " + Primitives.class.getCanonicalName() + ";\n" +
            "global java.util.List list;\n" +
            "global Boolean nGlobal;\n" +
            "global Object arrayGlobal;\n" +
            "rule \"contains in array\"\n" +
            "     salience 10\n" +
            "     when\n" +
            "         Primitives( $b : booleanPrimitive, intPrimitive == 10, $array1 :  primitiveBooleanArray ) \n" +
            "         Primitives( booleanPrimitive == false, intPrimitive != 10, $array2 : primitiveBooleanArray not contains nGlobal,  primitiveBooleanArray not contains $b, " +
            "                     booleanPrimitive not memberOf $array2, booleanPrimitive not memberOf $array1, booleanPrimitive not memberOf arrayGlobal )\n" +
            "     then\n" +
            "        list.add( \"ok1\" );\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration, drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, false, new Boolean[]{true, true});

            final Primitives p1 = new Primitives();
            p1.setIntPrimitive(10);
            p1.setPrimitiveBooleanArray(new boolean[]{true, true});
            p1.setBooleanPrimitive(false);

            final Primitives p2 = new Primitives();
            p2.setPrimitiveBooleanArray(new boolean[]{true, true});
            p2.setBooleanPrimitive(false);

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testContainsByteArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Byte", false, "1"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, (byte) 1, new Byte[]{1, 2, 3});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveByteArray(new byte[]{1, 2, 3});
            p1.setBytePrimitive((byte) 2);

            final Primitives p2 = new Primitives();
            p2.setPrimitiveByteArray(new byte[]{1, 2, 3});
            p2.setBytePrimitive((byte) 1);

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testNotContainsByteArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Byte", true, "1"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, (byte) 1, new Byte[]{4, 5, 6});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveByteArray(new byte[]{4, 5, 6});
            p1.setBytePrimitive((byte) 2);

            final Primitives p2 = new Primitives();
            p2.setPrimitiveByteArray(new byte[]{4, 5, 6});
            p2.setBytePrimitive((byte) 1);

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testContainsShortArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Short", false, "1"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, (short) 1, new Short[]{1, 2, 3});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveShortArray(new short[]{1, 2, 3});
            p1.setShortPrimitive((short) 2);

            final Primitives p2 = new Primitives();
            p2.setPrimitiveShortArray(new short[]{1, 2, 3});
            p2.setShortPrimitive((short) 1);

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testNotContainsShortArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Short", true, "1"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, (short) 1, new Short[]{4, 5, 6});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveShortArray(new short[]{4, 5, 6});
            p1.setShortPrimitive((short) 2);

            final Primitives p2 = new Primitives();
            p2.setPrimitiveShortArray(new short[]{4, 5, 6});
            p2.setShortPrimitive((short) 1);

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testContainsCharArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Character", false, "'c'"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 'c', new Character[]{'a', 'b', 'c'});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveCharArray(new char[]{'a', 'b', 'c'});
            p1.setCharPrimitive('a');

            final Primitives p2 = new Primitives();
            p2.setPrimitiveCharArray(new char[]{'a', 'b', 'c'});
            p2.setCharPrimitive('c');

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testNotContainsCharArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Character", true, "'c'"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 'c', new Character[]{'d', 'e', 'f'});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveCharArray(new char[]{'d', 'e', 'f'});
            p1.setCharPrimitive('a');

            final Primitives p2 = new Primitives();
            p2.setPrimitiveCharArray(new char[]{'d', 'e', 'f'});
            p2.setCharPrimitive('c');

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testContainsIntArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Integer", false, "10"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 10, new Integer[]{5, 10, 20});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveIntArray(new int[]{5, 10, 20});
            p1.setIntPrimitive(5);

            final Primitives p2 = new Primitives();
            p2.setPrimitiveIntArray(new int[]{5, 10, 20});
            p2.setIntPrimitive(10);

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testNotContainsIntArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Integer", true, "10"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 10, new Integer[]{40, 50, 60});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveIntArray(new int[]{40, 50, 60});
            p1.setIntPrimitive(5);

            final Primitives p2 = new Primitives();
            p2.setPrimitiveIntArray(new int[]{40, 50, 60});
            p2.setIntPrimitive(10);

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testContainsLongArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Long", false, "10"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 10L, new Long[]{5L, 10L, 20L});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveLongArray(new long[]{5, 10, 20});
            p1.setLongPrimitive(5);

            final Primitives p2 = new Primitives();
            p2.setLongPrimitive(10);
            p2.setPrimitiveLongArray(new long[]{5, 10, 20});

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testNotContainsLongArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Long", true, "10"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 10L, new Long[]{40L, 50L, 60L});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveLongArray(new long[]{40, 50, 60});
            p1.setLongPrimitive(5);

            final Primitives p2 = new Primitives();
            p2.setLongPrimitive(10);
            p2.setPrimitiveLongArray(new long[]{40, 50, 60});

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testContainsFloatArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Float", false, "10f"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 10.0f, new Float[]{5f, 10f, 20f});

            final Primitives p1 = new Primitives();
            p1.setFloatPrimitive(5);
            p1.setPrimitiveFloatArray(new float[]{5, 10, 20});

            final Primitives p2 = new Primitives();
            p2.setFloatPrimitive(10);
            p2.setPrimitiveFloatArray(new float[]{5, 10, 20});

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testNotContainsFloatArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Float", true, "10f"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 10.0f, new Float[]{40f, 50f, 60f});

            final Primitives p1 = new Primitives();
            p1.setFloatPrimitive(5);
            p1.setPrimitiveFloatArray(new float[]{40, 50, 60});

            final Primitives p2 = new Primitives();
            p2.setFloatPrimitive(10);
            p2.setPrimitiveFloatArray(new float[]{40, 50, 60});

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testContainsDoubleArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Double", false, "10"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 10.0d, new Double[]{5d, 10d, 20d});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveDoubleArray(new double[]{5, 10, 20});
            p1.setDoublePrimitive(5);

            final Primitives p2 = new Primitives();
            p2.setDoublePrimitive(10);
            p2.setPrimitiveDoubleArray(new double[]{5, 10, 20});

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }
    
    @Test
    public void testNotContainsDoubleArray() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration,
                                                                         getDrl("Double", true, "10"));
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            addGlobalsToSession(kieSession, list, 10.0d, new Double[]{40d, 50d, 60d});

            final Primitives p1 = new Primitives();
            p1.setPrimitiveDoubleArray(new double[]{40, 50, 60});
            p1.setDoublePrimitive(5);

            final Primitives p2 = new Primitives();
            p2.setDoublePrimitive(10);
            p2.setPrimitiveDoubleArray(new double[]{40, 50, 60});

            testArrayContains(kieSession, p1, p2, list);
        } finally {
            kieSession.dispose();
        }
    }

    private String getDrl(final String type, final boolean testNot, final String primitiveTestValue) {
        final String primitiveName = getPrimitiveAttributeName(type);
        final String primitiveArrayName = getPrimitiveArrayName(type);
        final String notString = testNot ? " not " : "";

        return "package org.drools.compiler;\n" +
                "import " + Primitives.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "global " + type + " nGlobal;\n" +
                "global Object arrayGlobal;\n" +
                "rule \"contains in array\"\n" +
                "     salience 10\n" +
                "     when\n" +
                "         Primitives( $d : " + primitiveName + " == " + primitiveTestValue + ", $array1 :  " + primitiveArrayName + " ) \n" +
                "         Primitives( " + primitiveName + " != " + primitiveTestValue + ", $array2 : " + primitiveArrayName + notString + " contains nGlobal,  " + primitiveArrayName + notString + " contains $d, \n" +
                "                     " + primitiveName + notString + " memberOf $array2, " + primitiveName + notString + " memberOf $array1, " + primitiveName + notString + " memberOf arrayGlobal )\n" +
                "     then\n" +
                "        list.add( \"ok1\" );\n" +
                "end\n";
    }

    private String getPrimitiveAttributeName(final String type) {
        if ("Character".equals(type)) {
            return "charPrimitive";
        } else if ("Integer".equals(type)) {
            return "intPrimitive";
        } else {
            return type.toLowerCase() + "Primitive";
        }
    }

    private String getPrimitiveArrayName(final String type) {
        if ("Character".equals(type)) {
            return "primitiveCharArray";
        } else if ("Integer".equals(type)) {
            return "primitiveIntArray";
        } else {
            return "primitive" + type + "Array";
        }
    }

    private void addGlobalsToSession(final KieSession kieSession, final List list, final Object dGlobal,
            final Object[] dArrayGlobal) {
        kieSession.setGlobal("list", list);
        kieSession.setGlobal("nGlobal", dGlobal);
        kieSession.setGlobal("arrayGlobal", dArrayGlobal);
    }

    private void testArrayContains(final KieSession kieSession, final Primitives p1, final Primitives p2,
            final List resultsList) {
        final FactHandle p1h = kieSession.insert(p1);
        kieSession.insert(p2);
        kieSession.fireAllRules();
        assertThat(resultsList.size()).isEqualTo(1);

        kieSession.delete(p1h);
        kieSession.insert(p1);
        kieSession.fireAllRules();
        assertThat(resultsList.size()).isEqualTo(2);
    }

    @Test
    public void testPrimitiveArray() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Primitives.class.getCanonicalName() + "\n" +
                "global java.util.List result;\n" +
                "\n" +
                "function boolean testNonEmptyArray(int[] elements) {\n" +
                "   return elements != null && elements.length > 0;\n" +
                "}\n" +
                "\n" +
                "function boolean isNonEmptyObjectArray(Object[] elements) {\n" +
                "   return elements != null && elements.length > 0;\n" +
                "}\n" +
                "\n" +
                "rule \"Primitive elements in function\" salience 20\n" +
                "  when\n" +
                "    Primitives( $elements : primitiveIntArray )\n" +
                "    eval( testNonEmptyArray($elements) )\n" +
                "  then\n" +
                "     result.add( new Integer( $elements.length ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Primitive Array with Object in function\" salience 10\n" +
                " when\n" +
                "   Primitives( $sArray : arrayAttribute )\n" +
                "   eval( isNonEmptyObjectArray($sArray) )\n" +
                " then\n" +
                "     result.add( new Integer( $sArray.length ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Test Array\" salience 0\n" +
                "  when\n" +
                "    Primitives( $elements : primitiveIntArray, eval(($elements != null) && ($elements.length > 0)))\n" +
                "  then\n" +
                "    result.add( new Integer( $elements.length ) );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            final List result = new ArrayList();
            session.setGlobal("result", result);

            final Primitives p1 = new Primitives();
            p1.setPrimitiveIntArray(new int[]{1, 2, 3});
            p1.setArrayAttribute(new String[]{"a", "b"});

            session.insert(p1);
            session.fireAllRules();

            assertThat(result.size()).isEqualTo(3);
            assertThat(((Integer) result.get(0)).intValue()).isEqualTo(3);
            assertThat(((Integer) result.get(1)).intValue()).isEqualTo(2);
            assertThat(((Integer) result.get(2)).intValue()).isEqualTo(3);
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testArrayUsage() {
        final String drl =
                "import " + TestParam.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Intercept\"\n" +
                "when\n" +
                "    TestParam( value1 == \"extract\", $args : elements )\n" +
                "    $s : String( this == $args[$s.length() - $s.length()] )\n" +
                "    $s1 : String( this == $args[0] )\n" +
                "    $s2 : String( this == $args[1] )\n" +
                "    Integer( this == 2 ) from $args.length\n" +
                "    $s3 : String( this == $args[$args.length - $args.length  + 1] )\n" +
                "then\n" +
                "    delete( $s1 );  \n" +
                "    delete( $s2 );  \n" +
                "    list.add( $s1 ); \n" +
                "    list.add( $s2 ); \n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("array-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final int N = 2;
            for (int j = 0; j < N; j++) {
                final TestParam o = new TestParam();
                o.setValue1("extract");
                o.setElements(new Object[]{"x1_" + j, "x2_" + j});
                ksession.insert("x1_" + j);
                ksession.insert("x2_" + j);
                ksession.insert(o);
                ksession.fireAllRules();
            }

            assertThat(list.size()).isEqualTo(4);
            assertThat(list.contains("x1_0")).isTrue();
            assertThat(list.contains("x1_1")).isTrue();
            assertThat(list.contains("x2_0")).isTrue();
            assertThat(list.contains("x2_1")).isTrue();
        } finally {
            ksession.dispose();
        }
    }
}