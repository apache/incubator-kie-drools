package org.drools.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;

public class JavaWhileBlockDescr extends AbstractJavaContainerBlockDescr
    implements
    JavaBlockDescr,
    JavaContainerBlockDescr {
    private int                  start;
    private int                  end;
    private int                  textStart;

    public JavaWhileBlockDescr() {
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

    public String getTargetExpression() {
        throw new UnsupportedOperationException();
    }

    public void setTargetExpression(String str) {
        throw new UnsupportedOperationException();
    }

    public BlockType getType() {
        return BlockType.WHILE;
    }

}
