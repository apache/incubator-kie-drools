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

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;

import com.github.javaparser.TokenRange;
import com.github.javaparser.JavaToken;


/**
 * Helper class for {@link GeneratedJavaParser}
 */
class RangedList<T extends Node> {
    /* A ranged list MUST be set to a begin and end,
       or these temporary values will leak out */
    TokenRange range = new TokenRange(JavaToken.INVALID, JavaToken.INVALID);
    NodeList<T> list;

    RangedList(NodeList<T> list) {
        this.list = list;
    }

    void beginAt(JavaToken begin) {
        range = range.withBegin(begin);
    }

    void endAt(JavaToken end) {
        range = range.withEnd(end);
    }

    void add(T t) {
        if (list == null) {
            list = new NodeList<>();
        }
        list.add(t);
    }
}
