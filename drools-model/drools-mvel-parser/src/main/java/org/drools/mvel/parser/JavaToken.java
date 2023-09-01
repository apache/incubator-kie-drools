package org.drools.mvel.parser;

import java.util.Optional;

import com.github.javaparser.Range;
import com.github.javaparser.TokenTypes;

public class JavaToken extends com.github.javaparser.JavaToken {

    private int kind;
    private String text;
    private Range range;

    public JavaToken(int kind, String text) {
        super(kind, text);
        this.kind = kind;
        this.text = text;
    }

    public JavaToken(com.github.javaparser.Token token, String text) {
        super(Range.range(token.beginLine, token.beginColumn, token.endLine, token.endColumn), token.kind, text, null, null);
        this.text = text;
        this.range = Range.range(token.beginLine, token.beginColumn, token.endLine, token.endColumn);
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        this.text = text;
    }

    @Override
    public int getKind() {
        return kind;
    }

    @Override
    public com.github.javaparser.JavaToken.Category getCategory() {
        return TokenTypes.getCategory(kind);
    }

    @Override
    public int hashCode() {
        int result = kind;
        result = 31 * result + text.hashCode();
        return result;
    }

    @Override
    public Optional<Range> getRange() {
        return Optional.of(Range.range(0,0,0,0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        org.drools.mvel.parser.JavaToken javaToken = (org.drools.mvel.parser.JavaToken) o;
        if (kind != javaToken.kind) {
            return false;
        }
        return text.equals(javaToken.text);
    }

}
