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
package org.kie.pmml.compiler.commons.codegenfactories;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.CAST_INTEGER;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.models.Interval;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.api.models.TargetValue;
import org.kie.pmml.commons.model.KiePMMLTarget;
import org.kie.pmml.commons.model.KiePMMLTargetValue;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.utils.ModelUtils;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.KiePMMLUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.util.FileUtils.getFileContent;
import static org.drools.util.FileUtils.getFileInputStream;
import static org.kie.pmml.commons.Constants.GET_MODEL;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.commons.Constants.PMML_SUFFIX;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomCastInteger;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomDataField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomMiningField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomOpType;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomOutputField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomTarget;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.GET_CREATED_KIEPMMLMININGFIELDS;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.GET_CREATED_KIEPMMLOUTPUTFIELDS;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.GET_CREATED_KIEPMMLTARGETS;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.GET_CREATED_LOCAL_TRANSFORMATIONS;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.GET_CREATED_MININGFIELDS;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.GET_CREATED_OUTPUTFIELDS;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.GET_CREATED_TRANSFORMATION_DICTIONARY;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLModelFactoryUtilsTest {

    private static final String SOURCE_BASE = "TransformationsSample";
    private static final String SOURCE = SOURCE_BASE + PMML_SUFFIX;
    private static final String TEMPLATE_SOURCE = "Template.tmpl";
    private static final String TEMPLATE_CLASS_NAME = "Template";
    private static final String TEST_01_SOURCE = "KiePMMLModelFactoryUtilsTest_01.txt";
    private static final String TEST_02_SOURCE = "KiePMMLModelFactoryUtilsTest_02.txt";
    private static final String TEST_03_SOURCE = "KiePMMLModelFactoryUtilsTest_03.txt";
    private static final String TEST_04_SOURCE = "KiePMMLModelFactoryUtilsTest_04.txt";
    private static final String TEST_05_SOURCE = "KiePMMLModelFactoryUtilsTest_05.txt";
    private static final String TEST_06_SOURCE = "KiePMMLModelFactoryUtilsTest_06.txt";
    private static final String TEST_07_SOURCE = "KiePMMLModelFactoryUtilsTest_07.txt";
    private static final String TEST_08_SOURCE = "KiePMMLModelFactoryUtilsTest_08.txt";
    private static final String TEST_09_SOURCE = "KiePMMLModelFactoryUtilsTest_09.txt";
    private static final String TEST_10_SOURCE = "KiePMMLModelFactoryUtilsTest_10.txt";
    private static final String TEST_11_SOURCE = "KiePMMLModelFactoryUtilsTest_11.txt";
    private static final String TEST_12_SOURCE = "KiePMMLModelFactoryUtilsTest_12.txt";
    private static final String TEST_13_SOURCE = "KiePMMLModelFactoryUtilsTest_13.txt";
    private static final String TEST_14_SOURCE = "KiePMMLModelFactoryUtilsTest_14.txt";
    private static final String TEST_15_SOURCE = "KiePMMLTargetFactoryTest_01.txt";
    private static CompilationUnit compilationUnit;
    private static PMML pmmlModel;
    private static TreeModel model;
    private ConstructorDeclaration constructorDeclaration;
    private MethodDeclaration staticGetterMethod;
    private ExplicitConstructorInvocationStmt superInvocation;
    private ClassOrInterfaceDeclaration classOrInterfaceDeclaration;

    @BeforeAll
    public static void setup() throws Exception {
        pmmlModel = KiePMMLUtil.load(getFileInputStream(SOURCE), "");
        assertThat(pmmlModel).isNotNull();
        model = (TreeModel) pmmlModel.getModels().get(0);
        assertThat(model).isNotNull();
        compilationUnit = getFromFileName(TEMPLATE_SOURCE);
    }

    @BeforeEach
    public void initTest() {
        CompilationUnit clonedCompilationUnit = compilationUnit.clone();
        classOrInterfaceDeclaration = clonedCompilationUnit.getClassByName(TEMPLATE_CLASS_NAME)
                .orElseThrow(() -> new RuntimeException("Failed to retrieve ClassOrInterfaceDeclaration " + TEMPLATE_CLASS_NAME + "  from " + TEMPLATE_SOURCE))
                .clone();

        constructorDeclaration = classOrInterfaceDeclaration
                .getDefaultConstructor()
                .orElseThrow(() -> new RuntimeException("Failed to retrieve default constructor from " + TEMPLATE_SOURCE));
        assertThat(constructorDeclaration).isNotNull();
        assertThat(constructorDeclaration.getBody()).isNotNull();

        staticGetterMethod = classOrInterfaceDeclaration
                .getMethodsByName(GET_MODEL)
                .get(0);

        Optional<ExplicitConstructorInvocationStmt> optSuperInvocation =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(constructorDeclaration.getBody());
        assertThat(optSuperInvocation).isPresent();
        superInvocation = optSuperInvocation.get();
        assertThat(constructorDeclaration.getName().asString()).isEqualTo("Template"); // as in the original template
        assertThat(superInvocation.toString()).isEqualTo("super(fileName, name, Collections.emptyList(), operator, second);"); // as in
        // the original template
        assertThat(clonedCompilationUnit.getClassByName(TEMPLATE_CLASS_NAME)).isPresent();
    }

    @Test
    void setKiePMMLConstructorSuperNameInvocation() {
        String generatedClassName = "generatedClassName";
        String fileName = "fileName";
        String name = "newName";
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.setKiePMMLConstructorSuperNameInvocation(generatedClassName,
                                                                                                                         constructorDeclaration,
                                                                                                                         fileName,
                                                                                                                         name);
        commonVerifySuperInvocation(generatedClassName, fileName, name);
    }


    @Test
    void setKiePMMLModelConstructor() {
        String generatedClassName = "generatedClassName";
        String fileName = "fileName";
        String name = "newName";
        List<MiningField> miningFields = IntStream.range(0, 3)
                .mapToObj(i -> ModelUtils.convertToKieMiningField(getRandomMiningField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        List<OutputField> outputFields = IntStream.range(0, 2)
                .mapToObj(i -> ModelUtils.convertToKieOutputField(getRandomOutputField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        List<TargetField> targetFields = IntStream.range(0, 2)
                .mapToObj(i -> ModelUtils.convertToKieTargetField(getRandomTarget()))
                .collect(Collectors.toList());
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.setKiePMMLModelConstructor(generatedClassName,
                                                                                                           constructorDeclaration,
                                                                                                           fileName,
                                                                                                           name,
                                                                                                           miningFields,
                                                                                                           outputFields,
                                                                                                           targetFields);
        commonVerifySuperInvocation(generatedClassName,  fileName, name);
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

        retrieved = getMethodCallExprList(constructorDeclaration.getBody(), outputFields.size(), "kiePMMLTargets",
                                          "add");
        addMethodCall = retrieved.get(0);
        arguments = addMethodCall.getArguments();
        commonVerifyKiePMMLTargetFieldsMethodCallExpr(arguments, targetFields);
    }

    @Test
    void addGetCreatedKiePMMLMiningFieldsMethod() throws IOException {
        final CompilationDTO compilationDTO = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                     pmmlModel,
                                                                                                     model,
                                                                                                     new PMMLCompilationContextMock(), SOURCE_BASE);
        ClassOrInterfaceDeclaration modelTemplate = new ClassOrInterfaceDeclaration();
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.addGetCreatedKiePMMLMiningFieldsMethod(modelTemplate,
                                                                                                                       compilationDTO.getMiningSchema().getMiningFields(), compilationDTO.getFields());
        final MethodDeclaration retrieved = modelTemplate.getMethodsByName(GET_CREATED_KIEPMMLMININGFIELDS).get(0);
        String text = getFileContent(TEST_12_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(text);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved.getBody().get())).isTrue();
    }

    @Test
    void populateGetCreatedMiningFieldsMethod() throws IOException {
        final CompilationDTO compilationDTO = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                     pmmlModel,
                                                                                                     model,
                                                                                                     new PMMLCompilationContextMock(), SOURCE_BASE);
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.populateGetCreatedMiningFieldsMethod(classOrInterfaceDeclaration,
                                                                                                                     compilationDTO.getKieMiningFields());
        final MethodDeclaration retrieved =
                classOrInterfaceDeclaration.getMethodsByName(GET_CREATED_MININGFIELDS).get(0);
        String text = getFileContent(TEST_14_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    @Test
    void populateGetCreatedOutputFieldsMethod() throws IOException {
        final CompilationDTO compilationDTO = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                     pmmlModel,
                                                                                                     model,
                                                                                                     new PMMLCompilationContextMock(), SOURCE_BASE);
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.populateGetCreatedOutputFieldsMethod(classOrInterfaceDeclaration,
                                                                                                                     compilationDTO.getKieOutputFields());
        final MethodDeclaration retrieved =
                classOrInterfaceDeclaration.getMethodsByName(GET_CREATED_OUTPUTFIELDS).get(0);
        String text = getFileContent(TEST_13_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    @Test
    void populateGetCreatedKiePMMLMiningFieldsMethod() throws IOException {
        final CompilationDTO compilationDTO = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                     pmmlModel,
                                                                                                     model,
                                                                                                     new PMMLCompilationContextMock(), SOURCE_BASE);
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.populateGetCreatedKiePMMLMiningFieldsMethod(classOrInterfaceDeclaration,
                                                                                                                            compilationDTO.getMiningSchema().getMiningFields(), compilationDTO.getFields());
        final MethodDeclaration retrieved =
                classOrInterfaceDeclaration.getMethodsByName(GET_CREATED_KIEPMMLMININGFIELDS).get(0);
        String text = getFileContent(TEST_12_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(text);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved.getBody().get())).isTrue();
    }

    @Test
    void addGetCreatedKiePMMLOutputFieldsMethod() throws IOException {
        ClassOrInterfaceDeclaration modelTemplate = new ClassOrInterfaceDeclaration();
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.addGetCreatedKiePMMLOutputFieldsMethod(modelTemplate,
                                                                                                                       model.getOutput().getOutputFields());
        final MethodDeclaration retrieved = modelTemplate.getMethodsByName(GET_CREATED_KIEPMMLOUTPUTFIELDS).get(0);
        String text = getFileContent(TEST_11_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(text);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved.getBody().get())).isTrue();
    }

    @Test
    void populateGetCreatedKiePMMLOutputFieldsMethod() throws IOException {
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.populateGetCreatedKiePMMLOutputFieldsMethod(classOrInterfaceDeclaration,
                                                                                                                            model.getOutput().getOutputFields());
        final MethodDeclaration retrieved =
                classOrInterfaceDeclaration.getMethodsByName(GET_CREATED_KIEPMMLOUTPUTFIELDS).get(0);
        String text = getFileContent(TEST_11_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(text);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved.getBody().get())).isTrue();
    }

    @Test
    void populateGetCreatedKiePMMLTargetsMethod() throws IOException {
        Random random = new Random();
        List<TargetField> kiePMMLTargets = IntStream.range(0, 3).mapToObj(i -> new TargetField(Collections.emptyList(),
                                                                                               OP_TYPE.byName(getRandomOpType().value()),
                                                                                               "Target-" + i,
                                                                                               CAST_INTEGER.byName(getRandomCastInteger().value()),
                                                                                               (double) random.nextInt(20),
                                                                                               (double) random.nextInt(60) + 20,
                                                                                               (double) random.nextInt(100) / 100,
                                                                                               (double) random.nextInt(100) / 100
        )).collect(Collectors.toList());

        String opType0 = OP_TYPE.class.getCanonicalName() + "." + kiePMMLTargets.get(0).getOpType().toString();
        String castInteger0 =
                CAST_INTEGER.class.getCanonicalName() + "." + kiePMMLTargets.get(0).getCastInteger().toString();
        String opType1 = OP_TYPE.class.getCanonicalName() + "." + kiePMMLTargets.get(1).getOpType().toString();
        String castInteger1 =
                CAST_INTEGER.class.getCanonicalName() + "." + kiePMMLTargets.get(1).getCastInteger().toString();
        String opType2 = OP_TYPE.class.getCanonicalName() + "." + kiePMMLTargets.get(2).getOpType().toString();
        String castInteger2 =
                CAST_INTEGER.class.getCanonicalName() + "." + kiePMMLTargets.get(2).getCastInteger().toString();

        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.populateGetCreatedKiePMMLTargetsMethod(classOrInterfaceDeclaration,
                                                                                                                       kiePMMLTargets);
        final MethodDeclaration retrieved =
                classOrInterfaceDeclaration.getMethodsByName(GET_CREATED_KIEPMMLTARGETS).get(0);
        String text = getFileContent(TEST_10_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(String.format(text,
                                                                               kiePMMLTargets.get(0).getName(),
                                                                               opType0,
                                                                               castInteger0,
                                                                               kiePMMLTargets.get(0).getMin(),
                                                                               kiePMMLTargets.get(0).getMax(),
                                                                               kiePMMLTargets.get(0).getRescaleConstant(),
                                                                               kiePMMLTargets.get(0).getRescaleFactor(),
                                                                               kiePMMLTargets.get(1).getName(),
                                                                               opType1,
                                                                               castInteger1,
                                                                               kiePMMLTargets.get(1).getMin(),
                                                                               kiePMMLTargets.get(1).getMax(),
                                                                               kiePMMLTargets.get(1).getRescaleConstant(),
                                                                               kiePMMLTargets.get(1).getRescaleFactor(),
                                                                               kiePMMLTargets.get(2).getName(),
                                                                               opType2,
                                                                               castInteger2,
                                                                               kiePMMLTargets.get(2).getMin(),
                                                                               kiePMMLTargets.get(2).getMax(),
                                                                               kiePMMLTargets.get(2).getRescaleConstant(),
                                                                               kiePMMLTargets.get(2).getRescaleFactor()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    @Test
    void populateGetCreatedTransformationDictionaryMethod() throws IOException {
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.populateGetCreatedTransformationDictionaryMethod(classOrInterfaceDeclaration,
                                                                                                                                 pmmlModel.getTransformationDictionary());
        final MethodDeclaration retrieved =
                classOrInterfaceDeclaration.getMethodsByName(GET_CREATED_TRANSFORMATION_DICTIONARY).get(0);
        String text = getFileContent(TEST_09_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    @Test
    void populateGetCreatedLocalTransformationsMethod() throws IOException {
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.populateGetCreatedLocalTransformationsMethod(classOrInterfaceDeclaration,
                                                                                                                             model.getLocalTransformations());
        final MethodDeclaration retrieved =
                classOrInterfaceDeclaration.getMethodsByName(GET_CREATED_LOCAL_TRANSFORMATIONS).get(0);
        String text = getFileContent(TEST_08_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    @Test
    void addTransformationsInClassOrInterfaceDeclaration() throws IOException {
        assertThat(classOrInterfaceDeclaration.getMethodsByName("createTransformationDictionary")).isEmpty();
        assertThat(classOrInterfaceDeclaration.getMethodsByName("createLocalTransformations")).isEmpty();
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration(classOrInterfaceDeclaration,
                                                                                                                                pmmlModel.getTransformationDictionary(),
                                                                                                                                model.getLocalTransformations());
        assertThat(classOrInterfaceDeclaration.getMethodsByName("createTransformationDictionary")).hasSize(1);
        assertThat(classOrInterfaceDeclaration.getMethodsByName("createLocalTransformations")).hasSize(1);
        String text = getFileContent(TEST_01_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        MethodDeclaration retrieved =
                classOrInterfaceDeclaration.getMethodsByName("createTransformationDictionary").get(0);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        text = getFileContent(TEST_02_SOURCE);
        expected = JavaParserUtils.parseMethod(text);
        retrieved = classOrInterfaceDeclaration.getMethodsByName("createLocalTransformations").get(0);
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    @Test
    void init() throws IOException {
        final CompilationDTO compilationDTO = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                     pmmlModel,
                                                                                                     model,
                                                                                                     new PMMLCompilationContextMock(), SOURCE_BASE);
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.init(compilationDTO, classOrInterfaceDeclaration);
        BlockStmt body = constructorDeclaration.getBody();
        String text = getFileContent(TEST_03_SOURCE);
        Statement expected = JavaParserUtils.parseConstructorBlock(text);
        assertThat(JavaParserUtils.equalsNode(expected, body)).isTrue();
    }

    @Test
    void initStaticGetter() throws IOException {
        final CompilationDTO compilationDTO = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                     pmmlModel,
                                                                                                     model,
                                                                                                     new PMMLCompilationContextMock(), SOURCE_BASE);
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.initStaticGetter(compilationDTO,
                                                                                                 classOrInterfaceDeclaration);
        String text = getFileContent(TEST_04_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertThat(staticGetterMethod.toString()).isEqualTo(expected.toString());
        assertThat(JavaParserUtils.equalsNode(expected, staticGetterMethod)).isTrue();
    }

    @Test
    void getMiningFieldsObjectCreations() {
        List<MiningField> miningFields = IntStream.range(0, 3)
                .mapToObj(i -> ModelUtils.convertToKieMiningField(getRandomMiningField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        List retrieved = org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.getMiningFieldsObjectCreations(miningFields);
        commonVerifyMiningFieldsObjectCreation(retrieved, miningFields);
    }

    @Test
    void createIntervalsExpression() {
        List<Interval> intervals = IntStream.range(0, 3)
                .mapToObj(i -> {
                    int leftMargin = new Random().nextInt(40);
                    int rightMargin = leftMargin + 13;
                    return new Interval(leftMargin, rightMargin);
                })
                .collect(Collectors.toList());
        Expression retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.createIntervalsExpression(intervals);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).isInstanceOf(MethodCallExpr.class);
        MethodCallExpr mtdExp = (MethodCallExpr) retrieved;
        String expected = "java.util.Arrays";
        assertThat(mtdExp.getScope().get().asNameExpr().toString()).isEqualTo(expected);
        expected = "asList";
        assertThat(mtdExp.getName().asString()).isEqualTo(expected);
        NodeList<Expression> arguments = mtdExp.getArguments();
        assertThat(arguments).hasSameSizeAs(intervals);
        arguments.forEach(argument -> {
            assertThat(argument).isInstanceOf(ObjectCreationExpr.class);
            ObjectCreationExpr objCrt = (ObjectCreationExpr) argument;
            assertThat(objCrt.getType().asString()).isEqualTo(Interval.class.getCanonicalName());
            Optional<Interval> intervalOpt = intervals.stream()
                    .filter(interval -> String.valueOf(interval.getLeftMargin()).equals(objCrt.getArgument(0).asNameExpr().toString()) &&
                            String.valueOf(interval.getRightMargin()).equals(objCrt.getArgument(1).asNameExpr().toString()))
                    .findFirst();
            assertThat(intervalOpt).isPresent();
        });
    }

    @Test
    void getObjectCreationExprFromInterval() {
        Interval interval = new Interval(null, -14);
        ObjectCreationExpr retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.getObjectCreationExprFromInterval(interval);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getType().asString()).isEqualTo(Interval.class.getCanonicalName());
        NodeList<Expression> arguments = retrieved.getArguments();
        assertThat(arguments).hasSize(2);
        assertThat(arguments.get(0)).isInstanceOf(NullLiteralExpr.class);
        assertThat(arguments.get(1).asNameExpr().toString()).isEqualTo(String.valueOf(interval.getRightMargin()));
        interval = new Interval(-13, 10);
        retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.getObjectCreationExprFromInterval(interval);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getType().asString()).isEqualTo(Interval.class.getCanonicalName());
        arguments = retrieved.getArguments();
        assertThat(arguments).hasSize(2);
        assertThat(arguments.get(0).asNameExpr().toString()).isEqualTo(String.valueOf(interval.getLeftMargin()));
        assertThat(arguments.get(1).asNameExpr().toString()).isEqualTo(String.valueOf(interval.getRightMargin()));
        interval = new Interval(-13, null);
        retrieved =
                org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.getObjectCreationExprFromInterval(interval);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getType().asString()).isEqualTo(Interval.class.getCanonicalName());
        arguments = retrieved.getArguments();
        assertThat(arguments).hasSize(2);
        assertThat(arguments.get(0).asNameExpr().toString()).isEqualTo(String.valueOf(interval.getLeftMargin()));
        assertThat(arguments.get(1)).isInstanceOf(NullLiteralExpr.class);
    }

    @Test
    void getOutputFieldsObjectCreations() {
        List<OutputField> outputFields = IntStream.range(0, 2)
                .mapToObj(i -> ModelUtils.convertToKieOutputField(getRandomOutputField(),
                                                                  getRandomDataField()))
                .collect(Collectors.toList());
        List retrieved = org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.getOutputFieldsObjectCreations(outputFields);
        commonVerifyOutputFieldsObjectCreation(retrieved, outputFields);
    }

    @Test
    void populateTransformationsInConstructor() throws IOException {
        final String createTransformationDictionary = "createTransformationDictionary";
        final String createLocalTransformations = "createLocalTransformations";
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.populateTransformationsInConstructor(constructorDeclaration,
                                                                                                                     createTransformationDictionary,
                                                                                                                     createLocalTransformations);
        String text = getFileContent(TEST_07_SOURCE);
        BlockStmt expected = JavaParserUtils.parseConstructorBlock(text);
        assertThat(JavaParserUtils.equalsNode(expected, constructorDeclaration.getBody())).isTrue();
    }

    @Test
    void commonPopulateGetCreatedKiePMMLMiningFieldsMethod() throws IOException {
        final CompilationDTO compilationDTO = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                     pmmlModel,
                                                                                                     model,
                                                                                                     new PMMLCompilationContextMock(), SOURCE_BASE);
        final MethodDeclaration methodDeclaration = new MethodDeclaration();
        org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.commonPopulateGetCreatedKiePMMLMiningFieldsMethod(methodDeclaration,
                                                                                                                                  compilationDTO.getMiningSchema().getMiningFields(), compilationDTO.getFields());
        String text = getFileContent(TEST_06_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertThat(JavaParserUtils.equalsNode(expected, methodDeclaration)).isTrue();
    }

    @Test
    void commonPopulateGetCreatedKiePMMLOutputFieldsMethod() throws IOException {
        final CompilationDTO compilationDTO = CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                                                     pmmlModel,
                                                                                                     model,
                                                                                                     new PMMLCompilationContextMock(), SOURCE_BASE);
        final MethodDeclaration methodDeclaration = new MethodDeclaration();
        KiePMMLModelFactoryUtils.commonPopulateGetCreatedKiePMMLOutputFieldsMethod(methodDeclaration,
                                                                                   compilationDTO.getOutput().getOutputFields());
        String text = getFileContent(TEST_05_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertThat(JavaParserUtils.equalsNode(expected, methodDeclaration)).isTrue();
    }

    private void commonVerifyMiningFieldsObjectCreation(List<Expression> toVerify, List<MiningField> miningFields) {
        toVerify.forEach(expression -> {
            assertThat(expression).isInstanceOf(ObjectCreationExpr.class);
            ObjectCreationExpr objCrt = (ObjectCreationExpr) expression;
            assertThat(objCrt.getType().asString()).isEqualTo(MiningField.class.getCanonicalName());
            Optional<MiningField> miningFieldOpt = miningFields.stream()
                    .filter(miningField -> miningField.getName().equals(objCrt.getArgument(0).asStringLiteralExpr().asString()))
                    .findFirst();
            assertThat(miningFieldOpt).isPresent();
            MiningField miningField = miningFieldOpt.get();
            assertThat(objCrt.getType().asString()).isEqualTo(MiningField.class.getCanonicalName());
            String expected = miningField.getUsageType() != null ?
                    FIELD_USAGE_TYPE.class.getCanonicalName() + "." + miningField.getUsageType() : "null";
            assertThat(objCrt.getArgument(1).toString()).isEqualTo(expected);
            expected = miningField.getOpType() != null ?
                    OP_TYPE.class.getCanonicalName() + "." + miningField.getOpType() : "null";
            assertThat(objCrt.getArgument(2).toString()).isEqualTo(expected);
            expected = miningField.getDataType() != null ?
                    DATA_TYPE.class.getCanonicalName() + "." + miningField.getDataType() : "null";
            assertThat(objCrt.getArgument(3).toString()).isEqualTo(expected);
            expected = miningField.getMissingValueTreatmentMethod() != null ?
                    MISSING_VALUE_TREATMENT_METHOD.class.getCanonicalName() + "." + miningField.getMissingValueTreatmentMethod() : "null";
            assertThat(objCrt.getArgument(4).toString()).isEqualTo(expected);
            expected = miningField.getInvalidValueTreatmentMethod() != null ?
                    INVALID_VALUE_TREATMENT_METHOD.class.getCanonicalName() + "." + miningField.getInvalidValueTreatmentMethod() : "null";
            assertThat(objCrt.getArgument(5).toString()).isEqualTo(expected);
            expected = miningField.getMissingValueReplacement() != null ? miningField.getMissingValueReplacement() :
                    "null";
            assertThat(objCrt.getArgument(6).asStringLiteralExpr().asString()).isEqualTo(expected);
            expected = miningField.getInvalidValueReplacement() != null ? miningField.getInvalidValueReplacement() :
                    "null";
            assertThat(objCrt.getArgument(7).asStringLiteralExpr().asString()).isEqualTo(expected);
            MethodCallExpr allowedValuesMethod = objCrt.getArgument(8).asMethodCallExpr();
            IntStream.range(0, 3).forEach(i -> {
                String exp = miningField.getAllowedValues().get(i);
                assertThat(allowedValuesMethod.getArgument(i).asStringLiteralExpr().asString()).isEqualTo(exp);
            });
            MethodCallExpr intervalsMethod = objCrt.getArgument(9).asMethodCallExpr();
            IntStream.range(0, 3).forEach(i -> {
                Interval interval = miningField.getIntervals().get(i);
                ObjectCreationExpr objectCreationExpr = intervalsMethod.getArgument(i).asObjectCreationExpr();
                String exp = interval.getLeftMargin().toString();
                assertThat(objectCreationExpr.getArgument(0).asNameExpr().toString()).isEqualTo(exp);
                exp = interval.getRightMargin().toString();
                assertThat(objectCreationExpr.getArgument(1).asNameExpr().toString()).isEqualTo(exp);
            });
        });
    }

    private void commonVerifyOutputFieldsObjectCreation(List<Expression> toVerify, List<OutputField> outputFields) {
        toVerify.forEach(argument -> {
            assertThat(argument).isInstanceOf(ObjectCreationExpr.class);
            ObjectCreationExpr objCrt = (ObjectCreationExpr) argument;
            assertThat(objCrt.getType().asString()).isEqualTo(OutputField.class.getCanonicalName());
            Optional<OutputField> outputFieldOpt = outputFields.stream()
                    .filter(outputField -> outputField.getName().equals(objCrt.getArgument(0).asStringLiteralExpr().asString()))
                    .findFirst();
            assertThat(outputFieldOpt).isPresent();
            OutputField outputField = outputFieldOpt.get();
            String expected = OP_TYPE.class.getCanonicalName() + "." + outputField.getOpType();
            assertThat(objCrt.getArgument(1).asNameExpr().toString()).isEqualTo(expected);
            expected = DATA_TYPE.class.getCanonicalName() + "." + outputField.getDataType();
            assertThat(objCrt.getArgument(2).asNameExpr().toString()).isEqualTo(expected);
            expected = outputField.getTargetField();
            assertThat(objCrt.getArgument(3).asStringLiteralExpr().asString()).isEqualTo(expected);
            expected = RESULT_FEATURE.class.getCanonicalName() + "." + outputField.getResultFeature();
            assertThat(objCrt.getArgument(4).asNameExpr().toString()).isEqualTo(expected);
            MethodCallExpr allowedValuesMethod = objCrt.getArgument(5).asMethodCallExpr();
            IntStream.range(0, 3).forEach(i -> {
                String exp = outputField.getAllowedValues().get(i);
                assertThat(allowedValuesMethod.getArgument(i).asStringLiteralExpr().asString()).isEqualTo(exp);
            });
        });
    }

    private void commonVerifyKiePMMLTargetFieldsMethodCallExpr(List<Expression> toVerify,
                                                               List<TargetField> targetFields) {
        toVerify.forEach(argument -> {
            assertThat(argument).isInstanceOf(MethodCallExpr.class);
            MethodCallExpr mtdfClExpr = (MethodCallExpr) argument;
            assertThat(mtdfClExpr.getName().asString()).isEqualTo("build");
            final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", mtdfClExpr);
            Optional<TargetField> targetFieldOpt = targetFields.stream()
                    .filter(targetField -> targetField.getName().equals(builder.getArgument(0).asStringLiteralExpr().asString()))
                    .findFirst();
            assertThat(targetFieldOpt).isPresent();
            TargetField targetField = targetFieldOpt.get();
            try {
                commonVerifyKiePMMLTargetFieldsMethodCallExpr(mtdfClExpr, targetField);
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    private void commonVerifyKiePMMLTargetFieldsMethodCallExpr(MethodCallExpr retrieved, TargetField kieTargetField) throws IOException {
        String text = getFileContent(TEST_15_SOURCE);
        List<TargetValue> kieTargetValues = kieTargetField.getTargetValues();
        String opType = OP_TYPE.class.getCanonicalName() + "." + kieTargetField.getOpType().toString();
        String castInteger = CAST_INTEGER.class.getCanonicalName() + "." + kieTargetField.getCastInteger().toString();
        Expression expected = JavaParserUtils.parseExpression(String.format(text,
                                                                            kieTargetField.getName(),
                                                                            kieTargetValues.get(0).getValue(),
                                                                            kieTargetValues.get(0).getDisplayValue(),
                                                                            kieTargetValues.get(0).getPriorProbability(),
                                                                            kieTargetValues.get(0).getDefaultValue(),
                                                                            kieTargetValues.get(1).getValue(),
                                                                            kieTargetValues.get(1).getDisplayValue(),
                                                                            kieTargetValues.get(1).getPriorProbability(),
                                                                            kieTargetValues.get(1).getDefaultValue(),
                                                                            kieTargetValues.get(2).getValue(),
                                                                            kieTargetValues.get(2).getDisplayValue(),
                                                                            kieTargetValues.get(2).getPriorProbability(),
                                                                            kieTargetValues.get(2).getDefaultValue(),
                                                                            opType,
                                                                            kieTargetField.getField(),
                                                                            castInteger,
                                                                            kieTargetField.getMin(),
                                                                            kieTargetField.getMax(),
                                                                            kieTargetField.getRescaleConstant(),
                                                                            kieTargetField.getRescaleFactor()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
        List<Class<?>> imports = Arrays.asList(Arrays.class, Collections.class, KiePMMLTarget.class,
                                               KiePMMLTargetValue.class, TargetField.class, TargetValue.class);
        commonValidateCompilationWithImports(retrieved, imports);
    }

    private void commonVerifySuperInvocation(String generatedClassName, String fileName, String name) {
        assertThat(constructorDeclaration.getName().asString()).isEqualTo(generatedClassName); // modified by invocation
        String expected = String.format("super(\"%s\", \"%s\", Collections.emptyList(), operator, second);", fileName, name);
        assertThat(superInvocation.toString()).isEqualTo(expected); // modified by invocation
    }


    /**
     * Return a <code>List&lt;MethodCallExpr&gt;</code> where every element <b>scope' name</b> is <code>scope</code>
     * and every element <b>name</b> is <code>method</code>
     *
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
        assertThat(toReturn).hasSize(expectedSize);
        return toReturn;
    }

    /**
     * Verify the <b>scope' name</b> scope of the given <code>MethodCallExpr</code> is <code>scope</code>
     * and the <b>name</b> of the given <code>MethodCallExpr</code> is <code>method</code>
     *
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