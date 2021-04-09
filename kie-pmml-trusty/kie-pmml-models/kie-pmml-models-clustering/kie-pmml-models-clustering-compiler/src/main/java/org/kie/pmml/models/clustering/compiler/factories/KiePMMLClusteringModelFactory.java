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
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Array;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.clustering.Cluster;
import org.dmg.pmml.clustering.ClusteringField;
import org.dmg.pmml.clustering.ClusteringModel;
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
import org.kie.pmml.models.clustering.model.compare.CompareFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setKiePMMLModelConstructor;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;

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

        logger.warn("getKiePMMLClusteringModelSourcesMap {} {} {}", dataDictionary, model, packageName);

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

//        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, transformationDictionary, model.getLocalTransformations());

        Map<String, String> sourcesMap = new HashMap<>();
        sourcesMap.put(getFullClassName(compilationUnit), compilationUnit.toString());

        System.out.println(sourcesMap);

//        sourcesMap.put(HARDCODED_MODEL_CLASS_NAME, HARDCODED_MODEL_CLASS_SOURCE);
        return sourcesMap;
    }

    private static KiePMMLClusteringModel.ModelClass modelClassFrom(ClusteringModel.ModelClass input) {
        switch (input) {
            case CENTER_BASED:
                return KiePMMLClusteringModel.ModelClass.CENTER_BASED;
            case DISTRIBUTION_BASED:
                return KiePMMLClusteringModel.ModelClass.DISTRIBUTION_BASED;
        }
        throw new IllegalStateException("Invalid model class " + input);
    }

    private static ObjectCreationExpr clusterCreationExprFrom(Cluster cluster) {
        NodeList<Expression> arguments = new NodeList<>();
        arguments.add(literalExprFrom(cluster.getId()));
        arguments.add(literalExprFrom(cluster.getName()));

        if (cluster.getArray() != null && cluster.getArray().getType() == Array.Type.REAL) {
            String arrayStringValue = (String) cluster.getArray().getValue();
            try {
                Arrays.stream(arrayStringValue.split(" "))
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
        arguments.add(clusteringField.getCompareFunction() == null ? new NullLiteralExpr() : new NameExpr(CompareFunctions.class.getCanonicalName() + "." + clusteringField.getCompareFunction().value() + "()"));
        arguments.add(new NullLiteralExpr());

        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, KiePMMLClusteringField.class.getCanonicalName()), arguments);
    }

    private static AssignExpr assignExprFrom(String target, Enum<?> value) {
        return new AssignExpr(new NameExpr(target), literalExprFrom(value), AssignExpr.Operator.ASSIGN);
    }

    private static AssignExpr assignExprFrom(String target, String value) {
        return new AssignExpr(new NameExpr(target), literalExprFrom(value), AssignExpr.Operator.ASSIGN);
    }

    private static Expression literalExprFrom(Enum<?> input) {
        return input == null ? new NullLiteralExpr() : new NameExpr(input.getClass().getCanonicalName() + "." + input.name());
    }

    private static Expression literalExprFrom(String input) {
        return input == null ? new NullLiteralExpr() : new StringLiteralExpr(input);
    }

    private static MethodCallExpr methodCallExprFrom(String scope, String name, Expression... arguments) {
        return new MethodCallExpr(new NameExpr(scope), name, new NodeList<>(arguments));
    }

    private static final String HARDCODED_MODEL_CLASS_NAME = "singleiriskmeans.SingleIrisKMeansClusteringModel";
    private static final String HARDCODED_MODEL_CLASS_SOURCE = "" +
            "package singleiriskmeans;\n" +
            "\n" +
            "import org.kie.pmml.api.enums.MINING_FUNCTION;\n" +
            "import org.kie.pmml.api.enums.PMML_MODEL;\n" +
            "import org.kie.pmml.models.clustering.model.KiePMMLCluster;\n" +
            "import org.kie.pmml.models.clustering.model.KiePMMLClusteringField;\n" +
            "import org.kie.pmml.models.clustering.model.KiePMMLClusteringModel;\n" +
            "import org.kie.pmml.models.clustering.model.KiePMMLComparisonMeasure;\n" +
            "import org.kie.pmml.models.clustering.model.aggregate.AggregateFunctions;\n" +
            "import org.kie.pmml.models.clustering.model.compare.CompareFunctions;\n" +
            "\n" +
            "public class SingleIrisKMeansClusteringModel extends KiePMMLClusteringModel {\n" +
            "\n" +
            "    public SingleIrisKMeansClusteringModel() {\n" +
            "        super(\"SingleIrisKMeans\");\n" +
            "\n" +
            "        // KiePMMLModel\n" +
            "        pmmlMODEL = PMML_MODEL.CLUSTERING_MODEL;\n" +
            "        miningFunction = MINING_FUNCTION.CLUSTERING;\n" +
            "        targetField = \"class\";\n" +
            "\n" +
            "        // KiePMMLClusteringModel\n" +
            "        modelClass = ModelClass.CENTER_BASED;\n" +
            "\n" +
            "        clusters.add(new KiePMMLCluster(null, null, 6.9125000000000005, 3.0999999999999999, 5.846874999999999, 2.1312499999999996));\n" +
            "        clusters.add(new KiePMMLCluster(null, null, 6.2365853658536600, 2.8585365853658535, 4.807317073170731, 1.62195121951219433));\n" +
            "        clusters.add(new KiePMMLCluster(null, null, 5.0059999999999990, 3.4180000000000006, 1.464000000000000, 0.24399999999999999));\n" +
            "        clusters.add(new KiePMMLCluster(null, null, 5.5296296296296290, 2.6222222222222222, 3.940740740740741, 1.21851851851851886));\n" +
            "\n" +
            "        clusteringFields.add(new KiePMMLClusteringField(\"sepal_length\", 1.0, true, CompareFunctions.absDiff(), null));\n" +
            "        clusteringFields.add(new KiePMMLClusteringField(\"sepal_width\", 1.0, true, CompareFunctions.absDiff(), null));\n" +
            "        clusteringFields.add(new KiePMMLClusteringField(\"petal_length\", 1.0, true, CompareFunctions.absDiff(), null));\n" +
            "        clusteringFields.add(new KiePMMLClusteringField(\"petal_width\", 1.0, true, CompareFunctions.absDiff(), null));\n" +
            "\n" +
            "        comparisonMeasure = new KiePMMLComparisonMeasure(KiePMMLComparisonMeasure.Kind.DISTANCE, AggregateFunctions::squaredEuclidean, CompareFunctions.absDiff());\n" +
            "    }\n" +
            "}";
}
