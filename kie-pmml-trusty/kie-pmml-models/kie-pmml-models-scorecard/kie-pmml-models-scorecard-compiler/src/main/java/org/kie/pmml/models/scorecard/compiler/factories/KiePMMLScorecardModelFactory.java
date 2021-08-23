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
package org.kie.pmml.models.scorecard.compiler.factories;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.scorecard.Scorecard;
import org.kie.pmml.api.enums.REASONCODE_ALGORITHM;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.utils.KiePMMLModelUtils;
import org.kie.pmml.compiler.commons.builders.KiePMMLModelCodegenUtils;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.scorecard.model.KiePMMLScorecardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getDerivedFields;
import static org.kie.pmml.models.scorecard.compiler.factories.KiePMMLCharacteristicsFactory.getKiePMMLCharacteristicsSourcesMap;

public class KiePMMLScorecardModelFactory {

    static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA = "KiePMMLScorecardModelTemplate.tmpl";
    static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE = "KiePMMLScorecardModelTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelFactory.class.getName());

    private KiePMMLScorecardModelFactory() {
        // Avoid instantiation
    }

    public static KiePMMLScorecardModel getKiePMMLScorecardModel(final DataDictionary dataDictionary,
                                                                 final TransformationDictionary transformationDictionary,
                                                                 final Scorecard model,
                                                                 final String packageName,
                                                                 final HasClassLoader hasClassLoader) {
        String className = getSanitizedClassName(model.getModelName());
        Map<String, String> sourcesMap = getKiePMMLScorecardModelSourcesMap(dataDictionary, transformationDictionary,
                                                                            model, packageName);
        String fullClassName = packageName + "." + className;
        try {
            Class<?> kiePMMLScorecardModelClass = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLScorecardModel) kiePMMLScorecardModelClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLScorecardModelSourcesMap(final DataDictionary dataDictionary,
                                                                         final TransformationDictionary transformationDictionary,
                                                                         final Scorecard model,
                                                                         final String packageName) {
        String className = getSanitizedClassName(model.getModelName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA, KIE_PMML_SCORECARD_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        String characteristicsClassName = KiePMMLModelUtils.getGeneratedClassName("Characteristics");
        String fullCharacteristicsClassName = String.format(PACKAGE_CLASS_TEMPLATE, packageName,
                                                            characteristicsClassName);
        final List<DerivedField> derivedFields = getDerivedFields(transformationDictionary,
                                                                  model.getLocalTransformations());
        Map<String, String> toReturn = getKiePMMLCharacteristicsSourcesMap(model.getCharacteristics(), derivedFields,
                                                                           dataDictionary, characteristicsClassName,
                                                                           packageName);
        setConstructor(model,
                       dataDictionary,
                       transformationDictionary,
                       modelTemplate,
                       fullCharacteristicsClassName);
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final Scorecard scorecardModel,
                               final DataDictionary dataDictionary,
                               final TransformationDictionary transformationDictionary,
                               final ClassOrInterfaceDeclaration modelTemplate,
                               final String fullCharacteristicsClassName) {
        KiePMMLModelCodegenUtils.init(modelTemplate,
                                      dataDictionary,
                                      transformationDictionary,
                                      scorecardModel);
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        ClassOrInterfaceType characteristicsClass = parseClassOrInterfaceType(fullCharacteristicsClassName);
        ObjectCreationExpr characteristicsReference = new ObjectCreationExpr();
        characteristicsReference.setType(characteristicsClass);
        superStatement.setArgument(2, characteristicsReference);
        superStatement.setArgument(3, getExpressionForObject(scorecardModel.getInitialScore()));
        superStatement.setArgument(4, getExpressionForObject(scorecardModel.isUseReasonCodes()));
        REASONCODE_ALGORITHM reasoncodeAlgorithm =
                REASONCODE_ALGORITHM.byName(scorecardModel.getReasonCodeAlgorithm().value());
        NameExpr reasonCodeExpr = new NameExpr(REASONCODE_ALGORITHM.class.getName() + "." + reasoncodeAlgorithm.name());
        superStatement.setArgument(5, reasonCodeExpr);
        superStatement.setArgument(6, getExpressionForObject(scorecardModel.getBaselineScore()));
    }
}
