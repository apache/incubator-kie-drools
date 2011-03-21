package org.drools.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class used during java code parsing to identify
 * and handle update() blocks
 */
public class JavaUpdateBlockDescr extends AbstractJavaBlockDescr implements JavaBlockDescr {
    private int start;
    private int end;
    private String targetExpression;
    
    public JavaUpdateBlockDescr( String targetExpression ) {
        this.targetExpression = targetExpression;
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
    public String getTargetExpression() {
        return targetExpression;
    }
    
    public void setTargetExpression(String targetExpression) {
        this.targetExpression = targetExpression;
    }
    
    public String toString() {
        return "UpdateBlock( start="+start+" end="+end+" expression="+targetExpression+" )";
    }

    public BlockType getType() {
        return BlockType.MODIFY;
    }

}
