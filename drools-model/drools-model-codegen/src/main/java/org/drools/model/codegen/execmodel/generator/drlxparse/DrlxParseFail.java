package org.drools.model.codegen.execmodel.generator.drlxparse;

import com.github.javaparser.ast.expr.BinaryExpr;
import org.drools.drl.parser.DroolsError;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.DRLIdGenerator;

public class DrlxParseFail implements DrlxParseResult {

    private DroolsError specificError;
    private String originalDrlConstraint;

    public DrlxParseFail() {
    }

    public DrlxParseFail(DroolsError specificError) {
        this.specificError = specificError;
    }

    @Override
    public void accept(ParseResultVoidVisitor parseVisitor) {
        parseVisitor.onFail(this);
    }

    @Override
    public <T> T acceptWithReturnValue(ParseResultVisitor<T> visitor) {
        return visitor.onFail(this);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public DrlxParseResult combineWith(DrlxParseResult other, BinaryExpr.Operator operator) {
        return this;
    }

    @Override
    public String getExprId(DRLIdGenerator exprIdGenerator) {
        return "invalidEpxr";
    }

    @Override
    public DrlxParseResult setOriginalDrlConstraint(String originalDrlConstraint) {
        this.originalDrlConstraint = originalDrlConstraint;
        return this;
    }

    @Override
    public String getOriginalDrlConstraint() {
        return originalDrlConstraint;
    }

    public DroolsError getError() {
        if(specificError != null) {
            return specificError;
        } else {
            return new InvalidExpressionErrorResult("Unable to parse left part of expression: " + originalDrlConstraint);
        }
    }
}
