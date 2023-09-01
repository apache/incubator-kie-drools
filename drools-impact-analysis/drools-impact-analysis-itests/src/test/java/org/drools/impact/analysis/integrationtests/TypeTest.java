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
package org.drools.impact.analysis.integrationtests;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.util.StringUtils;
import org.drools.impact.analysis.graph.Graph;
import org.drools.impact.analysis.graph.ModelToGraphConverter;
import org.drools.impact.analysis.graph.ReactivityType;
import org.drools.impact.analysis.integrationtests.domain.PropHolder;
import org.drools.impact.analysis.model.AnalysisModel;
import org.drools.impact.analysis.parser.ModelBuilder;
import org.junit.Test;

public class TypeTest extends AbstractGraphTest {

    @Test
    public void testBooleanPrimitive() {
        testLiteralsEqualityMvel("booleanPrimitive", "true", "false");
        testLiteralsEqualityJava("booleanPrimitive", "true", "false");
    }

    @Test
    public void testCharPrimitive() {
        // Not testing char with mvel because mvel treats a single quoted value as a String
        // See: http://mvel.documentnode.com/#string-literals
        testLiteralsEqualityJava("charPrimitive", "'a'", "'b'");
    }

    @Test
    public void testBytePrimitive() {
        testLiteralsEqualityMvel("bytePrimitive", "(byte)0", "(byte)1");
        testLiteralsEqualityJava("bytePrimitive", "(byte)0", "(byte)1");
    }

    @Test
    public void testShortPrimitive() {
        testLiteralsEqualityMvel("shortPrimitive", "(short)0", "(short)1");
        testLiteralsEqualityJava("shortPrimitive", "(short)0", "(short)1");
    }

    @Test
    public void testIntPrimitive() {
        testLiteralsEqualityMvel("intPrimitive", "0", "1");
        testLiteralsEqualityJava("intPrimitive", "0", "1");
    }

    @Test
    public void testLongPrimitive() {
        testLiteralsEqualityMvel("longPrimitive", "0l", "1l");
        testLiteralsEqualityJava("longPrimitive", "0l", "1l");
    }

    @Test
    public void testFloatPrimitive() {
        // Not testing with equality because of precision
        testLiteralsInequalityMvel("floatPrimitive", "0.1f", "0.2f", "0.15f");
        testLiteralsInequalityJava("floatPrimitive", "0.1f", "0.2f", "0.15f");
    }

    @Test
    public void testDoublePrimitive() {
        // Not testing with equality because of precision
        testLiteralsInequalityMvel("doublePrimitive", "0.1d", "0.2d", "0.15d");
        testLiteralsInequalityJava("doublePrimitive", "0.1d", "0.2d", "0.15d");
    }

    @Test
    public void testBooleanWrapper() {
        testLiteralsEqualityMvel("booleanWrapper", "true", "false");
        testLiteralsEqualityJava("booleanWrapper", "true", "false");
    }

    @Test
    public void testCharWrapper() {
        // Not testing char with mvel because mvel treats a single quoted value as a String
        // See: http://mvel.documentnode.com/#string-literals
        testLiteralsEqualityJava("charWrapper", "'a'", "'b'");
    }

    @Test
    public void testByteWrapper() {
        testLiteralsEqualityMvel("byteWrapper", "(byte)0", "(byte)1");
        testLiteralsEqualityJava("byteWrapper", "(byte)0", "(byte)1");
    }

    @Test
    public void testShortWrapper() {
        testLiteralsEqualityMvel("shortWrapper", "(short)0", "(short)1");
        testLiteralsEqualityJava("shortWrapper", "(short)0", "(short)1");
    }

    @Test
    public void testIntWrapper() {
        testLiteralsEqualityMvel("intWrapper", "0", "1");
        testLiteralsEqualityJava("intWrapper", "0", "1");
    }

    @Test
    public void testLongWrapper() {
        testLiteralsEqualityMvel("longWrapper", "0l", "1l");
        testLiteralsEqualityJava("longWrapper", "0l", "1l");
    }

    @Test
    public void testFloatWrapper() {
        // Not testing with equality because of precision
        testLiteralsInequalityMvel("floatWrapper", "0.1f", "0.2f", "0.15f");
        testLiteralsInequalityJava("floatWrapper", "0.1f", "0.2f", "0.15f");
    }

    @Test
    public void testDoubleWrapper() {
        // Not testing with equality because of precision
        testLiteralsInequalityMvel("doubleWrapper", "0.1d", "0.2d", "0.15d");
        testLiteralsInequalityJava("doubleWrapper", "0.1d", "0.2d", "0.15d");
    }

    @Test
    public void testString() {
        testLiteralsEqualityMvel("stringAttribute", "\"ABC\"", "\"XYZ\"");
        testLiteralsEqualityJava("stringAttribute", "\"ABC\"", "\"XYZ\"");
    }

    @Test
    public void testObjectString() {
        testLiteralsEqualityMvel("object", "\"ABC\"", "\"XYZ\"");
        testLiteralsEqualityJava("object", "\"ABC\"", "\"XYZ\"");
    }

    @Test
    public void testBigDecimal() {
        testLiteralsEqualityMvel("bigDecimal", "10.1B", "10.2B");
        testLiteralsEqualityJava("bigDecimal", "new BigDecimal(\"10.1\")", "new BigDecimal(\"10.2\")");
    }

    @Test
    public void testBigInteger() {
        testLiteralsEqualityMvel("bigInteger", "0I", "1I");
        testLiteralsEqualityJava("bigInteger", "new BigInteger(\"0\")", "new BigInteger(\"1\")");
    }

    private void testLiteralsEqualityMvel(String propName, String value1, String value2) {
        testLiteralsEquality(propName, value1, value2, "mvel");
    }

    private void testLiteralsEqualityJava(String propName, String value1, String value2) {
        testLiteralsEquality(propName, value1, value2, "java");
    }

    private void testLiteralsEquality(String propName, String value1, String value2, String dialect) {
        String setter = "set" + StringUtils.ucFirst(propName);
        String str = "package mypkg;\n" +
                     "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                     "import " + BigInteger.class.getCanonicalName() + ";\n" +
                     "import " + PropHolder.class.getCanonicalName() + ";\n" +
                     "dialect \"" + dialect + "\"\n" +
                     "rule R1 when\n" +
                     "  $p : PropHolder(id == 0)\n" +
                     "then\n" +
                     "  modify ($p) {" + setter + "(" + value1 + ")};\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : PropHolder(id == 1)\n" +
                     "then\n" +
                     "  modify ($p) {" + setter + "(" + value2 + ")};\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $p : PropHolder(" + propName + " == " + value1 + ")\n" +
                     "then\n" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $p : PropHolder(" + propName + " == " + value2 + ")\n" +
                     "then\n" +
                     "end\n";

        // PropHolder holder = new PropHolder();
        // holder.setId(0);
        // runRule(str, holder);

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R4", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R4", ReactivityType.POSITIVE);
    }

    private void testLiteralsInequalityMvel(String propName, String value1, String value2, String thresholdValue) {
        testLiteralsInequality(propName, value1, value2, thresholdValue, "mvel");
    }

    private void testLiteralsInequalityJava(String propName, String value1, String value2, String thresholdValue) {
        testLiteralsInequality(propName, value1, value2, thresholdValue, "java");
    }

    private void testLiteralsInequality(String propName, String value1, String value2, String thresholdValue, String dialect) {
        String setter = "set" + StringUtils.ucFirst(propName);
        String str = "package mypkg;\n" +
                     "import " + PropHolder.class.getCanonicalName() + ";\n" +
                     "dialect \"" + dialect + "\"\n" +
                     "rule R1 when\n" +
                     "  $p : PropHolder(id == 0)\n" +
                     "then\n" +
                     "  modify ($p) {" + setter + "(" + value1 + ")};\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : PropHolder(id == 1)\n" +
                     "then\n" +
                     "  modify ($p) {" + setter + "(" + value2 + ")};\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $p : PropHolder(" + propName + " < " + thresholdValue + ")\n" +
                     "then\n" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $p : PropHolder(" + propName + " > " + thresholdValue + ")\n" +
                     "then\n" +
                     "end\n";

        // PropHolder holder = new PropHolder();
        // holder.setId(0);
        // runRule(str, holder);

        AnalysisModel analysisModel = new ModelBuilder().build(str);

        ModelToGraphConverter converter = new ModelToGraphConverter();
        Graph graph = converter.toGraph(analysisModel);

        assertLink(graph, "mypkg.R1", "mypkg.R3", ReactivityType.POSITIVE);
        assertLink(graph, "mypkg.R1", "mypkg.R4", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R3", ReactivityType.NEGATIVE);
        assertLink(graph, "mypkg.R2", "mypkg.R4", ReactivityType.POSITIVE);
    }
}
