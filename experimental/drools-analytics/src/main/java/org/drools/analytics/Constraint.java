package org.drools.analytics;

public class Constraint {

    private String field;
    private String operator;
    private String value;
    private String objectType;
    private String ruleName;
    private String parentCE = "rule";
    
    public static String CE_OR;
    public static String CE_NOT;
    public static String CE_EXIST;    
    
    public String getParentCE() {
        return parentCE;
    }
    public void setParentCE(String parentCE) {
        this.parentCE = parentCE;
    }
    public String getObjectType() {
        return objectType;
    }
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    public String getRuleName() {
        return ruleName;
    }
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public String getOperator() {
        return operator;
    }
    public void setOperator(String operator) {
        this.operator = operator;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    
}
