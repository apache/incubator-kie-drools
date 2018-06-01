package org.drools.modelcompiler.builder.generator.drlxparse;

import org.drools.compiler.compiler.DroolsError;

public class DrlxParseFail implements DrlxParseResult {

    private final DroolsError error;

    public DrlxParseFail() {
        this(null);
    }

    public DrlxParseFail( DroolsError error ) {
        this.error = error;
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

    public DroolsError getError() {
        return error;
    }
}
