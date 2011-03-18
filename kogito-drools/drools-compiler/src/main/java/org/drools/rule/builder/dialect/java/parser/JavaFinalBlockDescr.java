package org.drools.rule.builder.dialect.java.parser;

import org.drools.rule.builder.dialect.java.parser.JavaBlockDescr.BlockType;

public class JavaFinalBlockDescr implements JavaBlockDescr {
    private int start;
    private int end;
    private int textStart;      
    
    public JavaFinalBlockDescr() {
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
    
    public int getTextStart() {
        return textStart;
    }

    public void setTextStart(int textStart) {
        this.textStart = textStart;
    }

    public BlockType getType() {
        return BlockType.FINALLY;
    }
    
    public String getTargetExpression() {
        throw new UnsupportedOperationException();
    }

    public void setTargetExpression(String str) {
        throw new UnsupportedOperationException();
    }

}
