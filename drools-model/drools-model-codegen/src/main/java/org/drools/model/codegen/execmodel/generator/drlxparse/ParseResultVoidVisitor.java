package org.drools.model.codegen.execmodel.generator.drlxparse;

@FunctionalInterface
public interface ParseResultVoidVisitor {
    void onSuccess(DrlxParseSuccess drlxParseResult);

    default void onFail(DrlxParseFail failure) {

    }

}
