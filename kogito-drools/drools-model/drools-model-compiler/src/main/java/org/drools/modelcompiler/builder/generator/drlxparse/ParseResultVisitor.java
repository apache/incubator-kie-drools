package org.drools.modelcompiler.builder.generator.drlxparse;

public interface ParseResultVisitor<T> {
    T onSuccess(DrlxParseSuccess drlxParseResult);

    T onFail(DrlxParseFail failure);

}
