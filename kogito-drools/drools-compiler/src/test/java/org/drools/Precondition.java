package org.drools;

public class Precondition {
    private String code;
    private String value;
    
    public Precondition() {
        
    }
    
    public Precondition(String code, String value) {
        super();
        this.code = code;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
} 