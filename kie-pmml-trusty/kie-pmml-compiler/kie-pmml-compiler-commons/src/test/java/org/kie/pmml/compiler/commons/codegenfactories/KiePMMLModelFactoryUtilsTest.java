/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.compiler.commons.codegenfactories;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.models.Interval;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;
import org.kie.pmml.compiler.commons.utils.ModelUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomMiningField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomOutputField;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class KiePMMLModelFactoryUtilsTest {

    private static final String SOURCE = "TransformationsSample.pmml";
    private static final String TEMPLATE_SOURCE = "Template.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "Template";
    private static CompilationUnit compilationUnit;
    private static PMML pmmlModel;
    private static TreeModel model;
    private ConstructorDeclaration constructorDeclaration;
    private ExplicitConstructorInvocationStmt superInvocation;
    private ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

    @BeforeClass
    public static void setup() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(SOURCE), "");
        assertNotNull(pmmlModel);
        model = (TreeModel) pmmlModel.getModels().get(0);
        assertNotNull(model);
        compilationUnit = getFromFileName(TEMPLATE_SOURCE);
    }

    @Before
    public void init() {
        CompilationUnit clonedCompilationUnit = compilationUnit.clone();
        constructorDeclaration = clonedCompilationUnit.getClassByName(TEMPLATE_CLASS_NAME)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve ClassOrInterfaceDeclaration " + TEMPLATE_CLASS_NAME + "  from " + TEMPLATE_SOURCE))
                .getDefaultConstructor()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve default constructor from " + TEMPLATE_SOURCE));
        assertNotNull(constructorDeclaration);
        assertNotNull(constructorDeclaration.getBody());
        Optional<ExplicitConstructorInvocationStmt> optSuperInvocation =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(constructorDeclaration.getBody());
        assertTrue(optSuperInvocation.isPresent());
        superInvocation = optSuperInvocation.get();
        assertEquals("Template", constructorDeclaration.getName().asString()); // as in the original template
        assertEquals("super(name, Collections.emptyList(), operator, second);", superInvocation.toString()); // as in
        // the original template
        assertTrue(clonedCompilationUnit.getClassByName(TEMPLATE_CLASS_NAME).isPresent());
        classOrInterfaceDeclaration = clonedCompilationUnit.getClassByName(TEMPLATE_CLASS_NAME).get().clone();
    }

    @Test
    public void setConstructorSuperNameInvocation() {
        String generatedClassName = "generatedClassName";
        String name = "newName";
        KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation(generatedClassName,
                                                                   constructorDeclaration,
                                                                   name);
        commonVerifySuperInvocation(generatedClassName, name);
    }

    @Test
    public void setKiePMMLModelConstructor() {
        String generatedClassName = "generatedClassName";
        String name = "newName";
        List<MiningField> miningFields = IntStream.range(0, 3)
                .mapToObj(i -> ModelUtils.convertToKieMiningField(getRandomMiningField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        List<OutputField> outputFields = IntStream.range(0, 2)
                .mapToObj(i -> ModelUtils.convertToKieOutputField(getRandomOutputField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        KiePMMLModelFactoryUtils.setKiePMMLModelConstructor(generatedClassName,
                                                            constructorDeclaration,
                                                            name,
                                                            miningFields,
                                                            outputFields);
        commonVerifySuperInvocation(generatedClassName, name);
        List<MethodCallExpr> retrieved = getMethodCallExprList(constructorDeclaration.getBody(), miningFields.size(),
                                                               "miningFields",
                                                               "add");
        MethodCallExpr addMethodCall = retrieved.get(0);
        NodeList<Expression> arguments = addMethodCall.getArguments();
        commonVerifyMiningFieldsObjectCreation(arguments, miningFields);
        retrieved = getMethodCallExprList(constructorDeclaration.getBody(), outputFields.size(), "outputFields",
                                          "add");
        addMethodCall = retrieved.get(0);
        arguments = addMethodCall.getArguments();
        commonVerifyOutputFieldsObjectCreation(arguments, outputFields);
    }

    @Test
    public void addTransformationsInClassOrInterfaceDeclaration() {
        assertTrue(classOrInterfaceDeclaration.getMethodsByName("createTransformationDictionary").isEmpty());
        assertTrue(classOrInterfaceDeclaration.getMethodsByName("createLocalTransformations").isEmpty());
        KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration(classOrInterfaceDeclaration,
                                                                                 pmmlModel.getTransformationDictionary(),
                                                                                 model.getLocalTransformations());
        assertEquals(1, classOrInterfaceDeclaration.getMethodsByName("createTransformationDictionary").size());
        assertEquals(1, classOrInterfaceDeclaration.getMethodsByName("createLocalTransformations").size());
        MethodDeclaration expected = JavaParserUtils.parseMethod("private org.kie.pmml.commons.transformations" +
                                                                         ".KiePMMLTransformationDictionary " +
                                                                         "createTransformationDictionary() {\n" +
                                                                         "    KiePMMLParameterField " +
                                                                         "CONSTANT_FUNCTION_0 = KiePMMLParameterField" +
                                                                         ".builder(\"empty\", Collections.emptyList()" +
                                                                         ").withDataType(null).withOpType(null)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLConstant " +
                                                                         "CONSTANT_FUNCTION_Expression = new " +
                                                                         "KiePMMLConstant" +
                                                                         "(\"CONSTANT_FUNCTION_Expression\", " +
                                                                         "Collections.emptyList(), " +
                                                                         "\"CONSTANT_FUNCTION_VALUE\", null);\n" +
                                                                         "    KiePMMLDefineFunction CONSTANT_FUNCTION" +
                                                                         " = new KiePMMLDefineFunction" +
                                                                         "(\"CONSTANT_FUNCTION\", Collections" +
                                                                         ".emptyList(), org.kie.pmml.api.enums" +
                                                                         ".DATA_TYPE.STRING, org.kie.pmml.api.enums" +
                                                                         ".OP_TYPE.CATEGORICAL, Arrays.asList" +
                                                                         "(CONSTANT_FUNCTION_0), " +
                                                                         "CONSTANT_FUNCTION_Expression);\n" +
                                                                         "    KiePMMLParameterField " +
                                                                         "FIELDREF_FUNCTION_0 = KiePMMLParameterField" +
                                                                         ".builder(\"fieldRed\", Collections" +
                                                                         ".emptyList()).withDataType(null).withOpType" +
                                                                         "(null).withDisplayName(null).build();\n" +
                                                                         "    KiePMMLFieldRef " +
                                                                         "FIELDREF_FUNCTION_Expression = new " +
                                                                         "KiePMMLFieldRef(\"Petal.Length\", " +
                                                                         "Collections.emptyList(), null);\n" +
                                                                         "    KiePMMLDefineFunction FIELDREF_FUNCTION" +
                                                                         " = new KiePMMLDefineFunction" +
                                                                         "(\"FIELDREF_FUNCTION\", Collections" +
                                                                         ".emptyList(), org.kie.pmml.api.enums" +
                                                                         ".DATA_TYPE.DOUBLE, org.kie.pmml.api.enums" +
                                                                         ".OP_TYPE.CONTINUOUS, Arrays.asList" +
                                                                         "(FIELDREF_FUNCTION_0), " +
                                                                         "FIELDREF_FUNCTION_Expression);\n" +
                                                                         "    KiePMMLParameterField APPLY_FUNCTION_0 " +
                                                                         "= KiePMMLParameterField.builder" +
                                                                         "(\"fieldRed\", Collections.emptyList())" +
                                                                         ".withDataType(null).withOpType(null)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLFieldRef " +
                                                                         "APPLY_FUNCTION_Expression_0 = new " +
                                                                         "KiePMMLFieldRef(\"Petal.Length\", " +
                                                                         "Collections.emptyList(), null);\n" +
                                                                         "    KiePMMLApply APPLY_FUNCTION_Expression " +
                                                                         "= KiePMMLApply.builder" +
                                                                         "(\"APPLY_FUNCTION_Expression\", Collections" +
                                                                         ".emptyList(), \"FIELDREF_FUNCTION\")" +
                                                                         ".withDefaultValue(null).withMapMissingTo" +
                                                                         "(null).withInvalidValueTreatmentMethod" +
                                                                         "(\"returnInvalid\").withKiePMMLExpressions" +
                                                                         "(Arrays.asList(APPLY_FUNCTION_Expression_0)" +
                                                                         ").build();\n" +
                                                                         "    KiePMMLDefineFunction APPLY_FUNCTION = " +
                                                                         "new KiePMMLDefineFunction" +
                                                                         "(\"APPLY_FUNCTION\", Collections.emptyList" +
                                                                         "(), org.kie.pmml.api.enums.DATA_TYPE" +
                                                                         ".DOUBLE, org.kie.pmml.api.enums.OP_TYPE" +
                                                                         ".CONTINUOUS, Arrays.asList" +
                                                                         "(APPLY_FUNCTION_0), " +
                                                                         "APPLY_FUNCTION_Expression);\n" +
                                                                         "    KiePMMLParameterField " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_0 = " +
                                                                         "KiePMMLParameterField.builder" +
                                                                         "(\"reviewText\", Collections.emptyList())" +
                                                                         ".withDataType(null).withOpType(null)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLParameterField " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_1 = " +
                                                                         "KiePMMLParameterField.builder(\"term\", " +
                                                                         "Collections.emptyList()).withDataType(null)" +
                                                                         ".withOpType(null).withDisplayName(null)" +
                                                                         ".build();\n" +
                                                                         "    KiePMMLFieldRef " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_Expression = new KiePMMLFieldRef(\"term\", Collections.emptyList(), null);\n" +
                                                                         "    Map<String, Object> " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_0_columnValues = Stream.of(new Object[][] { { \"regex\", \"true\" }, { \"string\", \"interfaces?\" }, { \"stem\", \"interface\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_0 = new KiePMMLRow(TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_0_columnValues);\n" +
                                                                         "    Map<String, Object> " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_1_columnValues = Stream.of(new Object[][] { { \"regex\", \"true\" }, { \"string\", \"is|are|seem(ed|s?)|were\" }, { \"stem\", \"be\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_1 = new KiePMMLRow(TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_1_columnValues);\n" +
                                                                         "    Map<String, Object> " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_2_columnValues = Stream.of(new Object[][] { { \"regex\", \"true\" }, { \"string\", \"user friendl(y|iness)\" }, { \"stem\", \"user_friendly\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_2 = new KiePMMLRow(TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_2_columnValues);\n" +
                                                                         "    KiePMMLInlineTable " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable = new KiePMMLInlineTable(\"TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable\", Collections.emptyList(), Arrays.asList(TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_0, TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_1, TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable_2));\n" +
                                                                         "    KiePMMLTextIndexNormalization " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0 = KiePMMLTextIndexNormalization.builder(\"TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0\", Collections.emptyList()).withInField(\"string\").withOutField(\"stem\").withKiePMMLInlineTable(TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0_InlineTable).withRegexField(\"regex\").withRecursive(false).withIsCaseSensitive(false).withMaxLevenshteinDistance(null).withWordSeparatorCharacterRE(null).withTokenize(false).build();\n" +
                                                                         "    Map<String, Object> " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1_InlineTable_0_columnValues = Stream.of(new Object[][] { { \"regex\", \"true\" }, { \"re\", \"interface be (user_friendly|well designed|excellent)\" }, { \"feature\", \"ui_good\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1_InlineTable_0 = new KiePMMLRow(TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1_InlineTable_0_columnValues);\n" +
                                                                         "    KiePMMLInlineTable " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1_InlineTable = new KiePMMLInlineTable(\"TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1_InlineTable\", Collections.emptyList(), Arrays.asList(TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1_InlineTable_0));\n" +
                                                                         "    KiePMMLTextIndexNormalization " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1 = KiePMMLTextIndexNormalization.builder(\"TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1\", Collections.emptyList()).withInField(\"re\").withOutField(\"feature\").withKiePMMLInlineTable(TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1_InlineTable).withRegexField(\"regex\").withRecursive(false).withIsCaseSensitive(false).withMaxLevenshteinDistance(null).withWordSeparatorCharacterRE(null).withTokenize(false).build();\n" +
                                                                         "    KiePMMLTextIndex " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression = KiePMMLTextIndex.builder(\"reviewText\", Collections.emptyList(), TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_Expression).withLocalTermWeights(org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS.BINARY).withIsCaseSensitive(false).withMaxLevenshteinDistance(0).withCountHits(org.kie.pmml.api.enums.COUNT_HITS.ALL_HITS).withWordSeparatorCharacterRE(\"\\\\s+\").withTokenize(true).withTextIndexNormalizations(Arrays.asList(TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_0, TEXT_INDEX_NORMALIZATION_FUNCTION_Expression_1)).build();\n" +
                                                                         "    KiePMMLDefineFunction " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION = new " +
                                                                         "KiePMMLDefineFunction" +
                                                                         "(\"TEXT_INDEX_NORMALIZATION_FUNCTION\", " +
                                                                         "Collections.emptyList(), null, org.kie.pmml" +
                                                                         ".api.enums.OP_TYPE.CONTINUOUS, Arrays" +
                                                                         ".asList" +
                                                                         "(TEXT_INDEX_NORMALIZATION_FUNCTION_0, " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_1), " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION_Expression);\n" +
                                                                         "    KiePMMLConstant " +
                                                                         "transformationDictionaryDerivedField_0_0 = " +
                                                                         "new KiePMMLConstant" +
                                                                         "(\"transformationDictionaryDerivedField_0_0" +
                                                                         "\", Collections.emptyList(), " +
                                                                         "\"CONSTANT_DERIVEDFIELD_VALUE\", null);\n" +
                                                                         "    KiePMMLDerivedField " +
                                                                         "transformationDictionaryDerivedField_0 = " +
                                                                         "KiePMMLDerivedField.builder" +
                                                                         "(\"CONSTANT_DERIVEDFIELD\", Collections" +
                                                                         ".emptyList(), org.kie.pmml.api.enums" +
                                                                         ".DATA_TYPE.STRING, org.kie.pmml.api.enums" +
                                                                         ".OP_TYPE.CATEGORICAL, " +
                                                                         "transformationDictionaryDerivedField_0_0)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLFieldRef " +
                                                                         "transformationDictionaryDerivedField_1_0_0 " +
                                                                         "= new KiePMMLFieldRef(\"Petal.Length\", " +
                                                                         "Collections.emptyList(), null);\n" +
                                                                         "    KiePMMLApply " +
                                                                         "transformationDictionaryDerivedField_1_0 = " +
                                                                         "KiePMMLApply.builder" +
                                                                         "(\"transformationDictionaryDerivedField_1_0" +
                                                                         "\", Collections.emptyList(), " +
                                                                         "\"APPLY_FUNCTION\").withDefaultValue(null)" +
                                                                         ".withMapMissingTo(null)" +
                                                                         ".withInvalidValueTreatmentMethod" +
                                                                         "(\"returnInvalid\").withKiePMMLExpressions" +
                                                                         "(Arrays.asList" +
                                                                         "(transformationDictionaryDerivedField_1_0_0)).build();\n" +
                                                                         "    KiePMMLDerivedField " +
                                                                         "transformationDictionaryDerivedField_1 = " +
                                                                         "KiePMMLDerivedField.builder" +
                                                                         "(\"APPLY_DERIVEDFIELD\", Collections" +
                                                                         ".emptyList(), org.kie.pmml.api.enums" +
                                                                         ".DATA_TYPE.DOUBLE, org.kie.pmml.api.enums" +
                                                                         ".OP_TYPE.CONTINUOUS, " +
                                                                         "transformationDictionaryDerivedField_1_0)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLFieldRef " +
                                                                         "transformationDictionaryDerivedField_2_0 = " +
                                                                         "new KiePMMLFieldRef(\"Ref\", Collections" +
                                                                         ".emptyList(), null);\n" +
                                                                         "    KiePMMLDerivedField " +
                                                                         "transformationDictionaryDerivedField_2 = " +
                                                                         "KiePMMLDerivedField.builder(\"BackRef\", " +
                                                                         "Collections.emptyList(), org.kie.pmml.api" +
                                                                         ".enums.DATA_TYPE.DOUBLE, org.kie.pmml.api" +
                                                                         ".enums.OP_TYPE.CONTINUOUS, " +
                                                                         "transformationDictionaryDerivedField_2_0)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLFieldRef " +
                                                                         "transformationDictionaryDerivedField_3_0 = " +
                                                                         "new KiePMMLFieldRef(\"Petal.Width\", " +
                                                                         "Collections.emptyList(), null);\n" +
                                                                         "    KiePMMLDerivedField " +
                                                                         "transformationDictionaryDerivedField_3 = " +
                                                                         "KiePMMLDerivedField.builder(\"StageOne\", " +
                                                                         "Collections.emptyList(), org.kie.pmml.api" +
                                                                         ".enums.DATA_TYPE.DOUBLE, org.kie.pmml.api" +
                                                                         ".enums.OP_TYPE.CONTINUOUS, " +
                                                                         "transformationDictionaryDerivedField_3_0)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLFieldRef " +
                                                                         "transformationDictionaryDerivedField_4_0 = " +
                                                                         "new KiePMMLFieldRef(\"StageOne\", " +
                                                                         "Collections.emptyList(), null);\n" +
                                                                         "    KiePMMLDerivedField " +
                                                                         "transformationDictionaryDerivedField_4 = " +
                                                                         "KiePMMLDerivedField.builder(\"StageTwo\", " +
                                                                         "Collections.emptyList(), org.kie.pmml.api" +
                                                                         ".enums.DATA_TYPE.DOUBLE, org.kie.pmml.api" +
                                                                         ".enums.OP_TYPE.CONTINUOUS, " +
                                                                         "transformationDictionaryDerivedField_4_0)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLFieldRef " +
                                                                         "transformationDictionaryDerivedField_5_0 = " +
                                                                         "new KiePMMLFieldRef(\"StageTwo\", " +
                                                                         "Collections.emptyList(), null);\n" +
                                                                         "    KiePMMLDerivedField " +
                                                                         "transformationDictionaryDerivedField_5 = " +
                                                                         "KiePMMLDerivedField.builder(\"StageThree\"," +
                                                                         " Collections.emptyList(), org.kie.pmml.api" +
                                                                         ".enums.DATA_TYPE.DOUBLE, org.kie.pmml.api" +
                                                                         ".enums.OP_TYPE.CONTINUOUS, " +
                                                                         "transformationDictionaryDerivedField_5_0)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLFieldColumnPair " +
                                                                         "transformationDictionaryDerivedField_6_0_0 " +
                                                                         "= new KiePMMLFieldColumnPair(\"BAND\", " +
                                                                         "Collections.emptyList(), \"band\");\n" +
                                                                         "    KiePMMLFieldColumnPair " +
                                                                         "transformationDictionaryDerivedField_6_0_1 " +
                                                                         "= new KiePMMLFieldColumnPair(\"STATE\", " +
                                                                         "Collections.emptyList(), \"state\");\n" +
                                                                         "    Map<String, Object> " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_0_columnValues = Stream.of(new Object[][] { { \"band\", \"1\" }, { \"state\", \"MN\" }, { \"out\", \"10000\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_0 = new KiePMMLRow(transformationDictionaryDerivedField_6_0_InlineTable_0_columnValues);\n" +
                                                                         "    Map<String, Object> " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_1_columnValues = Stream.of(new Object[][] { { \"band\", \"1\" }, { \"state\", \"IL\" }, { \"out\", \"12000\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_1 = new KiePMMLRow(transformationDictionaryDerivedField_6_0_InlineTable_1_columnValues);\n" +
                                                                         "    Map<String, Object> " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_2_columnValues = Stream.of(new Object[][] { { \"band\", \"1\" }, { \"state\", \"NY\" }, { \"out\", \"20000\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_2 = new KiePMMLRow(transformationDictionaryDerivedField_6_0_InlineTable_2_columnValues);\n" +
                                                                         "    Map<String, Object> " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_3_columnValues = Stream.of(new Object[][] { { \"band\", \"2\" }, { \"state\", \"MN\" }, { \"out\", \"20000\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_3 = new KiePMMLRow(transformationDictionaryDerivedField_6_0_InlineTable_3_columnValues);\n" +
                                                                         "    Map<String, Object> " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_4_columnValues = Stream.of(new Object[][] { { \"band\", \"2\" }, { \"state\", \"IL\" }, { \"out\", \"23000\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_4 = new KiePMMLRow(transformationDictionaryDerivedField_6_0_InlineTable_4_columnValues);\n" +
                                                                         "    Map<String, Object> " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_5_columnValues = Stream.of(new Object[][] { { \"band\", \"2\" }, { \"state\", \"NY\" }, { \"out\", \"30000\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable_5 = new KiePMMLRow(transformationDictionaryDerivedField_6_0_InlineTable_5_columnValues);\n" +
                                                                         "    KiePMMLInlineTable " +
                                                                         "transformationDictionaryDerivedField_6_0_InlineTable = new KiePMMLInlineTable(\"transformationDictionaryDerivedField_6_0_InlineTable\", Collections.emptyList(), Arrays.asList(transformationDictionaryDerivedField_6_0_InlineTable_0, transformationDictionaryDerivedField_6_0_InlineTable_1, transformationDictionaryDerivedField_6_0_InlineTable_2, transformationDictionaryDerivedField_6_0_InlineTable_3, transformationDictionaryDerivedField_6_0_InlineTable_4, transformationDictionaryDerivedField_6_0_InlineTable_5));\n" +
                                                                         "    KiePMMLMapValues " +
                                                                         "transformationDictionaryDerivedField_6_0 = " +
                                                                         "KiePMMLMapValues.builder" +
                                                                         "(\"transformationDictionaryDerivedField_6_0" +
                                                                         "\", Collections.emptyList(), \"out\")" +
                                                                         ".withDefaultValue(null).withMapMissingTo" +
                                                                         "(null).withDataType(org.kie.pmml.api.enums" +
                                                                         ".DATA_TYPE.INTEGER).withKiePMMLInlineTable" +
                                                                         "(transformationDictionaryDerivedField_6_0_InlineTable).withKiePMMLFieldColumnPairs(Arrays.asList(transformationDictionaryDerivedField_6_0_0, transformationDictionaryDerivedField_6_0_1)).build();\n" +
                                                                         "    KiePMMLDerivedField " +
                                                                         "transformationDictionaryDerivedField_6 = " +
                                                                         "KiePMMLDerivedField.builder(\"mapvalued\", " +
                                                                         "Collections.emptyList(), org.kie.pmml.api" +
                                                                         ".enums.DATA_TYPE.DOUBLE, org.kie.pmml.api" +
                                                                         ".enums.OP_TYPE.CONTINUOUS, " +
                                                                         "transformationDictionaryDerivedField_6_0)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLFieldColumnPair " +
                                                                         "transformationDictionaryDerivedField_7_0_0 " +
                                                                         "= new KiePMMLFieldColumnPair" +
                                                                         "(\"FLAG_OWN_CAR\", Collections.emptyList()," +
                                                                         " \"data:input\");\n" +
                                                                         "    Map<String, Object> " +
                                                                         "transformationDictionaryDerivedField_7_0_InlineTable_0_columnValues = Stream.of(new Object[][] { { \"data:output\", \"0.0\" }, { \"data:input\", \"0\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "transformationDictionaryDerivedField_7_0_InlineTable_0 = new KiePMMLRow(transformationDictionaryDerivedField_7_0_InlineTable_0_columnValues);\n" +
                                                                         "    Map<String, Object> " +
                                                                         "transformationDictionaryDerivedField_7_0_InlineTable_1_columnValues = Stream.of(new Object[][] { { \"data:output\", \"1.0\" }, { \"data:input\", \"1\" } }).collect(Collectors.toMap(data -> (String) data[0], data -> data[1]));\n" +
                                                                         "    KiePMMLRow " +
                                                                         "transformationDictionaryDerivedField_7_0_InlineTable_1 = new KiePMMLRow(transformationDictionaryDerivedField_7_0_InlineTable_1_columnValues);\n" +
                                                                         "    KiePMMLInlineTable " +
                                                                         "transformationDictionaryDerivedField_7_0_InlineTable = new KiePMMLInlineTable(\"transformationDictionaryDerivedField_7_0_InlineTable\", Collections.emptyList(), Arrays.asList(transformationDictionaryDerivedField_7_0_InlineTable_0, transformationDictionaryDerivedField_7_0_InlineTable_1));\n" +
                                                                         "    KiePMMLMapValues " +
                                                                         "transformationDictionaryDerivedField_7_0 = " +
                                                                         "KiePMMLMapValues.builder" +
                                                                         "(\"transformationDictionaryDerivedField_7_0" +
                                                                         "\", Collections.emptyList(), " +
                                                                         "\"data:output\").withDefaultValue(null)" +
                                                                         ".withMapMissingTo(null).withDataType(null)" +
                                                                         ".withKiePMMLInlineTable" +
                                                                         "(transformationDictionaryDerivedField_7_0_InlineTable).withKiePMMLFieldColumnPairs(Arrays.asList(transformationDictionaryDerivedField_7_0_0)).build();\n" +
                                                                         "    KiePMMLDerivedField " +
                                                                         "transformationDictionaryDerivedField_7 = " +
                                                                         "KiePMMLDerivedField.builder" +
                                                                         "(\"dataencoded\", Collections.emptyList(), " +
                                                                         "org.kie.pmml.api.enums.DATA_TYPE.DOUBLE, " +
                                                                         "org.kie.pmml.api.enums.OP_TYPE.CATEGORICAL," +
                                                                         " transformationDictionaryDerivedField_7_0)" +
                                                                         ".withDisplayName(null).build();\n" +
                                                                         "    KiePMMLTransformationDictionary " +
                                                                         "transformationDictionary = " +
                                                                         "KiePMMLTransformationDictionary.builder" +
                                                                         "(\"transformationDictionary\", Collections" +
                                                                         ".emptyList()).withDefineFunctions(Arrays" +
                                                                         ".asList(CONSTANT_FUNCTION, " +
                                                                         "FIELDREF_FUNCTION, APPLY_FUNCTION, " +
                                                                         "TEXT_INDEX_NORMALIZATION_FUNCTION))" +
                                                                         ".withDerivedFields(Arrays.asList" +
                                                                         "(transformationDictionaryDerivedField_0, " +
                                                                         "transformationDictionaryDerivedField_1, " +
                                                                         "transformationDictionaryDerivedField_2, " +
                                                                         "transformationDictionaryDerivedField_3, " +
                                                                         "transformationDictionaryDerivedField_4, " +
                                                                         "transformationDictionaryDerivedField_5, " +
                                                                         "transformationDictionaryDerivedField_6, " +
                                                                         "transformationDictionaryDerivedField_7))" +
                                                                         ".build();\n" +
                                                                         "    return transformationDictionary;\n" +
                                                                         "}");
        MethodDeclaration retrieved =
                classOrInterfaceDeclaration.getMethodsByName("createTransformationDictionary").get(0);
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
        expected = JavaParserUtils.parseMethod("private org.kie.pmml.commons.transformations" +
                                                       ".KiePMMLLocalTransformations createLocalTransformations() {\n" +
                                                       "    KiePMMLConstant localTransformationsDerivedField_0_0 = " +
                                                       "new KiePMMLConstant(\"localTransformationsDerivedField_0_0\"," +
                                                       " Collections.emptyList(), " +
                                                       "\"LOCAL_CONSTANT_DERIVEDFIELD_VALUE\", null);\n" +
                                                       "    KiePMMLDerivedField localTransformationsDerivedField_0 = " +
                                                       "KiePMMLDerivedField.builder(\"LOCAL_CONSTANT_DERIVEDFIELD\", " +
                                                       "Collections.emptyList(), org.kie.pmml.api.enums.DATA_TYPE" +
                                                       ".STRING, org.kie.pmml.api.enums.OP_TYPE.CATEGORICAL, " +
                                                       "localTransformationsDerivedField_0_0).withDisplayName(null)" +
                                                       ".build();\n" +
                                                       "    KiePMMLFieldRef localTransformationsDerivedField_1_0 = " +
                                                       "new KiePMMLFieldRef(\"Ref\", Collections.emptyList(), null);" +
                                                       "\n" +
                                                       "    KiePMMLDerivedField localTransformationsDerivedField_1 = " +
                                                       "KiePMMLDerivedField.builder(\"LOCAL_Ref\", Collections" +
                                                       ".emptyList(), org.kie.pmml.api.enums.DATA_TYPE.DOUBLE, org" +
                                                       ".kie.pmml.api.enums.OP_TYPE.CONTINUOUS, " +
                                                       "localTransformationsDerivedField_1_0).withDisplayName(null)" +
                                                       ".build();\n" +
                                                       "    KiePMMLFieldRef localTransformationsDerivedField_2_0 = " +
                                                       "new KiePMMLFieldRef(\"BackRef\", Collections.emptyList(), " +
                                                       "null);\n" +
                                                       "    KiePMMLDerivedField localTransformationsDerivedField_2 = " +
                                                       "KiePMMLDerivedField.builder(\"LOCAL_BackRef\", Collections" +
                                                       ".emptyList(), org.kie.pmml.api.enums.DATA_TYPE.DOUBLE, org" +
                                                       ".kie.pmml.api.enums.OP_TYPE.CONTINUOUS, " +
                                                       "localTransformationsDerivedField_2_0).withDisplayName(null)" +
                                                       ".build();\n" +
                                                       "    KiePMMLLocalTransformations localTransformations = " +
                                                       "KiePMMLLocalTransformations.builder(\"localTransformations\"," +
                                                       " Collections.emptyList()).withDerivedFields(Arrays.asList" +
                                                       "(localTransformationsDerivedField_0, " +
                                                       "localTransformationsDerivedField_1, " +
                                                       "localTransformationsDerivedField_2)).build();\n" +
                                                       "    return localTransformations;\n" +
                                                       "}");
        retrieved = classOrInterfaceDeclaration.getMethodsByName("createLocalTransformations").get(0);
        assertTrue(JavaParserUtils.equalsNode(expected, retrieved));
    }

    @Test
    public void getMiningFieldsObjectCreations() {
        List<MiningField> miningFields = IntStream.range(0, 3)
                .mapToObj(i -> ModelUtils.convertToKieMiningField(getRandomMiningField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        List retrieved = KiePMMLModelFactoryUtils.getMiningFieldsObjectCreations(miningFields);
        commonVerifyMiningFieldsObjectCreation(retrieved, miningFields);
    }

    @Test
    public void createIntervalsExpression() {
        List<Interval> intervals = IntStream.range(0, 3)
                .mapToObj(i -> {
                    int leftMargin = new Random().nextInt(40);
                    int rightMargin = leftMargin + 13;
                    return new Interval(leftMargin, rightMargin);
                })
                .collect(Collectors.toList());
        Expression retrieved = KiePMMLModelFactoryUtils.createIntervalsExpression(intervals);
        assertNotNull(retrieved);
        assertTrue(retrieved instanceof MethodCallExpr);
        MethodCallExpr mtdExp = (MethodCallExpr) retrieved;
        String expected = "java.util.Arrays";
        assertEquals(expected, mtdExp.getScope().get().asNameExpr().toString());
        expected = "asList";
        assertEquals(expected, mtdExp.getName().asString());
        NodeList<Expression> arguments = mtdExp.getArguments();
        assertEquals(intervals.size(), arguments.size());
        arguments.forEach(argument -> {
            assertTrue(argument instanceof ObjectCreationExpr);
            ObjectCreationExpr objCrt = (ObjectCreationExpr) argument;
            assertEquals(Interval.class.getCanonicalName(), objCrt.getType().asString());
            Optional<Interval> intervalOpt = intervals.stream()
                    .filter(interval -> String.valueOf(interval.getLeftMargin()).equals(objCrt.getArgument(0).asNameExpr().toString()) &&
                            String.valueOf(interval.getRightMargin()).equals(objCrt.getArgument(1).asNameExpr().toString()))
                    .findFirst();
            assertTrue(intervalOpt.isPresent());
        });
    }

    @Test
    public void getObjectCreationExprFromInterval() {
        Interval interval = new Interval(null, -14);
        ObjectCreationExpr retrieved = KiePMMLModelFactoryUtils.getObjectCreationExprFromInterval(interval);
        assertNotNull(retrieved);
        assertEquals(Interval.class.getCanonicalName(), retrieved.getType().asString());
        NodeList<Expression> arguments = retrieved.getArguments();
        assertEquals(2, arguments.size());
        assertTrue(arguments.get(0) instanceof NullLiteralExpr);
        assertEquals(String.valueOf(interval.getRightMargin()), arguments.get(1).asNameExpr().toString());
        interval = new Interval(-13, 10);
        retrieved = KiePMMLModelFactoryUtils.getObjectCreationExprFromInterval(interval);
        assertNotNull(retrieved);
        assertEquals(Interval.class.getCanonicalName(), retrieved.getType().asString());
        arguments = retrieved.getArguments();
        assertEquals(2, arguments.size());
        assertEquals(String.valueOf(interval.getLeftMargin()), arguments.get(0).asNameExpr().toString());
        assertEquals(String.valueOf(interval.getRightMargin()), arguments.get(1).asNameExpr().toString());
        interval = new Interval(-13, null);
        retrieved = KiePMMLModelFactoryUtils.getObjectCreationExprFromInterval(interval);
        assertNotNull(retrieved);
        assertEquals(Interval.class.getCanonicalName(), retrieved.getType().asString());
        arguments = retrieved.getArguments();
        assertEquals(2, arguments.size());
        assertEquals(String.valueOf(interval.getLeftMargin()), arguments.get(0).asNameExpr().toString());
        assertTrue(arguments.get(1) instanceof NullLiteralExpr);
    }

    @Test
    public void getOutputFieldsObjectCreations() {
        List<OutputField> outputFields = IntStream.range(0, 2)
                .mapToObj(i -> ModelUtils.convertToKieOutputField(getRandomOutputField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        List retrieved = KiePMMLModelFactoryUtils.getOutputFieldsObjectCreations(outputFields);
        commonVerifyOutputFieldsObjectCreation(retrieved, outputFields);
    }

    private void commonVerifyMiningFieldsObjectCreation(List<Expression> toVerify, List<MiningField> miningFields) {
        toVerify.forEach(expression -> {
            assertTrue(expression instanceof ObjectCreationExpr);
            ObjectCreationExpr objCrt = (ObjectCreationExpr) expression;
            assertEquals(MiningField.class.getCanonicalName(), objCrt.getType().asString());
            Optional<MiningField> miningFieldOpt = miningFields.stream()
                    .filter(miningField -> miningField.getName().equals(objCrt.getArgument(0).asStringLiteralExpr().asString()))
                    .findFirst();
            assertTrue(miningFieldOpt.isPresent());
            MiningField miningField = miningFieldOpt.get();
            assertEquals(MiningField.class.getCanonicalName(), objCrt.getType().asString());
            String expected = miningField.getUsageType() != null ?
                    FIELD_USAGE_TYPE.class.getCanonicalName() + "." + miningField.getUsageType() : "null";
            assertEquals(expected, objCrt.getArgument(1).toString());
            expected = miningField.getOpType() != null ?
                    OP_TYPE.class.getCanonicalName() + "." + miningField.getOpType() : "null";
            assertEquals(expected, objCrt.getArgument(2).toString());
            expected = miningField.getDataType() != null ?
                    DATA_TYPE.class.getCanonicalName() + "." + miningField.getDataType() : "null";
            assertEquals(expected, objCrt.getArgument(3).toString());
            expected = miningField.getMissingValueTreatmentMethod() != null ?
                    MISSING_VALUE_TREATMENT_METHOD.class.getCanonicalName() + "." + miningField.getMissingValueTreatmentMethod() : "null";
            assertEquals(expected, objCrt.getArgument(4).toString());
            expected = miningField.getInvalidValueTreatmentMethod() != null ?
                    INVALID_VALUE_TREATMENT_METHOD.class.getCanonicalName() + "." + miningField.getInvalidValueTreatmentMethod() : "null";
            assertEquals(expected, objCrt.getArgument(5).toString());
            expected = miningField.getMissingValueReplacement() != null ? miningField.getMissingValueReplacement() :
                    "null";
            assertEquals(expected, objCrt.getArgument(6).toString());
            expected = miningField.getInvalidValueReplacement() != null ? miningField.getInvalidValueReplacement() :
                    "null";
            assertEquals(expected, objCrt.getArgument(7).toString());
            expected = "java.util.Arrays.asList()";
            assertEquals(expected, objCrt.getArgument(8).toString());
            assertEquals(expected, objCrt.getArgument(9).toString());
        });
    }

    private void commonVerifyOutputFieldsObjectCreation(List<Expression> toVerify, List<OutputField> outputFields) {
        toVerify.forEach(argument -> {
            assertTrue(argument instanceof ObjectCreationExpr);
            ObjectCreationExpr objCrt = (ObjectCreationExpr) argument;
            assertEquals(OutputField.class.getCanonicalName(), objCrt.getType().asString());
            Optional<OutputField> outputFieldOpt = outputFields.stream()
                    .filter(miningField -> miningField.getName().equals(objCrt.getArgument(0).asStringLiteralExpr().asString()))
                    .findFirst();
            assertTrue(outputFieldOpt.isPresent());
            OutputField outputField = outputFieldOpt.get();
            String expected = OP_TYPE.class.getCanonicalName() + "." + outputField.getOpType();
            assertEquals(expected, objCrt.getArgument(1).asNameExpr().toString());
            expected = DATA_TYPE.class.getCanonicalName() + "." + outputField.getDataType();
            assertEquals(expected, objCrt.getArgument(2).asNameExpr().toString());
            expected = outputField.getTargetField();
            assertEquals(expected, objCrt.getArgument(3).asStringLiteralExpr().asString());
            expected = RESULT_FEATURE.class.getCanonicalName() + "." + outputField.getResultFeature();
            assertEquals(expected, objCrt.getArgument(4).asNameExpr().toString());
            expected = "java.util.Arrays.asList()";
            assertEquals(expected, objCrt.getArgument(5).asMethodCallExpr().toString());
        });
    }

    private void commonVerifySuperInvocation(String generatedClassName, String name) {
        assertEquals(generatedClassName, constructorDeclaration.getName().asString()); // modified by invocation
        String expected = String.format("super(\"%s\", Collections.emptyList(), operator, second);", name);
        assertEquals(expected, superInvocation.toString()); // modified by invocation
    }

    /**
     * Return a <code>List&lt;MethodCallExpr&gt;</code> where every element <b>scope' name</b> is <code>scope</code>
     * and every element <b>name</b> is <code>method</code>
     * @param blockStmt
     * @param expectedSize
     * @param scope
     * @param method
     * @return
     */
    private List<MethodCallExpr> getMethodCallExprList(BlockStmt blockStmt, int expectedSize, String scope,
                                                       String method) {
        Stream<Statement> statementStream = getStatementStream(blockStmt);
        List<MethodCallExpr> toReturn = statementStream
                .filter(Statement::isExpressionStmt)
                .map(expressionStmt -> ((ExpressionStmt) expressionStmt).getExpression())
                .filter(expression -> expression instanceof MethodCallExpr)
                .map(expression -> (MethodCallExpr) expression)
                .filter(methodCallExpr -> evaluateMethodCallExpr(methodCallExpr, scope, method))
                .collect(Collectors.toList());
        assertEquals(expectedSize, toReturn.size());
        return toReturn;
    }

    /**
     * Verify the <b>scope' name</b> scope of the given <code>MethodCallExpr</code> is <code>scope</code>
     * and the <b>name</b> of the given <code>MethodCallExpr</code> is <code>method</code>
     * @param methodCallExpr
     * @param scope
     * @param method
     * @return
     */
    private boolean evaluateMethodCallExpr(MethodCallExpr methodCallExpr, String scope, String method) {
        return methodCallExpr.getScope().isPresent() &&
                methodCallExpr.getScope().get().isNameExpr() &&
                ((NameExpr) methodCallExpr.getScope().get()).getName().asString().equals(scope) &&
                methodCallExpr.getName().asString().equals(method);
    }

    private Stream<Statement> getStatementStream(BlockStmt blockStmt) {
        final NodeList<Statement> statements = blockStmt.getStatements();
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        statements.iterator(),
                        Spliterator.ORDERED), false);
    }
}