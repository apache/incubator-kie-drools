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
package  org.kie.pmml.models.clustering.compiler.factories;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Array;
import org.dmg.pmml.ComparisonMeasure;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.clustering.Cluster;
import org.dmg.pmml.clustering.ClusteringField;
import org.dmg.pmml.clustering.ClusteringModel;
import org.dmg.pmml.clustering.MissingValueWeights;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.models.MiningField;
import org.kie.pmml.api.models.OutputField;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.ModelUtils;
import org.kie.pmml.models.clustering.model.KiePMMLCluster;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringField;
import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;
import org.kie.pmml.models.clustering.model.KiePMMLComparisonMeasure;
import org.kie.pmml.models.clustering.model.KiePMMLMissingValueWeights;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.assignExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.literalExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.methodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setKiePMMLModelConstructor;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringConversionUtils.aggregateFunctionFrom;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringConversionUtils.compareFunctionFrom;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringConversionUtils.comparisonMeasureKindFrom;
import static org.kie.pmml.models.clustering.compiler.factories.KiePMMLClusteringConversionUtils.modelClassFrom;

public class KiePMMLClusteringModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLClusteringModelFactory.class.getName());
    static final String KIE_PMML_CLUSTERING_MODEL_TEMPLATE_JAVA = "KiePMMLClusteringModelTemplate.tmpl";
    static final String KIE_PMML_CLUSTERING_MODEL_TEMPLATE = "KiePMMLClusteringModelTemplate";

    private KiePMMLClusteringModelFactory(){
        // Avoid instantiation
    }

    public static KiePMMLClusteringModel getKiePMMLClusteringModel(final DataDictionary dataDictionary,
                                                                       final TransformationDictionary transformationDictionary,
                                                                       final ClusteringModel model,
                                                                       final String packageName,
                                                                       final HasClassLoader hasClassLoader) {
        logger.trace("getKiePMMLClusteringModel {} {}", dataDictionary, model);

        String canonicalClassName = packageName + "." + getSanitizedClassName(model.getModelName());

        Map<String, String> sourcesMap = getKiePMMLClusteringModelSourcesMap(dataDictionary, transformationDictionary, model, packageName);
        try {
            Class<?> clusteringModelClass = hasClassLoader.compileAndLoadClass(sourcesMap, canonicalClassName);
            return (KiePMMLClusteringModel) clusteringModelClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLClusteringModelSourcesMap(final DataDictionary dataDictionary,
                                                                                 final TransformationDictionary transformationDictionary,
                                                                                 final ClusteringModel model,
                                                                                 final String packageName) {

        logger.trace("getKiePMMLClusteringModelSourcesMap {} {} {}", dataDictionary, model, packageName);

        String simpleClassName = getSanitizedClassName(model.getModelName());

        CompilationUnit compilationUnit = JavaParserUtils.getKiePMMLModelCompilationUnit(simpleClassName, packageName, KIE_PMML_CLUSTERING_MODEL_TEMPLATE_JAVA, KIE_PMML_CLUSTERING_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = compilationUnit.getClassByName(simpleClassName)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + simpleClassName));

        ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        constructorDeclaration.setName(simpleClassName);

        List<MiningField> miningFields = ModelUtils.convertToKieMiningFieldList(model.getMiningSchema(), dataDictionary);
        List<OutputField> outputFields = ModelUtils.convertToKieOutputFieldList(model.getOutput(), dataDictionary);
        setKiePMMLModelConstructor(simpleClassName, constructorDeclaration, model.getModelName(), miningFields, outputFields);

        BlockStmt body = constructorDeclaration.getBody();
        body.addStatement(assignExprFrom("pmmlMODEL", PMML_MODEL.CLUSTERING_MODEL));
        body.addStatement(assignExprFrom("miningFunction", MINING_FUNCTION.byName(model.getMiningFunction().value())));
        body.addStatement(assignExprFrom("targetField", getTargetFieldName(dataDictionary, model).orElse(null)));

        body.addStatement(assignExprFrom("modelClass", modelClassFrom(model.getModelClass())));

        model.getClusters().stream()
                .map(KiePMMLClusteringModelFactory::clusterCreationExprFrom)
                .map(expr -> methodCallExprFrom("clusters", "add", expr))
                .forEach(body::addStatement);

        model.getClusteringFields().stream()
                .map(KiePMMLClusteringModelFactory::clusteringFieldCreationExprFrom)
                .map(expr -> methodCallExprFrom("clusteringFields", "add", expr))
                .forEach(body::addStatement);

        body.addStatement(assignExprFrom("comparisonMeasure", comparisonMeasureCreationExprFrom(model.getComparisonMeasure())));

        if (model.getMissingValueWeights() != null) {
            body.addStatement(assignExprFrom("missingValueWeights", missingValueWeightsCreationExprFrom(model.getMissingValueWeights())));
        }

        Map<String, String> sourcesMap = new HashMap<>();
        sourcesMap.put(getFullClassName(compilationUnit), compilationUnit.toString());

        return sourcesMap;
    }

    private static ObjectCreationExpr clusterCreationExprFrom(Cluster cluster) {
        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(literalExprFrom(cluster.getId()));
        arguments.add(literalExprFrom(cluster.getName()));

        if (cluster.getArray() != null && cluster.getArray().getType() == Array.Type.REAL) {
            String arrayStringValue = (String) cluster.getArray().getValue();
            String[] arrayStringChunks = arrayStringValue.split(" ");
            try {
                Arrays.stream(arrayStringChunks)
                        .map(Double::parseDouble)
                        .map(DoubleLiteralExpr::new)
                        .forEach(arguments::add);
            } catch (NumberFormatException e) {
                logger.error("Can't parse \"real\" cluster with value \"" + arrayStringValue + "\"", e);
            }
        }
        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, KiePMMLCluster.class.getCanonicalName()), arguments);
    }

    private static ObjectCreationExpr clusteringFieldCreationExprFrom(ClusteringField clusteringField) {
        double fieldWeight = clusteringField.getFieldWeight() == null ? 1.0 : clusteringField.getFieldWeight().doubleValue();
        boolean isCenterField = clusteringField.getCenterField() == null || clusteringField.getCenterField() == ClusteringField.CenterField.TRUE;

        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(literalExprFrom(clusteringField.getField().getValue()));
        arguments.add(new DoubleLiteralExpr(fieldWeight));
        arguments.add(new BooleanLiteralExpr(isCenterField));
        arguments.add(clusteringField.getCompareFunction() == null ? new NullLiteralExpr() : literalExprFrom(compareFunctionFrom(clusteringField.getCompareFunction())));
        arguments.add(new NullLiteralExpr());

        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, KiePMMLClusteringField.class.getCanonicalName()), arguments);
    }

    private static ObjectCreationExpr comparisonMeasureCreationExprFrom(ComparisonMeasure comparisonMeasure) {
        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(literalExprFrom(comparisonMeasureKindFrom(comparisonMeasure.getKind())));
        arguments.add(literalExprFrom(aggregateFunctionFrom(comparisonMeasure.getMeasure())));
        arguments.add(literalExprFrom(compareFunctionFrom(comparisonMeasure.getCompareFunction())));

        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, KiePMMLComparisonMeasure.class.getCanonicalName()), arguments);
    }

    private static ObjectCreationExpr missingValueWeightsCreationExprFrom(MissingValueWeights missingValueWeights) {
        NodeList<Expression> arguments = new NodeList<>();

        if (missingValueWeights.getArray() != null && missingValueWeights.getArray().getType() == Array.Type.REAL) {
            String arrayStringValue = (String) missingValueWeights.getArray().getValue();
            try {
                Arrays.stream(arrayStringValue.split(" "))
                        .map(Double::parseDouble)
                        .map(DoubleLiteralExpr::new)
                        .forEach(arguments::add);
            } catch (NumberFormatException e) {
                logger.error("Can't parse \"real\" missing value weights with value \"" + arrayStringValue + "\"", e);
            }
        }

        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, KiePMMLMissingValueWeights.class.getCanonicalName()), arguments);
    }
}
