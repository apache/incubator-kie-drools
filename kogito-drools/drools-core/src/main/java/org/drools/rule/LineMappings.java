/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
