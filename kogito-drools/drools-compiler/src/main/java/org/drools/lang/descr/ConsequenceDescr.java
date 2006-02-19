package org.drools.lang.descr;

public class ConsequenceDescr extends PatternDescr {
    private String text;
    
    private String classMethodName;

    public ConsequenceDescr(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
    
    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(String classMethodName) {
        this.classMethodName = classMethodName;
    }       
}
