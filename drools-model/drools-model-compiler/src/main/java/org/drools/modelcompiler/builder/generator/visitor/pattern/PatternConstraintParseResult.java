package org.drools.modelcompiler.builder.generator.visitor.pattern;

import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;

public class PatternConstraintParseResult {
    private String expression;
    private String patternIdentifier;
    private DrlxParseResult drlxParseResult;

    public PatternConstraintParseResult(String expression, String patternIdentifier, DrlxParseResult drlxParseResult) {
        this.expression = expression;
        this.patternIdentifier = patternIdentifier;
        this.drlxParseResult = drlxParseResult;
    }

    public String getExpression() {
        return expression;
    }

    public String getPatternIdentifier() {
        return patternIdentifier;
    }

    public DrlxParseResult getDrlxParseResult() {
        return drlxParseResult;
    }
}
