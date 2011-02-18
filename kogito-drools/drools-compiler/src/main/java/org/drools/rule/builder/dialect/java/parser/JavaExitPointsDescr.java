package org.drools.rule.builder.dialect.java.parser;


/**
 * A helper class used during java code parsing to identify
 * and handle exitPoints calls
 * 
 */
public class JavaExitPointsDescr {
    private int start;
    private int end;
    private String id;
    
    public JavaExitPointsDescr( String id ) {
        this.id = id;
    }
    
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    public int getEnd() {
        return end;
    }
    public void setEnd(int end) {
        this.end = end;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String toString() {
        return "ExitPoints( start="+start+" end="+end+" id="+id+" )";
    }

}
