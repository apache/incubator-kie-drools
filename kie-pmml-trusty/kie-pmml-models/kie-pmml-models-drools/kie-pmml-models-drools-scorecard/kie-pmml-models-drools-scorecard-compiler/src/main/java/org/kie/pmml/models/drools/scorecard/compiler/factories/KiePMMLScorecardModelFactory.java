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
package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.scorecard.Scorecard;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.compiler.commons.builders.KiePMMLModelConstructorBuilder;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLOutputFieldFactory.getOutputFields;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.models.drools.utils.KiePMMLDroolsModelFactoryUtils.getKiePMMLModelCompilationUnit;

/**
 * Class used to generate <code>KiePMMLScorecard</code> out of a <code>DataDictionary</code> and a <code>ScorecardModel</code>
 */
public class KiePMMLScorecardModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelFactory.class.getName());

    static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA = "KiePMMLScorecardModelTemplate.tmpl";
    static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE = "KiePMMLScorecardModelTemplate";

    private KiePMMLScorecardModelFactory() {
        // Avoid instantiation
    }

    public static KiePMMLScorecardModel getKiePMMLScorecardModel(final DataDictionary dataDictionary,
                                                                 final TransformationDictionary transformationDictionary,
                                                                 final Scorecard model,
                                                                 final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                                 final String packageName,
                                                                 final HasClassLoader hasClassLoader) throws IllegalAccessException, InstantiationException {
        logger.trace("getKiePMMLScorecardModel {} {}", packageName, model);
        String className = getSanitizedClassName(model.getModelName());
        Map<String, String> sourcesMap = getKiePMMLScorecardModelSourcesMap(dataDictionary, transformationDictionary, model, fieldTypeMap, packageName);
        String fullClassName = packageName + "." + className;
        try {
            Class<?> kiePMMLScorecardModelClass = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLScorecardModel) kiePMMLScorecardModelClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLScorecardModelSourcesMap(final DataDictionary dataDictionary, final TransformationDictionary transformationDictionary, final Scorecard model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final String packageName) {
        logger.trace("getKiePMMLScorecardModelSourcesMap {} {} {}", dataDictionary, model, packageName);
        CompilationUnit cloneCU = getKiePMMLModelCompilationUnit(dataDictionary, model, fieldTypeMap, packageName, KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA, KIE_PMML_SCORECARD_MODEL_TEMPLATE);
        String className = getSanitizedClassName(model.getModelName());
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        String targetFieldName = getTargetFieldName(dataDictionary, model).orElse(null);
        setConstructor(model,
                       dataDictionary,
                       transformationDictionary,
                       modelTemplate,
                       targetFieldName);
        Map<String, String> toReturn = new HashMap<>();
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    /**
     * This method returns a <code>KiePMMLDroolsAST</code> out of the given <code>DataDictionary</code> and <code>Scorecard</code>.
     * <b>It also populate the given <code>Map</code> that has to be used for final <code>KiePMMLScorecardModel</code></b>
     *
     * @param dataDictionary
     * @param model
     * @param fieldTypeMap
     * @param types
     * @return
     */
    public static KiePMMLDroolsAST getKiePMMLDroolsAST(final DataDictionary dataDictionary,
                                                       final Scorecard model,
                                                       final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                       final List<KiePMMLDroolsType> types) {
        logger.trace("getKiePMMLDroolsAST {}", model);
        return KiePMMLScorecardModelASTFactory.getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap, types);
    }

    static void setConstructor(final Scorecard scorecard,
                               final DataDictionary dataDictionary,
                               final TransformationDictionary transformationDictionary,
                               final ClassOrInterfaceDeclaration modelTemplate,
                               final String targetField) {
        final List<KiePMMLOutputField> outputFields = getOutputFields(scorecard);
        Expression miningFunctionExpression;
        if (scorecard.getMiningFunction() != null) {
            MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(scorecard.getMiningFunction().value());
            miningFunctionExpression = new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name());
        } else {
            miningFunctionExpression = new NullLiteralExpr();
        }
        final KiePMMLModelConstructorBuilder builder = KiePMMLModelConstructorBuilder.get(modelTemplate,
                                                                                          getSanitizedClassName(scorecard.getModelName()),
                                                                                          scorecard.getModelName(),
                                                                                          dataDictionary)
                .withMiningFields(scorecard.getMiningSchema())
                .withOutputFields(scorecard.getOutput())
                .withTransformationDictionary(transformationDictionary)
                .withLocalTransformations(scorecard.getLocalTransformations())
                .withKiePMMLOutputFields(outputFields)
                .withTargetField(targetField)
                .withMiningFunction(miningFunctionExpression)
                .withPMMLModel(PMML_MODEL.SCORECARD_MODEL.name());
        builder.build();
    }
}