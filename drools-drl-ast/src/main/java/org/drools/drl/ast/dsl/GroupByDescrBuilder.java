package org.drools.drl.ast.dsl;

public interface GroupByDescrBuilder<P extends DescrBuilder< ?, ? >> extends AccumulateDescrBuilder<P> {
    GroupByDescrBuilder<P> groupingFunction( String block );
    GroupByDescrBuilder<P> groupingFunction( String block, String key );
}
