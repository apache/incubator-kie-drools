package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class LineMappings implements Externalizable {
    private String className;
    private int    startLine;
    private int    offset;

    public LineMappings() {
    }

    public LineMappings(final String className) {
        this.className = className;
    }

    public void readExternal(ObjectInput stream) throws IOException, ClassNotFoundException {
        className   = (String)stream.readObject();
        startLine   = stream.readInt();
        offset      = stream.readInt();
    }

    public void writeExternal(ObjectOutput stream) throws IOException {
        stream.writeObject(className);
        stream.writeInt(startLine);
        stream.writeInt(offset);
    }

    public String getClassName() {
        return this.className;
    }

    public void setStartLine(final int startLine) {
        this.startLine = startLine;
    }

    public int getStartLine() {
        return this.startLine;
    }

    public void setOffset(final int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return this.offset;
    }

}
