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

    ExecutableStatement getExecutableStatement(Object object, Map<String, Object> vars) {
        ensureCompleteEvaluation(object, vars);
        return stmt;
    }

    private void ensureCompleteEvaluation(Object object, Map<String, Object> vars) {
        ASTNode node = stmt instanceof CompiledExpression ? ((CompiledExpression)stmt).getFirstNode() : ((ExecutableAccessor)stmt).getNode();
        if (!(node instanceof And || node instanceof Or)) return;
        ASTNode rightNode = ((BooleanNode)node).getRight();
        if (!isEvaluated(rightNode)) {
            evaluate(asCompiledExpression(rightNode), object, vars);
        }
    }

    private boolean isEvaluated(ASTNode node) {
        return node instanceof BinaryOperation ? ((BinaryOperation)node).getLeft().getAccessor() != null : node.getAccessor() != null;
    }

    private CompiledExpression asCompiledExpression(ASTNode node) {
        return new CompiledExpression(new ASTLinkedList(node), null, Object.class, parserContext, false);
    }
}
