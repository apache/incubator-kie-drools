package org.drools.rule.constraint;

import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.ast.*;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.compiler.ExecutableLiteral;
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

    private Object lastEvaluatedObject;
    private Map<String, Object> lastEvaluatedVars;

    MvelConditionEvaluator(ParserConfiguration conf, String expression) {
        this.expression = expression;
        this.parserContext = new ParserContext(conf);
        stmt = (ExecutableStatement)MVEL.compileExpression(expression, parserContext);
    }

    public boolean evaluate(Object object, Map<String, Object> vars) {
        if (!evaluated) {
            lastEvaluatedObject = object;
            lastEvaluatedVars = vars;
        }
        return evaluate(stmt, object, vars);
    }

    private boolean evaluate(ExecutableStatement statement, Object object, Map<String, Object> vars) {
        return vars == null ? (Boolean)MVEL.executeExpression(statement, object) : (Boolean)MVEL.executeExpression(statement, object, vars);
    }

    ConditionAnalyzer.Condition getAnalyzedCondition() {
        return getAnalyzedCondition(lastEvaluatedObject, lastEvaluatedVars);
    }

    ConditionAnalyzer.Condition getAnalyzedCondition(Object object, Map<String, Object> vars) {
        ensureCompleteEvaluation(object, vars);
        return new ConditionAnalyzer(stmt, vars).analyzeCondition();
    }

    private void ensureCompleteEvaluation(Object object, Map<String, Object> vars) {
        if (!evaluated) {
            ASTNode rootNode = getRootNode();
            if (rootNode != null) {
                ensureCompleteEvaluation(rootNode, object, vars);
            }
            evaluated = true;
            lastEvaluatedObject = null;
            lastEvaluatedVars = null;
        }
    }

    private void ensureCompleteEvaluation(ASTNode node, Object object, Map<String, Object> vars) {
        node = unwrapNegation(node);
        node = unwrapSubstatement(node);
        if (!(node instanceof And || node instanceof Or)) {
            return;
        }
        ensureBranchEvaluation(object, vars, ((BooleanNode)node).getLeft());
        ensureBranchEvaluation(object, vars, ((BooleanNode)node).getRight());
    }

    private void ensureBranchEvaluation(Object object, Map<String, Object> vars, ASTNode node) {
        if (!isEvaluated(node)) {
            ASTNode next = node.nextASTNode;
            node.nextASTNode = null;
            evaluate(asCompiledExpression(node), object, vars);
            node.nextASTNode = next;
        }
        ensureCompleteEvaluation(node, object, vars);
    }

    private ASTNode unwrapNegation(ASTNode node) {
        return node instanceof Negation ? ((ExecutableAccessor)((Negation)node).getStatement()).getNode() : node;
    }

    private ASTNode unwrapSubstatement(ASTNode node) {
        return node instanceof Substatement ? ((ExecutableAccessor)((Substatement)node).getStatement()).getNode() : node;
    }

    private boolean isEvaluated(ASTNode node) {
        node = unwrapSubstatement(node);
        if (node instanceof Contains) {
            return ((Contains)node).getFirstStatement().getAccessor() != null;
        }
        return node instanceof BinaryOperation ? ((BinaryOperation)node).getLeft().getAccessor() != null : node.getAccessor() != null;
    }

    private CompiledExpression asCompiledExpression(ASTNode node) {
        return new CompiledExpression(new ASTLinkedList(node), null, Object.class, parserContext, false);
    }

    private ASTNode getRootNode() {
        if (stmt instanceof ExecutableLiteral) {
            return null;
        }
        return stmt instanceof CompiledExpression ? ((CompiledExpression)stmt).getFirstNode() : ((ExecutableAccessor)stmt).getNode();
    }
}
