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
package org.drools.mvel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.compiler.rule.builder.EvaluatorWrapper;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.mvel.expr.MVELCompilationUnit;
import org.drools.mvel.expr.MvelEvaluator;
import org.kie.api.runtime.rule.FactHandle;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.ast.ASTNode;
import org.mvel2.ast.And;
import org.mvel2.ast.BooleanNode;
import org.mvel2.ast.Contains;
import org.mvel2.ast.LineLabel;
import org.mvel2.ast.LiteralNode;
import org.mvel2.ast.Negation;
import org.mvel2.ast.Or;
import org.mvel2.ast.Substatement;
import org.mvel2.compiler.Accessor;
import org.mvel2.compiler.AccessorNode;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.compiler.ExecutableLiteral;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.optimizers.impl.refl.nodes.MethodAccessor;
import org.mvel2.util.ASTLinkedList;

import static org.drools.mvel.EvaluatorHelper.valuesAsMap;
import static org.drools.mvel.expr.MvelEvaluator.createMvelEvaluator;

public class MVELConditionEvaluator implements ConditionEvaluator {

    private final Declaration[] declarations;
    private final EvaluatorWrapper[] operators;

    private final String conditionClass;
    private final ParserConfiguration parserConfiguration;
    private final ExecutableStatement executableStatement;
    private final MVELCompilationUnit compilationUnit;

    private final MvelEvaluator<Boolean> evaluator;

    private boolean evaluated = false;

    public MVELConditionEvaluator( ParserConfiguration configuration,
                                   String expression,
                                   Declaration[] declarations,
                                   EvaluatorWrapper[] operators,
                                   String conditionClass) {
        this(null,
             configuration,
             (ExecutableStatement)MVEL.compileExpression(expression, new ParserContext(configuration)),
             declarations,
             operators,
             conditionClass);
    }

    public MVELConditionEvaluator( MVELCompilationUnit compilationUnit,
                                   ParserConfiguration parserConfiguration,
                                   ExecutableStatement executableStatement,
                                   Declaration[] declarations,
                                   EvaluatorWrapper[] operators,
                                   String conditionClass) {
        this.declarations = declarations;
        this.operators = operators;
        this.conditionClass = conditionClass;
        this.compilationUnit = compilationUnit;
        this.parserConfiguration = parserConfiguration;
        this.executableStatement = executableStatement;
        this.evaluator = createMvelEvaluator( executableStatement );
    }

    public boolean evaluate(FactHandle handle, ValueResolver valueResolver, BaseTuple tuple) {
        return evaluate(evaluator, handle, valueResolver, tuple);
    }

    private boolean evaluate(MvelEvaluator<Boolean> evaluator, FactHandle handle, ValueResolver valueResolver, BaseTuple tuple) {
        if (compilationUnit == null) {
            Map<String, Object> vars = valuesAsMap(handle.getObject(), valueResolver, tuple, declarations);
            if (operators.length > 0) {
                if (vars == null) {
                    vars = new HashMap<>();
                }
                FactHandle[] handles = tuple != null ? tuple.toFactHandles() : new FactHandle[0];
                for (EvaluatorWrapper operator : operators) {
                    vars.put( operator.getBindingName(), operator );
                    operator.loadHandles(handles, handle);
                }
            }
            return evaluate(evaluator, handle.getObject(), vars);
        }

        VariableResolverFactory factory = compilationUnit.createFactory();
        compilationUnit.updateFactory( handle, tuple, null, valueResolver,
                                       valueResolver.getGlobalResolver(),
                                       factory );

        return evaluator.evaluate( handle.getObject(), factory );
    }

    private boolean evaluate(MvelEvaluator<Boolean> evaluator, Object object, Map<String, Object> vars) {
        return vars == null ?
                evaluator.evaluate( object ) :
                evaluator.evaluate( object, vars );
    }

    ConditionAnalyzer.Condition getAnalyzedCondition( FactHandle handle, ValueResolver valueResolver, BaseTuple leftTuple) {
        ensureCompleteEvaluation(handle, valueResolver, leftTuple);
        return new ConditionAnalyzer(executableStatement, declarations, operators, conditionClass).analyzeCondition();
    }

    private void ensureCompleteEvaluation(FactHandle handle, ValueResolver valueResolver, BaseTuple tuple) {
        if (!evaluated) {
            ASTNode rootNode = getRootNode(executableStatement);
            if (rootNode != null) {
                ensureCompleteEvaluation(rootNode, handle, valueResolver, tuple);
            }
            evaluated = true;
        }
    }

    private void ensureCompleteEvaluation(ASTNode node, FactHandle handle, ValueResolver valueResolver, BaseTuple tuple) {
        node = unwrap(node);
        if (!(node instanceof And || node instanceof Or)) {
            evaluateIfNecessary(handle, valueResolver, tuple, node);
            return;
        }
        ensureBranchEvaluation(handle, valueResolver, tuple, ((BooleanNode)node).getLeft());
        ensureBranchEvaluation(handle, valueResolver, tuple, ((BooleanNode)node).getRight());
    }

    private void ensureBranchEvaluation(FactHandle handle, ValueResolver valueResolver, BaseTuple tuple, ASTNode node) {
        evaluateIfNecessary( handle, valueResolver, tuple, node );
        ensureCompleteEvaluation(node, handle, valueResolver, tuple);
    }

    private void evaluateIfNecessary(FactHandle handle, ValueResolver valueResolver, BaseTuple tuple, ASTNode node ) {
        if (!isEvaluated(node)) {
            ASTNode next = node.nextASTNode;
            node.nextASTNode = null;
            evaluate( createMvelEvaluator(evaluator, asCompiledExpression(node) ), handle, valueResolver, tuple);
            node.nextASTNode = next;
        }
    }

    private CompiledExpression asCompiledExpression(ASTNode node) {
        return new CompiledExpression(new ASTLinkedList(node), null, Object.class, parserConfiguration, false);
    }

    public static boolean isFullyEvaluated(Serializable executableStatement) {
        return isEvaluated( getRootNode( executableStatement ) );
    }

    private static boolean isEvaluated(ASTNode node) {
        node = unwrapSubstatement(node);
        if (node == null) {
            return true;
        }

        if (node instanceof Contains) {
            return ((Contains)node).getFirstStatement().getAccessor() != null;
        }

        if (node instanceof BooleanNode) {
            return isEvaluated(((BooleanNode) node).getLeft()) && isEvaluated(((BooleanNode) node).getRight());
        }

        Accessor accessor = node.getAccessor();
        if (accessor == null) {
            return node instanceof LiteralNode;
        }

        if (accessor instanceof AccessorNode) {
            AccessorNode nextNode = ((AccessorNode) accessor).getNextNode();
            if (nextNode instanceof MethodAccessor && ((MethodAccessor) nextNode).getParms() != null) {
                for (ExecutableStatement param : ((MethodAccessor) nextNode).getParms()) {
                    if (!isFullyEvaluated(param)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static ASTNode getRootNode(Serializable executableStatement) {
        if (executableStatement instanceof ExecutableLiteral) {
            return null;
        }
        return executableStatement instanceof CompiledExpression ? ((CompiledExpression) executableStatement).getFirstNode() : ((ExecutableAccessor) executableStatement).getNode();
    }

    private static ASTNode unwrap(ASTNode node) {
        while (node instanceof Negation || node instanceof LineLabel || node instanceof Substatement) {
            node = unwrapNegation(node);
            node = unwrapSubstatement(node);
        }
        return node;
    }

    private static ASTNode unwrapNegation(ASTNode node) {
        if (node instanceof Negation) {
            ExecutableStatement statement = ((Negation)node).getStatement();
            return statement instanceof ExecutableAccessor ? ((ExecutableAccessor)statement).getNode() : null;
        }
        return node;
    }

    private static ASTNode unwrapSubstatement(ASTNode node) {
        if (node instanceof LineLabel) {
            return node.nextASTNode;
        }
        return node instanceof Substatement ? ((ExecutableAccessor)((Substatement)node).getStatement()).getNode() : node;
    }
}
