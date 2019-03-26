package org.kie.dmn.feel.runtime.decisiontables;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

/**
 * Custom DT collector: it only 1 output, return the output itself.
 * If more than 1 output, return a FEEL Context (hashmap) with name of the output and its value.
 * 
 * From the specs:
 * The output expression of a rule in a single output decision table is simply the rule's output entry. The output
 expression of a multiple output decision table is a context with entries composed from the output names and
 the rule's corresponding output entries. 
 */
public class SingleValueOrContextCollector<T> implements Collector<T, List<T>, Object> {
    private final List<String> outputNames;
    
    public SingleValueOrContextCollector( List<String> outputNames ) {
        List<String> names = new ArrayList<>(outputNames);
        this.outputNames = Collections.unmodifiableList(names); 
    }

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
                // zip outputEntry with its name; do not use .collect( Collectors.toMap ) as it does not support null for values.
                Map<String, Object> res = new HashMap<>();
                for ( int i=0 ; i < outputNames.size(); i++  ) {
                    res.put(outputNames.get(i), list.get(i));
                }
                return res;
            }
        };
    }

    @Override
    public Set<Collector.Characteristics> characteristics() {
        return Collections.emptySet();
    }

}
