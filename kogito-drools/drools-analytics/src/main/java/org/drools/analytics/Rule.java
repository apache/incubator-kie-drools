package org.drools.analytics;

public class Rule {

    
    public String name;
    public Pattern[] patterns;
    public String rhs;
    
    private int salienceValue;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Pattern[] getPatterns() {
        return patterns;
    }
    public void setPatterns(Pattern[] patterns) {
        this.patterns = patterns;
    }
    public String getRhs() {
        return rhs;
    }
    public void setRhs(String rhs) {
        this.rhs = rhs;
    }
    public int getSalienceValue() {
        return salienceValue;
    }
    public void setSalienceValue(int salience) {
        this.salienceValue = salience;
    }
}
