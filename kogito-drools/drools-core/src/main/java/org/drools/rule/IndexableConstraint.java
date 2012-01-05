package org.drools.rule;

import org.drools.core.util.AbstractHashTable.FieldIndex;
import org.drools.spi.Constraint;

public interface IndexableConstraint extends Constraint {

    boolean isUnification();

    boolean isIndexable();

    FieldIndex getFieldIndex();
}
