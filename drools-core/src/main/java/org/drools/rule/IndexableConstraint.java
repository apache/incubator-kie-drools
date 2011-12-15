package org.drools.rule;

import org.drools.spi.Constraint;
import org.drools.spi.InternalReadAccessor;

public interface IndexableConstraint extends Constraint {

    boolean isUnification();

    boolean isIndexable();

    InternalReadAccessor getFieldExtractor();

    IndexEvaluator getIndexEvaluator();

    Declaration getIndexingDeclaration();
}
