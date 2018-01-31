package org.drools.modelcompiler.builder.generator.drlxparse;

public interface DrlxParseResult {

    void accept(ParseResultVoidVisitor visitor);

    <T> T acceptWithReturnValue(ParseResultVisitor<T> visitor);

}
