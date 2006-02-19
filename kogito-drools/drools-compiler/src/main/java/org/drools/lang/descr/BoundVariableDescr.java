package org.drools.lang.descr;

public class BoundVariableDescr extends PatternDescr {
    private String fieldName;
    private String evaluator;
    private String declarationIdentifier;

    public BoundVariableDescr(String fieldName,
                              String evaluator,
                              String identifier) {
        this.fieldName = fieldName;
        this.declarationIdentifier = identifier;
        this.evaluator = evaluator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getEvaluator() {
        return evaluator;
    }

    public String getDeclarationIdentifier() {
        return this.declarationIdentifier;
    }
}
