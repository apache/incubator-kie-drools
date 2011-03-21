package org.drools.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;

public class JavaCatchBlockDescr extends AbstractJavaContainerBlockDescr
    implements
    JavaBlockDescr,
    JavaContainerBlockDescr {
    private int                  start;
    private int                  end;
    private int                  clauseStart;
    private int                  textStart;

    private String               clause;

    public JavaCatchBlockDescr(String clause) {
        this.clause = clause;
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

    public int getClauseStart() {
        return clauseStart;
    }

    public void setClauseStart(int clauseStart) {
        this.clauseStart = clauseStart;
    }

    public String getTargetExpression() {
        throw new UnsupportedOperationException();
    }

    public void setTargetExpression(String str) {
        throw new UnsupportedOperationException();
    }

    public BlockType getType() {
        return BlockType.CATCH;
    }

    public String getClause() {
        return clause;
    }

}
