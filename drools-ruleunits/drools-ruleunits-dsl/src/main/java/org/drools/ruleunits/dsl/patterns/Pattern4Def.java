package org.drools.ruleunits.dsl.patterns;

import org.drools.model.Index;
import org.drools.model.functions.Block1;
import org.drools.model.functions.Block4;
import org.drools.model.functions.Block5;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function3;
import org.drools.model.functions.Predicate4;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.impl.datasources.ConsequenceDataStore;

public interface Pattern4Def<A, B, C, D> extends PatternDef {

    Pattern4Def<A, B, C, D> filter(Predicate4<A, B, C, D> predicate);

    <V> Pattern4Def<A, B, C, D> filter(Function1<D, V> leftExtractor, Index.ConstraintType constraintType, Function3<A, B, C, V> rightExtractor);
    <V> Pattern4Def<A, B, C, D> filter(String fieldName, Function1<D, V> leftExtractor, Index.ConstraintType constraintType, Function3<A, B, C, V> rightExtractor);

    void execute(Block4<A, B, C, D> block);

    <G> void execute(G globalObject, Block5<G, A, B, C, D> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block1<ConsequenceDataStore<T>> block);

    <T> void executeOnDataStore(DataStore<T> dataStore, Block5<ConsequenceDataStore<T>, A, B, C, D> block);
}
