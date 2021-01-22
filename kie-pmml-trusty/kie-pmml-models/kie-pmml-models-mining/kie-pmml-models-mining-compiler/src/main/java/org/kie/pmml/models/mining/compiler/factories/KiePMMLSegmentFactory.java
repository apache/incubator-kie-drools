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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.Segment;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.getPredicate;
import static org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory.getPredicateSourcesMap;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModel;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModelWithSources;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;

public class KiePMMLSegmentFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSegmentFactory.class.getName());
    static final String KIE_PMML_SEGMENT_TEMPLATE_JAVA = "KiePMMLSegmentTemplate.tmpl";
    static final String KIE_PMML_SEGMENT_TEMPLATE = "KiePMMLSegmentTemplate";
    private static final String GET_SEGMENTS= "getSegments {}";
    private static final String GET_SEGMENT= "getSegment {}";

    private KiePMMLSegmentFactory() {
    }

    public static List<KiePMMLSegment> getSegments(final String parentPackageName,
                                                   final DataDictionary dataDictionary,
                                                   final TransformationDictionary transformationDictionary,
                                                   final List<Segment> segments,
                                                   final HasClassLoader hasClassloader) {
        logger.debug(GET_SEGMENTS, segments);
        return segments.stream().map(segment -> getSegment(parentPackageName,
                                                           dataDictionary,
                                                           transformationDictionary,
                                                           segment,
                                                           hasClassloader)).collect(Collectors.toList());
    }

    public static KiePMMLSegment getSegment(final String parentPackageName,
                                            final DataDictionary dataDictionary,
                                            final TransformationDictionary transformationDictionary,
                                            final Segment segment,
                                            final HasClassLoader hasClassloader) {
        logger.debug(GET_SEGMENT, segment);
        final String packageName = getSanitizedPackageName(parentPackageName + "." + segment.getId());
        return KiePMMLSegment.builder(segment.getId(),
                                      getKiePMMLExtensions(segment.getExtensions()),
                                      getPredicate(segment.getPredicate(), dataDictionary),
                                      getFromCommonDataAndTransformationDictionaryAndModel(packageName,
                                                                                           dataDictionary,
                                                                                           transformationDictionary,
                                                                                           segment.getModel(),
                                                                                           hasClassloader).orElseThrow(() -> new KiePMMLException("Failed to get the KiePMMLModel for segment " + segment.getModel().getModelName())))
                .withWeight(segment.getWeight().doubleValue())
                .build();
    }

    public static Map<String, String> getSegmentsSourcesMap(final String parentPackageName,
                                                            final DataDictionary dataDictionary,
                                                            final TransformationDictionary transformationDictionary,
                                                            final List<Segment> segments,
                                                            final HasClassLoader hasClassloader,
                                                            final List<KiePMMLModel> nestedModels) {
        logger.debug(GET_SEGMENTS, segments);
        final Map<String, String> toReturn = new HashMap<>();
        segments.forEach(segment -> toReturn.putAll(getSegmentSourcesMap(parentPackageName,
                                                                         dataDictionary,
                                                                         transformationDictionary, segment,
                                                                         hasClassloader,
                                                                         nestedModels)));

        return toReturn;
    }

    public static Map<String, String> getSegmentSourcesMap(
            final String parentPackageName,
            final DataDictionary dataDictionary,
            final TransformationDictionary transformationDictionary,
            final Segment segment,
            final HasClassLoader hasClassloader,
            final List<KiePMMLModel> nestedModels) {
        logger.debug(GET_SEGMENT, segment);
        final String packageName = getSanitizedPackageName(parentPackageName + "." + segment.getId());
        final KiePMMLModel nestedModel = getFromCommonDataAndTransformationDictionaryAndModelWithSources(
                packageName,
                dataDictionary,
                transformationDictionary,
                segment.getModel(),
                hasClassloader)
                .orElseThrow(() -> new KiePMMLException("Failed to get the KiePMMLModel for segment " + segment.getModel().getModelName()));
        if (!(nestedModel instanceof HasSourcesMap)) {
            throw new KiePMMLException("Retrieved KiePMMLModel for segment " + segment.getModel().getModelName() + " " +
                                               "does not implement HasSources");
        }
        nestedModels.add(nestedModel);
        return getSegmentSourcesMap(packageName, dataDictionary, segment);
    }

    public static Map<String, String> getSegmentSourcesMap(
            final String packageName,
            final DataDictionary dataDictionary,
            final Segment segment) {
        logger.debug(GET_SEGMENT, segment);
        String kiePMMLModelClass = packageName + "." + getSanitizedClassName(segment.getModel().getModelName());
        final String className = getSanitizedClassName(segment.getId());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName, KIE_PMML_SEGMENT_TEMPLATE_JAVA, KIE_PMML_SEGMENT_TEMPLATE);
        ClassOrInterfaceDeclaration segmentTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = segmentTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, segmentTemplate.getName())));
        KiePMMLPredicate predicate = getPredicate(segment.getPredicate(), dataDictionary);
        final Map<String, String> toReturn = new HashMap<>(getPredicateSourcesMap(predicate, packageName));
        String predicateClassName = packageName + "." +  getSanitizedClassName(predicate.getId());
        setConstructor(segment.getId(), className, constructorDeclaration, predicateClassName,  kiePMMLModelClass, segment.getWeight().doubleValue());
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }


    static void setConstructor(final String segmentName,
                               final String generatedClassName,
                               final ConstructorDeclaration constructorDeclaration,
                               final String predicateClassName,
                               final String kiePMMLModelClass,
                               final double weight) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, segmentName);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement = CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        ClassOrInterfaceType classOrInterfaceType = parseClassOrInterfaceType(predicateClassName);
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(classOrInterfaceType);
        CommonCodegenUtils.setExplicitConstructorInvocationArgument(superStatement, "kiePMMLPredicate", objectCreationExpr.toString());
        classOrInterfaceType = parseClassOrInterfaceType(kiePMMLModelClass);
        objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(classOrInterfaceType);
        CommonCodegenUtils.setExplicitConstructorInvocationArgument(superStatement, "model", objectCreationExpr.toString());
        CommonCodegenUtils.setAssignExpressionValue(body, "weight", new DoubleLiteralExpr(weight));
        CommonCodegenUtils.setAssignExpressionValue(body, "id", new StringLiteralExpr(segmentName));
    }
}
