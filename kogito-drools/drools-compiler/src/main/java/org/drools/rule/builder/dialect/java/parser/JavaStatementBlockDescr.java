package org.drools.rule.builder.dialect.java.parser;

public class JavaStatementBlockDescr extends AbstractJavaBlockDescr implements JavaBlockDescr {

    private int start;
    private int end;
    private String targetExpression;
    private final BlockType type;

    public JavaStatementBlockDescr( String targetExpression, BlockType type ) {
        this.targetExpression = targetExpression;
        this.type = type;
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
        return type + "( start="+start+" end="+end+" expression="+targetExpression+" )";
    }

    public BlockType getType() {
        return type;
    }
}
