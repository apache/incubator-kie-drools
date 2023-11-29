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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.Model;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.FIELD_USAGE_TYPE;
import org.kie.pmml.api.enums.INVALID_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.MISSING_VALUE_TREATMENT_METHOD;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.models.Interval;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.api.models.TargetField;
import org.kie.pmml.commons.model.KiePMMLMiningField;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.transformations.KiePMMLLocalTransformations;
import org.kie.pmml.commons.transformations.KiePMMLTransformationDictionary;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;

import static org.kie.pmml.commons.Constants.GET_MODEL;
import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.TO_RETURN;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedVariableName;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLLocalTransformationsFactory.LOCAL_TRANSFORMATIONS;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLMiningFieldFactory.getMiningFieldVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLOutputFieldFactory.getOutputFieldVariableDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLTransformationDictionaryFactory.TRANSFORMATION_DICTIONARY;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.addListPopulationByMethodCallExpr;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.addListPopulationByObjectCreationExpr;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.createArraysAsListFromList;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getReturnStmt;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypeNames;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.populateListInListGetter;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.setAssignExpressionValue;

/**
 * Class to provide shared, helper methods to be invoked by model-specific
 * <b>factories</b> (e.g. KiePMMLTreeModelFactory, KiePMMLScorecardModelFactory, KiePMMLRegressionModelFactory)
 */
public class KiePMMLModelFactoryUtils {

    public static final String GET_CREATED_MININGFIELDS = "getCreatedMiningFields";
    public static final String GET_CREATED_OUTPUTFIELDS = "getCreatedOutputFields";
    public static final String GET_CREATED_KIEPMMLMININGFIELDS = "getCreatedKiePMMLMiningFields";
    public static final String GET_CREATED_KIEPMMLOUTPUTFIELDS = "getCreatedKiePMMLOutputFields";
    public static final String GET_CREATED_KIEPMMLTARGETS = "getCreatedKiePMMLTargets";
    public static final String GET_CREATED_LOCAL_TRANSFORMATIONS = "getCreatedLocalTransformations";
    public static final String GET_CREATED_TRANSFORMATION_DICTIONARY = "getCreatedTransformationDictionary";

    private KiePMMLModelFactoryUtils() {
        // Avoid instantiation
    }

    /**
     * Set the <b>name</b> parameter on <b>super</b> invocation
     * @param generatedClassName
     * @param constructorDeclaration
     * @param name
     */
    public static void setKiePMMLConstructorSuperNameInvocation(final String generatedClassName,
                                                         final ConstructorDeclaration constructorDeclaration,
                                                         final String fileName,
                                                         final String name) {
        constructorDeclaration.setName(generatedClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        CommonCodegenUtils.setExplicitConstructorInvocationStmtArgument(superStatement, "fileName", String.format("\"%s\"",
                                                                                                                  fileName));
        CommonCodegenUtils.setExplicitConstructorInvocationStmtArgument(superStatement, "name", String.format("\"%s\"",
                                                                                                              name));
    }

    /**
     * Set the <b>name</b> parameter on <b>super</b> invocation
     * @param generatedClassName
     * @param constructorDeclaration
     * @param name
     */
    public static void setConstructorSuperNameInvocation(final String generatedClassName,
                                                         final ConstructorDeclaration constructorDeclaration,
                                                         final String name) {
        constructorDeclaration.setName(generatedClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        CommonCodegenUtils.setExplicitConstructorInvocationStmtArgument(superStatement, "name", String.format("\"%s\"",
                                                                                                              name));
    }

    /**
     * Set the <b>name</b> parameter on <b>super</b> invocation and populate the <b>miningFields/outputFields</b>
     * @param generatedClassName
     * @param constructorDeclaration
     * @param name
     * @param miningFields
     * @param outputFields
     * @param targetFields
     */
    public static void setKiePMMLModelConstructor(final String generatedClassName,
                                                  final ConstructorDeclaration constructorDeclaration,
                                                  final String fileName,
                                                  final String name,
                                                  final List<MiningField> miningFields,
                                                  final List<OutputField> outputFields,
                                                  final List<TargetField> targetFields) {
        setKiePMMLConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, fileName, name);
        final BlockStmt body = constructorDeclaration.getBody();
        final List<ObjectCreationExpr> miningFieldsObjectCreations = getMiningFieldsObjectCreations(miningFields);
        addListPopulationByObjectCreationExpr(miningFieldsObjectCreations, body, "miningFields");
        final List<ObjectCreationExpr> outputFieldsObjectCreations = getOutputFieldsObjectCreations(outputFields);
        addListPopulationByObjectCreationExpr(outputFieldsObjectCreations, body, "outputFields");
        final List<MethodCallExpr> kiePMMLTargetFieldsObjectCreations =
                getKiePMMLTargetFieldsObjectCreations(targetFields);
        addListPopulationByMethodCallExpr(kiePMMLTargetFieldsObjectCreations, body, "kiePMMLTargets");
    }

    /**
     * Add the <code>getCreatedKiePMMLMiningFields</code> method to the given <code>ClassOrInterfaceDeclaration</code>
     * @param modelTemplate
     * @param miningFields
     * @param fields
     */
    public static void addGetCreatedKiePMMLMiningFieldsMethod(final ClassOrInterfaceDeclaration modelTemplate,
                                                              final List<org.dmg.pmml.MiningField> miningFields,
                                                              final List<org.dmg.pmml.Field<?>> fields) {
        final MethodDeclaration methodDeclaration = modelTemplate.addMethod(GET_CREATED_KIEPMMLMININGFIELDS,
                                                                            Modifier.Keyword.PRIVATE);
        final ClassOrInterfaceType returnedType =
                getTypedClassOrInterfaceTypeByTypeNames(List.class.getSimpleName(),
                                                        Collections.singletonList(KiePMMLMiningField.class.getSimpleName()));
        methodDeclaration.setType(returnedType);
        commonPopulateGetCreatedKiePMMLMiningFieldsMethod(methodDeclaration, miningFields, fields);
    }

    /**
     * Populate the <code>getCreatedKiePMMLMiningFields</code> method
     * @param modelTemplate
     * @param miningFields
     */
    public static void populateGetCreatedMiningFieldsMethod(final ClassOrInterfaceDeclaration modelTemplate,
                                                            final List<MiningField> miningFields) {
        final MethodDeclaration methodDeclaration = modelTemplate.getMethodsByName(GET_CREATED_MININGFIELDS).get(0);
        final List<ObjectCreationExpr> miningFieldsObjectCreations = getMiningFieldsObjectCreations(miningFields);
        populateListInListGetter(miningFieldsObjectCreations, methodDeclaration, TO_RETURN);
    }

    /**
     * Populate the <code>getCreatedKiePMMLMiningFields</code> method
     * @param modelTemplate
     * @param outputFields
     */
    public static void populateGetCreatedOutputFieldsMethod(final ClassOrInterfaceDeclaration modelTemplate,
                                                            final List<OutputField> outputFields) {
        final MethodDeclaration methodDeclaration = modelTemplate.getMethodsByName(GET_CREATED_OUTPUTFIELDS).get(0);
        final List<ObjectCreationExpr> outputFieldsObjectCreations = getOutputFieldsObjectCreations(outputFields);
        populateListInListGetter(outputFieldsObjectCreations, methodDeclaration, TO_RETURN);
    }

    /**
     * Populate the <code>getCreatedKiePMMLMiningFields</code> method
     * @param modelTemplate
     * @param miningFields
     * @param fields
     */
    public static void populateGetCreatedKiePMMLMiningFieldsMethod(final ClassOrInterfaceDeclaration modelTemplate,
                                                                   final List<org.dmg.pmml.MiningField> miningFields,
                                                                   final List<org.dmg.pmml.Field<?>> fields) {
        final MethodDeclaration methodDeclaration =
                modelTemplate.getMethodsByName(GET_CREATED_KIEPMMLMININGFIELDS).get(0);
        commonPopulateGetCreatedKiePMMLMiningFieldsMethod(methodDeclaration, miningFields, fields);
    }

    public static void addGetCreatedKiePMMLOutputFieldsMethod(final ClassOrInterfaceDeclaration modelTemplate,
                                                              final List<org.dmg.pmml.OutputField> outputFields) {
        final MethodDeclaration methodDeclaration = modelTemplate.addMethod(GET_CREATED_KIEPMMLOUTPUTFIELDS,
                                                                            Modifier.Keyword.PRIVATE);
        final ClassOrInterfaceType returnedType =
                getTypedClassOrInterfaceTypeByTypeNames(List.class.getSimpleName(),
                                                        Collections.singletonList(KiePMMLOutputField.class.getSimpleName()));
        methodDeclaration.setType(returnedType);
        commonPopulateGetCreatedKiePMMLOutputFieldsMethod(methodDeclaration, outputFields);
    }

    public static void populateGetCreatedKiePMMLOutputFieldsMethod(final ClassOrInterfaceDeclaration modelTemplate,
                                                                   final List<org.dmg.pmml.OutputField> outputFields) {
        final MethodDeclaration methodDeclaration =
                modelTemplate.getMethodsByName(GET_CREATED_KIEPMMLOUTPUTFIELDS).get(0);
        commonPopulateGetCreatedKiePMMLOutputFieldsMethod(methodDeclaration, outputFields);
    }

    public static void populateGetCreatedKiePMMLTargetsMethod(final ClassOrInterfaceDeclaration modelTemplate,
                                                              final List<TargetField> targetFields) {
        final MethodDeclaration methodDeclaration = modelTemplate.getMethodsByName(GET_CREATED_KIEPMMLTARGETS).get(0);
        final List<MethodCallExpr> kiePMMLTargetFieldsObjectCreations =
                getKiePMMLTargetFieldsObjectCreations(targetFields);
        populateListInListGetter(kiePMMLTargetFieldsObjectCreations, methodDeclaration, TO_RETURN);
    }

    public static void populateGetCreatedTransformationDictionaryMethod(final ClassOrInterfaceDeclaration toPopulate,
                                                                        final TransformationDictionary transformationDictionary) {
        if (transformationDictionary != null) {
            BlockStmt createTransformationDictionaryBody =
                    KiePMMLTransformationDictionaryFactory.getKiePMMLTransformationDictionaryVariableDeclaration(transformationDictionary);
            createTransformationDictionaryBody.addStatement(getReturnStmt(TRANSFORMATION_DICTIONARY));
            final MethodDeclaration methodDeclaration =
                    toPopulate.getMethodsByName(GET_CREATED_TRANSFORMATION_DICTIONARY).get(0);
            methodDeclaration.setBody(createTransformationDictionaryBody);
        }
    }

    /**
     * Add <b>common</b> and <b>local</b> transformations management inside the given
     * <code>ClassOrInterfaceDeclaration</code>
     * @param toPopulate
     * @param localTransformations
     */
    public static void populateGetCreatedLocalTransformationsMethod(final ClassOrInterfaceDeclaration toPopulate,
                                                                    final LocalTransformations localTransformations) {
        if (localTransformations != null) {
            BlockStmt createLocalTransformationsBody =
                    KiePMMLLocalTransformationsFactory.getKiePMMLLocalTransformationsVariableDeclaration(localTransformations);
            createLocalTransformationsBody.addStatement(getReturnStmt(LOCAL_TRANSFORMATIONS));
            final MethodDeclaration methodDeclaration =
                    toPopulate.getMethodsByName(GET_CREATED_LOCAL_TRANSFORMATIONS).get(0);
            methodDeclaration.setBody(createLocalTransformationsBody);
        }
    }

    /**
     * Add <b>common</b> and <b>local</b> transformations management inside the given
     * <code>ClassOrInterfaceDeclaration</code>
     * @param toPopulate
     * @param transformationDictionary
     * @param localTransformations
     */
    public static void addTransformationsInClassOrInterfaceDeclaration(final ClassOrInterfaceDeclaration toPopulate,
                                                                       final TransformationDictionary transformationDictionary,
                                                                       final LocalTransformations localTransformations) {
        String createTransformationDictionary = null;
        if (transformationDictionary != null) {
            BlockStmt createTransformationDictionaryBody =
                    KiePMMLTransformationDictionaryFactory.getKiePMMLTransformationDictionaryVariableDeclaration(transformationDictionary);
            createTransformationDictionaryBody.addStatement(getReturnStmt(TRANSFORMATION_DICTIONARY));
            createTransformationDictionary = "createTransformationDictionary";
            MethodDeclaration createTransformationDictionaryMethod =
                    toPopulate.addMethod(createTransformationDictionary, Modifier.Keyword.PRIVATE);
            createTransformationDictionaryMethod.setType(KiePMMLTransformationDictionary.class.getName());
            createTransformationDictionaryMethod.setBody(createTransformationDictionaryBody);
        }
        String createLocalTransformations = null;
        if (localTransformations != null) {
            BlockStmt createLocalTransformationsBody =
                    KiePMMLLocalTransformationsFactory.getKiePMMLLocalTransformationsVariableDeclaration(localTransformations);
            createLocalTransformationsBody.addStatement(getReturnStmt(LOCAL_TRANSFORMATIONS));
            createLocalTransformations = "createLocalTransformations";
            MethodDeclaration createLocalTransformationsMethod = toPopulate.addMethod(createLocalTransformations,
                                                                                      Modifier.Keyword.PRIVATE);
            createLocalTransformationsMethod.setType(KiePMMLLocalTransformations.class.getName());
            createLocalTransformationsMethod.setBody(createLocalTransformationsBody);
        }
        final ConstructorDeclaration constructorDeclaration =
                toPopulate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, toPopulate.getName())));
        populateTransformationsInConstructor(constructorDeclaration, createTransformationDictionary,
                                             createLocalTransformations);
    }

    /**
     * Initialize the given <code>ClassOrInterfaceDeclaration</code> with all the <b>common</b> code needed to
     * generate a <code>KiePMMLModel</code>
     * @param compilationDTO
     * @param modelTemplate
     */
    public static void init(final CompilationDTO<? extends Model> compilationDTO,
                            final ClassOrInterfaceDeclaration modelTemplate) {
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        final String fileName = compilationDTO.getFileName();
        final String name = compilationDTO.getModelName();
        final String generatedClassName = compilationDTO.getSimpleClassName();
        final List<MiningField> miningFields = compilationDTO.getKieMiningFields();
        final List<OutputField> outputFields = compilationDTO.getKieOutputFields();
        final List<TargetField> targetFields = compilationDTO.getKieTargetFields();

        final Expression miningFunctionExpression;
        if (compilationDTO.getMINING_FUNCTION() != null) {
            MINING_FUNCTION miningFunction = compilationDTO.getMINING_FUNCTION();
            miningFunctionExpression = new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name());
        } else {
            miningFunctionExpression = new NullLiteralExpr();
        }
        final PMML_MODEL pmmlModelEnum = compilationDTO.getPMML_MODEL();
        final NameExpr pmmlMODELExpression =
                new NameExpr(pmmlModelEnum.getClass().getName() + "." + pmmlModelEnum.name());
        String targetFieldName = compilationDTO.getTargetFieldName();
        final Expression targetFieldExpression;
        if (targetFieldName != null) {
            targetFieldExpression = new StringLiteralExpr(targetFieldName);
        } else {
            targetFieldExpression = new NullLiteralExpr();
        }
        setKiePMMLModelConstructor(generatedClassName,
                                   constructorDeclaration,
                                   fileName,
                                   name,
                                   miningFields,
                                   outputFields,
                                   targetFields);
        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, compilationDTO.getTransformationDictionary(),
                                                        compilationDTO.getLocalTransformations());
        final BlockStmt body = constructorDeclaration.getBody();
        CommonCodegenUtils.setAssignExpressionValue(body, "pmmlMODEL", pmmlMODELExpression);
        CommonCodegenUtils.setAssignExpressionValue(body, "miningFunction", miningFunctionExpression);
        CommonCodegenUtils.setAssignExpressionValue(body, "targetField", targetFieldExpression);

        addGetCreatedKiePMMLMiningFieldsMethod(modelTemplate, compilationDTO.getMiningSchema().getMiningFields(),
                                               compilationDTO.getFields());
        MethodCallExpr getCreatedKiePMMLMiningFieldsExpr = new MethodCallExpr();
        getCreatedKiePMMLMiningFieldsExpr.setScope(new ThisExpr());
        getCreatedKiePMMLMiningFieldsExpr.setName(GET_CREATED_KIEPMMLMININGFIELDS);
        CommonCodegenUtils.setAssignExpressionValue(body, "kiePMMLMiningFields", getCreatedKiePMMLMiningFieldsExpr);

        if (compilationDTO.getOutput() != null) {
            addGetCreatedKiePMMLOutputFieldsMethod(modelTemplate, compilationDTO.getOutput().getOutputFields());
            MethodCallExpr getCreatedKiePMMLOutputFieldsExpr = new MethodCallExpr();
            getCreatedKiePMMLOutputFieldsExpr.setScope(new ThisExpr());
            getCreatedKiePMMLOutputFieldsExpr.setName(GET_CREATED_KIEPMMLOUTPUTFIELDS);
            CommonCodegenUtils.setAssignExpressionValue(body, "kiePMMLOutputFields", getCreatedKiePMMLOutputFieldsExpr);
        }
    }

    /**
     * Populate the given <code>ClassOrInterfaceDeclaration</code>' <b>staticGetter</b> with the <b>common</b>
     * parameters needed to
     * instantiate a <code>KiePMMLModel</code>
     * @param compilationDTO
     * @param modelTemplate
     * @return
     */
    public static void initStaticGetter(final CompilationDTO<? extends Model> compilationDTO,
                                        final ClassOrInterfaceDeclaration modelTemplate) {
        final MethodDeclaration staticGetterMethod =
                modelTemplate.getMethodsByName(GET_MODEL).get(0);
        final BlockStmt staticGetterBody =
                staticGetterMethod.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, staticGetterMethod)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(staticGetterBody, TO_RETURN).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TO_RETURN, staticGetterBody)));

        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      TO_RETURN, staticGetterBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        final String fileName = compilationDTO.getFileName();
        final String name = compilationDTO.getModelName();
        final Expression miningFunctionExpression;
        if (compilationDTO.getMINING_FUNCTION() != null) {
            MINING_FUNCTION miningFunction = compilationDTO.getMINING_FUNCTION();
            miningFunctionExpression = new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name());
        } else {
            miningFunctionExpression = new NullLiteralExpr();
        }
        builder.setArgument(0, new StringLiteralExpr(fileName));
        builder.setArgument(1, new StringLiteralExpr(name));
        builder.setArgument(2, miningFunctionExpression);

        String targetFieldName = compilationDTO.getTargetFieldName();
        final Expression targetFieldExpression;
        if (targetFieldName != null) {
            targetFieldExpression = new StringLiteralExpr(targetFieldName);
        } else {
            targetFieldExpression = new NullLiteralExpr();
        }
        getChainedMethodCallExprFrom("withTargetField", initializer).setArgument(0, targetFieldExpression);

        //
        populateGetCreatedMiningFieldsMethod(modelTemplate, compilationDTO.getKieMiningFields());
        populateGetCreatedOutputFieldsMethod(modelTemplate, compilationDTO.getKieOutputFields());
        populateGetCreatedKiePMMLMiningFieldsMethod(modelTemplate, compilationDTO.getMiningSchema().getMiningFields()
                , compilationDTO.getFields());
        if (compilationDTO.getOutput() != null) {
            populateGetCreatedKiePMMLOutputFieldsMethod(modelTemplate, compilationDTO.getOutput().getOutputFields());
        }
        if (compilationDTO.getKieTargetFields() != null) {
            populateGetCreatedKiePMMLTargetsMethod(modelTemplate, compilationDTO.getKieTargetFields());
        }
        populateGetCreatedTransformationDictionaryMethod(modelTemplate, compilationDTO.getTransformationDictionary());
        populateGetCreatedLocalTransformationsMethod(modelTemplate, compilationDTO.getLocalTransformations());
    }

    /**
     * Create a <code>List&lt;ObjectCreationExpr&gt;</code> for the given <code>List&lt;MiningField&gt;</code>
     * @param miningFields
     * @return
     */
    static List<ObjectCreationExpr> getMiningFieldsObjectCreations(final List<MiningField> miningFields) {
        return miningFields.stream()
                .map(miningField -> {
                    ObjectCreationExpr toReturn = new ObjectCreationExpr();
                    toReturn.setType(MiningField.class.getCanonicalName());
                    Expression name = miningField.getName() != null ?
                            new StringLiteralExpr(miningField.getName())
                            : new NullLiteralExpr();
                    FIELD_USAGE_TYPE fieldUsageType = miningField.getUsageType();
                    Expression usageType = fieldUsageType != null ?
                            new NameExpr(fieldUsageType.getClass().getName() + "." + fieldUsageType.name())
                            : new NullLiteralExpr();
                    OP_TYPE oPT = miningField.getOpType();
                    Expression opType = oPT != null ?
                            new NameExpr(oPT.getClass().getName() + "." + oPT.name())
                            : new NullLiteralExpr();
                    DATA_TYPE dtT = miningField.getDataType();
                    Expression dataType = dtT != null ?
                            new NameExpr(dtT.getClass().getName() + "." + dtT.name())
                            : new NullLiteralExpr();
                    MISSING_VALUE_TREATMENT_METHOD mVTM = miningField.getMissingValueTreatmentMethod();
                    Expression missingValueTreatmentMethod = mVTM != null ?
                            new NameExpr(mVTM.getClass().getName() + "." + mVTM.name())
                            : new NullLiteralExpr();
                    INVALID_VALUE_TREATMENT_METHOD iVTM = miningField.getInvalidValueTreatmentMethod();
                    Expression invalidValueTreatmentMethod = iVTM != null ?
                            new NameExpr(iVTM.getClass().getName() + "." + iVTM.name())
                            : new NullLiteralExpr();
                    Expression missingValueReplacement = miningField.getMissingValueReplacement() != null ?
                            new StringLiteralExpr(miningField.getMissingValueReplacement())
                            : new NullLiteralExpr();
                    Expression invalidValueReplacement = miningField.getInvalidValueReplacement() != null ?
                            new StringLiteralExpr(miningField.getInvalidValueReplacement())
                            : new NullLiteralExpr();
                    Expression allowedValues = miningField.getAllowedValues() != null ?
                            createArraysAsListFromList(miningField.getAllowedValues()).getExpression()
                            : new NullLiteralExpr();
                    Expression intervals = miningField.getIntervals() != null ?
                            createIntervalsExpression(miningField.getIntervals())
                            : new NullLiteralExpr();
                    toReturn.setArguments(NodeList.nodeList(name,
                                                            usageType,
                                                            opType,
                                                            dataType,
                                                            missingValueTreatmentMethod,
                                                            invalidValueTreatmentMethod,
                                                            missingValueReplacement,
                                                            invalidValueReplacement,
                                                            allowedValues,
                                                            intervals));
                    return toReturn;
                })
                .collect(Collectors.toList());
    }

    static Expression createIntervalsExpression(List<Interval> intervals) {
        ExpressionStmt arraysAsListStmt = CommonCodegenUtils.createArraysAsListExpression();
        MethodCallExpr arraysCallExpression = arraysAsListStmt.getExpression().asMethodCallExpr();
        NodeList<Expression> arguments = new NodeList<>();
        intervals.forEach(value -> arguments.add(getObjectCreationExprFromInterval(value)));
        arraysCallExpression.setArguments(arguments);
        arraysAsListStmt.setExpression(arraysCallExpression);
        return arraysAsListStmt.getExpression();
    }

    static ObjectCreationExpr getObjectCreationExprFromInterval(Interval source) {
        ObjectCreationExpr toReturn = new ObjectCreationExpr();
        toReturn.setType(Interval.class.getCanonicalName());
        NodeList<Expression> arguments = new NodeList<>();
        if (source.getLeftMargin() != null) {
            arguments.add(new NameExpr(source.getLeftMargin().toString()));
        } else {
            arguments.add(new NullLiteralExpr());
        }
        if (source.getRightMargin() != null) {
            arguments.add(new NameExpr(source.getRightMargin().toString()));
        } else {
            arguments.add(new NullLiteralExpr());
        }
        toReturn.setArguments(arguments);
        return toReturn;
    }

    /**
     * Create a <code>List&lt;ObjectCreationExpr&gt;</code> for the given <code>List&lt;OutputField&gt;</code>
     * @param outputFields
     * @return
     */
    static List<ObjectCreationExpr> getOutputFieldsObjectCreations(final List<OutputField> outputFields) {
        return outputFields.stream()
                .map(outputField -> {
                    ObjectCreationExpr toReturn = new ObjectCreationExpr();
                    toReturn.setType(OutputField.class.getCanonicalName());
                    Expression name = outputField.getName() != null ?
                            new StringLiteralExpr(outputField.getName())
                            : new NullLiteralExpr();
                    OP_TYPE oPT = outputField.getOpType();
                    Expression opType = oPT != null ?
                            new NameExpr(oPT.getClass().getName() + "." + oPT.name())
                            : new NullLiteralExpr();
                    DATA_TYPE datT = outputField.getDataType();
                    Expression dataType = datT != null ?
                            new NameExpr(datT.getClass().getName() + "." + datT.name())
                            : new NullLiteralExpr();
                    Expression targetField = outputField.getTargetField() != null ?
                            new StringLiteralExpr(outputField.getTargetField())
                            : new NullLiteralExpr();
                    RESULT_FEATURE rsltF = outputField.getResultFeature();
                    Expression resultFeature = rsltF != null ?
                            new NameExpr(rsltF.getClass().getName() + "." + rsltF.name())
                            : new NullLiteralExpr();
                    Expression allowedValues = outputField.getAllowedValues() != null ?
                            createArraysAsListFromList(outputField.getAllowedValues()).getExpression()
                            : new NullLiteralExpr();
                    toReturn.setArguments(NodeList.nodeList(name, opType, dataType, targetField, resultFeature,
                                                            allowedValues));
                    return toReturn;
                })
                .collect(Collectors.toList());
    }

    /**
     * Create a <code>List&lt;ObjectCreationExpr&gt;</code> for the given <code>List&lt;KiePMMLTarget&gt;</code>
     * @param targetFields
     * @return
     */
    static List<MethodCallExpr> getKiePMMLTargetFieldsObjectCreations(final List<TargetField> targetFields) {
        return targetFields.stream()
                .map(KiePMMLTargetFactory::getKiePMMLTargetVariableInitializer)
                .collect(Collectors.toList());
    }

    /**
     * Populating the <b>transformationDictionary</b> and <b>localTransformations</b> variables inside the constructor
     * @param constructorDeclaration
     * @param createTransformationDictionary
     * @param createLocalTransformations
     */
    static void populateTransformationsInConstructor(final ConstructorDeclaration constructorDeclaration,
                                                     final String createTransformationDictionary,
                                                     final String createLocalTransformations) {
        Expression createTransformationDictionaryInitializer = createTransformationDictionary != null ?
                new MethodCallExpr(new NameExpr("this"), createTransformationDictionary, NodeList.nodeList()) :
                new NullLiteralExpr();
        setAssignExpressionValue(constructorDeclaration.getBody(), TRANSFORMATION_DICTIONARY,
                                 createTransformationDictionaryInitializer);
        Expression createLocalTransformationsInitializer = createLocalTransformations != null ?
                new MethodCallExpr(new NameExpr("this"), createLocalTransformations, NodeList.nodeList()) :
                new NullLiteralExpr();
        setAssignExpressionValue(constructorDeclaration.getBody(), LOCAL_TRANSFORMATIONS,
                                 createLocalTransformationsInitializer);
    }

    /**
     * Populate the <code>getCreatedKiePMMLMiningFields</code> method
     * @param methodDeclaration
     * @param miningFields
     * @param fields
     */
    static void commonPopulateGetCreatedKiePMMLMiningFieldsMethod(final MethodDeclaration methodDeclaration,
                                                                  final List<org.dmg.pmml.MiningField> miningFields,
                                                                  final List<org.dmg.pmml.Field<?>> fields) {
        BlockStmt body = new BlockStmt();
        NodeList<Expression> arguments = new NodeList<>();
        for (org.dmg.pmml.MiningField miningField : miningFields) {
            String miningFieldVariableName = getSanitizedVariableName(miningField.getName()).toLowerCase();
            BlockStmt toAdd = getMiningFieldVariableDeclaration(miningFieldVariableName, miningField, fields);
            toAdd.getStatements().forEach(body::addStatement);
            arguments.add(new NameExpr(miningFieldVariableName));
        }
        MethodCallExpr methodCallExpr = new MethodCallExpr();
        methodCallExpr.setScope(new NameExpr(Arrays.class.getSimpleName()));
        methodCallExpr.setName("asList");
        methodCallExpr.setArguments(arguments);
        ReturnStmt returnStmt = new ReturnStmt();
        returnStmt.setExpression(methodCallExpr);
        body.addStatement(returnStmt);
        methodDeclaration.setBody(body);
    }

    static void commonPopulateGetCreatedKiePMMLOutputFieldsMethod(final MethodDeclaration methodDeclaration,
                                                                  final List<org.dmg.pmml.OutputField> outputFields) {
        BlockStmt body = new BlockStmt();
        NodeList<Expression> arguments = new NodeList<>();
        for (org.dmg.pmml.OutputField outputField : outputFields) {
            String outputFieldVariableName = getSanitizedVariableName(outputField.getName()).toLowerCase();
            BlockStmt toAdd = getOutputFieldVariableDeclaration(outputFieldVariableName, outputField);
            toAdd.getStatements().forEach(body::addStatement);
            arguments.add(new NameExpr(outputFieldVariableName));
        }
        MethodCallExpr methodCallExpr = new MethodCallExpr();
        methodCallExpr.setScope(new NameExpr(Arrays.class.getSimpleName()));
        methodCallExpr.setName("asList");
        methodCallExpr.setArguments(arguments);
        ReturnStmt returnStmt = new ReturnStmt();
        returnStmt.setExpression(methodCallExpr);
        body.addStatement(returnStmt);
        methodDeclaration.setBody(body);
    }
}
