/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2016 The JavaParser Team.
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Modified by Red Hat, Inc.
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
