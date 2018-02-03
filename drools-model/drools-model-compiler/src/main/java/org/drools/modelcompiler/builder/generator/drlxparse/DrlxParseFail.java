package org.drools.modelcompiler.builder.generator.drlxparse;

public class DrlxParseFail implements DrlxParseResult {

    @Override
    public void accept(ParseResultVoidVisitor parseVisitor) {
        parseVisitor.onFail(this);
    }

    @Override
    public <T> T acceptWithReturnValue(ParseResultVisitor<T> visitor) {
        return visitor.onFail(this);
    }
}
