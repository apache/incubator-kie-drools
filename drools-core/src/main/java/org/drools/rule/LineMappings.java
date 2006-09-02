package org.drools.rule;

public class LineMappings {
    private String className;
    private int startLine;
    private int offset;
    
    public LineMappings(String className) {
        this.className = className;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }
    
    public int getStartLine() {
        return this.startLine;
    }
  
    public void setOffset(int offset) {
        this.offset = offset;
    } 
    
    public int getOffset() {
       return this.offset;
    }      
    
    
}
