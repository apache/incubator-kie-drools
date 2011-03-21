package org.drools.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class used during java code parsing to identify
 * and handle update() blocks
 */
public class JavaThrowBlockDescr extends AbstractJavaBlockDescr implements JavaBlockDescr {
    private int start;
    private int end;
    private int textStart;    
    
    public JavaThrowBlockDescr( ) {
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
  
    public String toString() {
        return "throw( start="+start+" end="+end+" )";
    }

    public BlockType getType() {
        return BlockType.THROW;
    }
    
    public String getTargetExpression() {
        throw new UnsupportedOperationException();
    }

    public void setTargetExpression(String str) {
        throw new UnsupportedOperationException();
    }    

}
