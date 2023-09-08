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
package org.kie.pmml.models.mining.compiler.factories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.dmg.pmml.Field;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.Model;
import org.dmg.pmml.Output;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.mining.Segment;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.mining.compiler.dto.MiningModelCompilationDTO;
import org.kie.pmml.models.mining.compiler.dto.SegmentCompilationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLPredicateFactory.getKiePMMLPredicate;
import static org.kie.pmml.compiler.commons.factories.KiePMMLFactoryFactory.getInstantiationExpression;
import static org.kie.pmml.compiler.commons.implementations.KiePMMLModelRetriever.getFromCommonDataAndTransformationDictionaryAndModelWithSources;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;

public class KiePMMLSegmentFactory {

    static final String KIE_PMML_SEGMENT_TEMPLATE_JAVA = "KiePMMLSegmentTemplate.tmpl";
    static final String KIE_PMML_SEGMENT_TEMPLATE = "KiePMMLSegmentTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSegmentFactory.class.getName());
    private static final String GET_SEGMENTS = "getSegments {}";
    private static final String GET_SEGMENT = "getSegment {}";
    private static final String GET_PREDICATE = "getPredicate";
    private static final String PREDICATE = "predicate";

    private KiePMMLSegmentFactory() {
    }

    public static Map<String, String> getSegmentsSourcesMap(final MiningModelCompilationDTO compilationDTO,
                                                            final List<KiePMMLModel> nestedModels) {
        final List<Segment> segments = compilationDTO.getModel().getSegmentation().getSegments();
        logger.debug(GET_SEGMENTS, segments);
        final Map<String, String> toReturn = new HashMap<>();
        segments.forEach(segment -> {
            final SegmentCompilationDTO segmentCompilationDTO =
                    SegmentCompilationDTO.fromGeneratedPackageNameAndFields(compilationDTO, segment,
                                                                            compilationDTO.getFields());
            toReturn.putAll(getSegmentSourcesMap(segmentCompilationDTO, nestedModels));
            compilationDTO.addFields(segmentCompilationDTO.getFields());
        });

        return toReturn;
    }

    public static Map<String, String> getSegmentSourcesMap(final SegmentCompilationDTO segmentCompilationDTO,
                                                           final List<KiePMMLModel> nestedModels) {
        logger.debug(GET_SEGMENT, segmentCompilationDTO.getSegment());
        final KiePMMLModel nestedModel =
                getFromCommonDataAndTransformationDictionaryAndModelWithSources(segmentCompilationDTO)
                        .orElseThrow(() -> new KiePMMLException("Failed to get the KiePMMLModel for segment " + segmentCompilationDTO.getModel().getModelName()));
        final Map<String, String> toReturn = getSegmentSourcesMapCommon(segmentCompilationDTO, nestedModels,
                                                                        nestedModel);
        segmentCompilationDTO.addFields(getFieldsFromModel(segmentCompilationDTO.getModel()));
        return toReturn;
    }

    static Map<String, String> getSegmentSourcesMapCommon(
            final SegmentCompilationDTO segmentCompilationDTO,
            final List<KiePMMLModel> nestedModels,
            final KiePMMLModel nestedModel) {
        logger.debug(GET_SEGMENT, segmentCompilationDTO.getSegment());
        if (!(nestedModel instanceof HasSourcesMap)) {
            throw new KiePMMLException("Retrieved KiePMMLModel for segment " + segmentCompilationDTO.getModel().getModelName() + " " +
                                               "does not implement HasSources");
        }
        nestedModels.add(nestedModel);
        return getSegmentSourcesMap(segmentCompilationDTO, ((HasSourcesMap) nestedModel).isInterpreted());
    }

    static Map<String, String> getSegmentSourcesMap(final SegmentCompilationDTO segmentCompilationDTO,
                                                    final boolean isInterpreted) {
        logger.debug(GET_SEGMENT, segmentCompilationDTO.getSegment());
        String kiePMMLModelClass = segmentCompilationDTO.getPackageCanonicalClassName();
        final String className = getSanitizedClassName(segmentCompilationDTO.getId());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className,
                                                                                 segmentCompilationDTO.getPackageName(),
                                                                                 KIE_PMML_SEGMENT_TEMPLATE_JAVA,
                                                                                 KIE_PMML_SEGMENT_TEMPLATE);
        ClassOrInterfaceDeclaration segmentTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration =
                segmentTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, segmentTemplate.getName())));
        final Map<String, String> toReturn = new HashMap<>();

        setConstructor(segmentCompilationDTO.getId(), className, constructorDeclaration, kiePMMLModelClass,
                       isInterpreted,
                       segmentCompilationDTO.getWeight().doubleValue());
        populateGetPredicateMethod(segmentCompilationDTO.getPredicate(),
                                   segmentCompilationDTO.getFields(),
                                   segmentTemplate);
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final String segmentName,
                               final String generatedClassName,
                               final ConstructorDeclaration constructorDeclaration,
                               final String kiePMMLModelClass,
                               final boolean isInterpreted,
                               final double weight) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, segmentName);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement =
                CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                        .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        final Expression instantiationExpression = getInstantiationExpression(kiePMMLModelClass, isInterpreted);
        String modelInstantiationString = instantiationExpression.toString();

        CommonCodegenUtils.setExplicitConstructorInvocationStmtArgument(superStatement, "model",
                                                                        modelInstantiationString);
        CommonCodegenUtils.setAssignExpressionValue(body, "weight", new DoubleLiteralExpr(weight));
        CommonCodegenUtils.setAssignExpressionValue(body, "id", new StringLiteralExpr(segmentName));
    }

    static void populateGetPredicateMethod(final Predicate predicate,
                                           final List<Field<?>> fields,
                                           final ClassOrInterfaceDeclaration segmentTemplate) {
        BlockStmt toSet = getKiePMMLPredicate(PREDICATE, predicate, fields);
        toSet.addStatement(new ReturnStmt(PREDICATE));
        MethodDeclaration methodDeclaration = segmentTemplate.getMethodsByName(GET_PREDICATE).get(0);
        methodDeclaration.setBody(toSet);
    }

    static List<Field<?>> getFieldsFromModel(final Model model) {
        final List<Field<?>> toReturn = new ArrayList<>();
        LocalTransformations localTransformations = model.getLocalTransformations();
        if (localTransformations != null && localTransformations.hasDerivedFields()) {
            localTransformations.getDerivedFields().stream().map(Field.class::cast)
                    .forEach(toReturn::add);
        }
        Output output = model.getOutput();
        if (output != null && output.hasOutputFields()) {
            output.getOutputFields().stream().map(Field.class::cast)
                    .forEach(toReturn::add);
        }
        return toReturn;
    }
}
