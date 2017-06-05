/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Primitives;
import org.drools.compiler.TestParam;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class ArrayTest extends CommonTestMethodBase {

    @Test
    public void testEqualsOnIntArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";
        str += "global java.util.List list;\n";
        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( primitiveIntArray[0] == 1 )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        KieBase kbase = loadKnowledgeBaseFromString(str);

        kbase = SerializationHelper.serializeObject(kbase);
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Primitives p1 = new Primitives();
        p1.setPrimitiveIntArray(new int[]{1, 2});
        final FactHandle p1h = ksession.insert(p1);

        ksession.fireAllRules();
        assertEquals(1, list.size());
    }

    @Test
    public void testContainsBooleanArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Boolean", false, "true"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, true, new Boolean[]{true, false});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveBooleanArray(new boolean[]{true, false});
        p1.setBooleanPrimitive(false);

        final Primitives p2 = new Primitives();
        p2.setPrimitiveBooleanArray(new boolean[]{true, false});
        p2.setBooleanPrimitive(true);

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testNotContainsBooleanArray() throws Exception {
        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global Boolean nGlobal;\n";
        str += "global Object arrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $b : booleanPrimitive, intPrimitive == 10, $array1 :  primitiveBooleanArray ) \n";
        str += "         Primitives( booleanPrimitive == false, intPrimitive != 10, $array2 : primitiveBooleanArray not contains nGlobal,  primitiveBooleanArray not contains $b, ";
        str += "                     booleanPrimitive not memberOf $array2, booleanPrimitive not memberOf $array1, booleanPrimitive not memberOf arrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";

        final KieSession kieSession = createKieSessionFromDrl(str);
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
    }
    
    @Test
    public void testContainsByteArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Byte", false, "1"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, (byte) 1, new Byte[]{1, 2, 3});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveByteArray(new byte[]{1, 2, 3});
        p1.setBytePrimitive((byte) 2);

        final Primitives p2 = new Primitives();
        p2.setPrimitiveByteArray(new byte[]{1, 2, 3});
        p2.setBytePrimitive((byte) 1);

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testNotContainsByteArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Byte", true, "1"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, (byte) 1, new Byte[]{4, 5, 6});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveByteArray(new byte[]{4, 5, 6});
        p1.setBytePrimitive((byte) 2);

        final Primitives p2 = new Primitives();
        p2.setPrimitiveByteArray(new byte[]{4, 5, 6});
        p2.setBytePrimitive((byte) 1);

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testContainsShortArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Short", false, "1"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, (short) 1, new Short[]{1, 2, 3});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveShortArray(new short[]{1, 2, 3});
        p1.setShortPrimitive((short) 2);

        final Primitives p2 = new Primitives();
        p2.setPrimitiveShortArray(new short[]{1, 2, 3});
        p2.setShortPrimitive((short) 1);

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testNotContainsShortArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Short", true, "1"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, (short) 1, new Short[]{4, 5, 6});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveShortArray(new short[]{4, 5, 6});
        p1.setShortPrimitive((short) 2);

        final Primitives p2 = new Primitives();
        p2.setPrimitiveShortArray(new short[]{4, 5, 6});
        p2.setShortPrimitive((short) 1);

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testContainsCharArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Character", false, "'c'"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 'c', new Character[]{'a', 'b', 'c'});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveCharArray(new char[]{'a', 'b', 'c'});
        p1.setCharPrimitive('a');

        final Primitives p2 = new Primitives();
        p2.setPrimitiveCharArray(new char[]{'a', 'b', 'c'});
        p2.setCharPrimitive('c');

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testNotContainsCharArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Character", true, "'c'"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 'c', new Character[]{'d', 'e', 'f'});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveCharArray(new char[]{'d', 'e', 'f'});
        p1.setCharPrimitive('a');

        final Primitives p2 = new Primitives();
        p2.setPrimitiveCharArray(new char[]{'d', 'e', 'f'});
        p2.setCharPrimitive('c');

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testContainsIntArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Integer", false, "10"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 10, new Integer[]{5, 10, 20});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveIntArray(new int[]{5, 10, 20});
        p1.setIntPrimitive(5);

        final Primitives p2 = new Primitives();
        p2.setPrimitiveIntArray(new int[]{5, 10, 20});
        p2.setIntPrimitive(10);

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testNotContainsIntArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Integer", true, "10"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 10, new Integer[]{40, 50, 60});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveIntArray(new int[]{40, 50, 60});
        p1.setIntPrimitive(5);

        final Primitives p2 = new Primitives();
        p2.setPrimitiveIntArray(new int[]{40, 50, 60});
        p2.setIntPrimitive(10);

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testContainsLongArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Long", false, "10"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 10L, new Long[]{5L, 10L, 20L});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveLongArray(new long[]{5, 10, 20});
        p1.setLongPrimitive(5);

        final Primitives p2 = new Primitives();
        p2.setLongPrimitive(10);
        p2.setPrimitiveLongArray(new long[]{5, 10, 20});

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testNotContainsLongArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Long", true, "10"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 10L, new Long[]{40L, 50L, 60L});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveLongArray(new long[]{40, 50, 60});
        p1.setLongPrimitive(5);

        final Primitives p2 = new Primitives();
        p2.setLongPrimitive(10);
        p2.setPrimitiveLongArray(new long[]{40, 50, 60});

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testContainsFloatArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Float", false, "10"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 10.0f, new Float[]{5f, 10f, 20f});

        final Primitives p1 = new Primitives();
        p1.setFloatPrimitive(5);
        p1.setPrimitiveFloatArray(new float[]{5, 10, 20});

        final Primitives p2 = new Primitives();
        p2.setFloatPrimitive(10);
        p2.setPrimitiveFloatArray(new float[]{5, 10, 20});

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testNotContainsFloatArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Float", true, "10"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 10.0f, new Float[]{40f, 50f, 60f});

        final Primitives p1 = new Primitives();
        p1.setFloatPrimitive(5);
        p1.setPrimitiveFloatArray(new float[]{40, 50, 60});

        final Primitives p2 = new Primitives();
        p2.setFloatPrimitive(10);
        p2.setPrimitiveFloatArray(new float[]{40, 50, 60});

        testArrayContains(kieSession, p1, p2, list);
    }
       
    
    @Test
    public void testContainsDoubleArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Double", false, "10"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 10.0d, new Double[]{5d, 10d, 20d});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveDoubleArray(new double[]{5, 10, 20});
        p1.setDoublePrimitive(5);

        final Primitives p2 = new Primitives();
        p2.setDoublePrimitive(10);
        p2.setPrimitiveDoubleArray(new double[]{5, 10, 20});

        testArrayContains(kieSession, p1, p2, list);
    }
    
    @Test
    public void testNotContainsDoubleArray() throws Exception {
        final KieSession kieSession = createKieSessionFromDrl(getDrl("Double", true, "10"));
        final List list = new ArrayList();
        addGlobalsToSession(kieSession, list, 10.0d, new Double[]{40d, 50d, 60d});

        final Primitives p1 = new Primitives();
        p1.setPrimitiveDoubleArray(new double[]{40, 50, 60});
        p1.setDoublePrimitive(5);

        final Primitives p2 = new Primitives();
        p2.setDoublePrimitive(10);
        p2.setPrimitiveDoubleArray(new double[]{40, 50, 60});

        testArrayContains(kieSession, p1, p2, list);
    }

    private String getDrl(final String type, final boolean testNot, final String primitiveTestValue) {
        final String primitiveName = getPrimitiveAttributeName(type);
        final String primitiveArrayName = getPrimitiveArrayName(type);
        final String notString = testNot ? " not " : "";

        String str = "";
        str += "package org.drools.compiler;\n";

        str += "global java.util.List list;\n";
        str += "global " + type + " nGlobal;\n";
        str += "global Object arrayGlobal;\n";

        str += "rule \"contains in array\"\n";
        str += "     salience 10\n";
        str += "     when\n";
        str += "         Primitives( $d : " + primitiveName + " == " + primitiveTestValue + ", $array1 :  " + primitiveArrayName + " ) \n";
        str += "         Primitives( " + primitiveName + " != " + primitiveTestValue + ", $array2 : " + primitiveArrayName + notString + " contains nGlobal,  " + primitiveArrayName + notString + " contains $d, \n";
        str += "                     " + primitiveName + notString + " memberOf $array2, " + primitiveName + notString + " memberOf $array1, " + primitiveName + notString + " memberOf arrayGlobal )\n";
        str += "     then\n";
        str += "        list.add( \"ok1\" );\n";
        str += "end\n";
        return str;
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

    private KieSession createKieSessionFromDrl(final String drl) throws IOException, ClassNotFoundException {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(drl));
        return createKnowledgeSession(kbase);
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
        assertEquals(1, resultsList.size());

        kieSession.delete(p1h);
        kieSession.insert(p1);
        kieSession.fireAllRules();
        assertEquals(2, resultsList.size());
    }

    @Test
    public void testPrimitiveArray() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_primitiveArray.drl"));
        KieSession session = createKnowledgeSession(kbase);

        List result = new ArrayList();
        session.setGlobal("result", result);

        final Primitives p1 = new Primitives();
        p1.setPrimitiveIntArray(new int[]{1, 2, 3});
        p1.setArrayAttribute(new String[]{"a", "b"});

        session.insert(p1);

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        result = (List) session.getGlobal("result");

        session.fireAllRules();
        assertEquals(3, result.size());
        assertEquals(3, ((Integer) result.get(0)).intValue());
        assertEquals(2, ((Integer) result.get(1)).intValue());
        assertEquals(3, ((Integer) result.get(2)).intValue());
    }

    @Test
    public void testArrayUsage() {
        final String str = "import org.drools.compiler.TestParam;\n" +
                "\n" +
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

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final java.util.List list = new java.util.ArrayList();
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

        assertEquals(4, list.size());
        assertTrue(list.contains("x1_0"));
        assertTrue(list.contains("x1_1"));
        assertTrue(list.contains("x2_0"));
        assertTrue(list.contains("x2_1"));

        ksession.dispose();
    }
}