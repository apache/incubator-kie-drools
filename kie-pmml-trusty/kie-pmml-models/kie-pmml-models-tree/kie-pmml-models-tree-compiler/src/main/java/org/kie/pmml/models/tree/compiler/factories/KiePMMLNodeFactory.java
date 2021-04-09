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

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.tree.Node;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.compiler.commons.factories.KiePMMLPredicateFactory;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.tree.model.KiePMMLNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;

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
        String className = getSanitizedClassName(node.getId().toString());
        Map<String, String> sourcesMap = getKiePMMLNodeSourcesMap(node, dataDictionary, packageName);
        String fullClassName = packageName + "." + className;
        try {
            Class<?> kiePMMLTreeModelClass = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLNode) kiePMMLTreeModelClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLNodeSourcesMap(final Node node,
                                                               final DataDictionary dataDictionary,
                                                               final String packageName) {
        logger.trace("getKiePMMLNodeSourcesMap {} {}", node, packageName);
        String className = getSanitizedClassName(node.getId().toString());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_NODE_TEMPLATE_JAVA,
                                                                                 KIE_PMML_NODE_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        final KiePMMLPredicate kiePMMLPredicate = KiePMMLPredicateFactory.getPredicate(node.getPredicate(), dataDictionary);
        final Map<String, String> toReturn = KiePMMLPredicateFactory.getPredicateSourcesMap(kiePMMLPredicate, packageName);
        String predicateClassName = getSanitizedClassName(kiePMMLPredicate.getId());
        String fullPredicateClassName =  packageName + "." + predicateClassName;
        setConstructor(className, node.getId().toString(), constructorDeclaration, fullPredicateClassName);
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final String nodeClassName,
                                final String nodeName,
                               final ConstructorDeclaration constructorDeclaration,
                               final String fullPredicateClassName) {
        setConstructorSuperNameInvocation(nodeClassName, constructorDeclaration, nodeName);
        String newPredicateInvocation = String.format("new %s()", fullPredicateClassName);
        CommonCodegenUtils.setExplicitConstructorInvocationArgument(constructorDeclaration, "predicate", newPredicateInvocation);
    }
}
