package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class ModifyStatementT extends TypedExpression {

    final TypedExpression modifyObject;

    public ModifyStatementT(Node originalExpression, TypedExpression modifyObject) {
        super(originalExpression);
        this.modifyObject = modifyObject;
    }

    @Override
    public Type getType() {
        return null;
    }

    @Override
    public Node toJavaExpression() {
        BlockStmt blockStmt = new BlockStmt();
        for(TypedExpression n : children) {
            blockStmt.addStatement((Expression) n.toJavaExpression());
        }
        return blockStmt;
    }
}
