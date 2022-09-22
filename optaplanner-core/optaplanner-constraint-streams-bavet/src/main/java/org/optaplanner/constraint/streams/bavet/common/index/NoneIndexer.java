package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.collection.TupleList;
import org.optaplanner.constraint.streams.bavet.common.collection.TupleListEntry;

final class NoneIndexer<T, Value_> implements Indexer<T> {

    private final TupleList<T> tupleList = new TupleList<>();

    @Override
    public TupleListEntry<T> put(IndexProperties indexProperties, T tuple) {
        return tupleList.add(tuple);
    }

    @Override
    public void remove(IndexProperties indexProperties, TupleListEntry<T> entry) {
        entry.remove();
    }

    @Override
    public int size(IndexProperties indexProperties) {
        return tupleList.size();
    }

    @Override
    public void forEach(IndexProperties indexProperties, Consumer<T> tupleConsumer) {
        tupleList.forEach(tupleConsumer);
    }

    @Override
    public boolean isEmpty() {
        return tupleList.size() == 0;
    }

    @Override
    public String toString() {
        return "size = " + tupleList.size();
    }

}
