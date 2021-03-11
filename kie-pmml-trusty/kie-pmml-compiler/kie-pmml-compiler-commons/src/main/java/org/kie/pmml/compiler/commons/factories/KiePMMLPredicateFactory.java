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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.Array;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.False;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.SimpleSetPredicate;
import org.dmg.pmml.True;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.enums.ARRAY_TYPE;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.DATA_TYPE;
import org.kie.pmml.api.enums.IN_NOTIN;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.commons.model.predicates.KiePMMLCompoundPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLFalsePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimplePredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLSimpleSetPredicate;
import org.kie.pmml.commons.model.predicates.KiePMMLTruePredicate;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.commons.Constants.MISSING_BODY_IN_METHOD;
import static org.kie.pmml.commons.Constants.MISSING_CONSTRUCTOR_IN_BODY;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.Constants.MISSING_EXPRESSION_IN_RETURN;
import static org.kie.pmml.commons.Constants.MISSING_METHOD_IN_CLASS;
import static org.kie.pmml.commons.Constants.MISSING_RETURN_IN_METHOD;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.api.enums.BOOLEAN_OPERATOR.SURROGATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;
import static org.kie.pmml.compiler.commons.utils.KiePMMLModelFactoryUtils.setConstructorSuperNameInvocation;

public class KiePMMLPredicateFactory {

    static final String KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA = "KiePMMLSimplePredicateTemplate.tmpl";
    static final String KIE_PMML_SIMPLE_PREDICATE_TEMPLATE = "KiePMMLSimplePredicateTemplate";
    static final String KIE_PMML_SIMPLE_SET_PREDICATE_TEMPLATE_JAVA = "KiePMMLSimpleSetPredicateTemplate.tmpl";
    static final String KIE_PMML_SIMPLE_SET_PREDICATE_TEMPLATE = "KiePMMLSimpleSetPredicateTemplate";
    static final String KIE_PMML_COMPOUND_PREDICATE_TEMPLATE_JAVA = "KiePMMLCompoundPredicateTemplate.tmpl";
    static final String KIE_PMML_COMPOUND_PREDICATE_TEMPLATE = "KiePMMLCompoundPredicateTemplate";
    static final String KIE_PMML_OPERATOR_FUNCTION_TEMPLATE = "KiePMMLOperatorFunctionTemplate.tmpl";
    static final String KIE_PMML_OPERATOR_FUNCTION = "KiePMMLOperatorFunctionTemplate";
    static final String KIE_PMML_TRUE_PREDICATE_TEMPLATE_JAVA = "KiePMMLTruePredicateTemplate.tmpl";
    static final String KIE_PMML_TRUE_PREDICATE_TEMPLATE = "KiePMMLTruePredicateTemplate";
    static final String KIE_PMML_FALSE_PREDICATE_TEMPLATE_JAVA = "KiePMMLFalsePredicateTemplate.tmpl";
    static final String KIE_PMML_FALSE_PREDICATE_TEMPLATE = "KiePMMLFalsePredicateTemplate";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLPredicateFactory.class.getName());

    private KiePMMLPredicateFactory() {
    }

    public static List<KiePMMLPredicate> getPredicates(List<Predicate> predicates, DataDictionary dataDictionary) {
        logger.trace("getPredicates {}", predicates);
        return predicates.stream().map(predicate -> getPredicate(predicate, dataDictionary)).collect(Collectors.toList());
    }

    public static KiePMMLPredicate getPredicate(Predicate predicate, DataDictionary dataDictionary) {
        logger.trace("getPredicate {}", predicate);
        if (predicate instanceof SimplePredicate) {
            final DataType dataType = dataDictionary.getDataFields().stream()
                    .filter(dataField -> dataField.getName().getValue().equals(((SimplePredicate) predicate).getField().getValue()))
                    .map(DataField::getDataType)
                    .findFirst()
                    .orElseThrow(() -> new KiePMMLException("Failed to find DataField for predicate " + ((SimplePredicate) predicate).getField().getValue()));
            return getKiePMMLSimplePredicate((SimplePredicate) predicate, dataType);
        } else if (predicate instanceof SimpleSetPredicate) {
            return getKiePMMLSimpleSetPredicate((SimpleSetPredicate) predicate);
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

    public static KiePMMLSimplePredicate getKiePMMLSimplePredicate(SimplePredicate predicate, DataType dataType) {
        return KiePMMLSimplePredicate.builder(predicate.getField().getValue(), Collections.emptyList(),
                                              OPERATOR.byName(predicate.getOperator().value()))
                .withValue(getActualValue(predicate.getValue(), dataType))
                .build();
    }

    public static KiePMMLSimpleSetPredicate getKiePMMLSimpleSetPredicate(SimpleSetPredicate predicate) {
        List<Object> values = getObjectsFromArray(predicate.getArray());
        return KiePMMLSimpleSetPredicate.builder(predicate.getField().getValue(),
                                                 Collections.emptyList(),
                                                 ARRAY_TYPE.byName(predicate.getArray().getType().value()),
                                                 IN_NOTIN.byName(predicate.getBooleanOperator().value()))
                .withValues(values)
                .build();
    }

    public static KiePMMLCompoundPredicate getKiePMMLCompoundPredicate(CompoundPredicate predicate,
                                                                       DataDictionary dataDictionary) {
        return KiePMMLCompoundPredicate.builder(Collections.emptyList(),
                                                BOOLEAN_OPERATOR.byName(predicate.getBooleanOperator().value()))
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
        logger.trace("getPredicateSourcesMap {}", kiePMMLPredicate);
        if (kiePMMLPredicate instanceof KiePMMLSimplePredicate) {
            return getKiePMMLSimplePredicateSourcesMap((KiePMMLSimplePredicate) kiePMMLPredicate, packageName);
        } else if (kiePMMLPredicate instanceof KiePMMLSimpleSetPredicate) {
            return getKiePMMLSimpleSetPredicateSourcesMap((KiePMMLSimpleSetPredicate) kiePMMLPredicate, packageName);
        } else if (kiePMMLPredicate instanceof KiePMMLCompoundPredicate) {
            return getKiePMMLCompoundPredicateSourcesMap((KiePMMLCompoundPredicate) kiePMMLPredicate, packageName);
        } else if (kiePMMLPredicate instanceof KiePMMLTruePredicate) {
            return getKiePMMLTruePredicateSourcesMap((KiePMMLTruePredicate) kiePMMLPredicate, packageName);
        } else if (kiePMMLPredicate instanceof KiePMMLFalsePredicate) {
            return getKiePMMLFalsePredicateSourcesMap((KiePMMLFalsePredicate) kiePMMLPredicate, packageName);
        } else {
            throw new KiePMMLException("Predicate of type " + kiePMMLPredicate.getClass().getName() + " not managed, " +
                                               "yet");
        }
    }

    static Map<String, String> getKiePMMLSimplePredicateSourcesMap(final KiePMMLSimplePredicate kiePMMLSimplePredicate, final String packageName) {
        String className = getSanitizedClassName(kiePMMLSimplePredicate.getId());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_SIMPLE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_SIMPLE_PREDICATE_TEMPLATE);
        ClassOrInterfaceDeclaration predicateTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = predicateTemplate.getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR,
                                                                              predicateTemplate.getName())));
        setSimplePredicateConstructor(className,
                                      kiePMMLSimplePredicate.getName(),
                                      constructorDeclaration,
                                      kiePMMLSimplePredicate.getOperator(),
                                      kiePMMLSimplePredicate.getValue());
        return Collections.singletonMap(getFullClassName(cloneCU), cloneCU.toString());
    }

    static Map<String, String> getKiePMMLSimpleSetPredicateSourcesMap(final KiePMMLSimpleSetPredicate kiePMMLSimpleSetPredicate,
                                                                      final String packageName) {
        String className = getSanitizedClassName(kiePMMLSimpleSetPredicate.getId());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_SIMPLE_SET_PREDICATE_TEMPLATE_JAVA, KIE_PMML_SIMPLE_SET_PREDICATE_TEMPLATE);
        ClassOrInterfaceDeclaration predicateTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = predicateTemplate.getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR,
                                                                              predicateTemplate.getName())));
        setSimpleSetPredicateConstructor(className,
                                         kiePMMLSimpleSetPredicate.getName(),
                                         constructorDeclaration,
                                         kiePMMLSimpleSetPredicate.getArrayType(),
                                         kiePMMLSimpleSetPredicate.getInNotIn(),
                                         kiePMMLSimpleSetPredicate.getValues());
        return Collections.singletonMap(getFullClassName(cloneCU), cloneCU.toString());
    }

    static Map<String, String> getKiePMMLCompoundPredicateSourcesMap(final KiePMMLCompoundPredicate kiePMMLCompoundPredicate, final String packageName) {
        String className = getSanitizedClassName(kiePMMLCompoundPredicate.getId());
        final Map<String, String> toReturn = new HashMap<>();
        if (kiePMMLCompoundPredicate.getKiePMMLPredicates() != null) {
            kiePMMLCompoundPredicate.getKiePMMLPredicates().forEach(kiePMMLPredicate -> toReturn.putAll(getPredicateSourcesMap(kiePMMLPredicate, packageName)));
        }
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_COMPOUND_PREDICATE_TEMPLATE_JAVA, KIE_PMML_COMPOUND_PREDICATE_TEMPLATE);
        ClassOrInterfaceDeclaration predicateTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration = predicateTemplate.getDefaultConstructor()
                .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR,
                                                                              predicateTemplate.getName())));
        Set<String> predicatesClasses = new HashSet<>();
        if (kiePMMLCompoundPredicate.getKiePMMLPredicates() != null) {
            predicatesClasses = kiePMMLCompoundPredicate.getKiePMMLPredicates().stream()
                    .map(predicate ->  packageName + "." + getSanitizedClassName(predicate.getId()))
                    .collect(Collectors.toSet());
        }
        if (!toReturn.keySet().containsAll(predicatesClasses)) {
            String missingClasses = String.join(", ", predicatesClasses);
            throw new KiePMMLException("Expected generated class " + missingClasses + " not found");
        }
        setCompoundPredicateConstructor(className,
                                        kiePMMLCompoundPredicate.getName(),
                                        constructorDeclaration,
                                        kiePMMLCompoundPredicate.getBooleanOperator(),
                                        predicatesClasses);
        toReturn.put(getFullClassName(cloneCU), cloneCU.toString());
        return toReturn;
    }

    static Map<String, String> getKiePMMLTruePredicateSourcesMap(final KiePMMLTruePredicate kiePMMLTruePredicate,
                                                                 final String packageName) {
        String className = getSanitizedClassName(kiePMMLTruePredicate.getId());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_TRUE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_TRUE_PREDICATE_TEMPLATE);
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

    static Map<String, String> getKiePMMLFalsePredicateSourcesMap(final KiePMMLFalsePredicate kiePMMLFalsePredicate,
                                                                  final String packageName) {
        String className = getSanitizedClassName(kiePMMLFalsePredicate.getId());
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className, packageName,
                                                                                 KIE_PMML_FALSE_PREDICATE_TEMPLATE_JAVA, KIE_PMML_FALSE_PREDICATE_TEMPLATE);
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
        if (operator.equals(OPERATOR.IS_MISSING) || operator.equals(OPERATOR.IS_NOT_MISSING) ) {
            throw new IllegalArgumentException(operator + " not supported, yet");
        }
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, predicateName);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement = CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        CommonCodegenUtils.setExplicitConstructorInvocationArgument(superStatement, "operator", operator.getClass().getCanonicalName() + "." + operator.name());
        Expression expression = value instanceof String ? new StringLiteralExpr((String) value) : new NameExpr(value.toString());
        CommonCodegenUtils.setAssignExpressionValue(body, "value", expression);
    }

    static void setSimpleSetPredicateConstructor(final String generatedClassName,
                                                 final String predicateName,
                                                 final ConstructorDeclaration constructorDeclaration,
                                                 final ARRAY_TYPE arrayType,
                                                 final IN_NOTIN inNotIn,
                                                 final List<Object> values) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, predicateName);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement = CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        CommonCodegenUtils.setExplicitConstructorInvocationArgument(superStatement, "arrayType", arrayType.getClass().getCanonicalName() + "." + arrayType.name());
        CommonCodegenUtils.setExplicitConstructorInvocationArgument(superStatement, "inNotIn", inNotIn.getClass().getCanonicalName() + "." + inNotIn.name());
        AssignExpr assignExpr = CommonCodegenUtils
                .getAssignExpression(body, "values")
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, "values", body)));
        ClassOrInterfaceType arrayClass = parseClassOrInterfaceType(ArrayList.class.getName());
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(arrayClass);
        assignExpr.setValue(objectCreationExpr);
        for (Object value : values) {
            Expression valueExpression;
            if (arrayType == ARRAY_TYPE.STRING) {
                valueExpression = new StringLiteralExpr(value.toString());
            } else {
                valueExpression = new NameExpr(value.toString());
            }
            NodeList<Expression> arguments = NodeList.nodeList(valueExpression);
            MethodCallExpr methodCallExpr = new MethodCallExpr();
            methodCallExpr.setScope(assignExpr.getTarget().asNameExpr());
            methodCallExpr.setName("add");
            methodCallExpr.setArguments(arguments);
            ExpressionStmt expressionStmt = new ExpressionStmt();
            expressionStmt.setExpression(methodCallExpr);
            body.addStatement(expressionStmt);
        }
    }

    static void setCompoundPredicateConstructor(final String generatedClassName,
                                                final String predicateName,
                                                final ConstructorDeclaration constructorDeclaration,
                                                final BOOLEAN_OPERATOR booleanOperator,
                                                final Set<String> predicatesClasses) {
        if (booleanOperator.equals(BOOLEAN_OPERATOR.SURROGATE)) {
            throw new IllegalArgumentException(SURROGATE + " not supported, yet");
        }
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, predicateName);
        final BlockStmt body = constructorDeclaration.getBody();
        final ExplicitConstructorInvocationStmt superStatement = CommonCodegenUtils.getExplicitConstructorInvocationStmt(body)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_CONSTRUCTOR_IN_BODY, body)));
        CommonCodegenUtils.setExplicitConstructorInvocationArgument(superStatement, "booleanOperator", booleanOperator.getClass().getCanonicalName() + "." + booleanOperator.name());
        AssignExpr assignExpr = CommonCodegenUtils
                .getAssignExpression(body, "operatorFunction")
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, "operatorFunction", body)));
        CompilationUnit operatorFunctionCU = getFromFileName(KIE_PMML_OPERATOR_FUNCTION_TEMPLATE).clone();
        ClassOrInterfaceDeclaration operatorFunctionClass = operatorFunctionCU.getClassByName(KIE_PMML_OPERATOR_FUNCTION)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND));
        String methodName = "getInnerBinaryOperator" + booleanOperator.name();
        final MethodDeclaration methodDeclaration = CommonCodegenUtils.getMethodDeclaration(operatorFunctionClass, methodName)
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_METHOD_IN_CLASS,methodName, operatorFunctionClass)));
        final BlockStmt methodBody =  methodDeclaration.getBody()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_IN_METHOD, methodDeclaration)));
        final ReturnStmt returnStmt = methodBody.getStatements()
                .stream()
                .filter(statement -> statement instanceof ReturnStmt)
                .map(statement -> (ReturnStmt) statement)
                .findFirst()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_RETURN_IN_METHOD, methodDeclaration)));
        final Expression expression = returnStmt.getExpression().orElseThrow(() -> new KiePMMLException(String.format(MISSING_EXPRESSION_IN_RETURN, returnStmt)));
        assignExpr.setValue(expression);
        assignExpr = CommonCodegenUtils
                .getAssignExpression(body, "kiePMMLPredicates")
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, "kiePMMLPredicates", body)));
        ClassOrInterfaceType arrayClass = parseClassOrInterfaceType(ArrayList.class.getName());
        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(arrayClass);
        assignExpr.setValue(objectCreationExpr);
        for (String predicateClass : predicatesClasses) {
            ClassOrInterfaceType kiePMMLPredicateClass = parseClassOrInterfaceType(predicateClass);
            objectCreationExpr = new ObjectCreationExpr();
            objectCreationExpr.setType(kiePMMLPredicateClass);
            NodeList<Expression> arguments = NodeList.nodeList(objectCreationExpr);
            MethodCallExpr methodCallExpr = new MethodCallExpr();
            methodCallExpr.setScope(assignExpr.getTarget().asNameExpr());
            methodCallExpr.setName("add");
            methodCallExpr.setArguments(arguments);
            ExpressionStmt expressionStmt = new ExpressionStmt();
            expressionStmt.setExpression(methodCallExpr);
            body.addStatement(expressionStmt);
        }
    }

    static void setTrueFalsePredicateConstructor(final String generatedClassName,
                                                 final String predicateName,
                                                 final ConstructorDeclaration constructorDeclaration) {
        setConstructorSuperNameInvocation(generatedClassName, constructorDeclaration, predicateName);
    }

    static List<Object> getObjectsFromArray(Array source) {
        Array.Type type = source.getType();
        List<Object> toReturn = new ArrayList<>();
        String stringValue = (String) source.getValue();
        String[] valuesArray = stringValue.split(" ");
        for (String s : valuesArray) {
            switch (type) {
                case INT:
                    toReturn.add(Integer.valueOf(s));
                    break;
                case STRING:
                    toReturn.add(s);
                    break;
                case REAL:
                    toReturn.add(Double.valueOf(s));
                    break;
                default:
                    throw new KiePMMLException("Unknown Array " + type);
            }
        }
        return toReturn;
    }

    private static Object getActualValue(Object rawValue, DataType dataType) {
        DATA_TYPE dataTypePmml = DATA_TYPE.byName(dataType.value());
        return dataTypePmml.getActualValue(rawValue);
    }
}
