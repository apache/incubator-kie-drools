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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.mining.Segmentation;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.mining.model.enums.MULTIPLE_MODEL_METHOD;
import org.kie.pmml.models.mining.model.segmentation.KiePMMLSegmentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLExtensionFactory.getKiePMMLExtensions;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentFactory.getSegments;
import static org.kie.pmml.models.mining.compiler.factories.KiePMMLSegmentFactory.getSegmentsSourcesMap;

public class KiePMMLSegmentationFactory {

    static final String KIE_PMML_SEGMENTATION_TEMPLATE_JAVA = "KiePMMLSegmentationTemplate.tmpl";
    static final String KIE_PMML_SEGMENTATION_TEMPLATE = "KiePMMLSegmentationTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSegmentationFactory.class.getName());

    private KiePMMLSegmentationFactory() {
    }

    public static KiePMMLSegmentation getSegmentation(final DataDictionary dataDictionary,
                                                      final TransformationDictionary transformationDictionary,
                                                      final Segmentation segmentation,
                                                      final String segmentationName,
                                                      final String parentPackageName,
                                                      final HasClassLoader hasClassloader) {
        logger.debug("getSegmentation {}", segmentation);
        final String packageName = getSanitizedPackageName(parentPackageName + "." + segmentationName);
        return KiePMMLSegmentation.builder(segmentationName,
                                           getKiePMMLExtensions(segmentation.getExtensions()),
                                           MULTIPLE_MODEL_METHOD.byName(segmentation.getMultipleModelMethod().value()))
                .withSegments(getSegments(packageName,
                                          dataDictionary,
                                          transformationDictionary,
                                          segmentation.getSegments(),
                                          hasClassloader))
                .build();
    }

    public static Map<String, String> getSegmentationSourcesMap(final String parentPackageName,
                                                                final DataDictionary dataDictionary,
                                                                final TransformationDictionary transformationDictionary,
                                                                final Segmentation segmentation,
                                                                final String segmentationName,
                                                                final HasClassLoader hasClassloader,
                                                                final List<KiePMMLModel> nestedModels) {
        logger.debug("getSegmentationSourcesMap {}", segmentation);
        final String packageName = getSanitizedPackageName(parentPackageName + "." + segmentationName);
        final Map<String, String> toReturn = getSegmentsSourcesMap(packageName,
                                                                   dataDictionary,
                                                                   transformationDictionary,
                                                                   segmentation.getSegments(),
                                                                   hasClassloader,
                                                                   nestedModels);
        String className = getSanitizedClassName(segmentationName);
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_SEGMENTATION_TEMPLATE_JAVA,
                                                                                 KIE_PMML_SEGMENTATION_TEMPLATE);
        ClassOrInterfaceDeclaration segmentationTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration =
                segmentationTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, segmentationName)));
        Set<String> segmentsClasses = segmentation.getSegments().stream()
                .map(segment -> getSanitizedPackageName(packageName + "." + segment.getId()) + "." + getSanitizedClassName(segment.getId()))
                .collect(Collectors.toSet());
        if (!toReturn.keySet().containsAll(segmentsClasses)) {
            String missingClasses = String.join(", ", segmentsClasses);
            throw new KiePMMLException("Expected generated class " + missingClasses + " not found");
        }
        setConstructor(className,
                       segmentationName,
                       constructorDeclaration,
                       MULTIPLE_MODEL_METHOD.byName(segmentation.getMultipleModelMethod().value()),
                       segmentsClasses);
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final String generatedClassName,
                               final String segmentationName,
                               final ConstructorDeclaration constructorDeclaration,
                               final MULTIPLE_MODEL_METHOD multipleModelMethod,
                               final Set<String> segmentsClasses) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, segmentationName);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        CommonCodegenUtils.setExplicitConstructorInvocationArgument(superStatement, "multipleModelMethod",
                                                                    multipleModelMethod.getClass().getCanonicalName() + "." + multipleModelMethod.name());
        final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
        assignExprs.forEach(assignExpr -> {
            if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("segments")) {
                for (String segmentClass : segmentsClasses) {
                    ClassOrInterfaceType kiePMMLSegmentClass = parseClassOrInterfaceType(segmentClass);
                    ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
                    objectCreationExpr.setType(kiePMMLSegmentClass);
                    NodeList<Expression> arguments = NodeList.nodeList(objectCreationExpr);
                    MethodCallExpr methodCallExpr = new MethodCallExpr();
                    methodCallExpr.setScope(assignExpr.getTarget().asNameExpr());
                    methodCallExpr.setName("add");
                    methodCallExpr.setArguments(arguments);
                    ExpressionStmt expressionStmt = new ExpressionStmt();
                    expressionStmt.setExpression(methodCallExpr);
                    body.addStatement(expressionStmt);
                }
            }
        });
    }
}
