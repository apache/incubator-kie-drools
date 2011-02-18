package org.drools.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class used during java code parsing to identify
 * and handle modify(){} blocks
 * 
 */
public class JavaModifyBlockDescr implements JavaBlockDescr {
    private int start;
    private int end;
    private String targetExpression;
    private List<String> expressions;
    
    public JavaModifyBlockDescr( String targetExpression ) {
        this.targetExpression = targetExpression;
        this.expressions = new ArrayList<String>();
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
    
    public List<String> getExpressions() {
        return expressions;
    }
    public void setExpressions(List<String> expressions) {
        this.expressions = expressions;
    }
    
    public String toString() {
        return "ModifyBlock( start="+start+" end="+end+" expression="+targetExpression+" )";
    }

    public BlockType getType() {
        return BlockType.MODIFY;
    }

}
