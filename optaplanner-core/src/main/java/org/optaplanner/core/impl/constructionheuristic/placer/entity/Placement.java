package org.optaplanner.core.impl.constructionheuristic.placer.entity;

import java.io.Serializable;
import java.util.Iterator;

import org.optaplanner.core.impl.move.Move;

public class Placement implements Iterable<Move>, Serializable {

    private final Iterator<Move> moveIterator;

    public Placement(Iterator<Move> moveIterator) {
        this.moveIterator = moveIterator;
    }

    public Iterator<Move> iterator() {
        return moveIterator;
    }

}
