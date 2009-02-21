package org.drools.rule.builder.dialect.java.parser;

public interface JavaBlockDescr {
    
    public static enum BlockType {
        MODIFY, ENTRY, EXIT
    }

    public BlockType getType();

    public int getStart();

    public int getEnd();

}