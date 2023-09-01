package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.descr.GroupByDescr;
import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.GroupByDescrBuilder;

public class GroupByDescrBuilderImpl<P extends DescrBuilder< ?, ? >> extends AccumulateDescrBuilderImpl<P> implements GroupByDescrBuilder<P> {

    public GroupByDescrBuilderImpl(P parent) {
        super(parent, new GroupByDescr());
    }

    @Override
    public GroupByDescrBuilder<P> groupingFunction(String block) {
        ((GroupByDescr) descr).setGroupingFunction(block);
        return this;
    }

    @Override
    public GroupByDescrBuilder<P> groupingFunction(String block, String key) {
        ((GroupByDescr) descr).setGroupingFunction(block);
        ((GroupByDescr) descr).setGroupingKey(key);
        return this;
    }
}
