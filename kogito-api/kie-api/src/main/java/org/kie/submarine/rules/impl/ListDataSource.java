package org.kie.submarine.rules.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

import org.kie.api.runtime.rule.FactHandle;
import org.kie.submarine.rules.DataSource;

public class ListDataSource<T> implements DataSource<T> {
    ArrayList<T> values = new ArrayList<>();

    public FactHandle add(T t) {
        values.add(t);
        return null;
    }

    @Override
    public void update(FactHandle handle, T object) {

    }

    @Override
    public void remove(FactHandle handle) {

    }

    public void addAll(Collection<? extends T> ts) {
        values.addAll(ts);
    }

    public void drainInto(Consumer<Object> sink) {
        Iterator<T> iter = values.iterator();
        while(iter.hasNext()) {
            T t = iter.next();
            sink.accept(t);
            iter.remove();
        }
    }
}
