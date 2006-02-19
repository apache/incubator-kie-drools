package org.drools.lang.descr;

public class ReturnValueDescr extends PatternDescr {    
    private String fieldName;
    private String evaluator;
    private String text;            
    private String[] declarations;
    
    private String classMethodName;
        
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
    
    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
    }    
    
    public String getEvaluator() {
        return evaluator;
    }
    
    public String getText() {
        return this.text;
    } 
    
    public void setDeclarations( String[] declarations) {
        this.declarations = declarations;
    }
    
    public String[] getDeclarations() {
        return this.declarations;
    }     
}
