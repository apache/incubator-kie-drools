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

import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.commons.codegenfactories.KiePMMLModelFactoryUtils;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.tree.compiler.dto.TreeCompilationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.getKiePMMLNodeSourcesMap;
import static org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils.createNodeClassName;

public class KiePMMLTreeModelFactory {

    static final String KIE_PMML_TREE_MODEL_TEMPLATE_JAVA = "KiePMMLTreeModelTemplate.tmpl";
    static final String KIE_PMML_TREE_MODEL_TEMPLATE = "KiePMMLTreeModelTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelFactory.class.getName());

    private KiePMMLTreeModelFactory() {
        // Avoid instantiation
    }

    public static Map<String, String> getKiePMMLTreeModelSourcesMap(final TreeCompilationDTO compilationDTO) {
        logger.trace("getKiePMMLTreeModelSourcesMap {} {} {}", compilationDTO.getFields(),
                     compilationDTO.getModel(),
                     compilationDTO.getPackageName());
        String className = compilationDTO.getSimpleClassName();
        String packageName = compilationDTO.getPackageName();
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_TREE_MODEL_TEMPLATE_JAVA,
                                                                                 KIE_PMML_TREE_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final Double missingValuePenalty = compilationDTO.getMissingValuePenalty();
        final KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO =
                new KiePMMLNodeFactory.NodeNamesDTO(compilationDTO.getNode(),
                                                                                                 createNodeClassName(), null, missingValuePenalty);
        String fullNodeClassName = packageName + "." + nodeNamesDTO.nodeClassName;
        Map<String, String> toReturn = getKiePMMLNodeSourcesMap(nodeNamesDTO,
                                                                compilationDTO.getFields(),
                                                                packageName);
        setConstructor(compilationDTO,
                       modelTemplate,
                       fullNodeClassName);
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final TreeCompilationDTO compilationDTO,
                               final ClassOrInterfaceDeclaration modelTemplate,
                               final String fullNodeClassName) {
        KiePMMLModelFactoryUtils.init(compilationDTO,
                                      modelTemplate);
        final ConstructorDeclaration constructorDeclaration =
                modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        final BlockStmt body = constructorDeclaration.getBody();
        // set predicate function
        MethodReferenceExpr nodeReference = new MethodReferenceExpr();
        nodeReference.setScope(new NameExpr(fullNodeClassName));
        nodeReference.setIdentifier("evaluateNode");
        CommonCodegenUtils.setAssignExpressionValue(body, "nodeFunction", nodeReference);
    }
}
