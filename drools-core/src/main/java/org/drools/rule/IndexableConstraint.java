package org.drools.rule;

import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.core.util.index.IndexUtil;
import org.drools.spi.Constraint;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;

public interface IndexableConstraint extends Constraint {

    boolean isUnification();

    boolean isIndexable(short nodeType);

    IndexUtil.ConstraintType getConstraintType();

    FieldValue getField();

    FieldIndex getFieldIndex();

    InternalReadAccessor getFieldExtractor();
}
