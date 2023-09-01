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
