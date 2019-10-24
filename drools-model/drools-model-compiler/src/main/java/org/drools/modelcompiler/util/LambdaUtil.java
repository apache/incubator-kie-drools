package org.drools.modelcompiler.util;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;

public class LambdaUtil {

    private LambdaUtil() {

    }

    public static Expression compose(LambdaExpr l1, LambdaExpr l2) {
        ExpressionStmt l1ExprStmt = (ExpressionStmt) l1.getBody();
        ExpressionStmt l2ExprStmt = (ExpressionStmt) l2.getBody();

        DrlxParseUtil.RemoveRootNodeResult removeRootNodeResult = DrlxParseUtil.removeRootNode(l2ExprStmt.getExpression());

        NodeWithOptionalScope<?> newExpr = (NodeWithOptionalScope<?>) removeRootNodeResult.getFirstChild();

        newExpr.setScope(l1ExprStmt.getExpression());
        l1.setBody(new ExpressionStmt(removeRootNodeResult.getWithoutRootNode()));
        return l1;
    }
}
