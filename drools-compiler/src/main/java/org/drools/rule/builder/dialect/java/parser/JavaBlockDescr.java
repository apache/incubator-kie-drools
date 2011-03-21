package org.drools.rule.builder.dialect.java.parser;

import java.util.Map;

public interface JavaBlockDescr {
    
    public static enum BlockType {
        MODIFY, UPDATE, RETRACT, ENTRY, EXIT, CHANNEL, TRY, CATCH, FINAL, IF, FOR, SWITCH, WHILE, THROW
    }

    public BlockType getType();

    public int getStart();

    public int getEnd();
    
    public String getTargetExpression();
    public void setTargetExpression(String str);

    public Map<String, Class<?>> getInputs();

    public void setInputs(Map<String, Class< ? >> variables);;

}