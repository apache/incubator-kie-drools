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
package org.kie.dmn.openapi.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.codegen.feel11.ASTUnaryTestTransform;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.visitor.ASTTemporalConstantVisitor;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;
import static org.kie.dmn.openapi.impl.RangeNodeSchemaMapper.populateSchemaFromListOfRanges;

public class FEELSchemaEnum {

    private static final Logger LOG = LoggerFactory.getLogger(FEELSchemaEnum.class);

//    public static void parseValuesIntoSchema(Schema schema, List<DMNUnaryTest> unaryTests) {
//        List<Object> expectLiterals = evaluateUnaryTests(schema, unaryTests);
//        try {
//            checkEvaluatedUnaryTestsForTypeConsistency(expectLiterals);
//        } catch (IllegalArgumentException e) {
//            LOG.warn("Unable to parse generic value into the JSON Schema for enumeration");
//            return;
//        }
//        if (expectLiterals.contains(null)) {
//            schema.setNullable(true);
//        }
//        boolean allLiterals = !expectLiterals.isEmpty() && expectLiterals.stream().allMatch(o -> o == null || o instanceof String || o instanceof Number || o instanceof Boolean);
//        if (allLiterals) {
//            schema.enumeration(expectLiterals);
//        } else {
//            LOG.warn("Unable to parse generic value into the JSON Schema for enumeration");
//        }
//    }
//
//    public static void parseRangeableValuesIntoSchema(Schema schema, List<DMNUnaryTest> list, Class<?> expectedType) {
//        List<Object> uts = evaluateUnaryTests(schema, list); // we leverage the property of the *base* FEEL grammar(non visited by ASTVisitor, only the ParseTree->AST Visitor) that `>x` is a Range
//        boolean allowNull = uts.remove(null);
//        if (allowNull) {
//            schema.setNullable(true);
//        }
//        if (uts.size() <= 2 && uts.stream().allMatch(o -> o instanceof Range)) {
////            Range range = consolidateRanges((List) uts); // cast intentional.
////            if (range != null) {
////                if (range.getLowEndPoint() != null) {
////                    schema.minimum((BigDecimal) range.getLowEndPoint());
////                    schema.exclusiveMinimum(range.getLowBoundary() == RangeBoundary.OPEN);
////                }
////                if (range.getHighEndPoint() != null) {
////                    schema.maximum((BigDecimal) range.getHighEndPoint());
////                    schema.exclusiveMaximum(range.getHighBoundary() == RangeBoundary.OPEN);
////                }
////            }
//        } else if (uts.stream().allMatch(expectedType::isInstance)) {
//            if (allowNull) {
//                uts.add(null);
//            }
//            schema.enumeration(uts);
//        } else {
//            LOG.warn("Unable to parse {} value into the JSON Schema for enumeration", expectedType);
//        }
//    }

    public static void populateSchemaFromUnaryTests(Schema toPopulate, List<DMNUnaryTest> unaryTests) {
        List<BaseNode> unaryEvaluationNodes = getUnaryEvaluationNodesFromUnaryTests(unaryTests);
        Map<Boolean, List<BaseNode>> map = unaryEvaluationNodes.stream().collect(groupingBy(baseNode -> baseNode instanceof RangeNode));
        if (map.containsKey(true)) {
            populateSchemaFromListOfRanges(toPopulate, map.get(true).stream().map(RangeNode.class::cast).toList());
        }
        if (map.containsKey(false)) {
            map.get(false).forEach(unaryEvaluationNode ->populateSchemaFromBaseNode(toPopulate, unaryEvaluationNode));
        }
    }

    static void populateSchemaFromBaseNode(Schema toPopulate, BaseNode unaryEvaluationNode) {
        if (unaryEvaluationNode instanceof InfixOpNode infixOpNode) {
            populateSchemaFromFunctionInvocationNode(toPopulate, infixOpNode);
        } else {
            BaseNodeSchemaMapper.populateSchemaFromBaseNode(unaryEvaluationNode, toPopulate);
        }
    }

    static void populateSchemaFromFunctionInvocationNode(Schema schema, InfixOpNode infixOpNode) {
        String functionString =  ((FunctionInvocationNode) infixOpNode.getLeft()).getName().getText();
        FEELFunction function = BuiltInFunctions.getFunction(functionString);
        InfixOperator operator = infixOpNode.getOperator();
        Object rightValue = infixOpNode.getRight();
        if (rightValue instanceof NumberNode numberNode) {
            rightValue = numberNode.getValue();
        }
        FEELFunctionSchemaMapper.populateSchemaFromFEELFunction(function, operator, rightValue, schema);
    }

    static List<BaseNode> getUnaryEvaluationNodesFromUnaryTests(List<DMNUnaryTest> unaryTests) {
        List<BaseNode> baseNodes = unaryTests.stream().map(dmnUnaryTest -> getBaseNode(dmnUnaryTest.toString())).toList();
        return baseNodes.stream().map(baseNode ->  ((UnaryTestNode) ((UnaryTestListNode) baseNode).getElements().get(0)).getValue()).toList();
    }

    /**
     * Method used to verify if the given <code>List</code> contains at most one <code>null</code>,
     * since those should be put in the "enum" attribute
     *
     * @param toCheck
     */
    static void checkEvaluatedUnaryTestsForNull(List<Object> toCheck) {
        if (toCheck.stream().filter(Objects::isNull).toList().size() > 1) {
            throw new IllegalArgumentException("More then one object is null, only one allowed at maximum");
        }
    }

    /**
     * Method used to verify if the given <code>List</code> contains the same type of <code>Object</code>s,
     * since those should be put in the "enum" attribute
     *
     * @param toCheck
     */
    static void checkEvaluatedUnaryTestsForTypeConsistency(List<Object> toCheck) {
        if (toCheck.stream().filter(Objects::nonNull)
                .map(Object::getClass)
                .collect(Collectors.toUnmodifiableSet())
                .size() > 1) {
            throw new IllegalArgumentException("Different types of objects, only one allowed");
        }
    }

    static BaseNode getBaseNode(String expression) {
        FEEL feelInstance = FEEL.newInstance();
        CompilerContext ctx = feelInstance.newCompilerContext();
        ParseTree tree = getFEELParser(expression, ctx).unaryTestsRoot();
        ASTBuilderVisitor astVisitor = new ASTBuilderVisitor(ctx.getInputVariableTypes(),
                                                             ctx.getFEELFeelTypeRegistry());
        BaseNode initialAst = tree.accept(astVisitor);
        BaseNode toReturn = initialAst.accept(new ASTUnaryTestTransform()).node();
        if (astVisitor.isVisitedTemporalCandidate()) {
            toReturn.accept(new ASTTemporalConstantVisitor(ctx));
        }
        return toReturn;
    }

    static FEEL_1_1Parser getFEELParser(String expression, CompilerContext ctx) {
        FEELEventListenersManager eventsManager =
                new FEELEventListenersManager();

        return FEELParser.parse(
                eventsManager,
                expression,
                ctx.getInputVariableTypes(),
                ctx.getInputVariables(),
                ctx.getFEELFunctions(),
                Collections.emptyList(),
                ctx.getFEELFeelTypeRegistry());
    }

    private FEELSchemaEnum() {
        // deliberate intention not to allow instantiation of this class.
    }
}
