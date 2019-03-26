package org.drools.modelcompiler.builder.generator.drlxparse;

import org.drools.javaparser.ast.expr.BinaryExpr;

public interface DrlxParseResult {

    void accept( ParseResultVoidVisitor visitor );

    <T> T acceptWithReturnValue( ParseResultVisitor<T> visitor );

    boolean isSuccess();

    DrlxParseResult combineWith( DrlxParseResult other, BinaryExpr.Operator operator);
}
