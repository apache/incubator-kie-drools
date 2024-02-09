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
package org.kie.pmml.models.tree.compiler.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Field;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.ScoreDistribution;
import org.dmg.pmml.tree.Node;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils;
import org.kie.pmml.models.tree.model.KiePMMLScoreDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_BODY_IN_METHOD;
import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.Constants.MISSING_METHOD_REFERENCE_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLPredicateFactory.getKiePMMLPredicate;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;

public class KiePMMLNodeFactory {

    static final String KIE_PMML_NODE_TEMPLATE_JAVA = "KiePMMLNodeTemplate.tmpl";
    static final String KIE_PMML_NODE_TEMPLATE = "KiePMMLNodeTemplate";
    static final String EVALUATE_NODE = "evaluateNode";
    static final String PREDICATE = "predicate";
    static final String SCORE = "score";
    static final String SCORE_DISTRIBUTIONS = "scoreDistributions";
    static final String MISSING_VALUE_PENALTY = "missingValuePenalty";
    static final String NODE_FUNCTIONS = "nodeFunctions";
    static final String EMPTY_LIST = "emptyList";
    static final String AS_LIST = "asList";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLNodeFactory.class.getName());

    private KiePMMLNodeFactory() {
        // Avoid instantiation
    }

    public static Map<String, String> getKiePMMLNodeSourcesMap(final NodeNamesDTO nodeNamesDTO,
                                                               final List<Field<?>> fields,
                                                               final String packageName) {
        logger.trace("getKiePMMLNodeSourcesMap {} {}", nodeNamesDTO, packageName);
        final JavaParserDTO javaParserDTO = new JavaParserDTO(nodeNamesDTO, packageName);
        final Map<String, String> toReturn = new HashMap<>();
        populateJavaParserDTOAndSourcesMap(javaParserDTO, toReturn, nodeNamesDTO, fields,true);
        return toReturn;
    }

    static void populateJavaParserDTOAndSourcesMap(final JavaParserDTO toPopulate,
                                                   final Map<String, String> sourcesMap,
                                                   final NodeNamesDTO nodeNamesDTO,
                                                   final List<Field<?>> fields,
                                                   final boolean isRoot) {
        // Set 'evaluateNode'
        populateEvaluateNode(toPopulate, nodeNamesDTO, fields, isRoot);
        // Set the nested nodes
        populatedNestedNodes(toPopulate, sourcesMap, fields, nodeNamesDTO);
        // merge generated methods in one class
        // dump generated sources
        sourcesMap.put(toPopulate.fullNodeClassName, toPopulate.getSource());
    }

    /**
     * Recursively populate the <code>JavaParserDTO.nodeTemplate</code> with methods generated by
     * <b>nested</b> <code>Node</code>s.
     *
     * It also dump generated sources when <code>JavaParserDTO.limitReach() == true</code>,
     * actually starting creation of a new class.
     *
     * @param toPopulate
     * @param sourcesMap
     * @param fields
     * @param nodeNamesDTO
     */
    static void populatedNestedNodes(final JavaParserDTO toPopulate,
                                     final Map<String, String> sourcesMap,
                                     final List<Field<?>> fields,
                                     final NodeNamesDTO nodeNamesDTO) {
        final Node node = nodeNamesDTO.node;
        if (node.hasNodes()) {
            for (Node nestedNode : node.getNodes()) {
                final NodeNamesDTO nestedNodeNamesDTO = new NodeNamesDTO(nestedNode, nodeNamesDTO.getNestedNodeClassName(nestedNode), nodeNamesDTO.nodeClassName, nodeNamesDTO.missingValuePenalty);
                if (toPopulate.limitReach()) {
                    // start creating new node
                    // 1) dump generated class source
                    sourcesMap.put(toPopulate.fullNodeClassName, toPopulate.getSource());
                    // 2) start creation of new node
                    final JavaParserDTO javaParserDTO = new JavaParserDTO(nestedNodeNamesDTO, toPopulate.packageName);
                    populateJavaParserDTOAndSourcesMap(javaParserDTO, sourcesMap, nestedNodeNamesDTO, fields,
                                                       true);
                } else {
                    // Set 'evaluateNode'
                    populateEvaluateNode(toPopulate, nestedNodeNamesDTO, fields,false);
                    mergeNode(toPopulate, nestedNodeNamesDTO);
                    populatedNestedNodes(toPopulate, sourcesMap, fields, nestedNodeNamesDTO);
                }
            }
        }
    }

    /**
     * Adjust the <b>evaluateNode(?)</b> references to the ones declared in the given
     * <code>JavaParserDTO.nodeTemplate</code>
     *
     * @param toPopulate
     * @param nestedNodeNamesDTO
     */
    static void mergeNode(final JavaParserDTO toPopulate, final NodeNamesDTO nestedNodeNamesDTO) {
        final MethodCallExpr evaluateNodeInitializer;
        // We are looking for the "parent" evaluateNodeMethod that invokes the evaluate method of the current nested
        // node
        if (Objects.equals(toPopulate.nodeClassName, nestedNodeNamesDTO.parentNodeClassName)) {
            evaluateNodeInitializer = toPopulate.evaluateRootNodeReferencesDeclarator.getInitializer()
                    .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                          NODE_FUNCTIONS,
                                                                          toPopulate.evaluateRootNodeReferencesDeclarator)))
                    .asMethodCallExpr();
        } else {
            String expected = EVALUATE_NODE + nestedNodeNamesDTO.parentNodeClassName;
            final MethodDeclaration evaluateNestedNodeMethod =
                    toPopulate.nodeTemplate.getMethodsByName(expected).get(0);
            final BlockStmt evaluateNestedNodeBody =
                    evaluateNestedNodeMethod.getBody()
                            .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_IN_METHOD,
                                                                                          expected)));
            final VariableDeclarator nestedNodeVariableDeclarator =
                    evaluateNestedNodeBody.findAll(VariableDeclarator.class)
                    .stream()
                    .filter(variableDeclarator -> variableDeclarator.getName().asString().equals(NODE_FUNCTIONS))
                    .findFirst()
                    .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_VARIABLE_IN_BODY,
                                                                                  expected,
                                                                                  evaluateNestedNodeBody)));
            evaluateNodeInitializer = nestedNodeVariableDeclarator.getInitializer()
                    .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                          NODE_FUNCTIONS,
                                                                          nestedNodeVariableDeclarator)))
                    .asMethodCallExpr();
        }
        mergeNodeReferences(toPopulate, nestedNodeNamesDTO, evaluateNodeInitializer);
    }

    /**
     * Adjust the <b>evaluateNode(?)</b> references to the ones declared in the given
     * <code>JavaParserDTO.nodeTemplate</code>
     *
     * @param toPopulate
     * @param nestedNodeNamesDTO
     * @param evaluateNodeInitializer
     */
    static void mergeNodeReferences(final JavaParserDTO toPopulate, final NodeNamesDTO nestedNodeNamesDTO, final MethodCallExpr evaluateNodeInitializer) {
        final NodeList<Expression> evaluateNodeReferences = evaluateNodeInitializer.getArguments();
        final String expectedReference = String.format(PACKAGE_CLASS_TEMPLATE, toPopulate.packageName,
                                                       nestedNodeNamesDTO.nodeClassName);
        Optional<MethodReferenceExpr> found = Optional.empty();
        for (Expression expression : evaluateNodeReferences) {
            if (expectedReference.equals(expression.asMethodReferenceExpr().getScope().toString())) {
                found = Optional.of(expression.asMethodReferenceExpr());
                break;
            }
        }
        final MethodReferenceExpr evaluateNodeReference = found
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_METHOD_REFERENCE_TEMPLATE,
                                                                      expectedReference, evaluateNodeInitializer)));
        String identifier = EVALUATE_NODE + nestedNodeNamesDTO.nodeClassName;
        evaluateNodeReference.setScope(new NameExpr(toPopulate.nodeClassName));
        evaluateNodeReference.setIdentifier(identifier);
    }

    /**
     * Populate <b>nodeFunctions, score, predicateFunction</b> <code>VariableDeclarator</code>s initializers of the given <code>BlockStmt</code>
     *
     * @param toPopulate
     * @param nodeNamesDTO
     * @param fields
     * @param isRoot
     */
    static void populateEvaluateNode(final JavaParserDTO toPopulate,
                                     final NodeNamesDTO nodeNamesDTO,
                                     final List<Field<?>> fields,
                                     final boolean isRoot) {
        String nodeClassName = nodeNamesDTO.nodeClassName;
        final BlockStmt evaluateNodeBody = isRoot ? toPopulate.evaluateRootNodeBody :
                toPopulate.getEvaluateNestedNodeMethodDeclaration(nodeClassName).getBody()
                        .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_TEMPLATE,
                                                                                      EVALUATE_NODE + nodeClassName)));

        // set 'predicate'
        populateEvaluateNodeWithPredicate(evaluateNodeBody, nodeNamesDTO.node.getPredicate(), fields);

        // set 'nodeFunctions'
        final List<String> nestedNodesFullClasses = nodeNamesDTO.getNestedNodesFullClassNames(toPopulate.packageName);
        populateEvaluateNodeWithNodeFunctions(evaluateNodeBody, nestedNodesFullClasses);

        // set 'score'
        populateEvaluateNodeWithScore(evaluateNodeBody, nodeNamesDTO.node.getScore());

        // set 'scoreDistributions'
        if (nodeNamesDTO.node.hasScoreDistributions()) {
            populateEvaluateNodeWithScoreDistributions(evaluateNodeBody, nodeNamesDTO.node.getScoreDistributions());
        }

        // set 'missingValuePenalty'
        if (nodeNamesDTO.missingValuePenalty != null) {
            populateEvaluateNodeWithMissingValuePenalty(evaluateNodeBody, nodeNamesDTO.missingValuePenalty);
        }
    }

    /**
     * Set the <b>nodeFunctions</b> <code>VariableDeclarator</code> initializer of the given <code>BlockStmt</code> with <code>MethodReferenceExpr</code>s to
     * <b>nested</b> <b>evaluateNode</b> methods.
     *
     * If <b>nestedNodesFullClasses</b>, set an empty list
     * <p>
     *     <code>Object nodeFunctions = java.util.Collections.emptyList();</code>
     * </p>
     *
     *  otherwise, an <code>ArrayList</code> of related references
     * <p>
     *     <code>Object nodeFunctions = java.util.Arrays.asList(full.node.NodeClassName0::evaluateNode, full.node.NodeClassName1::evaluateNode);</code>
     * </p>
     *
     * @param toPopulate
     * @param nestedNodesFullClasses
     */
    static void populateEvaluateNodeWithNodeFunctions(final BlockStmt toPopulate,
                                                      final List<String> nestedNodesFullClasses) {
        final MethodCallExpr valuesInit = new MethodCallExpr();
        if (nestedNodesFullClasses.isEmpty()) {
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Collections.class.getName())));
            valuesInit.setName(EMPTY_LIST);
        } else {
            final NodeList<Expression> methodReferenceExprs = NodeList.nodeList(nestedNodesFullClasses.stream()
                                                                                        .map(KiePMMLNodeFactory::getEvaluateNodeMethodReference)
                                                                                        .collect(Collectors.toList()));
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Arrays.class.getName())));
            valuesInit.setName(AS_LIST);
            valuesInit.setArguments(methodReferenceExprs);
        }
        CommonCodegenUtils.setVariableDeclaratorValue(toPopulate, NODE_FUNCTIONS, valuesInit);
    }

    /**
     * Return a <b>evaluateNode</b> <code>MethodReferenceExpr</code>
     * <p>
     *     <code>{_fullNodeClassName_}::evaluateNode</code>
     * </p>
     *
     * @param fullNodeClassName
     * @return
     */
    static MethodReferenceExpr getEvaluateNodeMethodReference(final String fullNodeClassName) {
        MethodReferenceExpr toAdd = new MethodReferenceExpr();
        toAdd.setScope(new NameExpr(fullNodeClassName));
        toAdd.setIdentifier(EVALUATE_NODE);
        return toAdd;
    }

    /**
     * Set the <b>score</b> <code>VariableDeclarator</code> initializer of the given <code>BlockStmt</code>.
     * If <b>scoreParam</b> is <code>null</code>, a <code>NullLiteralExpr</code> is set.
     * <p>
     *     <code>Object score = null;</code>
     * </p>
     * If <b>scoreParam</b> is a <code>String</code>, a <code>"{_score_}"</code> is set.
     * <p>
     *     <code>Object score = "scoreParam";</code>
     * </p>
     * If <b>scoreParam</b> is not a <code>String</code>, a <code>{_score_}</code> is set.
     * <p>
     *     <code>Object score = 234.12;</code>
     * </p>
     *
     *
     * @param toPopulate
     * @param scoreParam
     */
    static void populateEvaluateNodeWithScore(final BlockStmt toPopulate, Object scoreParam) {
        final Expression scoreExpression;
        if (scoreParam == null) {
            scoreExpression = new NullLiteralExpr();
        } else {
            String scoreParamExpr = scoreParam instanceof String ? String.format("\"%s\"", scoreParam) : scoreParam.toString();
            scoreExpression = new NameExpr(scoreParamExpr);
        }
        CommonCodegenUtils.setVariableDeclaratorValue(toPopulate, SCORE, scoreExpression);
    }

    /**
     * Set the <b>scoreDistribution</b> <code>VariableDeclarator</code> initializer of the given <code>BlockStmt</code>.
     * If <b>scoreDistributionsParam</b> is <code>null</code>, a <code>NullLiteralExpr</code> is set.
     * <p>
     *     <code>List<KiePMMLScoreDistribution> scoreDistributions = null;</code>
     * </p>
     * Otherwise
     * <p>
     *     <code>List<KiePMMLScoreDistribution> scoreDistributions = arrays.asList(new KiePMMLScoreDistribution(....));</code>
     * </p>
     *
     *
     * @param toPopulate
     * @param scoreDistributionsParam
     */
    static void populateEvaluateNodeWithScoreDistributions(final BlockStmt toPopulate,
                                                           final List<ScoreDistribution> scoreDistributionsParam) {
        final Expression scoreDistributionsExpression;
        if (scoreDistributionsParam == null) {
            scoreDistributionsExpression = new NullLiteralExpr();
        } else {
            int counter = 0;
            final NodeList<Expression> scoreDistributionsArguments = new NodeList<>();
            for (ScoreDistribution scoreDistribution : scoreDistributionsParam) {
                String nestedVariableName = String.format("scoreDistribution_%s", counter);
                scoreDistributionsArguments.add(getKiePMMLScoreDistribution(nestedVariableName, scoreDistribution));
                counter ++;
            }
            scoreDistributionsExpression = new MethodCallExpr();
            ((MethodCallExpr)scoreDistributionsExpression).setScope(new NameExpr(Arrays.class.getSimpleName()));
            ((MethodCallExpr)scoreDistributionsExpression).setName("asList");
            ((MethodCallExpr)scoreDistributionsExpression).setArguments(scoreDistributionsArguments);
        }
        CommonCodegenUtils.setVariableDeclaratorValue(toPopulate, SCORE_DISTRIBUTIONS, scoreDistributionsExpression);
    }

    /**
     * Set the <b>missingValuePenalty</b> <code>VariableDeclarator</code> initializer of the given <code>BlockStmt</code>.
     * <p>
     *     <code>final double missingValuePenalty = ...;</code>
     * </p>
     *
     *
     * @param toPopulate
     * @param missingValuePenalty
     */
    static void populateEvaluateNodeWithMissingValuePenalty(final BlockStmt toPopulate, final Double missingValuePenalty) {
        CommonCodegenUtils.setVariableDeclaratorValue(toPopulate, MISSING_VALUE_PENALTY, getExpressionForObject(missingValuePenalty));
    }

    static ObjectCreationExpr getKiePMMLScoreDistribution(final String variableName,
                                         final ScoreDistribution scoreDistribution) {
        final NodeList<Expression> scoreDistributionsArguments = new NodeList<>();
        scoreDistributionsArguments.add(getExpressionForObject(variableName));
        scoreDistributionsArguments.add(new NullLiteralExpr());
        scoreDistributionsArguments.add(getExpressionForObject(scoreDistribution.getValue().toString()));
        scoreDistributionsArguments.add(getExpressionForObject(scoreDistribution.getRecordCount().intValue()));
        Expression confidenceExpression = scoreDistribution.getConfidence() != null ? getExpressionForObject(scoreDistribution.getConfidence().doubleValue()) : new NullLiteralExpr();
        scoreDistributionsArguments.add(confidenceExpression);
        Expression probabilityExpression = scoreDistribution.getProbability() != null ? getExpressionForObject(scoreDistribution.getProbability().doubleValue()) : new NullLiteralExpr();
        scoreDistributionsArguments.add(probabilityExpression);

        return new ObjectCreationExpr(null, new ClassOrInterfaceType(null, KiePMMLScoreDistribution.class.getCanonicalName()),
                                                                       scoreDistributionsArguments);

    }

    /**
     * Set the <b>predicate</b> <code>VariableDeclarator</code> initializer of the given <code>BlockStmt</code>
     *
     *
     * @param toPopulate
     * @param predicate
     * @param fields
     */
    static void populateEvaluateNodeWithPredicate(final BlockStmt toPopulate,
                                                  final Predicate predicate,
                                                  final List<Field<?>> fields) {
        // set predicate
        BlockStmt toAdd = getKiePMMLPredicate(PREDICATE, predicate, fields);
        final NodeList<Statement> predicateStatements = toAdd.getStatements();
        for (int i = 0; i < predicateStatements.size(); i ++) {
            toPopulate.addStatement(i, predicateStatements.get(i));
        }
    }

    static class JavaParserDTO {

        final String nodeClassName;
        final String packageName;
        final String nodeName;
        final String fullNodeClassName;
        final CompilationUnit cloneCU;
        final ClassOrInterfaceDeclaration nodeTemplate;
        final ConstructorDeclaration constructorDeclaration;
        // evaluateNode
        final MethodDeclaration evaluateRootNodeMethod;
        final BlockStmt evaluateRootNodeBody;
        final BlockStmt evaluateRootNodeBodyClone;
        final VariableDeclarator evaluateRootNodeReferencesDeclarator;

        JavaParserDTO(final NodeNamesDTO nodeNamesDTO, final String packageName) {
            this.nodeClassName = nodeNamesDTO.nodeClassName;
            this.packageName = packageName;
            this.nodeName = nodeNamesDTO.nodeName;
            fullNodeClassName = String.format(PACKAGE_CLASS_TEMPLATE, packageName, nodeClassName);
            cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(nodeClassName,
                                                                     packageName,
                                                                     KiePMMLNodeFactory.KIE_PMML_NODE_TEMPLATE_JAVA,
                                                                     KiePMMLNodeFactory.KIE_PMML_NODE_TEMPLATE);
            nodeTemplate = cloneCU.getClassByName(nodeClassName)
                    .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + nodeClassName));
            constructorDeclaration =
                    nodeTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, nodeTemplate.getName())));
            setConstructorSuperNameInvocation(nodeClassName, constructorDeclaration, nodeName);
            evaluateRootNodeMethod = nodeTemplate.getMethodsByName(EVALUATE_NODE).get(0);
            evaluateRootNodeBody =
                    evaluateRootNodeMethod.getBody()
                            .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_IN_METHOD,
                                                                                          EVALUATE_NODE)));
            evaluateRootNodeBodyClone = evaluateRootNodeBody.clone();
            evaluateRootNodeReferencesDeclarator = evaluateRootNodeBody.findAll(VariableDeclarator.class)
                    .stream()
                    .filter(variableDeclarator -> variableDeclarator.getName().asString().equals(NODE_FUNCTIONS))
                    .findFirst()
                    .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_VARIABLE_IN_BODY,
                                                                                  EVALUATE_NODE,
                                                                                  evaluateRootNodeBody)));
        }

        boolean limitReach() {
            return nodeTemplate.findAll(MethodDeclaration.class).size() > 1000;
        }

        String getSource() {
            return cloneCU.toString();
        }

        MethodDeclaration getEvaluateNestedNodeMethodDeclaration(final String nodeClassNameParam) {
            final String methodName = EVALUATE_NODE + nodeClassNameParam;
            final MethodDeclaration toReturn = nodeTemplate.addMethod(methodName, Modifier.Keyword.PRIVATE,
                                                                      Modifier.Keyword.STATIC);
            toReturn.setType(evaluateRootNodeMethod.getType());
            toReturn.setParameters(evaluateRootNodeMethod.getParameters());
            BlockStmt blockStmt = evaluateRootNodeBodyClone.clone();
            final MethodCallExpr valuesInit = new MethodCallExpr();
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Arrays.class.getName())));
            valuesInit.setName(AS_LIST);
            CommonCodegenUtils.setVariableDeclaratorValue(blockStmt, NODE_FUNCTIONS, valuesInit);
            toReturn.setBody(blockStmt);
            return toReturn;
        }
    }

    static class NodeNamesDTO {

        final Node node;
        final String nodeClassName;
        final String nodeName;
        final Map<Node, String> childrenNodes;
        final String parentNodeClassName;
        final Double missingValuePenalty;

        public NodeNamesDTO(final Node node, final String nodeClassName, final String parentNodeClassName, final Double missingValuePenalty) {
            this.node = node;
            this.parentNodeClassName = parentNodeClassName;
            this.nodeClassName = nodeClassName;
            this.nodeName = node.getId() != null ? node.getId().toString() : nodeClassName;
            if (node.hasNodes()) {
                childrenNodes = new LinkedHashMap<>();
                for (Node nestedNode : node.getNodes()) {
                    childrenNodes.put(nestedNode, KiePMMLTreeModelUtils.createNodeClassName());
                }
            } else {
                childrenNodes = Collections.emptyMap();
            }
            this.missingValuePenalty = missingValuePenalty;
        }

        String getNestedNodeClassName(final Node nestedNode) {
            if (!childrenNodes.containsKey(nestedNode)) {
                throw new KiePMMLException("Missing expected nested node " + nestedNode);
            } else {
                return childrenNodes.get(nestedNode);
            }
        }

        List<String> getNestedNodesFullClassNames(String packageName) {
            List<String> toReturn = new ArrayList<>();
            for (String nestedNodeClassName : childrenNodes.values()) {
                toReturn.add(String.format(PACKAGE_CLASS_TEMPLATE, packageName, nestedNodeClassName));
            }
            return toReturn;
        }
    }
}
