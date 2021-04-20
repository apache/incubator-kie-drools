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
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
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
import org.kie.pmml.models.tree.model.KiePMMLNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getInitializerBlockStmt;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;
import static org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils.getNodeClassName;

public class KiePMMLNodeFactory {

    static final String KIE_PMML_NODE_TEMPLATE_JAVA = "KiePMMLNodeTemplate.tmpl";
    static final String KIE_PMML_NODE_TEMPLATE = "KiePMMLNodeTemplate";
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
        String nodeClassName = getNodeClassName(node);
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(nodeClassName, packageName,
                                                                                 KIE_PMML_NODE_TEMPLATE_JAVA,
                                                                                 KIE_PMML_NODE_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(nodeClassName)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + nodeClassName));
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        final List<MethodDeclaration> compoundPredicateMethods = new ArrayList<>();
        final BlockStmt predicateBody = KiePMMLPredicateFactory.getPredicateBody(node.getPredicate(), dataDictionary, compoundPredicateMethods, nodeClassName, new AtomicInteger());
        final Map<String, String> toReturn = new HashMap<>();
        setEvaluatePredicateBody(modelTemplate, predicateBody);
        addCompoundPredicateMethods(modelTemplate, compoundPredicateMethods);
        String fullClassName = packageName + "." + nodeClassName;
        final List<String> nestedNodes = new ArrayList<>();
        if (node.hasNodes()) {
            for (Node child : node.getNodes()) {
                String childPackage = getSanitizedPackageName(fullClassName);
                toReturn.putAll(getKiePMMLNodeSourcesMap(child, dataDictionary, childPackage));
                String childClassName = getNodeClassName(child);
                String childFullClassName = String.format("%s.%s", childPackage, childClassName);
                nestedNodes.add(childFullClassName);
            }
        }
        setEvaluateNodeBody(modelTemplate, nodeClassName, node.getScore(), nestedNodes);
        setConstructor(nodeClassName, node.getId().toString(), constructorDeclaration);
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static void setEvaluateNodeBody(final ClassOrInterfaceDeclaration modelTemplate, final String nodeClassName, final Object score, final List<String> nestedNodes) {
        final BlockStmt initializerBlock = CommonCodegenUtils.getMethodDeclarationBlockStmt (modelTemplate, "evaluateNode");
        // set score
        final Expression scoreExpression;
        if (score == null) {
            scoreExpression = new NullLiteralExpr();
        } else  {
            String scoreParam = score instanceof String ? String.format("\"%s\"", score) : score.toString();
            scoreExpression = new NameExpr(scoreParam);
        }
        CommonCodegenUtils.setVariableDeclaratorValue(initializerBlock, "score", scoreExpression);

        // set node functions
        final NodeList<Expression> functionsExpressions = new NodeList<>();
        for (String nestedNode : nestedNodes) {
            MethodReferenceExpr toAdd = new MethodReferenceExpr();
            toAdd.setScope(new NameExpr(nestedNode));
            toAdd.setIdentifier("evaluateNode");
            functionsExpressions.add(toAdd);
        }
        MethodCallExpr valuesInit = new MethodCallExpr();
        if (functionsExpressions.isEmpty()) {
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Collections.class.getName())));
            valuesInit.setName("emptyList");
        } else {
            valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Arrays.class.getName())));
            valuesInit.setName("asList");
            valuesInit.setArguments(functionsExpressions);
        }
        CommonCodegenUtils.setVariableDeclaratorValue(initializerBlock, "nodeFunctions", valuesInit);

        // set predicate function
        MethodReferenceExpr predicateReference = new MethodReferenceExpr();
        predicateReference.setScope(new NameExpr(nodeClassName));
        predicateReference.setIdentifier("evaluatePredicate");
        CommonCodegenUtils.setVariableDeclaratorValue(initializerBlock, "predicateFunction", predicateReference);
    }

    static void setConstructor(final String nodeClassName,
                               final String nodeName,
                               final ConstructorDeclaration constructorDeclaration) {
        setConstructorSuperNameInvocation(nodeClassName, constructorDeclaration, nodeName);
    }

    static void setEvaluatePredicateBody(final ClassOrInterfaceDeclaration modelTemplate, final BlockStmt predicateBody) {
        modelTemplate.getMethodsByName("evaluatePredicate").get(0)
                .setBody(predicateBody);
    }

    static void addCompoundPredicateMethods(final ClassOrInterfaceDeclaration modelTemplate, final List<MethodDeclaration> compoundPredicateMethods) {
        compoundPredicateMethods.forEach(compoundMethodPredicate -> {
            MethodDeclaration created = modelTemplate.addMethod(compoundMethodPredicate.getName().asString(), Modifier.Keyword.PRIVATE, Modifier.Keyword.STATIC);
            created.setType(compoundMethodPredicate.getType());
            created.setParameters(compoundMethodPredicate.getParameters());
            created.setBody(compoundMethodPredicate.getBody().get());
        });
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
}
