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
package org.kie.pmml.models.mining.compiler.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.MiningModel;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.commons.builders.KiePMMLModelCodegenUtils;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.mining.model.KiePMMLMiningModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getDerivedFields;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentationFactory.getSegmentationSourcesMap;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentationFactory.getSegmentationSourcesMapCompiled;

public class KiePMMLMiningModelFactory {

    static final String SEGMENTATIONNAME_TEMPLATE = "%s_Segmentation";
    static final String KIE_PMML_MINING_MODEL_TEMPLATE_JAVA = "KiePMMLMiningModelTemplate.tmpl";
    static final String KIE_PMML_MINING_MODEL_TEMPLATE = "KiePMMLMiningModelTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLMiningModelFactory.class.getName());

    private KiePMMLMiningModelFactory() {
        // Avoid instantiation
    }

    public static KiePMMLMiningModel getKiePMMLMiningModel(final DataDictionary dataDictionary,
                                                           final TransformationDictionary transformationDictionary,
                                                           final MiningModel model,
                                                           final String packageName,
                                                           final HasClassLoader hasClassLoader) {
        logger.debug("getKiePMMLMiningModel {}", model);
        String className = getSanitizedClassName(model.getModelName());
        final List<KiePMMLModel> nestedModels = new ArrayList<>();
        Map<String, String> sourcesMap = getKiePMMLMiningModelSourcesMapCompiled(dataDictionary,
                                                                                 transformationDictionary,
                                                                                 model,
                                                                                 packageName,
                                                                                 hasClassLoader,
                                                                                 nestedModels);
        String fullClassName = packageName + "." + className;
        try {
            Class<?> kiePMMLMiningModel = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLMiningModel) kiePMMLMiningModel.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLMiningModelSourcesMap(final DataDictionary dataDictionary,
                                                                      final TransformationDictionary transformationDictionary,
                                                                      final MiningModel model,
                                                                      final String parentPackageName,
                                                                      final HasClassLoader hasClassloader,
                                                                      final List<KiePMMLModel> nestedModels) {
        logger.trace("getKiePMMLMiningModelSourcesMap {} {} {}", dataDictionary, model, parentPackageName);
        final String segmentationName = String.format(SEGMENTATIONNAME_TEMPLATE, model.getModelName());
        final List<DerivedField> derivedFields = getDerivedFields(transformationDictionary,
                                                                  model.getLocalTransformations());
        final Map<String, String> toReturn = getSegmentationSourcesMap(parentPackageName,
                                                                       derivedFields,
                                                                       dataDictionary,
                                                                       transformationDictionary,
                                                                       model.getSegmentation(),
                                                                       segmentationName,
                                                                       hasClassloader,
                                                                       nestedModels);
        return getKiePMMLMiningModelSourcesMapCommon(dataDictionary, transformationDictionary,
                                                     model,
                                                     parentPackageName,
                                                     toReturn);
    }

    public static Map<String, String> getKiePMMLMiningModelSourcesMapCompiled(final DataDictionary dataDictionary,
                                                                              final TransformationDictionary transformationDictionary,
                                                                              final MiningModel model,
                                                                              final String parentPackageName,
                                                                              final HasClassLoader hasClassloader,
                                                                              final List<KiePMMLModel> nestedModels) {
        logger.trace("getKiePMMLMiningModelSourcesMapCompiled {} {} {}", dataDictionary, model, parentPackageName);
        final String segmentationName = String.format(SEGMENTATIONNAME_TEMPLATE, model.getModelName());
        final List<DerivedField> derivedFields = getDerivedFields(transformationDictionary,
                                                                  model.getLocalTransformations());
        final Map<String, String> toReturn = getSegmentationSourcesMapCompiled(parentPackageName,
                                                                               derivedFields,
                                                                               dataDictionary,
                                                                               transformationDictionary,
                                                                               model.getSegmentation(),
                                                                               segmentationName,
                                                                               hasClassloader,
                                                                               nestedModels);
        return getKiePMMLMiningModelSourcesMapCommon(dataDictionary, transformationDictionary,
                                                     model,
                                                     parentPackageName,
                                                     toReturn);
    }

    static Map<String, String> getKiePMMLMiningModelSourcesMapCommon(final DataDictionary dataDictionary,
                                                                     final TransformationDictionary transformationDictionary,
                                                                     final MiningModel model,
                                                                     final String parentPackageName,
                                                                     final Map<String, String> toReturn) {
        logger.trace("getKiePMMLMiningModelSourcesMap {} {} {}", dataDictionary, model, parentPackageName);
        final String segmentationName = String.format(SEGMENTATIONNAME_TEMPLATE, model.getModelName());
        String segmentationClass =
                getSanitizedPackageName(parentPackageName + "." + segmentationName) + "." + getSanitizedClassName(segmentationName);
        if (!toReturn.containsKey(segmentationClass)) {
            throw new KiePMMLException("Expected generated class " + segmentationClass + " not found");
        }
        String className = getSanitizedClassName(model.getModelName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, parentPackageName,
                                                                                 KIE_PMML_MINING_MODEL_TEMPLATE_JAVA,
                                                                                 KIE_PMML_MINING_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        setConstructor(model,
                       dataDictionary,
                       transformationDictionary,
                       modelTemplate,
                       segmentationClass);
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final MiningModel miningModel,
                               final DataDictionary dataDictionary,
                               final TransformationDictionary transformationDictionary,
                               final ClassOrInterfaceDeclaration modelTemplate,
                               final String segmentationClass) {
        KiePMMLModelCodegenUtils.init(modelTemplate,
                                      dataDictionary,
                                      transformationDictionary,
                                      miningModel);
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        final BlockStmt body = constructorDeclaration.getBody();
        ClassOrInterfaceType kiePMMLSegmentationClass = parseClassOrInterfaceType(segmentationClass);
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(kiePMMLSegmentationClass);
        CommonCodegenUtils.setAssignExpressionValue(body, "segmentation", objectCreationExpr);
    }
}
