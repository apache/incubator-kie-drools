package org.drools.modelcompiler.builder.generator.drlxparse;

public interface ParseResultVisitor<T> {
    T onSuccess(DrlxParseSuccess drlxParseResult);

    default T onFail(DrlxParseFail failure) { return null; }

}
