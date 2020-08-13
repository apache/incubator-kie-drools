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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.kie.pmml.commons.exceptions.KieDataFieldException;
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

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;

public class KiePMMLPredicateFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLPredicateFactory.class.getName());
    static final String KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA = "KiePMMLSimplePredicateTemplate.tmpl";
    static final String KIE_PMML_SIMPLE_PREDICATE_TEMPLATE = "KiePMMLSimplePredicateTemplate";
    static final String KIE_PMML_TRUE_PREDICATE_TEMPLATE_JAVA = "KiePMMLTruePredicateTemplate.tmpl";
    static final String KIE_PMML_TRUE_PREDICATE_TEMPLATE = "KiePMMLTruePredicateTemplate";
    static final String KIE_PMML_FALSE_PREDICATE_TEMPLATE_JAVA = "KiePMMLFalsePredicateTemplate.tmpl";
    static final String KIE_PMML_FALSE_PREDICATE_TEMPLATE = "KiePMMLFalsePredicateTemplate";


    private KiePMMLPredicateFactory() {
    }

    public static List<KiePMMLPredicate> getPredicates(List<Predicate> predicates, DataDictionary dataDictionary) throws KiePMMLException {
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
            return getKiePMMLTruePredicate((True) predicate);
        } else if (predicate instanceof False) {
            return getKiePMMLFalsePredicate((False) predicate);
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

    public static KiePMMLTruePredicate getKiePMMLTruePredicate(True predicate) throws KiePMMLException {
        return KiePMMLTruePredicate.builder(Collections.emptyList())
                .build();
    }

    public static KiePMMLFalsePredicate getKiePMMLFalsePredicate(False predicate) throws KiePMMLException {
        return KiePMMLFalsePredicate.builder(Collections.emptyList())
                .build();
    }

    public static Map<String, String> getPredicateSourcesMap(final KiePMMLPredicate kiePMMLPredicate,
                                                             final String packageName) {
        logger.info("getPredicateSourcesMap {}", kiePMMLPredicate);
        if (kiePMMLPredicate instanceof KiePMMLSimplePredicate) {
            return getKiePMMLSimplePredicateSourcesMap((KiePMMLSimplePredicate) kiePMMLPredicate, packageName);
//        } else if (predicate instanceof CompoundPredicate) {
//            return getKiePMMLCompoundPredicate((CompoundPredicate) predicate, dataDictionary);
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
                .orElseThrow(() -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration %s ", predicateTemplate.getName())));
        setSimplePredicateConstructor(className,
                                      kiePMMLSimplePredicate.getName(),
                                      constructorDeclaration,
                                      kiePMMLSimplePredicate.getOperator(),
                                      kiePMMLSimplePredicate.getValue());
        return Collections.singletonMap(getFullClassName(cloneCU), cloneCU.toString());
    }

    static Map<String, String> getKiePMMLTruePredicateSourcesMap(final KiePMMLTruePredicate kiePMMLTruePredicate, final String packageName)  {
        String className = getSanitizedClassName(kiePMMLTruePredicate.getName());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName, KIE_PMML_TRUE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_TRUE_PREDICATE_TEMPLATE);
        ClassOrInterfaceDeclaration predicateTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = predicateTemplate.getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(
                        "Missing default constructor in ClassOrInterfaceDeclaration %s ", predicateTemplate.getName())));
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
                        "Missing default constructor in ClassOrInterfaceDeclaration %s ", predicateTemplate.getName())));
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
        constructorDeclaration.setName(generatedClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        body.getStatements().iterator().forEachRemaining(statement -> {
            if (statement instanceof ExplicitConstructorInvocationStmt) {
                ExplicitConstructorInvocationStmt superStatement = (ExplicitConstructorInvocationStmt) statement;
                NameExpr nameExpr = (NameExpr) superStatement.getArgument(0);
                nameExpr.setName(String.format("\"%s\"", predicateName));
                nameExpr = (NameExpr) superStatement.getArgument(2);
                nameExpr.setName(operator.getClass().getSimpleName() + "." + operator.name());
            }
        });
        final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
        assignExprs.forEach(assignExpr -> {
            if (assignExpr.getTarget().asNameExpr().getNameAsString().equals("value")) {
                assignExpr.setValue(new StringLiteralExpr(value.toString()));
            }
        });
    }

    static void setTrueFalsePredicateConstructor(final String generatedClassName,
                                              final String predicateName,
                                              final ConstructorDeclaration constructorDeclaration) {
        constructorDeclaration.setName(generatedClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        body.getStatements().iterator().forEachRemaining(statement -> {
            if (statement instanceof ExplicitConstructorInvocationStmt) {
                ExplicitConstructorInvocationStmt superStatement = (ExplicitConstructorInvocationStmt) statement;
                NameExpr nameExpr = (NameExpr) superStatement.getArgument(0);
                nameExpr.setName(String.format("\"%s\"", predicateName));
            }
        });
    }

    private static Object getActualValue(Object rawValue, DataType dataType) throws KiePMMLException {
        DATA_TYPE data_type = DATA_TYPE.byName(dataType.value());
        final Class<?> mappedClass = data_type.getMappedClass();
        if (mappedClass.isAssignableFrom(rawValue.getClass())) {
            // No cast/transformation needed
            return rawValue;
        }
        if (rawValue instanceof String) {
            String stringValue = (String) rawValue;
            try {
                switch (data_type) {
                    case STRING:
                        return stringValue;
                    case INTEGER:
                        return Integer.parseInt(stringValue);
                    case FLOAT:
                        return Float.parseFloat(stringValue);
                    case DOUBLE:
                        return Double.parseDouble(stringValue);
                    case BOOLEAN:
                        return Boolean.parseBoolean(stringValue);
                    case DATE:
                        return LocalDate.parse(stringValue);
                    case TIME:
                        return LocalTime.parse(stringValue);
                    case DATE_TIME:
                        return LocalDateTime.parse(stringValue);
                    case DATE_DAYS_SINCE_0:
                    case DATE_DAYS_SINCE_1960:
                    case DATE_DAYS_SINCE_1970:
                    case DATE_DAYS_SINCE_1980:
                    case TIME_SECONDS:
                    case DATE_TIME_SECONDS_SINCE_0:
                    case DATE_TIME_SECONDS_SINCE_1960:
                    case DATE_TIME_SECONDS_SINCE_1970:
                    case DATE_TIME_SECONDS_SINCE_1980:
                        return Long.parseLong(stringValue);
                }
            } catch (Exception e) {
                throw new KieDataFieldException("Fail to convert " + rawValue + "[" + rawValue.getClass().getName() + "] to expected class " + mappedClass.getName(), e);
            }
        }
        throw new KieDataFieldException("Unexpected " + rawValue + "[" + rawValue.getClass().getName() + "] to convert");
    }
}
