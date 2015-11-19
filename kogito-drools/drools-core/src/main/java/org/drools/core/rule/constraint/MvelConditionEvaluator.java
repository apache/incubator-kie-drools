/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.rule.constraint;

import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Tuple;
import org.drools.core.util.MVELSafeHelper;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.ast.ASTNode;
import org.mvel2.ast.And;
import org.mvel2.ast.BinaryOperation;
import org.mvel2.ast.BooleanNode;
import org.mvel2.ast.Contains;
import org.mvel2.ast.LineLabel;
import org.mvel2.ast.Negation;
import org.mvel2.ast.Or;
import org.mvel2.ast.Substatement;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.compiler.ExecutableLiteral;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.util.ASTLinkedList;

import java.util.HashMap;
import java.util.Map;

import static org.drools.core.rule.constraint.EvaluatorHelper.valuesAsMap;

public class MvelConditionEvaluator implements ConditionEvaluator {

    protected final Declaration[] declarations;
    private final EvaluatorWrapper[] operators;

    private final String conditionClass;
    private final ParserConfiguration parserConfiguration;
    protected ExecutableStatement executableStatement;
    protected MVELCompilationUnit compilationUnit;

    private boolean evaluated = false;

    public MvelConditionEvaluator(ParserConfiguration configuration,
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

    public MvelConditionEvaluator(MVELCompilationUnit compilationUnit,
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
    }

    public boolean evaluate(InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple tuple) {
        return evaluate(executableStatement, handle, workingMemory, tuple);
    }

    public boolean evaluate(ExecutableStatement statement, InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple tuple) {
        if (compilationUnit == null) {
            Map<String, Object> vars = valuesAsMap(handle.getObject(), workingMemory, tuple, declarations);
            if (operators.length > 0) {
                if (vars == null) {
                    vars = new HashMap<String, Object>();
                }
                InternalFactHandle[] handles = tuple != null ? tuple.toFactHandles() : new InternalFactHandle[0];
                for (EvaluatorWrapper operator : operators) {
                    vars.put( operator.getBindingName(), operator );
                    operator.loadHandles(workingMemory, handles, handle);
                }
            }
            return evaluate(statement, handle.getObject(), vars);
        }

        VariableResolverFactory factory = compilationUnit.createFactory();
        compilationUnit.updateFactory( null, null, handle,
                                       tuple, null, workingMemory,
                                       workingMemory.getGlobalResolver(),
                                       factory );

        return (Boolean) MVELSafeHelper.getEvaluator().executeExpression( statement, handle.getObject(), factory );
    }

    private boolean evaluate(ExecutableStatement statement, Object object, Map<String, Object> vars) {
        return vars == null ?
               (Boolean)MVELSafeHelper.getEvaluator().executeExpression(statement, object) :
               (Boolean)MVELSafeHelper.getEvaluator().executeExpression(statement, object, vars);
    }

    ConditionAnalyzer.Condition getAnalyzedCondition() {
        return new ConditionAnalyzer(executableStatement, declarations, operators, conditionClass).analyzeCondition();
    }

    ConditionAnalyzer.Condition getAnalyzedCondition(InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple leftTuple) {
        ensureCompleteEvaluation(handle, workingMemory, leftTuple);
        return new ConditionAnalyzer(executableStatement, declarations, operators, conditionClass).analyzeCondition();
    }

    private void ensureCompleteEvaluation(InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple tuple) {
        if (!evaluated) {
            ASTNode rootNode = getRootNode();
            if (rootNode != null) {
                ensureCompleteEvaluation(rootNode, handle, workingMemory, tuple);
            }
            evaluated = true;
        }
    }

    private void ensureCompleteEvaluation(ASTNode node, InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple tuple) {
        node = unwrap(node);
        if (!(node instanceof And || node instanceof Or)) {
            return;
        }
        ensureBranchEvaluation(handle, workingMemory, tuple, ((BooleanNode)node).getLeft());
        ensureBranchEvaluation(handle, workingMemory, tuple, ((BooleanNode)node).getRight());
    }

    private ASTNode unwrap(ASTNode node) {
        while (node instanceof Negation || node instanceof LineLabel || node instanceof Substatement) {
            node = unwrapNegation(node);
            node = unwrapSubstatement(node);
        }
        return node;
    }

    private void ensureBranchEvaluation(InternalFactHandle handle, InternalWorkingMemory workingMemory, Tuple tuple, ASTNode node) {
        if (!isEvaluated(node)) {
            ASTNode next = node.nextASTNode;
            node.nextASTNode = null;
            evaluate(asCompiledExpression(node), handle, workingMemory, tuple);
            node.nextASTNode = next;
        }
        ensureCompleteEvaluation(node, handle, workingMemory, tuple);
    }

    private ASTNode unwrapNegation(ASTNode node) {
        if (node instanceof Negation) {
            ExecutableStatement statement = ((Negation)node).getStatement();
            return statement instanceof ExecutableAccessor ? ((ExecutableAccessor)statement).getNode() : null;
        }
        return node;
    }

    private ASTNode unwrapSubstatement(ASTNode node) {
        if (node instanceof LineLabel) {
            return node.nextASTNode;
        }
        return node instanceof Substatement ? ((ExecutableAccessor)((Substatement)node).getStatement()).getNode() : node;
    }

    private boolean isEvaluated(ASTNode node) {
        node = unwrapSubstatement(node);
        if (node instanceof Contains) {
            return ((Contains)node).getFirstStatement().getAccessor() != null;
        }
        return node instanceof BinaryOperation ? ((BooleanNode) node).getLeft().getAccessor() != null : node.getAccessor() != null;
    }

    private CompiledExpression asCompiledExpression(ASTNode node) {
        return new CompiledExpression(new ASTLinkedList(node), null, Object.class, parserConfiguration, false);
    }

    private ASTNode getRootNode() {
        if (executableStatement instanceof ExecutableLiteral) {
            return null;
        }
        return executableStatement instanceof CompiledExpression ? ((CompiledExpression) executableStatement).getFirstNode() : ((ExecutableAccessor) executableStatement).getNode();
    }
}
