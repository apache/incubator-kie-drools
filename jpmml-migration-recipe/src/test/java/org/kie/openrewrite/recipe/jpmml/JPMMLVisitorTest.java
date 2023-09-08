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
package org.kie.openrewrite.recipe.jpmml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openrewrite.Cursor;
import org.openrewrite.ExecutionContext;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.JavaType;
import org.openrewrite.java.tree.TypeTree;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.openrewrite.recipe.jpmml.CommonTestingUtilities.*;

class JPMMLVisitorTest {

    private JPMMLVisitor jpmmlVisitor;

    @BeforeEach
    public void init() {
        jpmmlVisitor = new JPMMLVisitor("org.dmg.pmml.ScoreDistribution", "org.dmg.pmml.ComplexScoreDistribution");
    }

    @Test
    public void visitBinary_StringFieldNameGetValue() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello(DataField dataField) {\n" +
                "        FieldName.create(\"DER_\" + dataField.getName().getValue());\n" +
                "    }\n" +
                "}";
        String binary = "\"DER_\" + dataField.getName().getValue()";
        J.Binary toTest = getBinaryFromClassSource(classTested, binary)
                .orElseThrow(() -> new RuntimeException("Failed to find J.Binary " + binary));
        J retrieved = jpmmlVisitor.visitBinary(toTest, getExecutionContext(null));
        String expected = "\"DER_\" +dataField.getName()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.Binary.class)
                .hasToString(expected);
    }

    @Test
    public void visitBinary_FieldNameGetValueString() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello(DataField dataField) {\n" +
                "        FieldName.create(dataField.getName().getValue() + \"DER_\");\n" +
                "    }\n" +
                "}";
        String binary = "dataField.getName().getValue() + \"DER_\"";
        J.Binary toTest = getBinaryFromClassSource(classTested, binary)
                .orElseThrow(() -> new RuntimeException("Failed to find J.Binary " + binary));
        J retrieved = jpmmlVisitor.visitBinary(toTest, getExecutionContext(null));
        String expected = "dataField.getName() + \"DER_\"";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.Binary.class)
                .hasToString(expected);
    }

    @Test
    public void visitBinary_FieldNameGetValueFieldNameGetValue() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello(DataField dataField) {\n" +
                "        FieldName.create(dataField.getName().getValue() + dataField.getName().getValue());\n" +
                "    }\n" +
                "}";
        String binary = "dataField.getName().getValue() + dataField.getName().getValue()";
        J.Binary toTest = getBinaryFromClassSource(classTested, binary)
                .orElseThrow(() -> new RuntimeException("Failed to find J.Binary " + binary));
        J retrieved = jpmmlVisitor.visitBinary(toTest, getExecutionContext(null));
        String expected = "dataField.getName() +dataField.getName()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.Binary.class)
                .hasToString(expected);
    }

    @Test
    public void visitMethodInvocation_NumericPredictorGetName() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.regression.NumericPredictor;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String bye(NumericPredictor numericPredictor) {\n" +
                "        FieldName fieldName = numericPredictor.getName();\n" +
                "        return fieldName.getValue();\n" +
                "    }" +
                "}";
        String methodTested = "numericPredictor.getName";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodTested)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation numericPredictor.getName()"));
        assertThat(toTest).isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        String expected = "numericPredictor.getField()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class)
                .hasToString(expected);
    }

    @Test
    public void visitMethodInvocation_CategoricalPredictorGetName() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.regression.CategoricalPredictor;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String bye(CategoricalPredictor categoricalPredictor) {\n" +
                "        FieldName fieldName = categoricalPredictor.getName();\n" +
                "        return fieldName.getValue();\n" +
                "    }" +
                "}";
        String methodTested = "categoricalPredictor.getName";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodTested)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation categoricalPredictor.getName()"));
        assertThat(toTest).isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        String expected = "categoricalPredictor.getField()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class)
                .hasToString(expected);
    }

    @Test
    public void visitMethodInvocation_FieldNameCreate() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello() {\n" +
                "        System.out.println(FieldName.create(\"OUTPUT_\"));\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String expressionTested = "FieldName.create";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, expressionTested)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation FieldName.create(\"OUTPUT_\")"));
        assertThat(toTest)
                .isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        String expected = "OUTPUT_";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.Literal.class);
        assertThat(((J.Literal) retrieved).getValue()).isEqualTo(expected);
    }

    @Test
    public void visitMethodInvocation_FieldNameCreateWithBinary() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.DataField;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(DataField dataField) {\n" +
                "        System.out.println(FieldName.create(\"OUTPUT_\" + dataField.getName().getValue()));\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String expressionTested = "System.out.println";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, expressionTested)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation System.out.println(FieldName.create(\"OUTPUT_\" + dataField.getName().getValue()))"));
        assertThat(toTest)
                .isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        String expected = "System.out.println(\"OUTPUT_\" +dataField.getName())";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class);
        assertThat(((J.MethodInvocation) retrieved)).hasToString(expected);
    }

    @Test
    public void visitMethodInvocation_AccessFieldName() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.DataType;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.MiningField;\n" +
                "import org.dmg.pmml.mining.MiningModel;\n" +
                "import org.dmg.pmml.OpType;\n" +
                "import org.dmg.pmml.OutputField;\n" +
                "import org.dmg.pmml.Target;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String bye() {\n" +
                "         MiningField toReturn = new MiningField(FieldName.create(new String(\"TestingFIeld\")));\n" +
                "        OutputField toConvert = new OutputField(FieldName.create(\"FIELDNAME\"), OpType.CATEGORICAL," +
                " DataType.BOOLEAN);\n" +
                "        final String name = toConvert.getName() != null ? toConvert.getName().getValue() : null;\n" +
                "        Target target = new Target();\n" +
                "        String field = target.getField().getValue();\n" +
                "        String key = target.getKey().getValue();\n" +
                "        return name;\n" +
                "    }" +
                "}";
        String methodTested = "toConvert.getName().getValue";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodTested)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation toConvert.getName().getValue"));
        assertThat(toTest).isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        String expected = "toConvert.getName()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class)
                .hasToString(expected);

        methodTested = "target.getField().getValue";
        toTest = getMethodInvocationFromClassSource(classTested, methodTested)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation target.getField().getValue"));
        assertThat(toTest).isNotNull();
        retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        expected = "target.getField()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class)
                .hasToString(expected);

        methodTested = "target.getKey().getValue";
        toTest = getMethodInvocationFromClassSource(classTested, methodTested)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation target.getKey().getValue"));
        assertThat(toTest).isNotNull();
        retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        expected = "target.getKey()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class)
                .hasToString(expected);
    }

    @Test
    public void visitMethodInvocation_FieldNameGetValue() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import java.util.Objects;\n" +
                "\n" +
                "import org.dmg.pmml.DataField;\n" +
                "import org.dmg.pmml.Field;\n" +
                "\n" +
                "public class Stub {\n" +
                "\n" +
                "    private List<Field<?>> fields;\n" +
                "\n" +
                "    public void bye() {\n" +
                "        DataField targetDataField = this.fields.stream()\n" +
                "                .filter(DataField.class::isInstance)\n" +
                "                .map(DataField.class::cast)\n" +
                "                .filter(field -> Objects.equals(getTargetFieldName(), field.getName().getValue()))\n" +
                "                .findFirst().orElse(null);\n" +
                "    }\n" +
                "    public String getTargetFieldName() {\n" +
                "        return \"targetDataFieldName\";\n" +
                "    }\n" +
                "}";
        String expressionTested = "field.getName().getValue";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, expressionTested)
                .orElseThrow(() -> new RuntimeException("Failed to find Expression FieldName.create(\"OUTPUT_\")"));
        assertThat(toTest)
                .isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        String expected = "field.getName()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class)
                .hasToString(expected);
    }

    @Test
    public void visitMethodInvocation_FieldNameGetNameToGetFieldMapped() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.regression.CategoricalPredictor;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(CategoricalPredictor categoricalPredictor) {\n" +
                "        FieldName fieldName = categoricalPredictor.getName();\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String methodInvocation = "categoricalPredictor.getName";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation categoricalPredictor.getName()"));
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        String expected = "categoricalPredictor.getField()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class)
                .hasToString(expected);
    }

    @Test
    public void visitMethodInvocation_HasFieldNameParameter() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "import org.dmg.pmml.DerivedField;\n" +
                "import java.util.Objects;\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello(DataField dataField) {\n" +
                "        DerivedField toReturn = new DerivedField();\n" +
                "        toReturn.setName(FieldName.create(\"DER_\" + dataField.getName().getValue()));\n" +
                "    }\n" +
                "}";
        String methodInvocation = "toReturn.setName";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to toReturn.setName"));
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitMethodInvocation(toTest, executionContext);
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class);
        String expected = "String";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class);
        assertThat(((J.MethodInvocation)retrieved).getMethodType().getParameterTypes().get(0))
                .hasToString(expected);
    }

    @Test
    public void visitNewClass_FieldNameCreate() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.MiningField;\n" +
                "\n" +
                "public class Stub {\n" +
                "\n" +
                "    public String hello() {\n" +
                "        MiningField toReturn = new MiningField(FieldName.create(new String(\"TestingField\")));\n" +
                "        return \"Hello from com.yourorg.FooLol!\";\n" +
                "    }\n" +
                "\n" +
                "}";
        String classInstantiated = "org.dmg.pmml.MiningField";
        J.NewClass toTest = getNewClassFromClassSource(classTested, classInstantiated)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.MiningField"));
        assertThat(toTest)
                .isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitNewClass(toTest, executionContext);
        String expected = "new MiningField(new String(\"TestingField\"))";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.NewClass.class)
                .hasToString(expected);
    }

    @Test
    public void visitNewClass_AccessFieldNameInsideConstructor() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.Target;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String bye() {\n" +
                "        Target target = new Target();\n" +
                "        String name = new String(target.getKey().getValue());\n" +
                "        return name;\n" +
                "    }" +
                "}";
        String classInstantiated = "java.lang.String";
        J.NewClass toTest = getNewClassFromClassSource(classTested, classInstantiated)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass java.lang.String"));
        assertThat(toTest).isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitNewClass(toTest, executionContext);
        String expected = "new String(target.getKey())";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.NewClass.class)
                .hasToString(expected);
    }

    @Test
    public void visitNewClass_ScoreDistribution() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.ScoreDistribution;\n" +
                "\n" +
                "public class Stub {\n" +
                "\n" +
                "    public String hello() {\n" +
                "        ScoreDistribution scoreDistribution = new ScoreDistribution();\n" +
                "        return \"Hello from com.yourorg.FooLol!\";\n" +
                "    }\n" +
                "\n" +
                "}";
        String classInstantiated = "org.dmg.pmml.ScoreDistribution";
        J.NewClass toTest = getNewClassFromClassSource(classTested, classInstantiated)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.ScoreDistribution"));
        assertThat(toTest)
                .isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitNewClass(toTest, executionContext);
        String expected = "new ComplexScoreDistribution()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.NewClass.class)
                .hasToString(expected);
    }

    @Test
    public void visitNewClass_DataDictionary() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.DataDictionary;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "public class Stub {\n" +
                "\n" +
                "    public String hello(List<DataField> dataFields) {\n" +
                "        DataDictionary dataDictionary = new DataDictionary(dataFields);\n" +
                "        return \"Hello from com.yourorg.FooLol!\";\n" +
                "    }\n" +
                "\n" +
                "}";
        String classInstantiated = "org.dmg.pmml.DataDictionary";
        J.NewClass toTest = getNewClassFromClassSource(classTested, classInstantiated)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.DataDictionary"));
        assertThat(toTest)
                .isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J retrieved = jpmmlVisitor.visitNewClass(toTest, executionContext);
        String expected = "new DataDictionary().addDataFields(dataFields.toArray(new org.dmg.pmml.DataField[0]))";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class)
                .hasToString(expected);
    }

    @Test
    public void visitVariableDeclarations_AccessFieldNameAsSecondParameter() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import java.util.Objects;\n" +
                "\n" +
                "import org.dmg.pmml.DataField;\n" +
                "import org.dmg.pmml.Field;\n" +
                "\n" +
                "public class Stub {\n" +
                "\n" +
                "    private List<Field<?>> fields;\n" +
                "\n" +
                "    public void bye() {\n" +
                "        DataField targetDataField = this.fields.stream()\n" +
                "                .filter(DataField.class::isInstance)\n" +
                "                .map(DataField.class::cast)\n" +
                "                .filter(field -> Objects.equals(getTargetFieldName(), field.getName().getValue()))\n" +
                "                .findFirst().orElse(null);\n" +
                "    }\n" +
                "    public String getTargetFieldName() {\n" +
                "        return \"targetDataFieldName\";\n" +
                "    }\n" +
                "}";
        String variableDeclaration = "DataField targetDataField = ";
        J.VariableDeclarations toTest = getVariableDeclarationsFromClassSource(classTested, variableDeclaration)
                .orElseThrow(() -> new RuntimeException("Failed to find J.VariableDeclarations DataField targetDataField = "));
        assertThat(toTest).isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J.VariableDeclarations retrieved = jpmmlVisitor.visitVariableDeclarations(toTest, executionContext);
        String expected = "DataField targetDataField = this.fields.stream()\n" +
                "                .filter(DataField.class::isInstance)\n" +
                "                .map(DataField.class::cast)\n" +
                "                .filter(field -> Objects.equals(getTargetFieldName(),field.getName()))\n" +
                "                .findFirst().orElse(null)";
        assertThat(retrieved).isNotNull()
                .hasToString(expected);
    }

    @Test
    public void visitVariableDeclarations_FieldName() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello() {\n" +
                "        FieldName fieldName = FieldName.create(\"OUTPUT_\");\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String variableDeclaration = "FieldName fieldName = FieldName.create(\"OUTPUT_\")";
        J.VariableDeclarations toTest = getVariableDeclarationsFromClassSource(classTested, variableDeclaration)
                .orElseThrow(() -> new RuntimeException("Failed to find J.VariableDeclarations FieldName fieldName = FieldName.create(\"OUTPUT_\")"));
        assertThat(toTest)
                .isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J.VariableDeclarations retrieved = jpmmlVisitor.visitVariableDeclarations(toTest, executionContext);
        String expected = "String fieldName =\"OUTPUT_\"";
        assertThat(retrieved)
                .isNotNull()
                .hasToString(expected);
    }

    @Test
    public void visitVariableDeclarations_CategoricalPredictorGetName() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.regression.CategoricalPredictor;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(CategoricalPredictor categoricalPredictor) {\n" +
                "        FieldName fieldName = categoricalPredictor.getName();\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String variableDeclaration = "FieldName fieldName = categoricalPredictor.getName()";
        J.VariableDeclarations toTest = getVariableDeclarationsFromClassSource(classTested, variableDeclaration)
                .orElseThrow(() -> new RuntimeException("Failed to find J.VariableDeclarations FieldName fieldName = categoricalPredictor.getName()"));
        assertThat(toTest)
                .isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J.VariableDeclarations retrieved = jpmmlVisitor.visitVariableDeclarations(toTest, executionContext);
        String expected = "String fieldName = categoricalPredictor.getField()";
        assertThat(retrieved)
                .isNotNull()
                .hasToString(expected);
    }

    @Test
    public void visitVariableDeclarations_NumericPredictorGetName() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.regression.NumericPredictor;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(NumericPredictor numericPredictor) {\n" +
                "        FieldName fieldName = numericPredictor.getName();\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String variableDeclaration = "FieldName fieldName = numericPredictor.getName()";
        J.VariableDeclarations toTest = getVariableDeclarationsFromClassSource(classTested, variableDeclaration)
                .orElseThrow(() -> new RuntimeException("Failed to find J.VariableDeclarations FieldName fieldName = FieldName.create(\"OUTPUT_\")"));
        assertThat(toTest)
                .isNotNull();
        ExecutionContext executionContext = getExecutionContext(null);
        J.VariableDeclarations retrieved = jpmmlVisitor.visitVariableDeclarations(toTest, executionContext);
        String expected = "String fieldName = numericPredictor.getField()";
        assertThat(retrieved)
                .isNotNull()
                .hasToString(expected);
    }

    @Test
    public void hasFieldNameImport_true() {
        String classTested = "package com.yourorg;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import java.util.List;\n" +
                "class FooBar {\n" +
                "};";
        J.CompilationUnit toTest = getCompilationUnitFromClassSource(classTested);
        assertThat(jpmmlVisitor.hasFieldNameImport(toTest))
                .isTrue();
    }

    @Test
    public void hasFieldNameImport_false() {
        String classTested = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "class FooBar {\n" +
                "};";
        J.CompilationUnit toTest = getCompilationUnitFromClassSource(classTested);
        assertThat(jpmmlVisitor.hasFieldNameImport(toTest))
                .isFalse();
    }

    @Test
    public void isFieldNameImport_true() {
        String classTested = "package com.yourorg;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "class FooBar {\n" +
                "};";
        J.Import toTest = getImportsFromClassSource(classTested).get(0);
        assertThat(jpmmlVisitor.isFieldNameImport(toTest))
                .isTrue();
    }

    @Test
    public void isFieldNameImport_false() {
        String classTested = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "class FooBar {\n" +
                "};";
        J.Import toTest = getImportsFromClassSource(classTested).get(0);
        assertThat(jpmmlVisitor.isFieldNameImport(toTest))
                .isFalse();
    }

    @Test
    public void addMissingMethod_Add() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello() {\n" +
                "        System.out.println(\"Hello\");\n" +
                "    }\n" +
                "}";
        String className = "Stub";
        J.CompilationUnit cu = getCompilationUnitFromClassSource(classTested);
        J.ClassDeclaration toTest = getClassDeclarationFromCompilationUnit(cu, className)
                .orElseThrow(() -> new RuntimeException("Failed to find J.ClassDeclaration Stub"));
        Cursor cursor = new Cursor(jpmmlVisitor.getCursor(), cu);
        JavaTemplate requireMiningSchemaTemplate = JavaTemplate.builder(() -> cursor,
                        "    public boolean requireMiningSchema() {\n" +
                                "        return null;\n" +
                                "    }\n")
                .build();
        J.ClassDeclaration retrieved = jpmmlVisitor.addMissingMethod(toTest, "requireMiningSchema", requireMiningSchemaTemplate);
        assertThat(retrieved)
                .isEqualTo(toTest);
        assertThat(jpmmlVisitor.methodExists(retrieved, "requireMiningSchema"))
                .isTrue();
    }

    @Test
    public void addMissingMethod_NotAdd() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello() {\n" +
                "        System.out.println(\"Hello\");\n" +
                "    }\n" +
                "}";
        String className = "Stub";
        J.ClassDeclaration toTest = getClassDeclarationFromClassSource(classTested, className)
                .orElseThrow(() -> new RuntimeException("Failed to find J.ClassDeclaration Stub"));
        assertThat(jpmmlVisitor.addMissingMethod(toTest, "hello", null))
                .isEqualTo(toTest);
    }

    @Test
    public void methodExists_true() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello() {\n" +
                "        System.out.println(\"Hello\");\n" +
                "    }\n" +
                "}";
        String className = "Stub";
        J.ClassDeclaration toTest = getClassDeclarationFromClassSource(classTested, className)
                .orElseThrow(() -> new RuntimeException("Failed to find J.ClassDeclaration Stub"));
        assertThat(jpmmlVisitor.methodExists(toTest, "hello"))
                .isTrue();
    }

    @Test
    public void methodExists_false() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello() {\n" +
                "        System.out.println(\"Hello\");\n" +
                "    }\n" +
                "}";
        String className = "Stub";
        J.ClassDeclaration toTest = getClassDeclarationFromClassSource(classTested, className)
                .orElseThrow(() -> new RuntimeException("Failed to find J.ClassDeclaration Stub"));
        assertThat(jpmmlVisitor.methodExists(toTest, "notHello"))
                .isFalse();
    }

    @Test
    public void replaceOriginalToTargetInstantiation_replaced() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.ScoreDistribution;\n" +
                "\n" +
                "public class Stub {\n" +
                "\n" +
                "    public String hello() {\n" +
                "        ScoreDistribution scoreDistribution = new ScoreDistribution();\n" +
                "        return \"Hello from com.yourorg.FooLol!\";\n" +
                "    }\n" +
                "\n" +
                "}";
        String classInstantiated = "org.dmg.pmml.ScoreDistribution";
        J.NewClass toTest = getNewClassFromClassSource(classTested, classInstantiated)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.ScoreDistribution"));
        assertThat(toTest)
                .isNotNull();
        J.NewClass retrieved = jpmmlVisitor.replaceOriginalToTargetInstantiation(toTest);
        String expected = "new ComplexScoreDistribution()";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.NewClass.class)
                .hasToString(expected);
    }

    @Test
    public void replaceOriginalToTargetInstantiation_notReplaced() {
        String classTested = "package com.yourorg;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "class FooBar {\n" +
                "static void method() {\n" +
                "        DataField dataField = new DataField();\n" +
                "}\n" +
                "}";
        String instantiatedClass = "org.dmg.pmml.DataField";
        J.NewClass toTest = getNewClassFromClassSource(classTested, instantiatedClass)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.DataField"));
        assertThat(toTest)
                .isNotNull();
        Expression retrieved =  jpmmlVisitor.replaceOriginalToTargetInstantiation(toTest);
        assertThat(retrieved)
                .isNotNull()
                .isEqualTo(toTest);
    }

    @Test
    public void replaceInstantiationListRemoved_replaced() {
        String classTested = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.DataDictionary;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "class FooBar {\n" +
                "static void method(List<DataField> dataFields) {\n" +
                "DataDictionary dataDictionary = new DataDictionary(dataFields);\n" +
                "}\n" +
                "}";
        String instantiatedClass = "org.dmg.pmml.DataDictionary";
        J.NewClass toTest = getNewClassFromClassSource(classTested, instantiatedClass)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.DataDictionary"));
        assertThat(toTest)
                .isNotNull();
        Expression retrieved =  jpmmlVisitor.replaceInstantiationListRemoved(toTest);
        String expected = "new DataDictionary().addDataFields(dataFields.toArray(new org.dmg.pmml.DataField[0]))";
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.MethodInvocation.class)
                .hasToString(expected);
    }

    @Test
    public void replaceInstantiationListRemoved_notReplaced() {
        String classTested = "package com.yourorg;\n" +
                "import org.dmg.pmml.ScoreDistribution;\n" +
                "class FooBar {\n" +
                "static void method() {\n" +
                "        ScoreDistribution scoreDistribution = new ScoreDistribution();\n" +
                "}\n" +
                "}";
        String instantiatedClass = "org.dmg.pmml.ScoreDistribution";
        J.NewClass toTest = getNewClassFromClassSource(classTested, instantiatedClass)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.DataDictionary"));
        assertThat(toTest)
                .isNotNull();
        Expression retrieved =  jpmmlVisitor.replaceInstantiationListRemoved(toTest);
        assertThat(retrieved)
                .isNotNull()
                .isEqualTo(toTest);
    }

    @Test
    public void getRemovedListTupla_present() {
        String classTested = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.DataDictionary;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "class FooBar {\n" +
                "static void method(List<DataField> dataFields) {\n" +
                "DataDictionary dataDictionary = new DataDictionary(dataFields);\n" +
                "}\n" +
                "}";
        String instantiatedClass = "org.dmg.pmml.DataDictionary";
        J.NewClass toTest = getNewClassFromClassSource(classTested, instantiatedClass)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.DataDictionary"));
        assertThat(toTest)
                .isNotNull();
        assertThat(jpmmlVisitor.getRemovedListTupla(toTest))
                .isPresent();
    }

    @Test
    public void getRemovedListTupla_notPresent() {
        String classTested = "package com.yourorg;\n" +
                "import org.dmg.pmml.ScoreDistribution;\n" +
                "class FooBar {\n" +
                "static void method() {\n" +
                "        ScoreDistribution scoreDistribution = new ScoreDistribution();\n" +
                "}\n" +
                "}";
        String instantiatedClass = "org.dmg.pmml.ScoreDistribution";
        J.NewClass toTest = getNewClassFromClassSource(classTested, instantiatedClass)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.ScoreDistribution"));
        assertThat(toTest)
                .isNotNull();
        assertThat(jpmmlVisitor.getRemovedListTupla(toTest))
                .isNotPresent();
    }

    @Test
    public void isFieldNameCreate_true() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello() {\n" +
                "        System.out.println(FieldName.create(\"OUTPUT_\"));\n" +
                "    }\n" +
                "}";
        String expressionTested = "FieldName.create";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, expressionTested)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation FieldName.create(\"OUTPUT_\")"));
        assertThat(toTest)
                .isNotNull();
        assertThat(jpmmlVisitor.isFieldNameCreate(toTest))
                .isTrue();
    }

    @Test
    public void isFieldNameCreate_false() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello() {\n" +
                "        System.out.println(FieldName.create(\"OUTPUT_\"));\n" +
                "    }\n" +
                "}";
        String expressionTested = "System.out.println";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, expressionTested)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation System.out.println(FieldName.create(\"OUTPUT_\"))"));
        assertThat(toTest)
                .isNotNull();
        assertThat(jpmmlVisitor.isFieldNameCreate(toTest))
                .isFalse();
    }

    @Test
    public void hasFieldNameParameter_true() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "import org.dmg.pmml.DerivedField;\n" +
                "import java.util.Objects;\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello(DataField dataField) {\n" +
                "        DerivedField toReturn = new DerivedField();\n" +
                "        toReturn.setName(FieldName.create(\"DER_\" + dataField.getName().getValue()));\n" +
                "    }\n" +
                "}";
        String methodInvocation = "toReturn.setName";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to toReturn.setName"));
        assertThat(jpmmlVisitor.hasFieldNameParameter(toTest))
                .isTrue();
    }

    @Test
    public void hasFieldNameParameter_false() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "import java.util.Objects;\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello(DataField field) {\n" +
                "        Objects.equals(null, field.getName().getValue());\n" +
                "    }\n" +
                "}";
        String methodInvocation = "Objects.equals";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to find Objects.equals"));
        assertThat(jpmmlVisitor.hasFieldNameParameter(toTest))
                .isFalse();
        classTested =  "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import java.util.Objects;\n" +
                "\n" +
                "class Stub {\n" +
                "    public void hello(FieldName fieldName) {\n" +
                "        Objects.equals(null, fieldName.getValue());\n" +
                "    }\n" +
                "}";
        toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation numericPredictor.getName()"));
        assertThat(jpmmlVisitor.hasFieldNameParameter(toTest))
                .isFalse();
    }

    @Test
    public void isFieldNameGetNameToGetFieldMapped_true() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.regression.CategoricalPredictor;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(CategoricalPredictor categoricalPredictor) {\n" +
                "        FieldName fieldName = categoricalPredictor.getName();\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String methodInvocation = "categoricalPredictor.getName";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation categoricalPredictor.getName()"));
        assertThat(jpmmlVisitor.isFieldNameGetNameToGetFieldMapped(toTest)).isTrue();
        classTested ="package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.regression.NumericPredictor;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(NumericPredictor numericPredictor) {\n" +
                "        FieldName fieldName = numericPredictor.getName();\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        methodInvocation = "numericPredictor.getName";
        toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation numericPredictor.getName()"));
        assertThat(jpmmlVisitor.isFieldNameGetNameToGetFieldMapped(toTest)).isTrue();
    }

    @Test
    public void isFieldNameGetNameToGetFieldMapped_false() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(DataField dataField) {\n" +
                "        FieldName fieldName = dataField.getName();\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String methodInvocation = "dataField.getName";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation dataField.getName()"));
        assertThat(jpmmlVisitor.isFieldNameGetNameToGetFieldMapped(toTest)).isFalse();
    }

    @Test
    public void useFieldNameGetValue_true() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(DataField field) {\n" +
                "        System.out.println(field.getName().getValue());\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String methodInvocation = "field.getName().getValue";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation field.getName().getValue()"));
        assertThat(jpmmlVisitor.useFieldNameGetValue(toTest)).isTrue();
        classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(FieldName field) {\n" +
                "        System.out.println(field.getValue());\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        methodInvocation = "field.getValue";
        toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation field.getValue()"));
        assertThat(jpmmlVisitor.useFieldNameGetValue(toTest)).isTrue();
    }

    @Test
    public void useFieldNameGetValue_false() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.DataField;\n" +
                "\n" +
                "class Stub {\n" +
                "    public String hello(DataField field) {\n" +
                "        System.out.println(field.getName().getValue());\n" +
                "        return \"Hello from com.yourorg.FooBar!\";\n" +
                "    }\n" +
                "}";
        String methodInvocation = "System.out.println";
        J.MethodInvocation toTest = getMethodInvocationFromClassSource(classTested, methodInvocation)
                .orElseThrow(() -> new RuntimeException("Failed to find J.MethodInvocation System.out.println()"));
        assertThat(jpmmlVisitor.useFieldNameGetValue(toTest)).isFalse();
    }

    @Test
    public void toMigrate_False() {
        String classTested = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "class FooBar {\n" +
                "};";
        List<J.Import> toTest = getImportsFromClassSource(classTested);
        assertThat(jpmmlVisitor.toMigrate(toTest))
                .isFalse();
        assertThat(toTest).hasSize(2);
    }

    @Test
    public void toMigrate_True() {
        String classTested = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.FieldName;\n" +
                "class FooBar {\n" +
                "};";
        List<J.Import> toTest = getImportsFromClassSource(classTested);
        assertThat(jpmmlVisitor.toMigrate(toTest))
                .isTrue();
        classTested = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "import org.jpmml.model.inlinetable.InputCell;\n" +
                "class FooBar {\n" +
                "};";
        toTest = getImportsFromClassSource(classTested);
        assertThat(jpmmlVisitor.toMigrate(toTest))
                .isTrue();
    }

    @Test
    public void updateMethodToTargetInstantiatedType() {
        JavaType.Method toTest = new JavaType.Method(null, 1025, jpmmlVisitor.originalInstantiatedType, "toArray",
                jpmmlVisitor.originalInstantiatedType,
                Collections.emptyList(),
                Collections.emptyList(), null, null);
        JavaType.Method retrieved = jpmmlVisitor.updateMethodToTargetInstantiatedType(toTest);
        assertThat(retrieved.getDeclaringType()).isEqualTo(jpmmlVisitor.targetInstantiatedType);
        assertThat(retrieved.getReturnType()).isEqualTo(jpmmlVisitor.targetInstantiatedType);
    }

    @Test
    public void updateTypeTreeToTargetInstantiatedType() {
        String classTested = "package com.yourorg;\n" +
                "\n" +
                "import org.dmg.pmml.ScoreDistribution;\n" +
                "\n" +
                "public class Stub {\n" +
                "\n" +
                "    public void hello() {\n" +
                "        ScoreDistribution scoreDistribution = new ScoreDistribution();\n" +
                "    }\n" +
                "\n" +
                "}";
        String classInstantiated = "org.dmg.pmml.ScoreDistribution";
        J.NewClass toTest = getNewClassFromClassSource(classTested, classInstantiated)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.ScoreDistribution"));
        assertThat(toTest)
                .isNotNull();
        TypeTree retrieved = jpmmlVisitor.updateTypeTreeToTargetInstantiatedType(toTest);
        assertThat(retrieved)
                .isNotNull()
                .isInstanceOf(J.Identifier.class);
        assertThat(retrieved.getType()).isEqualTo(jpmmlVisitor.targetInstantiatedType);
        assertThat(((J.Identifier) retrieved).getSimpleName()).isEqualTo(((JavaType.ShallowClass) jpmmlVisitor.targetInstantiatedType).getClassName());
    }

    @Test
    public void removedListaTupla_getJMethod() {
        JPMMLVisitor.RemovedListTupla removedListTupla = new JPMMLVisitor.RemovedListTupla("addDataFields", JavaType.buildType("org.dmg.pmml.DataField"));
        String classTested = "package com.yourorg;\n" +
                "import java.util.List;\n" +
                "import org.dmg.pmml.DataDictionary;\n" +
                "import org.dmg.pmml.DataField;\n" +
                "class FooBar {\n" +
                "static void method(List<DataField> dataFields) {\n" +
                "DataDictionary dataDictionary = new DataDictionary(dataFields);\n" +
                "}\n" +
                "}";
        String classInstantiated = "org.dmg.pmml.DataDictionary";
        J.NewClass toTest = getNewClassFromClassSource(classTested, classInstantiated)
                .orElseThrow(() -> new RuntimeException("Failed to find J.NewClass org.dmg.pmml.DataDictionary"));
       J.MethodInvocation retrieved = removedListTupla.getJMethod(toTest);
       String expected = "new DataDictionary().addDataFields(dataFields.toArray(new org.dmg.pmml.DataField[0]))";
       assertThat(retrieved)
               .isNotNull()
                       .hasToString(expected);

    }
}