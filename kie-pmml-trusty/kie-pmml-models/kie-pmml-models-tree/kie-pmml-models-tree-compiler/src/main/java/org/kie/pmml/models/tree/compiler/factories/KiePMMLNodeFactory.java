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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
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
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.PACKAGE_CLASS_TEMPLATE;
import static org.kie.pmml.commons.Constants.WRONG_EXPRESSION_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.factories.KiePMMLCompoundPredicateFactory.NESTED_PREDICATE_FUNCTIONS;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getMethodDeclaration;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableInitializer;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;
import static org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils.getNodeClassName;

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
                                             final String packageName,
                                             final HasClassLoader hasClassLoader) {
        logger.trace("getKiePMMLTreeNode {} {}", packageName, node);
        String className = getNodeClassName(node);
        Map<String, String> sourcesMap = getKiePMMLNodeSourcesMap(node, dataDictionary, packageName);
        String fullClassName = packageName + "." + className;
        try {
            Class<?> kiePMMLNodeClass = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLNode) kiePMMLNodeClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLNodeSourcesMap(final Node node,
                                                               final DataDictionary dataDictionary,
                                                               final String packageName) {
        logger.trace("getKiePMMLNodeSourcesMap {} {}", node, packageName);

        final NodeNamesDTO nodeNamesDTO = new NodeNamesDTO(node);
        final JavaParserDTO javaParserDTO = new JavaParserDTO(nodeNamesDTO, packageName);
        final Map<String, String> toReturn = new HashMap<>();
        populateJavaParserDTOAndSourcesMap(javaParserDTO, toReturn, nodeNamesDTO, dataDictionary, true);
        toReturn.put(javaParserDTO.fullNodeClassName, javaParserDTO.cloneCU.toString());
        return toReturn;
    }

    static void populateJavaParserDTOAndSourcesMap(final JavaParserDTO toPopulate,
                                                   final Map<String, String> sourcesMap,
                                                   final NodeNamesDTO nodeNamesDTO,
                                                   final DataDictionary dataDictionary,
                                                   final boolean isRoot) {
        // Set 'evaluatePredicate'
        populateEvaluatePredicate(toPopulate, dataDictionary, nodeNamesDTO, isRoot);
        // Set 'evaluateNode'
        populateEvaluateNode(toPopulate, nodeNamesDTO, isRoot);
        // Set the nested nodes
        populatedNestedNodes(toPopulate, sourcesMap, dataDictionary, nodeNamesDTO);
        // merge generated methods in one class
        mergeMethods(toPopulate);
        // dump generated sources
        sourcesMap.put(toPopulate.fullNodeClassName, toPopulate.getSource());
    }

    static void populatedNestedNodes(final JavaParserDTO toPopulate,
                                     final Map<String, String> sourcesMap,
                                     final DataDictionary dataDictionary,
                                     final NodeNamesDTO nodeNamesDTO) {
        final Node node = nodeNamesDTO.node;
        if (node.hasNodes()) {
            for (Node nestedNode : node.getNodes()) {
                final NodeNamesDTO nestedNodeNamesDTO = new NodeNamesDTO(nestedNode);
                if (toPopulate.limitReach()) {
                    mergeMethods(toPopulate);
                    // start creating new node
                    // 1) dump generated class source
                    sourcesMap.put(toPopulate.fullNodeClassName, toPopulate.getSource());
                    // 2) start creation of new node
                    final JavaParserDTO javaParserDTO = new JavaParserDTO(nestedNodeNamesDTO, toPopulate.packageName);
                    populateJavaParserDTOAndSourcesMap(javaParserDTO, sourcesMap, nestedNodeNamesDTO, dataDictionary, true);
                } else {
                    // Set 'evaluatePredicate'
                    populateEvaluatePredicate(toPopulate, dataDictionary, nestedNodeNamesDTO, false);
                    // Set 'evaluateNode'
                    populateEvaluateNode(toPopulate, nestedNodeNamesDTO, false);
                    populatedNestedNodes(toPopulate, sourcesMap, dataDictionary, nestedNodeNamesDTO);
                }
            }
        }
    }

    static void mergeMethods(final JavaParserDTO toPopulate) {
        toPopulate.getEvaluateNodeMethodDeclarations().forEach(methodDeclaration -> mergeEvaluateNodeMethods(toPopulate,
                                                                                                 methodDeclaration));
        toPopulate.getEvaluateCompoundPredicateMethodDeclarations().forEach(methodDeclaration -> mergeEvaluateCompoundPredicateMethods(toPopulate,
                                                                                                                          methodDeclaration));
    }

    static void mergeEvaluateNodeMethods(final JavaParserDTO toPopulate, MethodDeclaration toMerge) {
        // Merge nodeFunctions
        Expression initializer = getVariableInitializer(toMerge, NODE_FUNCTIONS);
        MethodCallExpr evaluateNodeInitializer;
        if (initializer instanceof MethodCallExpr) {
            evaluateNodeInitializer = initializer.asMethodCallExpr();
        } else {
            throw new KiePMMLException(String.format(WRONG_EXPRESSION_TEMPLATE, initializer.getClass().getSimpleName(), initializer, MethodCallExpr.class.getSimpleName(), toMerge));
        }
        NodeList<Expression> evaluateNodeReferences = evaluateNodeInitializer.getArguments();
        String referencedNodeClass = toMerge.getNameAsString().replace(EVALUATE_NODE, "");
        String referenceToRemove = String.format(PACKAGE_CLASS_TEMPLATE, toPopulate.packageName, referencedNodeClass);
        AtomicReference<Expression> toRemoveHolder = new AtomicReference<>();
        evaluateNodeReferences.forEach(evaluateNodeReference -> {
            if (((MethodReferenceExpr) evaluateNodeReference).getScope().toString().equals(referenceToRemove)) {
                toRemoveHolder.set(evaluateNodeReference);
            } else {
                replaceEvaluateNodeReference(toPopulate,
                                             (MethodReferenceExpr) evaluateNodeReference);
            }
        });
        Expression toRemove = toRemoveHolder.get();
        if (toRemove != null) {
            evaluateNodeReferences.remove(toRemove);
        }
        if (!toMerge.getNameAsString().equals(EVALUATE_NODE)) {
            // Merge predicateFunction for nested nodes
            MethodReferenceExpr predicateFunctionReference =
                    getVariableInitializer(toMerge, PREDICATE_FUNCTION).asMethodReferenceExpr();
            replacePredicateFunctionReference(toPopulate, predicateFunctionReference, referencedNodeClass);
        }
    }

    static void mergeEvaluateCompoundPredicateMethods(final JavaParserDTO toPopulate, MethodDeclaration toMerge) {
        // Merge nodeFunctions
        if (!toMerge.getNameAsString().equals(EVALUATE_PREDICATE)) {
            MethodCallExpr evaluateNodeInitializer = getVariableInitializer(toMerge, NESTED_PREDICATE_FUNCTIONS).asMethodCallExpr();
            NodeList<Expression> evaluatePredicateReferences = evaluateNodeInitializer.getArguments();
            evaluatePredicateReferences.forEach(evaluateNodeReference -> {
                evaluateNodeReference.asMethodReferenceExpr().setScope(new NameExpr(toPopulate.nodeClassName));
            });

        }
    }

    static void replaceEvaluateNodeReference(final JavaParserDTO toPopulate, final MethodReferenceExpr toReplace) {
        String referencedNode = toReplace.getScope().toString();
        String referencedNodeClass = referencedNode.substring(referencedNode.lastIndexOf('.') + 1);
        String expectedMethod = EVALUATE_NODE + referencedNodeClass;
        getMethodDeclaration(toPopulate.nodeTemplate, expectedMethod).ifPresent(referencedMethod -> {
            toReplace.setScope(new NameExpr(toPopulate.nodeClassName));
            toReplace.setIdentifier(expectedMethod);
        });
    }

    static void replacePredicateFunctionReference(final JavaParserDTO toPopulate, final MethodReferenceExpr toReplace
            , final String referencedNodeClass) {
        String expectedMethod = EVALUATE_PREDICATE + referencedNodeClass;
        getMethodDeclaration(toPopulate.nodeTemplate, expectedMethod).ifPresent(referencedMethod -> {
            toReplace.setScope(new NameExpr(toPopulate.nodeClassName));
            toReplace.setIdentifier(expectedMethod);
        });
    }

    static void populateEvaluatePredicate(final JavaParserDTO toPopulate,
                                          final DataDictionary dataDictionary,
                                          final NodeNamesDTO nodeNamesDTO,
                                          final boolean isRoot) {
        final List<MethodDeclaration> compoundPredicateMethods = new ArrayList<>();
        final BlockStmt evaluatePredicateBody =
                KiePMMLPredicateFactory.getPredicateBody(nodeNamesDTO.node.getPredicate(),
                                                                                         dataDictionary,
                                                                                         compoundPredicateMethods,
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
        populateEvaluateNodeWithPredicateFunction(evaluateNodeBody, toPopulate.nodeClassName);
    }

    /**
     * Return a <code>Map&lt;Node, List&lt;String&gt;&gt;</code> where the <code>Node</code> is the <b>key</b>
     * and the <b>value</b> is a <code>List</code> with the class names of the <b>nested</b> nodes
     * @param node
     * @return
     */
    static Map<Node, List<String>> getNestedNodesClassMap(final Node node) {
        final Map<Node, List<String>> toReturn;
        if (node.hasNodes()) {
            toReturn = new HashMap<>();
            List<String> nestedNodesList = node.getNodes()
                    .parallelStream()
                    .map(KiePMMLTreeModelUtils::getNodeClassName)
                    .collect(Collectors.toList());
            toReturn.put(node, nestedNodesList);
        } else {
            toReturn = Collections.emptyMap();
        }
        return toReturn;
    }

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

    static MethodReferenceExpr getEvaluateNodeMethodReference(final String fullNodeClassName) {
        MethodReferenceExpr toAdd = new MethodReferenceExpr();
        toAdd.setScope(new NameExpr(fullNodeClassName));
        toAdd.setIdentifier(EVALUATE_NODE);
        return toAdd;
    }

    static void populateEvaluateNodeWithScore(final BlockStmt toPopulate, Object score) {
        final Expression scoreExpression;
        if (score == null) {
            scoreExpression = new NullLiteralExpr();
        } else {
            String scoreParam = score instanceof String ? String.format("\"%s\"", score) : score.toString();
            scoreExpression = new NameExpr(scoreParam);
        }
        CommonCodegenUtils.setVariableDeclaratorValue(toPopulate, SCORE, scoreExpression);
    }

    static void populateEvaluateNodeWithPredicateFunction(final BlockStmt toPopulate,
                                                          final String nodeClassName) {
        // set predicate function
        MethodReferenceExpr predicateReference = new MethodReferenceExpr();
        predicateReference.setScope(new NameExpr(nodeClassName));
        predicateReference.setIdentifier(EVALUATE_PREDICATE);
        CommonCodegenUtils.setVariableDeclaratorValue(toPopulate, PREDICATE_FUNCTION, predicateReference);
    }

    static MethodDeclaration getEvaluateNestedPredicateMethodDeclaration(final BlockStmt body,
                                                                         final String nodeName) {
        final MethodDeclaration toReturn = new MethodDeclaration();
        toReturn.setType("boolean");
        Parameter parameter = new Parameter();
        parameter.setName(new SimpleName(STRING_OBJECT_MAP));
        parameter.setType(getTypedClassOrInterfaceType(Map.class.getName(),
                                                       Arrays.asList("String", "Object")));
        toReturn.setParameters(NodeList.nodeList(parameter));
        toReturn.setBody(body);
        toReturn.setModifiers(Modifier.Keyword.PRIVATE, Modifier.Keyword.STATIC);
        final String methodName = EVALUATE_PREDICATE + nodeName;
        toReturn.setName(new SimpleName(methodName));
        return toReturn;
    }

    /**
     * Create a <code>List&lt;ObjectCreationExpr&gt;</code> for the given <code>List&lt;String&gt;</code>
     * @param nestedNodes
     * @return
     */
    static List<ObjectCreationExpr> getNodesObjectCreations(final List<String> nestedNodes) {
        return nestedNodes.stream()
                .map(nestedNode -> {
                    ObjectCreationExpr toReturn = new ObjectCreationExpr();
                    toReturn.setType(nestedNode);
                    return toReturn;
                })
                .collect(Collectors.toList());
    }

    static class JavaParserDTO {

        final String nodeClassName;
        final String packageName;
        final String nodeName;
        final String fullNodeClassName;
        final CompilationUnit cloneCU;
        final ClassOrInterfaceDeclaration nodeTemplate;
        final ConstructorDeclaration constructorDeclaration;
        final MethodDeclaration evaluateRootNodeMethod;
        final BlockStmt evaluateRootNodeBody;
        final MethodDeclaration evaluateRootPredicateMethod;
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
            evaluateRootNodeReferencesDeclarator = evaluateRootNodeBody.findAll(VariableDeclarator.class)
                    .stream()
                    .filter(variableDeclarator -> variableDeclarator.getName().asString().equals(NODE_FUNCTIONS))
                    .findFirst()
                    .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_VARIABLE_IN_BODY,
                                                                                  EVALUATE_NODE,
                                                                                  evaluateRootNodeBody)));
            evaluateRootPredicateMethod = nodeTemplate.getMethodsByName(EVALUATE_PREDICATE).get(0);
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

        List<MethodDeclaration> getEvaluateNodeMethodDeclarations() {
            return nodeTemplate.getMethods().parallelStream()
                    .filter(methodDeclaration -> methodDeclaration.getNameAsString().startsWith(EVALUATE_NODE))
                    .collect(Collectors.toList());
        }

        List<MethodDeclaration> getEvaluateCompoundPredicateMethodDeclarations() {
            return nodeTemplate.getMethods().parallelStream()
                    .filter(methodDeclaration -> methodDeclaration.getNameAsString().startsWith(EVALUATE_PREDICATE) &&
                            CommonCodegenUtils.getVariableDeclarator(methodDeclaration, NESTED_PREDICATE_FUNCTIONS).isPresent())
                    .collect(Collectors.toList());
        }
    }

    static class NodeNamesDTO {

        final Node node;
        final String nodeClassName;
        final String nodeName;
        final Map<Node, String> childrenNodes;

        public NodeNamesDTO(final Node node) {
            this.node = node;
            nodeClassName = getNodeClassName(node);
            this.nodeName = node.getId() != null ? node.getId() .toString() : nodeClassName;
            if (node.hasNodes()) {
                childrenNodes = node.getNodes().parallelStream().collect(Collectors.toMap(nestedNode -> nestedNode,
                                                                                          KiePMMLTreeModelUtils::getNodeClassName));
            } else {
                childrenNodes = Collections.emptyMap();
            }
        }

        String getNestedNodeName(final Node nestedNode) {
            return childrenNodes.get(nestedNode);
        }

        List<String> getNestedNodesFullClassNames(String packageName) {
            return childrenNodes.values().parallelStream()
                    .map(nestedNodeClassName -> String.format(PACKAGE_CLASS_TEMPLATE, packageName, nestedNodeClassName))
                    .collect(Collectors.toList());
        }
    }
}
