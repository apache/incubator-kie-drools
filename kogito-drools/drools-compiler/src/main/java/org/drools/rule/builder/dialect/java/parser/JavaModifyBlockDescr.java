/**
 * 
 */
package org.drools.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class used during java code parsing to identify
 * and handle modify(){} blocks
 * 
 * @author etirelli
 */
public class JavaModifyBlockDescr implements JavaBlockDescr {
    private int start;
    private int end;
    private String modifyExpression;
    private List<String> expressions;
    
    public JavaModifyBlockDescr( String modifyExpression ) {
        this.modifyExpression = modifyExpression;
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
    public String getModifyExpression() {
        return modifyExpression;
    }
    public void setModifyExpression(String modifyExpression) {
        this.modifyExpression = modifyExpression;
    }
    public List<String> getExpressions() {
        return expressions;
    }
    public void setExpressions(List<String> expressions) {
        this.expressions = expressions;
    }
    
    public String toString() {
        return "ModifyBlock( start="+start+" end="+end+" expression="+modifyExpression+" )";
    }

    public BlockType getType() {
        return BlockType.MODIFY;
    }

}
