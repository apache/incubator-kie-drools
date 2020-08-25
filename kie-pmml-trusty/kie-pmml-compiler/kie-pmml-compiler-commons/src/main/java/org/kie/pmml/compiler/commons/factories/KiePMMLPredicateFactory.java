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
package org.kie.pmml.compiler.commons.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.False;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.True;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.commons.model.enums.DATA_TYPE;
import org.kie.pmml.commons.model.enums.OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;

public class KiePMMLPredicateFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLPredicateFactory.class.getName());
    static final String KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA = "KiePMMLSimplePredicateTemplate.tmpl";
    static final String KIE_PMML_SIMPLE_PREDICATE_TEMPLATE = "KiePMMLSimplePredicateTemplate";
    static final String KIE_PMML_COMPOUND_PREDICATE_TEMPLATE_JAVA = "KiePMMLCompoundPredicateTemplate.tmpl";
    static final String KIE_PMML_COMPOUND_PREDICATE_TEMPLATE = "KiePMMLCompoundPredicateTemplate";
    static final String KIE_PMML_TRUE_PREDICATE_TEMPLATE_JAVA = "KiePMMLTruePredicateTemplate.tmpl";
    static final String KIE_PMML_TRUE_PREDICATE_TEMPLATE = "KiePMMLTruePredicateTemplate";
    static final String KIE_PMML_FALSE_PREDICATE_TEMPLATE_JAVA = "KiePMMLFalsePredicateTemplate.tmpl";
    static final String KIE_PMML_FALSE_PREDICATE_TEMPLATE = "KiePMMLFalsePredicateTemplate";


    private KiePMMLPredicateFactory() {
    }

    public static List<KiePMMLPredicate> getPredicates(List<Predicate> predicates, DataDictionary dataDictionary) {
        logger.info("getPredicates {}", predicates);
        return predicates.stream().map(predicate -> getPredicate(predicate, dataDictionary)).collect(Collectors.toList());
    }

    public static KiePMMLPredicate getPredicate(Predicate predicate, DataDictionary dataDictionary) {
        logger.info("getPredicate {}", predicate);
        if (predicate instanceof SimplePredicate) {
            final DataType dataType = dataDictionary.getDataFields().stream()
                    .filter(dataField -> dataField.getName().getValue().equals(((SimplePredicate) predicate).getField().getValue()))
                    .map(DataField::getDataType)
                    .findFirst()
                    .orElseThrow(() -> new KiePMMLException("Failed to find DataField for predicate " + ((SimplePredicate) predicate).getField().getValue()));
            return getKiePMMLSimplePredicate((SimplePredicate) predicate, dataType);
        } else if (predicate instanceof CompoundPredicate) {
            return getKiePMMLCompoundPredicate((CompoundPredicate) predicate, dataDictionary);
        } else if (predicate instanceof True) {
            return getKiePMMLTruePredicate();
        } else if (predicate instanceof False) {
            return getKiePMMLFalsePredicate();
        } else {
            throw new KiePMMLException("Predicate of type " + predicate.getClass().getName() + " not managed, yet");
        }
    }

    public static KiePMMLSimplePredicate getKiePMMLSimplePredicate(SimplePredicate predicate, DataType dataType)  {
        return KiePMMLSimplePredicate.builder(predicate.getField().getValue(), Collections.emptyList(), OPERATOR.byName(predicate.getOperator().value()))
                .withValue(getActualValue(predicate.getValue(), dataType))
                .build();
    }

    public static KiePMMLCompoundPredicate getKiePMMLCompoundPredicate(CompoundPredicate predicate, DataDictionary dataDictionary) {
        return KiePMMLCompoundPredicate.builder(Collections.emptyList(), BOOLEAN_OPERATOR.byName(predicate.getBooleanOperator().value()))
                .withKiePMMLPredicates(getPredicates(predicate.getPredicates(), dataDictionary))
                .build();
    }

    public static KiePMMLTruePredicate getKiePMMLTruePredicate() {
        return KiePMMLTruePredicate.builder(Collections.emptyList())
                .build();
    }

    public static KiePMMLFalsePredicate getKiePMMLFalsePredicate() {
        return KiePMMLFalsePredicate.builder(Collections.emptyList())
                .build();
    }

    public static Map<String, String> getPredicateSourcesMap(final KiePMMLPredicate kiePMMLPredicate,
                                                             final String packageName) {
        logger.info("getPredicateSourcesMap {}", kiePMMLPredicate);
        if (kiePMMLPredicate instanceof KiePMMLSimplePredicate) {
            return getKiePMMLSimplePredicateSourcesMap((KiePMMLSimplePredicate) kiePMMLPredicate, packageName);
        } else if (kiePMMLPredicate instanceof KiePMMLCompoundPredicate) {
            return getKiePMMLCompoundPredicateSourcesMap((KiePMMLCompoundPredicate) kiePMMLPredicate, packageName);
        } else if (kiePMMLPredicate instanceof KiePMMLTruePredicate) {
            return getKiePMMLTruePredicateSourcesMap((KiePMMLTruePredicate) kiePMMLPredicate, packageName);
        } else if (kiePMMLPredicate instanceof KiePMMLFalsePredicate) {
            return getKiePMMLFalsePredicateSourcesMap((KiePMMLFalsePredicate) kiePMMLPredicate, packageName);
        } else {
            throw new KiePMMLException("Predicate of type " + kiePMMLPredicate.getClass().getName() + " not managed, yet");
        }
    }

    static Map<String, String> getKiePMMLSimplePredicateSourcesMap(final KiePMMLSimplePredicate kiePMMLSimplePredicate, final String packageName)  {
        String className = getSanitizedClassName(kiePMMLSimplePredicate.getName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName, KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_SIMPLE_PREDICATE_TEMPLATE);
        ClassOrInterfaceDeclaration predicateTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = predicateTemplate.getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, predicateTemplate.getName())));
        setSimplePredicateConstructor(className,
                                      kiePMMLSimplePredicate.getName(),
                                      constructorDeclaration,
                                      kiePMMLSimplePredicate.getOperator(),
                                      kiePMMLSimplePredicate.getValue());
        return Collections.singletonMap(getFullClassName(cloneCU), cloneCU.toString());
    }

    static Map<String, String> getKiePMMLCompoundPredicateSourcesMap(final KiePMMLCompoundPredicate kiePMMLCompoundPredicate, final String packageName)  {
        String className = getSanitizedClassName(kiePMMLCompoundPredicate.getName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName, KIE_PMML_COMPOUND_PREDICATE_TEMPLATE_JAVA, KIE_PMML_COMPOUND_PREDICATE_TEMPLATE);
        ClassOrInterfaceDeclaration predicateTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = predicateTemplate.getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, predicateTemplate.getName())));
        setCompoundPredicateConstructor(className,
                                      kiePMMLCompoundPredicate.getName(),
                                      constructorDeclaration,
                                      kiePMMLCompoundPredicate.getBooleanOperator());
        return Collections.singletonMap(getFullClassName(cloneCU), cloneCU.toString());
    }

    static Map<String, String> getKiePMMLTruePredicateSourcesMap(final KiePMMLTruePredicate kiePMMLTruePredicate, final String packageName)  {
        String className = getSanitizedClassName(kiePMMLTruePredicate.getName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName, KIE_PMML_TRUE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_TRUE_PREDICATE_TEMPLATE);
        ClassOrInterfaceDeclaration predicateTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = predicateTemplate.getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(
                        MISSING_DEFAULT_CONSTRUCTOR, predicateTemplate.getName())));
        setTrueFalsePredicateConstructor(className,
                                         kiePMMLTruePredicate.getName(),
                                      constructorDeclaration);
        return Collections.singletonMap(getFullClassName(cloneCU), cloneCU.toString());
    }

    static Map<String, String> getKiePMMLFalsePredicateSourcesMap(final KiePMMLFalsePredicate kiePMMLFalsePredicate, final String packageName)  {
        String className = getSanitizedClassName(kiePMMLFalsePredicate.getName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName, KIE_PMML_FALSE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_FALSE_PREDICATE_TEMPLATE);
        ClassOrInterfaceDeclaration predicateTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = predicateTemplate.getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(
                        MISSING_DEFAULT_CONSTRUCTOR, predicateTemplate.getName())));
        setTrueFalsePredicateConstructor(className,
                                         kiePMMLFalsePredicate.getName(),
                                         constructorDeclaration);
        return Collections.singletonMap(getFullClassName(cloneCU), cloneCU.toString());
    }

    static void setSimplePredicateConstructor(final String generatedClassName,
                                              final String predicateName,
                                              final ConstructorDeclaration constructorDeclaration,
                                              final OPERATOR operator,
                                              final Object value) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, predicateName);
        final BlockStmt body = constructorDeclaration.getBody();
        body.getStatements().iterator().forEachRemaining(statement -> {
            if (statement instanceof ExplicitConstructorInvocationStmt) {
                ExplicitConstructorInvocationStmt superStatement = (ExplicitConstructorInvocationStmt) statement;
                NameExpr nameExpr = (NameExpr) superStatement.getArgument(2);
                nameExpr.setName(operator.getClass().getCanonicalName() + "." + operator.name());
            }
        });
        final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
        assignExprs.forEach(assignExpr -> {
            if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("value")) {
                assignExpr.setValue(new StringLiteralExpr(value.toString()));
            }
        });
    }

    static void setCompoundPredicateConstructor(final String generatedClassName,
                                              final String predicateName,
                                              final ConstructorDeclaration constructorDeclaration,
                                              final BOOLEAN_OPERATOR booleanOperator) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, predicateName);
        final BlockStmt body = constructorDeclaration.getBody();
        body.getStatements().iterator().forEachRemaining(statement -> {
            if (statement instanceof ExplicitConstructorInvocationStmt) {
                ExplicitConstructorInvocationStmt superStatement = (ExplicitConstructorInvocationStmt) statement;
                NameExpr nameExpr = (NameExpr) superStatement.getArgument(2);
                nameExpr.setName(booleanOperator.getClass().getCanonicalName() + "." + booleanOperator.name());
            }
        });
    }

    static void setTrueFalsePredicateConstructor(final String generatedClassName,
                                              final String predicateName,
                                              final ConstructorDeclaration constructorDeclaration) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, predicateName);
    }

    private static Object getActualValue(Object rawValue, DataType dataType) {
        DATA_TYPE dataTypePmml = DATA_TYPE.byName(dataType.value());
        return dataTypePmml.getActualValue(rawValue);
   }
}
