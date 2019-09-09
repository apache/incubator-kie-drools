package org.drools.modelcompiler.builder.generator.visitor.accumulate;

public class MissingSemicolonInlineAccumulateException extends RuntimeException {

    private final String field;

    MissingSemicolonInlineAccumulateException(String field) {

        this.field = field;
    }

    @Override
    public String getMessage() {
        return String.format("Syntax error in %s block, insert ; to complete Statement ", field);
    }
}
