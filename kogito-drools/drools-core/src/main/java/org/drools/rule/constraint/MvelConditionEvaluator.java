package org.drools.rule.constraint;

import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.ast.*;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.util.ASTLinkedList;
import org.mvel2.util.Soundex;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MvelConditionEvaluator implements ConditionEvaluator {

    private ExecutableStatement stmt;
    private String expression;
    private ParserContext parserContext;
    private boolean evaluated = false;

    MvelConditionEvaluator(ParserConfiguration conf, String expression) {
        this.expression = expression;
        this.parserContext = new ParserContext(conf);
        stmt = (ExecutableStatement)MVEL.compileExpression(expression, parserContext);
    }

    public boolean evaluate(Object object, Map<String, Object> vars) {
        return evaluate(stmt, object, vars);
    }

    private boolean evaluate(ExecutableStatement statement, Object object, Map<String, Object> vars) {
        return vars == null ? (Boolean)MVEL.executeExpression(statement, object) : (Boolean)MVEL.executeExpression(statement, object, vars);
    }

    ConditionAnalyzer.Condition getAnalyzedCondition() {
        return isCompletelyEvaluated() ? new ConditionAnalyzer(stmt).analyzeCondition() : null;
    }

    ConditionAnalyzer.Condition getAnalyzedCondition(Object object, Map<String, Object> vars) {
        ensureCompleteEvaluation(object, vars);
        return new ConditionAnalyzer(stmt).analyzeCondition();
    }

    private void ensureCompleteEvaluation(Object object, Map<String, Object> vars) {
        if (!evaluated) {
            ensureCompleteEvaluation(getRootNode(), object, vars);
            evaluated = true;
        }
    }

    private void ensureCompleteEvaluation(ASTNode node, Object object, Map<String, Object> vars) {
        if (!(node instanceof And || node instanceof Or)) return;
        ASTNode rightNode = ((BooleanNode)node).getRight();
        if (!isEvaluated(rightNode)) {
            evaluate(asCompiledExpression(rightNode), object, vars);
        }
        ensureCompleteEvaluation(rightNode, object, vars);
    }

    private boolean isEvaluated(ASTNode node) {
        return node instanceof BinaryOperation ? ((BinaryOperation)node).getLeft().getAccessor() != null : node.getAccessor() != null;
    }

    private CompiledExpression asCompiledExpression(ASTNode node) {
        return new CompiledExpression(new ASTLinkedList(node), null, Object.class, parserContext, false);
    }

    private boolean isCompletelyEvaluated() {
        if (evaluated) return true;
        evaluated = isCompletelyEvaluated(getRootNode());
        return evaluated;
    }

    private boolean isCompletelyEvaluated(ASTNode node) {
        if (!(node instanceof And || node instanceof Or)) return true;
        ASTNode rightNode = ((BooleanNode)node).getRight();
        return isEvaluated(rightNode) && isCompletelyEvaluated(rightNode);
    }

    private ASTNode getRootNode() {
        return stmt instanceof CompiledExpression ? ((CompiledExpression)stmt).getFirstNode() : ((ExecutableAccessor)stmt).getNode();
    }
}
