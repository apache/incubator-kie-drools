package org.kie.dmn.feel.runtime.decisiontables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;


public class SingleValueOrListCollector<T> implements Collector<T, List<T>, Object> {

    @Override
    public Supplier<List<T>> supplier() {
        return () -> new ArrayList<T>();
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return (list, obj) -> list.add(obj);
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (list1, list2) -> { list1.addAll(list2); return list1; };
    }

    @Override
    public Function<List<T>, Object> finisher() {
        return (list) -> {
            if (list.size() == 1) {
                return list.get(0);
            } else {
                return list;
            }
        };
    }

    @Override
    public Set<java.util.stream.Collector.Characteristics> characteristics() {
        return Collections.emptySet();
    }

}
