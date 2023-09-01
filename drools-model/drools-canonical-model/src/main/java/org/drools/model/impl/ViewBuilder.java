package org.drools.model.impl;

import java.util.function.Function;

import org.drools.model.RuleItemBuilder;
import org.drools.model.patterns.CompositePatterns;

public interface ViewBuilder extends Function<RuleItemBuilder<?>[], CompositePatterns> {
    ViewBuilder PATTERN = new ViewPatternBuilder();
}
