/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
