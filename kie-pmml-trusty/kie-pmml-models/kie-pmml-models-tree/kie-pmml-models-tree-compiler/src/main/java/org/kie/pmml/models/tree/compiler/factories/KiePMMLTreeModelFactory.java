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
package  org.kie.pmml.models.tree.compiler.factories;

import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.compiler.commons.utils.ModelUtils;
import org.kie.pmml.models.tree.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setKiePMMLModelConstructor;
import static org.kie.pmml.compiler.commons.utils.ModelUtils.getTargetFieldName;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLNodeFactory.getKiePMMLNodeSourcesMap;
import static org.kie.pmml.models.tree.compiler.utils.KiePMMLTreeModelUtils.createNodeClassName;

public class KiePMMLTreeModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelFactory.class.getName());

    static final String KIE_PMML_TREE_MODEL_TEMPLATE_JAVA = "KiePMMLTreeModelTemplate.tmpl";
    static final String KIE_PMML_TREE_MODEL_TEMPLATE = "KiePMMLTreeModelTemplate";

    private KiePMMLTreeModelFactory(){
        // Avoid instantiation
    }

    public static KiePMMLTreeModel getKiePMMLTreeModel(final DataDictionary dataDictionary,
                                                                       final TransformationDictionary transformationDictionary,
                                                                       final TreeModel model,
                                                                       final String packageName,
                                                                       final HasClassLoader hasClassLoader) {
        logger.trace("getKiePMMLTreeModel {} {}", packageName, model);
        String className = getSanitizedClassName(model.getModelName());
        Map<String, String> sourcesMap = getKiePMMLTreeModelSourcesMap(dataDictionary, transformationDictionary, model, packageName);
        String fullClassName = packageName + "." + className;
        try {
            Class<?> kiePMMLTreeModelClass = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLTreeModel) kiePMMLTreeModelClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLTreeModelSourcesMap(final DataDictionary dataDictionary,
                                                                                 final TransformationDictionary transformationDictionary,
                                                                                 final TreeModel model,
                                                                                 final String packageName) {
        logger.trace("getKiePMMLTreeModelSourcesMap {} {} {}", dataDictionary, model, packageName);
        String className = getSanitizedClassName(model.getModelName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName, KIE_PMML_TREE_MODEL_TEMPLATE_JAVA, KIE_PMML_TREE_MODEL_TEMPLATE);
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final KiePMMLNodeFactory.NodeNamesDTO nodeNamesDTO = new KiePMMLNodeFactory.NodeNamesDTO(model.getNode(), createNodeClassName(), null);
        String fullNodeClassName =  packageName + "." + nodeNamesDTO.nodeClassName;
        Map<String, String> toReturn = getKiePMMLNodeSourcesMap(nodeNamesDTO,  dataDictionary, packageName);
        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        String targetFieldName = getTargetFieldName(dataDictionary, model).orElse(null);
        setConstructor(model, dataDictionary, constructorDeclaration, targetFieldName, fullNodeClassName);
        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, transformationDictionary, model.getLocalTransformations());
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    static void setConstructor(final TreeModel treeModel, final DataDictionary dataDictionary, final ConstructorDeclaration constructorDeclaration, final String targetField, final String fullNodeClassName) {
        final List<org.kie.pmml.api.models.MiningField> miningFields = ModelUtils.convertToKieMiningFieldList(treeModel.getMiningSchema(), dataDictionary);
        final List<org.kie.pmml.api.models.OutputField> outputFields = ModelUtils.convertToKieOutputFieldList(treeModel.getOutput(), dataDictionary);
        setKiePMMLModelConstructor( getSanitizedClassName(treeModel.getModelName()), constructorDeclaration, treeModel.getModelName(), miningFields, outputFields);
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(treeModel.getMiningFunction().value());
        final BlockStmt body = constructorDeclaration.getBody();
        CommonCodegenUtils.setAssignExpressionValue(body, "targetField", new StringLiteralExpr(targetField));
        CommonCodegenUtils.setAssignExpressionValue(body, "miningFunction", new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        CommonCodegenUtils.setAssignExpressionValue(body, "pmmlMODEL", new NameExpr(PMML_MODEL.TREE_MODEL.getClass().getName() + "." + PMML_MODEL.TREE_MODEL.name()));
        // set predicate function
        MethodReferenceExpr nodeReference = new MethodReferenceExpr();
        nodeReference.setScope(new NameExpr(fullNodeClassName));
        nodeReference.setIdentifier("evaluateNode");
        CommonCodegenUtils.setAssignExpressionValue(body, "nodeFunction", nodeReference);
    }


}
