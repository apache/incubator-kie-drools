package org.drools.model.codegen.execmodel.generator.drlxparse;

public interface ParseResultVisitor<T> {
    T onSuccess(DrlxParseSuccess drlxParseResult);

    default T onFail(DrlxParseFail failure) { return null; }

}
