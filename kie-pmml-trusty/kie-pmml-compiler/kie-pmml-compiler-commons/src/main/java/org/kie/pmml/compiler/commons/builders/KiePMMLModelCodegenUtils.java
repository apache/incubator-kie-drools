/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.compiler.commons.builders;

import java.util.List;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.Model;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.api.utils.ModelUtils;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.GET_CREATED_KIEPMMLOUTPUTFIELDS;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.addGetCreatedKiePMMLOutputFieldsMethod;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.setKiePMMLModelConstructor;

/**
 * Class meant to implement all the <b>common</b> code needed to generate a <code>KiePMMLModel</code>
 */
public class KiePMMLModelCodegenUtils {

    private KiePMMLModelCodegenUtils() {
        // Avoid instantiation
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
        final String name = compilationDTO.getModelName();
        final String generatedClassName = compilationDTO.getSimpleClassName();
        final List<MiningField> miningFields = ModelUtils.convertToKieMiningFieldList(compilationDTO.getMiningSchema(),
                                                                                      compilationDTO.getFields());
        final List<OutputField> outputFields = ModelUtils.convertToKieOutputFieldList(compilationDTO.getOutput(),
                                                                                      compilationDTO.getFields());
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
                                   name,
                                   miningFields,
                                   outputFields);
        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, compilationDTO.getTransformationDictionary(),
                                                        compilationDTO.getLocalTransformations());
        final BlockStmt body = constructorDeclaration.getBody();
        CommonCodegenUtils.setAssignExpressionValue(body, "pmmlMODEL", pmmlMODELExpression);
        CommonCodegenUtils.setAssignExpressionValue(body, "miningFunction", miningFunctionExpression);
        CommonCodegenUtils.setAssignExpressionValue(body, "targetField", targetFieldExpression);
        if (compilationDTO.getOutput() != null) {
            addGetCreatedKiePMMLOutputFieldsMethod(modelTemplate, compilationDTO.getOutput().getOutputFields());
            MethodCallExpr getCreatedKiePMMLOutputFieldsExpr = new MethodCallExpr();
            getCreatedKiePMMLOutputFieldsExpr.setScope(new ThisExpr());
            getCreatedKiePMMLOutputFieldsExpr.setName(GET_CREATED_KIEPMMLOUTPUTFIELDS);
            CommonCodegenUtils.setAssignExpressionValue(body, "kiePMMLOutputFields", getCreatedKiePMMLOutputFieldsExpr);
        }
    }
}
