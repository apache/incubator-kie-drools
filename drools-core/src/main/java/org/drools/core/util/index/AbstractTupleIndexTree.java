package org.drools.core.util.index;

import org.drools.base.util.index.ConstraintTypeOperator;
import org.drools.core.reteoo.Tuple;
import org.drools.base.util.FieldIndex;

public abstract class AbstractTupleIndexTree {
    protected FieldIndex index;

    protected ConstraintTypeOperator constraintType;


    protected int factSize;
    protected boolean left;


    public boolean isIndexed() {
        return true;
    }

    protected Comparable getLeftIndexedValue(Tuple tuple) {
        return getIndexedValue( tuple, left );
    }

    protected Comparable getRightIndexedValue(Tuple tuple) {
        return getIndexedValue( tuple, !left );
    }

    protected Comparable getIndexedValue(Tuple tuple, boolean left) {
        return left ?
               (Comparable) index.getLeftExtractor().getValue( tuple ) :
               (Comparable) index.getRightExtractor().getValue( tuple.getFactHandle().getObject() );
    }

    public static class IndexTupleList extends TupleList<Tuple> {
        private Comparable key;

        public IndexTupleList(Comparable key) {
            this.key = key;
        }

        public Comparable key() {
            return key;
        }
    }
}
