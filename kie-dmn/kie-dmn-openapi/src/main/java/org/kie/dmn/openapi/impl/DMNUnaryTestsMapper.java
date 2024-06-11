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

import org.antlr.v4.runtime.tree.ParseTree;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.codegen.feel11.ASTUnaryTestTransform;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.ast.visitor.ASTTemporalConstantVisitor;
import org.kie.dmn.feel.lang.impl.FEELBuilder;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;
import static org.kie.dmn.openapi.impl.RangeNodeSchemaMapper.populateSchemaFromListOfRanges;

public class DMNUnaryTestsMapper {

    private static final Logger LOG = LoggerFactory.getLogger(DMNUnaryTestsMapper.class);

    public static void populateSchemaFromUnaryTests(Schema toPopulate, List<DMNUnaryTest> unaryTests) {
        LOG.debug("populateSchemaFromUnaryTests {} {}", toPopulate, unaryTests);
        List<BaseNode> unaryEvaluationNodes = getUnaryEvaluationNodesFromUnaryTests(unaryTests);
        Map<Boolean, List<BaseNode>> map = unaryEvaluationNodes.stream().collect(groupingBy(RangeNode.class::isInstance));
        if (map.containsKey(true)) {
            populateSchemaFromListOfRanges(toPopulate, map.get(true).stream().map(RangeNode.class::cast).toList());
        }
        if (map.containsKey(false)) {
            List<BaseNode> enumBaseNodes = map.get(false);
            List<BaseNode> nullNodes = enumBaseNodes.stream().filter(NullNode.class::isInstance).toList();
            if (nullNodes.size() > 1) {
                throw new IllegalArgumentException("At most one null value is allowed for enum definition");
            }
            // If there is a NullNode, the item is nullable
            toPopulate.setNullable(!nullNodes.isEmpty());
            if (enumBaseNodes.size() > nullNodes.size()) {
                // Let's create enum only if there is at least one node != NullNode
                enumBaseNodes.forEach(unaryEvaluationNode -> populateSchemaFromBaseNode(toPopulate, unaryEvaluationNode));
            }

        }
    }

    static void populateSchemaFromBaseNode(Schema toPopulate, BaseNode unaryEvaluationNode) {
        LOG.debug("populateSchemaFromBaseNode {} {}", toPopulate, unaryEvaluationNode);
        if (unaryEvaluationNode instanceof InfixOpNode infixOpNode) {
            InfixOpNodeSchemaMapper.populateSchemaFromFunctionInvocationNode(toPopulate, infixOpNode);
        } else {
            BaseNodeSchemaMapper.populateSchemaFromBaseNode(toPopulate, unaryEvaluationNode);
        }
    }

    static List<BaseNode> getUnaryEvaluationNodesFromUnaryTests(List<DMNUnaryTest> unaryTests) {
        LOG.debug("getUnaryEvaluationNodesFromUnaryTests {}", unaryTests);
        List<BaseNode> baseNodes = unaryTests.stream().map(dmnUnaryTest -> getBaseNode(dmnUnaryTest.toString())).toList();
        return baseNodes.stream().map(baseNode ->  ((UnaryTestNode) ((UnaryTestListNode) baseNode).getElements().get(0)).getValue()).toList();
    }

    static BaseNode getBaseNode(String expression) {
        LOG.debug("getBaseNode {}", expression);
        FEEL feelInstance = FEELBuilder.builder().build();
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
        LOG.debug("getFEELParser {} {}", expression, ctx);
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

    private DMNUnaryTestsMapper() {
        // deliberate intention not to allow instantiation of this class.
    }
}
