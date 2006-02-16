package org.drools.lang.descr;

public class BoundVariableDescr extends PatternDescr {
    private String fieldName;
    private String evaluator;
    private String text;

    public BoundVariableDescr(String fieldName,
                        String evaluator,
                        String text) {
        this.fieldName = fieldName;
        this.text = text;
        this.evaluator = evaluator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getEvaluator() {
        return evaluator;
    }

    public String getText() {
        return this.text;
    }
}
