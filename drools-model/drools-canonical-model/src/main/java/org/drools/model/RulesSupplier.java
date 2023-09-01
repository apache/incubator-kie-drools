package org.drools.model;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@FunctionalInterface
public interface RulesSupplier {
    List<Rule> getRulesList();

    static List<Rule> getRules(RulesSupplier... suppliers) {
        return Stream.of(suppliers).parallel().flatMap( s -> s.getRulesList().stream() ).collect( toList() );
    }
}
