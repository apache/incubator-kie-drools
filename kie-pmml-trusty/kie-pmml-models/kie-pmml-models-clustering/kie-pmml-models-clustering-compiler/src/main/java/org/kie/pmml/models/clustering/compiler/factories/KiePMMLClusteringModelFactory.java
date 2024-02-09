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
package org.kie.pmml.models.clustering.compiler.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Array;
import org.dmg.pmml.ComparisonMeasure;
import org.dmg.pmml.clustering.Cluster;
import org.dmg.pmml.clustering.ClusteringField;
import org.dmg.pmml.clustering.ClusteringModel;
import org.dmg.pmml.clustering.MissingValueWeights;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.clustering.compiler.dto.ClusteringCompilationDTO;
import org.kie.pmml.models.clustering.model.KiePMMLCluster;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringField;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;
import org.kie.pmml.models.clustering.model.KiePMMLCompareFunction;
import org.kie.pmml.models.clustering.model.KiePMMLComparisonMeasure;
import org.kie.pmml.models.clustering.model.KiePMMLMissingValueWeights;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.GET_MODEL;
import static org.kie.pmml.commons.Constants.MISSING_METHOD_IN_CLASS;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.TO_RETURN;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.createArraysAsListFromList;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getMethodDeclarationBlockStmt;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.literalExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.populateListInListGetter;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringConversionUtils.aggregateFunctionFrom;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringConversionUtils.compareFunctionFrom;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringConversionUtils.comparisonMeasureKindFrom;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringConversionUtils.modelClassFrom;

public class KiePMMLClusteringModelFactory {

    static final String KIE_PMML_CLUSTERING_MODEL_TEMPLATE_JAVA = "KiePMMLClusteringModelTemplate.tmpl";
    static final String KIE_PMML_CLUSTERING_MODEL_TEMPLATE = "KiePMMLClusteringModelTemplate";
    static final String GET_CLUSTERS = "getClusters";
    static final String GET_CLUSTERING_FIELDS = "getClusteringFields";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLClusteringModelFactory.class.getName());

    private KiePMMLClusteringModelFactory() {
        // Avoid instantiation
    }

    //  KiePMMLClusteringModel instantiation

    public static KiePMMLClusteringModel getKiePMMLClusteringModel(final ClusteringCompilationDTO compilationDTO) {
        logger.trace("getKiePMMLClusteringModel {}", compilationDTO);
        try {
            ClusteringModel clusteringModel = compilationDTO.getModel();
            final KiePMMLClusteringModel.ModelClass modelClass = modelClassFrom(clusteringModel.getModelClass());
            final List<KiePMMLCluster> clusters = getKiePMMLClusters(clusteringModel.getClusters());
            final List<KiePMMLClusteringField> clusteringFields = getKiePMMLClusteringFields(clusteringModel.getClusteringFields());
            final KiePMMLComparisonMeasure comparisonMeasure = getKiePMMLComparisonMeasure(clusteringModel.getComparisonMeasure());
            final KiePMMLMissingValueWeights missingValueWeights = getKiePMMLMissingValueWeights(clusteringModel.getMissingValueWeights());

            return KiePMMLClusteringModel.builder(compilationDTO.getFileName(), compilationDTO.getModelName(), compilationDTO.getMINING_FUNCTION())
                    .withModelClass(modelClass)
                    .withClusters(clusters)
                    .withClusteringFields(clusteringFields)
                    .withComparisonMeasure(comparisonMeasure)
                    .withMissingValueWeights(missingValueWeights)
                    .withTargetField(compilationDTO.getTargetFieldName())
                    .withMiningFields(compilationDTO.getKieMiningFields())
                    .withOutputFields(compilationDTO.getKieOutputFields())
                    .withKiePMMLMiningFields(compilationDTO.getKiePMMLMiningFields())
                    .withKiePMMLOutputFields(compilationDTO.getKiePMMLOutputFields())
                    .withKiePMMLTargets(compilationDTO.getKiePMMLTargetFields())
                    .withKiePMMLTransformationDictionary(compilationDTO.getKiePMMLTransformationDictionary())
                    .withKiePMMLLocalTransformations(compilationDTO.getKiePMMLLocalTransformations())
                    .build();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    // Source code generation

    public static Map<String, String> getKiePMMLClusteringModelSourcesMap(final ClusteringCompilationDTO compilationDTO) {

        logger.trace("getKiePMMLClusteringModelSourcesMap {}", compilationDTO);

        String simpleClassName = compilationDTO.getSimpleClassName();

        CompilationUnit compilationUnit = JavaParserUtils.getKiePMMLModelCompilationUnit(simpleClassName,
                                                                                         compilationDTO.getPackageName(),
                                                                                         KIE_PMML_CLUSTERING_MODEL_TEMPLATE_JAVA,
                                                                                         KIE_PMML_CLUSTERING_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = compilationUnit.getClassByName(simpleClassName)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + simpleClassName));
        setStaticGetter(compilationDTO, modelTemplate);
        populateGetClustersMethod(modelTemplate, compilationDTO.getModel());
        populateGetClusteringFieldsMethod(modelTemplate, compilationDTO.getModel());

        Map<String, String> sourcesMap = new HashMap<>();
        sourcesMap.put(getFullClassName(compilationUnit), compilationUnit.toString());

        return sourcesMap;
    }

    //  not-public KiePMMLClusteringModel instantiation

    static List<KiePMMLCluster> getKiePMMLClusters(List<Cluster> clusters) {
        return clusters != null ?
                clusters.stream().map(KiePMMLClusteringModelFactory::getKiePMMLCluster).collect(Collectors.toList())
                : Collections.emptyList();
    }

    static KiePMMLCluster getKiePMMLCluster(Cluster cluster) {
        final List<Double> values = getClusterDoubleValues(cluster);
        return new KiePMMLCluster(cluster.getId(), cluster.getName(), values);
    }

    static List<KiePMMLClusteringField> getKiePMMLClusteringFields(List<ClusteringField> clusteringFields) {
        return clusteringFields != null ?
                clusteringFields.stream().map(KiePMMLClusteringModelFactory::getKiePMMLClusteringField).collect(Collectors.toList())
                : Collections.emptyList();
    }

    static KiePMMLClusteringField getKiePMMLClusteringField(ClusteringField clusteringField) {
        double fieldWeight = clusteringField.getFieldWeight() == null ? 1.0 :
                clusteringField.getFieldWeight().doubleValue();
        boolean isCenterField =
                clusteringField.getCenterField() == null || clusteringField.getCenterField() == ClusteringField.CenterField.TRUE;
        KiePMMLCompareFunction kiePMMLCompareFunction = clusteringField.getCompareFunction() != null ? compareFunctionFrom(clusteringField.getCompareFunction()) : null;
        return new KiePMMLClusteringField(clusteringField.getField(), fieldWeight, isCenterField,
                                          kiePMMLCompareFunction, null);
    }

    static KiePMMLComparisonMeasure getKiePMMLComparisonMeasure(ComparisonMeasure comparisonMeasure) {
        return new KiePMMLComparisonMeasure(comparisonMeasureKindFrom(comparisonMeasure.getKind()),
                                            aggregateFunctionFrom(comparisonMeasure.getMeasure()),
                                            compareFunctionFrom(comparisonMeasure.getCompareFunction()));

    }

    static KiePMMLMissingValueWeights getKiePMMLMissingValueWeights(MissingValueWeights missingValueWeights) {
        return missingValueWeights != null ? new KiePMMLMissingValueWeights(getMissingValueWeightsDoubleValues(missingValueWeights)) : null;
    }

    // not-public code-generation

    static void setStaticGetter(final CompilationDTO<ClusteringModel> compilationDTO,
                                final ClassOrInterfaceDeclaration modelTemplate) {
        KiePMMLModelFactoryUtils.initStaticGetter(compilationDTO, modelTemplate);
        final BlockStmt body = getMethodDeclarationBlockStmt(modelTemplate, GET_MODEL);
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(body, TO_RETURN).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TO_RETURN, body)));

        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      TO_RETURN, body)))
                .asMethodCallExpr();

        ClusteringModel clusteringModel = compilationDTO.getModel();
        KiePMMLClusteringModel.ModelClass modelClass = modelClassFrom(clusteringModel.getModelClass());

        getChainedMethodCallExprFrom("withModelClass", initializer).setArgument(0, literalExprFrom(modelClass));
        getChainedMethodCallExprFrom("withComparisonMeasure", initializer).setArgument(0,
                                                                                       comparisonMeasureCreationExprFrom(clusteringModel.getComparisonMeasure()));
        Expression missingValueWeights = clusteringModel.getMissingValueWeights() != null ?
                missingValueWeightsCreationExprFrom(clusteringModel.getMissingValueWeights()) : new NullLiteralExpr();

        getChainedMethodCallExprFrom("withMissingValueWeights", initializer).setArgument(0, missingValueWeights);
    }

    static void populateGetClustersMethod(final ClassOrInterfaceDeclaration toPopulate,
                                          final ClusteringModel clusteringModel) {
        MethodDeclaration methodDeclaration = getMethodDeclaration(toPopulate, GET_CLUSTERS)
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_METHOD_IN_CLASS, toPopulate,
                                                                              GET_CLUSTERS)));
        List<ObjectCreationExpr> objectCreationExprStream = clusteringModel.getClusters().stream()
                .map(KiePMMLClusteringModelFactory::clusterCreationExprFrom)
                .collect(Collectors.toList());
        populateListInListGetter(objectCreationExprStream, methodDeclaration, TO_RETURN);
    }

    static void populateGetClusteringFieldsMethod(final ClassOrInterfaceDeclaration toPopulate,
                                                  final ClusteringModel clusteringModel) {
        MethodDeclaration methodDeclaration = getMethodDeclaration(toPopulate, GET_CLUSTERING_FIELDS)
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_METHOD_IN_CLASS, toPopulate,
                                                                              GET_CLUSTERING_FIELDS)));
        List<ObjectCreationExpr> objectCreationExprStream = clusteringModel.getClusteringFields().stream()
                .map(KiePMMLClusteringModelFactory::clusteringFieldCreationExprFrom)
                .collect(Collectors.toList());
        populateListInListGetter(objectCreationExprStream, methodDeclaration, TO_RETURN);
    }

    private static ObjectCreationExpr clusterCreationExprFrom(Cluster cluster) {
        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(literalExprFrom(cluster.getId()));
        arguments.add(literalExprFrom(cluster.getName()));
        final List<Double> values = getClusterDoubleValues(cluster);
        arguments.add(createArraysAsListFromList(values).getExpression());
        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, KiePMMLCluster.class.getCanonicalName()),
                                      arguments);
    }

    private static ObjectCreationExpr clusteringFieldCreationExprFrom(ClusteringField clusteringField) {
        double fieldWeight = clusteringField.getFieldWeight() == null ? 1.0 :
                clusteringField.getFieldWeight().doubleValue();
        boolean isCenterField =
                clusteringField.getCenterField() == null || clusteringField.getCenterField() == ClusteringField.CenterField.TRUE;

        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(literalExprFrom(clusteringField.getField()));
        arguments.add(new DoubleLiteralExpr(fieldWeight));
        arguments.add(new BooleanLiteralExpr(isCenterField));
        arguments.add(clusteringField.getCompareFunction() == null ? new NullLiteralExpr() :
                              literalExprFrom(compareFunctionFrom(clusteringField.getCompareFunction())));
        arguments.add(new NullLiteralExpr());

        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null,
                                                                     KiePMMLClusteringField.class.getCanonicalName())
                , arguments);
    }

    private static ObjectCreationExpr comparisonMeasureCreationExprFrom(ComparisonMeasure comparisonMeasure) {
        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(literalExprFrom(comparisonMeasureKindFrom(comparisonMeasure.getKind())));
        arguments.add(literalExprFrom(aggregateFunctionFrom(comparisonMeasure.getMeasure())));
        arguments.add(literalExprFrom(compareFunctionFrom(comparisonMeasure.getCompareFunction())));

        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null,
                                                                     KiePMMLComparisonMeasure.class.getCanonicalName()), arguments);
    }

    private static ObjectCreationExpr missingValueWeightsCreationExprFrom(MissingValueWeights missingValueWeights) {
        NodeList<Expression> arguments = new NodeList<>();
        final List<Double> values = getMissingValueWeightsDoubleValues(missingValueWeights);
        arguments.add(createArraysAsListFromList(values).getExpression());
        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null,
                                                                     KiePMMLMissingValueWeights.class.getCanonicalName()), arguments);
    }

    private static List<Double> getClusterDoubleValues(Cluster cluster) {
        return cluster.getArray() != null ? getDoubleValuesFromArray(cluster.getArray()) : Collections.emptyList();
    }

    private static List<Double> getMissingValueWeightsDoubleValues(MissingValueWeights missingValueWeights) {
        return missingValueWeights.getArray() != null ? getDoubleValuesFromArray(missingValueWeights.getArray()) : Collections.emptyList();
    }

    private static List<Double> getDoubleValuesFromArray(Array array) {
        final List<Double> toReturn = new ArrayList<>();
        if (array.getType() == Array.Type.REAL) {
            String arrayStringValue = (String) array.getValue();
            String[] arrayStringChunks = arrayStringValue.split(" ");
            try {
                Arrays.stream(arrayStringChunks)
                        .map(Double::parseDouble)
                        .forEach(toReturn::add);
            } catch (NumberFormatException e) {
                logger.error("Can't parse \"real\" cluster with value \"" + arrayStringValue + "\"", e);
            }
        }
        return toReturn;
    }
}
