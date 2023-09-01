package org.drools.model.codegen.execmodel.generator.drlxparse;

import com.github.javaparser.ast.expr.BinaryExpr;
import org.drools.model.codegen.execmodel.generator.DRLIdGenerator;

public interface DrlxParseResult {

    void accept( ParseResultVoidVisitor visitor );

    <T> T acceptWithReturnValue( ParseResultVisitor<T> visitor );

    boolean isSuccess();

    DrlxParseResult combineWith( DrlxParseResult other, BinaryExpr.Operator operator);

    String getExprId(DRLIdGenerator exprIdGenerator);

    DrlxParseResult setOriginalDrlConstraint(String originalDrlConstraint);

    String getOriginalDrlConstraint();

    default boolean isOOPath() {
        return false;
    }
}
