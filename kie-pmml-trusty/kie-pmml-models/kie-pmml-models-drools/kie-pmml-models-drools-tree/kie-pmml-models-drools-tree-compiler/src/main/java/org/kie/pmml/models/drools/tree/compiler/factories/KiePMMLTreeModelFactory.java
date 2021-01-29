/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.drools.tree.compiler.factories;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.tree.TreeModel;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.HasClassLoader;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.ModelUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsType;
import org.kie.pmml.models.drools.tree.model.KiePMMLTreeModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.addTransformationsInClassOrInterfaceDeclaration;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setKiePMMLModelConstructor;
import static org.kie.pmml.models.drools.utils.KiePMMLDroolsModelFactoryUtils.getKiePMMLModelCompilationUnit;

/**
 * Class used to generate <code>KiePMMLTreeModel</code> out of a <code>DataDictionary</code> and a <code>TreeModel</code>
 */
public class KiePMMLTreeModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelFactory.class.getName());

    static final String KIE_PMML_TREE_MODEL_TEMPLATE_JAVA = "KiePMMLTreeModelTemplate.tmpl";
    static final String KIE_PMML_TREE_MODEL_TEMPLATE = "KiePMMLTreeModelTemplate";

    private KiePMMLTreeModelFactory() {
        // Avoid instantiation
    }

    public static KiePMMLTreeModel getKiePMMLTreeModel(final DataDictionary dataDictionary,
                                                       final TransformationDictionary transformationDictionary,
                                                       final TreeModel model,
                                                       final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                       final String packageName,
                                                       final HasClassLoader hasClassLoader) throws IllegalAccessException, InstantiationException {
        logger.trace("getKiePMMLTreeModel {} {}", packageName, model);
        String className = getSanitizedClassName(model.getModelName());
        Map<String, String> sourcesMap = getKiePMMLTreeModelSourcesMap(dataDictionary, transformationDictionary, model, fieldTypeMap, packageName);
        String fullClassName = packageName + "." + className;
        try {
            Class<?> kiePMMLScorecardModelClass = hasClassLoader.compileAndLoadClass(sourcesMap, fullClassName);
            return (KiePMMLTreeModel) kiePMMLScorecardModelClass.newInstance();
        } catch (Exception e) {
            throw new KiePMMLException(e);
        }
    }

    public static Map<String, String> getKiePMMLTreeModelSourcesMap(final DataDictionary dataDictionary,
                                                                    final TransformationDictionary transformationDictionary,
                                                                    final TreeModel model,
                                                                    final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                                    final String packageName) {
        logger.trace("getKiePMMLTreeModelSourcesMap {} {} {}", dataDictionary, model, packageName);
        CompilationUnit cloneCU = getKiePMMLModelCompilationUnit(dataDictionary, model, fieldTypeMap, packageName, KIE_PMML_TREE_MODEL_TEMPLATE_JAVA, KIE_PMML_TREE_MODEL_TEMPLATE);
        String className = getSanitizedClassName(model.getModelName());
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, modelTemplate.getName())));
        setConstructor(model, dataDictionary, constructorDeclaration, modelTemplate.getName());
        addTransformationsInClassOrInterfaceDeclaration(modelTemplate, transformationDictionary, model.getLocalTransformations());
        Map<String, String> toReturn = new HashMap<>();
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    /**
     * This method returns a <code>KiePMMLDroolsAST</code> out of the given <code>DataDictionary</code> and <code>TreeModel</code>.
     * <b>It also populate the given <code>Map</code> that has to be used for final <code>KiePMMLTreeModel</code></b>
     *
     * @param dataDictionary
     * @param model
     * @param fieldTypeMap
     * @param types
     * @return
     */
    public static KiePMMLDroolsAST getKiePMMLDroolsAST(final DataDictionary dataDictionary,
                                                       final TreeModel model,
                                                       final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap,
                                                       final List<KiePMMLDroolsType> types) {
        logger.trace("getKiePMMLDroolsAST {}", model);
        return KiePMMLTreeModelASTFactory.getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap, types);
    }

    static void setConstructor(final TreeModel treeModel, final DataDictionary dataDictionary, final ConstructorDeclaration constructorDeclaration, final SimpleName modelName) {
        final List<org.kie.pmml.api.models.MiningField> miningFields = ModelUtils.convertToKieMiningFieldList(treeModel.getMiningSchema(), dataDictionary);
        final List<org.kie.pmml.api.models.OutputField> outputFields = ModelUtils.convertToKieOutputFieldList(treeModel.getOutput(), dataDictionary);
        setKiePMMLModelConstructor(modelName.asString(), constructorDeclaration, treeModel.getModelName(), miningFields, outputFields);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement = CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        CommonCodegenUtils.setExplicitConstructorInvocationArgument(superStatement, "algorithmName", String.format("\"%s\"", treeModel.getAlgorithmName()));
    }
}
