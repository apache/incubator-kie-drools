/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;

public class JavaTryBlockDescr extends AbstractJavaContainerBlockDescr
    implements
    JavaBlockDescr,
    JavaContainerBlockDescr {
    private int                       start;
    private int                       end;

    private int                       textStart;

    private List<JavaCatchBlockDescr> catchBlocks = new ArrayList<JavaCatchBlockDescr>();
    private JavaFinalBlockDescr       finalBlock;

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
