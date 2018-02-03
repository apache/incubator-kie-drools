package org.drools.modelcompiler.builder.generator.drlxparse;

@FunctionalInterface
public interface ParseResultVoidVisitor {
    void onSuccess(DrlxParseSuccess drlxParseResult);

    default void onFail(DrlxParseFail failure) {

    }

}
