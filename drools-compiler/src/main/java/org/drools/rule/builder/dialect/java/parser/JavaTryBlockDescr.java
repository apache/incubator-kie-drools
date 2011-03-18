package org.drools.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;

import org.drools.rule.builder.dialect.java.parser.JavaBlockDescr.BlockType;

public class JavaTryBlockDescr implements JavaBlockDescr {
    private int start;
    private int end;
    
    private int textStart;  
    
    private List<JavaCatchBlockDescr> catchBlocks = new ArrayList<JavaCatchBlockDescr>();
    private JavaFinalBlockDescr finalBlock;
    
    
    public JavaTryBlockDescr() {
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
        return BlockType.TRY;
    }    
    

    public String getTargetExpression() {
        throw new UnsupportedOperationException();
    }

    public void setTargetExpression(String str) {
        throw new UnsupportedOperationException();
    }

    public void addCatch(JavaCatchBlockDescr cd) {
        this.catchBlocks.add( cd );
    }

    public void setFinally(JavaFinalBlockDescr fd) {
        this.finalBlock = fd;
    }


    public List<JavaCatchBlockDescr> getCatches() {
        return catchBlocks;
    }

    public JavaFinalBlockDescr getFinal() {
        return finalBlock;
    }
    
    

}
