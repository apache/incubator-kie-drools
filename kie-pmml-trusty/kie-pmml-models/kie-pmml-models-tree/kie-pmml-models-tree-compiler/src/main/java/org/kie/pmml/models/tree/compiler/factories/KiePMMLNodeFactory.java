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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DerivedField;
import org.dmg.pmml.tree.Node;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils;
import org.kie.pmml.models.tree.model.KiePMMLNode;
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
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;
import static org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils.createNodeClassName;

public class KiePMMLNodeFactory {

    static final String KIE_PMML_NODE_TEMPLATE_JAVA = "KiePMMLNodeTemplate.tmpl";
    static final String KIE_PMML_NODE_TEMPLATE = "KiePMMLNodeTemplate";
    static final String EVALUATE_NODE = "evaluateNode";
    static final String EVALUATE_PREDICATE = "evaluatePredicate";
    static final String PREDICATE_FUNCTION = "predicateFunction";
    static final String STRING_OBJECT_MAP = "stringObjectMap";
    static final String SCORE = "score";
    static final String NODE_FUNCTIONS = "nodeFunctions";
    static final String EMPTY_LIST = "emptyList";
    static final String AS_LIST = "asList";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLNodeFactory.class.getName());

    private KiePMMLNodeFactory() {
        // Avoid instantiation
    }

    public static KiePMMLNode getKiePMMLNode(final Node node,
                                             final DataDictionary dataDictionary,
                                             final List<DerivedField> derivedFields,
                                             final String packageName,
                                             final HasClassLoader hasClassLoader) {
        logger.trace("getKiePMMLTreeNode {} {}", packageName, node);
        final KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(node, createNodeClassName(), null);
        final Map<String, String> sourcesMap = getKiePMMLNodeSourcesMap(nodeNamesDTO, dataDictionary, derivedFields, packageName);
        String fullClassName = packageName + "." + nodeNamesDTO.nodeClassName;
        try {
            Class<?> kiePMMLNodeClass = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLNode) kiePMMLNodeClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLNodeSourcesMap(final NodeNamesDTO nodeNamesDTO,
                                                               final DataDictionary dataDictionary,
                                                               final List<DerivedField> derivedFields,
                                                               final String packageName) {
        logger.trace("getKiePMMLNodeSourcesMap {} {}", nodeNamesDTO, packageName);
        final JavaParserDTO javaParserDTO = new JavaParserDTO(nodeNamesDTO, packageName);
        final Map<String, String> toReturn = new HashMap<>();
        populateJavaParserDTOAndSourcesMap(javaParserDTO, toReturn, nodeNamesDTO, dataDictionary, derivedFields,true);
        return toReturn;
    }

    static void populateJavaParserDTOAndSourcesMap(final JavaParserDTO toPopulate,
                                                   final Map<String, String> sourcesMap,
                                                   final NodeNamesDTO nodeNamesDTO,
                                                   final DataDictionary dataDictionary,
                                                   final List<DerivedField> derivedFields,
                                                   final boolean isRoot) {
        // Set 'evaluatePredicate'
        populateEvaluatePredicate(toPopulate, dataDictionary, derivedFields, nodeNamesDTO, isRoot);
        // Set 'evaluateNode'
        populateEvaluateNode(toPopulate, nodeNamesDTO, isRoot);
        // Set the nested nodes
        populatedNestedNodes(toPopulate, sourcesMap, dataDictionary,derivedFields, nodeNamesDTO);
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
     * @param dataDictionary
     * @param derivedFields
     * @param nodeNamesDTO
     */
    static void populatedNestedNodes(final JavaParserDTO toPopulate,
                                     final Map<String, String> sourcesMap,
                                     final DataDictionary dataDictionary,
                                     final List<DerivedField> derivedFields,
                                     final NodeNamesDTO nodeNamesDTO) {
        final Node node = nodeNamesDTO.node;
        if (node.hasNodes()) {
            for (Node nestedNode : node.getNodes()) {
                final NodeNamesDTO nestedNodeNamesDTO = new NodeNamesDTO(nestedNode, nodeNamesDTO.getNestedNodeClassName(nestedNode), nodeNamesDTO.nodeClassName);
                if (toPopulate.limitReach()) {
                    // start creating new node
                    // 1) dump generated class source
                    sourcesMap.put(toPopulate.fullNodeClassName, toPopulate.getSource());
                    // 2) start creation of new node
                    final JavaParserDTO javaParserDTO = new JavaParserDTO(nestedNodeNamesDTO, toPopulate.packageName);
                    populateJavaParserDTOAndSourcesMap(javaParserDTO, sourcesMap, nestedNodeNamesDTO, dataDictionary,
                                                       derivedFields,
                                                       true);
                } else {
                    // Set 'evaluatePredicate'
                    populateEvaluatePredicate(toPopulate, dataDictionary, derivedFields, nestedNodeNamesDTO, false);
                    // Set 'evaluateNode'
                    populateEvaluateNode(toPopulate, nestedNodeNamesDTO, false);
                    mergeNode(toPopulate, nestedNodeNamesDTO);
                    populatedNestedNodes(toPopulate, sourcesMap, dataDictionary, derivedFields, nestedNodeNamesDTO);
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
     * Retrieve the <b>evaluatePredicate(?)</b> body for the given <code>NodeNamesDTO</code>' node.
     * If the method is invoked for <b>root</b> node, the actual <code>evaluatePredicate</code> will be populated,
     * otherwise the <b>evaluatePredicate{_NodeNamesDTO.nodeClassName_}</b>.
     * In this latter case, eventually <b>nested</b> <code>Predicate</code>s of <code>CompoundPredicate</code> will also
     * be add to <code>JavaParserDTO.nodeTemplate</code>
     *
     * @param toPopulate
     * @param dataDictionary
     * @param derivedFields
     * @param nodeNamesDTO
     * @param isRoot
     */
    static void populateEvaluatePredicate(final JavaParserDTO toPopulate,
                                          final DataDictionary dataDictionary,
                                          final List<DerivedField> derivedFields,
                                          final NodeNamesDTO nodeNamesDTO,
                                          final boolean isRoot) {
        final List<MethodDeclaration> compoundPredicateMethods = new ArrayList<>();
        final BlockStmt evaluatePredicateBody =
                KiePMMLPredicateFactory.getPredicateBody(nodeNamesDTO.node.getPredicate(),
                                                         dataDictionary,
                                                         derivedFields,
                                                         compoundPredicateMethods,
                                                         toPopulate.nodeClassName,
                                                         nodeNamesDTO.nodeClassName,
                                                         new AtomicInteger());

        if (isRoot) {
            toPopulate.evaluateRootPredicateMethod.setBody(evaluatePredicateBody);
        } else {
            final MethodDeclaration evaluatePredicateMethod =
                    getEvaluateNestedPredicateMethodDeclaration(evaluatePredicateBody, nodeNamesDTO.nodeClassName);
            compoundPredicateMethods.add(evaluatePredicateMethod);
        }
        CommonCodegenUtils.addMethodDeclarationsToClass(toPopulate.nodeTemplate, compoundPredicateMethods);
    }

    /**
     * Populate <b>nodeFunctions, score, predicateFunction</b> <code>VariableDeclarator</code>s initializers of the given <code>BlockStmt</code>
     * @param toPopulate
     * @param nodeNamesDTO <code>NodeNamesDTO</code>
     * @param isRoot
     */
    static void populateEvaluateNode(final JavaParserDTO toPopulate,
                                     final NodeNamesDTO nodeNamesDTO,
                                     final boolean isRoot) {
        String nodeClassName = nodeNamesDTO.nodeClassName;
        final BlockStmt evaluateNodeBody = isRoot ? toPopulate.evaluateRootNodeBody :
                toPopulate.getEvaluateNestedNodeMethodDeclaration(nodeClassName).getBody()
                        .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_TEMPLATE,
                                                                                      EVALUATE_NODE + nodeClassName)));

        // set 'nodeFunctions'
        final List<String> nestedNodesFullClasses = nodeNamesDTO.getNestedNodesFullClassNames(toPopulate.packageName);
        populateEvaluateNodeWithNodeFunctions(evaluateNodeBody, nestedNodesFullClasses);

        // set 'score'
        populateEvaluateNodeWithScore(evaluateNodeBody, nodeNamesDTO.node.getScore());

        // set 'predicateFunction'
        populateEvaluateNodeWithPredicateFunction(evaluateNodeBody, toPopulate.nodeClassName, nodeClassName, isRoot);
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
     *  otherwise an <code>ArrayList</code> of related references
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
     * Set the <b>predicateFunction</b> <code>VariableDeclarator</code> initializer of the given <code>BlockStmt</code> with a <code>MethodReferenceExpr</code> to an
     * <b>evaluatePredicate</b> method.
     *
     * If the populated block belongs to the <b>root</b> <code>Node</code>, the reference would be to <code>evaluatePredicate</code>,
     * otherwise to <code>evaluatePredicate{_nestedNodeClassName_}</code>
     *
     * For root:
     * <p>
     *     <code>{_nodeClassName_}::evaluatePredicate</code>
     * </p>
     *
     * For children:
     * <p>
     *     <code>{_nodeClassName_}::evaluatePredicate{_nestedNodeClassName_}</code>
     * </p>
     *
     *
     * @param toPopulate
     * @param nodeClassName
     * @param nestedNodeClassName
     * @param isRoot
     */
    static void populateEvaluateNodeWithPredicateFunction(final BlockStmt toPopulate,
                                                          final String nodeClassName,
                                                          final String nestedNodeClassName,
                                                          final boolean isRoot) {
        // set predicate function
        MethodReferenceExpr predicateReference = new MethodReferenceExpr();
        predicateReference.setScope(new NameExpr(nodeClassName));
        String identifier = isRoot ? EVALUATE_PREDICATE : EVALUATE_PREDICATE + nestedNodeClassName;
        predicateReference.setIdentifier(identifier);
        CommonCodegenUtils.setVariableDeclaratorValue(toPopulate, PREDICATE_FUNCTION, predicateReference);
    }

    /**
     * Returns the <code>MethodDeclaration</code> of a <b>nested</b> <code>Node</code> predicate,
     * i.e. the predicate of a <b>children</b> <code>Node</code> .
     * The method name will be <code>EVALUATE_PREDICATE + nodeClassName</code>.
     * This method must not be invoked for the <b>root</b> <code>Node</code>
     * <p>
     *     <code>private static boolean evaluatePredicate{_nodeClassName_}(java.util.Map<String, Object> stringObjectMap)</code>
     * </p>
     * @param body
     * @param nodeClassName
     * @return
     */
    static MethodDeclaration getEvaluateNestedPredicateMethodDeclaration(final BlockStmt body,
                                                                         final String nodeClassName) {
        final MethodDeclaration toReturn = new MethodDeclaration();
        toReturn.setType("boolean");
        Parameter parameter = new Parameter();
        parameter.setName(new SimpleName(STRING_OBJECT_MAP));
        parameter.setType(getTypedClassOrInterfaceType(Map.class.getName(),
                                                       Arrays.asList("String", "Object")));
        toReturn.setParameters(NodeList.nodeList(parameter));
        toReturn.setBody(body);
        toReturn.setModifiers(Modifier.Keyword.PRIVATE, Modifier.Keyword.STATIC);
        final String methodName = EVALUATE_PREDICATE + nodeClassName;
        toReturn.setName(new SimpleName(methodName));
        return toReturn;
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
        final VariableDeclarator evaluateRootNodeReferencesDeclarator;
        // evaluatePredicate
        final MethodDeclaration evaluateRootPredicateMethod;
        final BlockStmt evaluateRootPredicateBody;

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
            evaluateRootNodeReferencesDeclarator = evaluateRootNodeBody.findAll(VariableDeclarator.class)
                    .stream()
                    .filter(variableDeclarator -> variableDeclarator.getName().asString().equals(NODE_FUNCTIONS))
                    .findFirst()
                    .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_VARIABLE_IN_BODY,
                                                                                  EVALUATE_NODE,
                                                                                  evaluateRootNodeBody)));
            evaluateRootPredicateMethod = nodeTemplate.getMethodsByName(EVALUATE_PREDICATE).get(0);
            evaluateRootPredicateBody =
                    evaluateRootPredicateMethod.getBody()
                            .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_IN_METHOD,
                                                                                          EVALUATE_PREDICATE)));
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
            BlockStmt blockStmt = evaluateRootNodeBody.clone();
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

        public NodeNamesDTO(final Node node, final String nodeClassName, final String parentNodeClassName) {
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
