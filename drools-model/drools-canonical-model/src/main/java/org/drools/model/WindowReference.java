package org.drools.model;

import org.drools.model.functions.Predicate1;

public interface WindowReference<T> extends WindowDefinition, DeclarationSource {

    Class<T> getPatternType();

    Predicate1<T>[] getPredicates();

    String getName();

    EntryPoint getEntryPoint();
}
