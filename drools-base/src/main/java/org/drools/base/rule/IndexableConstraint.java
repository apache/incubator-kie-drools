package org.drools.base.rule;

import org.drools.base.rule.accessor.FieldValue;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.accessor.TupleValueExtractor;
import org.drools.base.rule.constraint.Constraint;
import org.drools.base.util.FieldIndex;
import org.drools.base.util.index.ConstraintTypeOperator;
import org.kie.api.KieBaseConfiguration;

public interface IndexableConstraint extends Constraint {

    boolean isUnification();

    boolean isIndexable(short nodeType, KieBaseConfiguration config);

    ConstraintTypeOperator getConstraintType();

    FieldValue getField();

    FieldIndex getFieldIndex();

    ReadAccessor getFieldExtractor();

    default void unsetUnification() { }

    TupleValueExtractor getIndexExtractor();
}
