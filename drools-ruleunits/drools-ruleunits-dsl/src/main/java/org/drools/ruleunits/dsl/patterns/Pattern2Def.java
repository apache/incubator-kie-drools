package org.drools.ruleunits.dsl.patterns;

import org.drools.model.Index;
import org.drools.model.functions.Block2;
import org.drools.model.functions.Block3;
import org.drools.model.functions.Function1;

public interface Pattern2Def<A, B> extends PatternDef {
    <V> Pattern2Def<A, B> filter(Index.ConstraintType constraintType, Function1<A, B> rightExtractor);

    <V> Pattern2Def<A, B> filter(Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor);

    <V> Pattern2Def<A, B> filter(String fieldName, Function1<B, V> leftExtractor, Index.ConstraintType constraintType, Function1<A, V> rightExtractor);

    void execute(Block2<A, B> block);

    <G> void execute(G globalObject, Block3<G, A, B> block);
}
