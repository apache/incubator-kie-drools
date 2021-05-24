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
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Output;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.ModelUtils;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addKiePMMLOutputFieldsPopulation;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setKiePMMLModelConstructor;

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
                                                     final String generatedClassName,
                                                     final String name,
                                                     final DataDictionary dataDictionary) {
        return new KiePMMLModelConstructorBuilder(modelTemplate, generatedClassName, name, dataDictionary);
    }

    private KiePMMLModelConstructorBuilder(final ClassOrInterfaceDeclaration modelTemplate,
                                           final String generatedClassName,
                                           final String name,
                                           final DataDictionary dataDictionary) {
        this.modelTemplate = modelTemplate;
        this.constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        this.generatedClassName = generatedClassName;
        this.name = name;
        this.dataDictionary = dataDictionary;
    }

    public KiePMMLModelConstructorBuilder withMiningFields(final MiningSchema miningSchema) {
        this.miningFields = ModelUtils.convertToKieMiningFieldList(miningSchema, dataDictionary);
        return this;
    }

    public KiePMMLModelConstructorBuilder withOutputFields(final Output output) {
        this.outputFields = ModelUtils.convertToKieOutputFieldList(output, dataDictionary);
        return this;
    }

    public KiePMMLModelConstructorBuilder withTransformationDictionary(final TransformationDictionary transformationDictionary) {
        this.transformationDictionary = transformationDictionary;
        return this;
    }

    public KiePMMLModelConstructorBuilder withLocalTransformations(final LocalTransformations localTransformations) {
        this.localTransformations = localTransformations;
        return this;
    }

    public KiePMMLModelConstructorBuilder withKiePMMLOutputFields(final List<KiePMMLOutputField> kiePMMLOutputFields) {
        this.kiePMMLOutputFields = kiePMMLOutputFields;
        return this;
    }

    public KiePMMLModelConstructorBuilder withTargetField(final String targetField) {
        this.targetField = targetField;
        return this;
    }

    public KiePMMLModelConstructorBuilder withMiningFunction(final Expression miningFunctionExpression) {
        this.miningFunctionExpression = miningFunctionExpression;
        return this;
    }

    public KiePMMLModelConstructorBuilder withPMMLModel(final String pmmlModel) {
        this.pmmlMODEL = PMML_MODEL.MINING_MODEL.getClass().getName() + "." + pmmlModel;
        return this;
    }

    public void build() {
        setKiePMMLModelConstructor(generatedClassName, constructorDeclaration, name, miningFields, outputFields);
        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, transformationDictionary, localTransformations);
        final BlockStmt body = constructorDeclaration.getBody();
        if (kiePMMLOutputFields != null) {
            addKiePMMLOutputFieldsPopulation(body, kiePMMLOutputFields);
        }
        if (targetField != null) {
            CommonCodegenUtils.setAssignExpressionValue(body, "targetField", new StringLiteralExpr(targetField));
        }
        if (miningFunctionExpression != null) {
            CommonCodegenUtils.setAssignExpressionValue(body, "miningFunction", miningFunctionExpression);
        }
        if (pmmlMODEL != null) {
            CommonCodegenUtils.setAssignExpressionValue(body, "pmmlMODEL",  new NameExpr(pmmlMODEL));
        }
    }

}
