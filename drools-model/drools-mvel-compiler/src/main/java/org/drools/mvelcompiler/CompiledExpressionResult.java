package org.drools.mvelcompiler;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import static org.drools.mvel.parser.printer.PrintUtil.printNode;

public class CompiledExpressionResult implements CompiledResult {

    private Expression expression;
    private Optional<Type> type;
    private Set<String> usedBindings = new HashSet<>();

    public CompiledExpressionResult(Expression expression, Optional<Type> type) {
        this.expression = expression;
        this.type = type;
    }

    public Expression getExpression() {
        return expression;
    }

    public Optional<Type> getType() {
        return type;
    }

    public String resultAsString() {
        return printNode(expression);
    }

    @Override
    public BlockStmt statementResults() {
        return new BlockStmt(NodeList.nodeList(new ExpressionStmt(expression)));
    }

    @Override
    public Set<String> getUsedBindings() {
        return usedBindings;
    }

    public CompiledExpressionResult setUsedBindings(Set<String> usedBindings) {
        this.usedBindings = usedBindings;
        return this;
    }

}
