package org.drools.rule.builder.dialect.java.parser;

public interface JavaBlockDescr {
    
    public static enum BlockType {
        MODIFY, UPDATE, RETRACT, ENTRY, EXIT, CHANNEL
    }

    public BlockType getType();

    public int getStart();

    public int getEnd();
    
    public String getTargetExpression();
    public void setTargetExpression(String str);

}