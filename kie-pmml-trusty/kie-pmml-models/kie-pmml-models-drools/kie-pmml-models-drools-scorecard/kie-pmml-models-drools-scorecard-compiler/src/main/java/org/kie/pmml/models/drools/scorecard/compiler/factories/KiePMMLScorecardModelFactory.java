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
package org.kie.pmml.models.drools.scorecard.compiler.factories;

import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.scorecard.Scorecard;
import org.kie.memorycompiler.KieMemoryCompiler;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsAST;
import org.kie.pmml.models.drools.scorecard.model.KiePMMLScorecardModel;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.models.drools.utils.KiePMMLDroolsModelFactoryUtils.getKiePMMLModelCompilationUnit;

/**
 * Class used to generate <code>KiePMMLScorecard</code> out of a <code>DataDictionary</code> and a <code>ScorecardModel</code>
 */
public class KiePMMLScorecardModelFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLScorecardModelFactory.class.getName());

    private static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA = "KiePMMLScorecardModelTemplate.tmpl";
    private static final String KIE_PMML_SCORECARD_MODEL_TEMPLATE = "KiePMMLScorecardModelTemplate";

    private KiePMMLScorecardModelFactory() {
        // Avoid instantiation
    }

    public static KiePMMLScorecardModel getKiePMMLScorecardModel(DataDictionary dataDictionary, Scorecard model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) throws IllegalAccessException, InstantiationException {
        logger.trace("getKiePMMLScorecardModel {}", model);
        String className = getSanitizedClassName(model.getModelName());
        String packageName = getSanitizedPackageName(className);
        Map<String, String> sourcesMap = getKiePMMLScorecardModelSourcesMap(dataDictionary, model, fieldTypeMap, packageName);
        String fullClassName = packageName + "." + className;
        final Map<String, Class<?>> compiledClasses = KieMemoryCompiler.compile(sourcesMap, Thread.currentThread().getContextClassLoader());
        return (KiePMMLScorecardModel) compiledClasses.get(fullClassName).newInstance();
    }

    public static Map<String, String> getKiePMMLScorecardModelSourcesMap(final DataDictionary dataDictionary, final Scorecard model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final String packageName) {
        logger.trace("getKiePMMLScorecardModelSourcesMap {} {} {}", dataDictionary, model, packageName);
        CompilationUnit cloneCU = getKiePMMLModelCompilationUnit(dataDictionary, model, fieldTypeMap, packageName, KIE_PMML_SCORECARD_MODEL_TEMPLATE_JAVA, KIE_PMML_SCORECARD_MODEL_TEMPLATE);
        String className = getSanitizedClassName(model.getModelName());
        ClassOrInterfaceDeclaration modelTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration %s ", modelTemplate.getName())));
        setSuperInvocation(model, constructorDeclaration, modelTemplate.getName());
        Map<String, String> toReturn = new HashMap<>();
        String fullClassName = packageName + "." + className;
        toReturn.put(fullClassName, cloneCU.toString());
        return toReturn;
    }

    /**
     * This method returns a <code>KiePMMLDroolsAST</code> out of the given <code>DataDictionary</code> and <code>Scorecard</code>.
     * <b>It also populate the given <code>Map</code> that has to be used for final <code>KiePMMLScorecardModel</code></b>
     * @param dataDictionary
     * @param model
     * @param fieldTypeMap
     * @return
     */
    public static KiePMMLDroolsAST getKiePMMLDroolsAST(DataDictionary dataDictionary, Scorecard model, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        logger.trace("getKiePMMLDroolsAST {}", model);
        return KiePMMLScorecardModelASTFactory.getKiePMMLDroolsAST(dataDictionary, model, fieldTypeMap);
    }

    private static void setSuperInvocation(final Scorecard scorecard, final ConstructorDeclaration constructorDeclaration, final SimpleName tableName) {
        constructorDeclaration.setName(tableName);
        final BlockStmt body = constructorDeclaration.getBody();
        body.getStatements().iterator().forEachRemaining(statement -> {
            if (statement instanceof ExplicitConstructorInvocationStmt) {
                ExplicitConstructorInvocationStmt superStatement = (ExplicitConstructorInvocationStmt) statement;
                NameExpr modelNameExpr = (NameExpr) superStatement.getArgument(0);
                modelNameExpr.setName(String.format("\"%s\"", scorecard.getModelName()));
            }
        });
    }
}
