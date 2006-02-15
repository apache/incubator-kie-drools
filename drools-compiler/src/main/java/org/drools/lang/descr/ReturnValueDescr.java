package org.drools.lang.descr;

public class ReturnValueDescr extends PatternDescr  {
    private String fieldName;
    private String evaluator;
    private String text;
        
    public ReturnValueDescr(String fieldName,
                                    String evaluator,
                                    String text) {
        this.fieldName = fieldName;
        this.evaluator = evaluator;
        this.text = text;
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
