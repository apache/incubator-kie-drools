package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class BlockStmtT implements TypedExpression {

    private List<TypedExpression> statements;

    public BlockStmtT(List<TypedExpression> statements) {
        this.statements = statements;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.empty();
    }

    @Override
    public Node toJavaExpression() {
        BlockStmt blockStmt = new BlockStmt();
        for(TypedExpression te : statements) {
            blockStmt.addStatement((Statement) te.toJavaExpression());
        }
        return blockStmt;
    }
}
