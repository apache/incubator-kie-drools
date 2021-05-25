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
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.Model;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.ModelUtils;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addKiePMMLOutputFieldsPopulation;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setKiePMMLModelConstructor;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;

public class KiePMMLModelConstructorBuilder {

    private final ClassOrInterfaceDeclaration modelTemplate;
    private final ConstructorDeclaration constructorDeclaration;
    private final DataDictionary dataDictionary;
    private final String generatedClassName;
    private final String name;
    private List<MiningField> miningFields;
    private List<OutputField> outputFields;
    private TransformationDictionary transformationDictionary;
    private LocalTransformations localTransformations;
    private List<KiePMMLOutputField> kiePMMLOutputFields;
    private String targetField;
    private Expression miningFunctionExpression;
    private String pmmlMODEL;


    public static KiePMMLModelConstructorBuilder get(final ClassOrInterfaceDeclaration modelTemplate,
                                                     final DataDictionary dataDictionary,
                                                     final TransformationDictionary transformationDictionary,
                                                     final Model pmmlModel) {
        return new KiePMMLModelConstructorBuilder(modelTemplate, dataDictionary, transformationDictionary, pmmlModel);
    }

    private KiePMMLModelConstructorBuilder(final ClassOrInterfaceDeclaration modelTemplate,
                                           final DataDictionary dataDictionary,
                                           final TransformationDictionary transformationDictionary,
                                           final Model pmmlModel) {
        this.modelTemplate = modelTemplate;
        this.constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        this.name = pmmlModel.getModelName();
        this.generatedClassName = getSanitizedClassName(name);
        this.dataDictionary = dataDictionary;
        this.miningFields = ModelUtils.convertToKieMiningFieldList(pmmlModel.getMiningSchema(), dataDictionary);
        this.outputFields = ModelUtils.convertToKieOutputFieldList(pmmlModel.getOutput(), dataDictionary);
        this.transformationDictionary = transformationDictionary;
        this.localTransformations = pmmlModel.getLocalTransformations();
        this.kiePMMLOutputFields = getOutputFields(pmmlModel);
        if (pmmlModel.getMiningFunction() != null) {
            MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(pmmlModel.getMiningFunction().value());
            this.miningFunctionExpression = new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name());
        } else {
            this.miningFunctionExpression = new NullLiteralExpr();
        }
        final PMML_MODEL pmmlModelEnum = PMML_MODEL.byName(pmmlModel.getClass().getSimpleName());
        this.pmmlMODEL = pmmlModelEnum.getClass().getName() + "." + pmmlModelEnum.name();
        this.targetField = getTargetFieldName(dataDictionary, pmmlModel).orElse(null);
    }

    public void build() {
        setKiePMMLModelConstructor(generatedClassName, constructorDeclaration, name, miningFields, outputFields);
        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, transformationDictionary, localTransformations);
        final BlockStmt body = constructorDeclaration.getBody();
        CommonCodegenUtils.setAssignExpressionValue(body, "pmmlMODEL",  new NameExpr(pmmlMODEL));
        CommonCodegenUtils.setAssignExpressionValue(body, "miningFunction", miningFunctionExpression);
        if (kiePMMLOutputFields != null) {
            addKiePMMLOutputFieldsPopulation(body, kiePMMLOutputFields);
        }
        if (targetField != null) {
            CommonCodegenUtils.setAssignExpressionValue(body, "targetField", new StringLiteralExpr(targetField));
        }

    }

}
