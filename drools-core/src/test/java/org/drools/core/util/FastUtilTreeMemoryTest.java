package org.drools.core.util;

import org.drools.core.base.ValueType;
import org.drools.core.base.extractors.BaseObjectClassFieldReader;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.accessor.ReadAccessor;
import org.drools.core.rule.accessor.TupleValueExtractor;
import org.drools.core.util.index.FastUtilTreeMemory;
import org.drools.core.util.index.FastUtilTreeMemory.TreeFastIterator;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FastUtilTreeMemoryTest {

    @Test
    public void testMocks() {
        TupleValueExtractor leftValueExtractor = getTupleValueExtractor();
        ReadAccessor rightValueExtractor = getRightExtractor();

        Tuple tuple10 = getLeftTuple(10);

        AbstractHashTable.FieldIndex fieldIndex = new AbstractHashTable.FieldIndex(rightValueExtractor, leftValueExtractor);
        assertThat(fieldIndex.getLeftExtractor().getValue(tuple10)).isEqualTo(10);
    }

    @Test
    public void testGreaterThan() {
        AbstractHashTable.FieldIndex fieldIndex = new AbstractHashTable.FieldIndex(getRightExtractor(), getTupleValueExtractor());
        FastUtilTreeMemory treeMemory = new FastUtilTreeMemory(ConstraintType.GREATER_THAN, fieldIndex, !true);

        Tuple tuple10 = getLeftTuple(10);
        Tuple tuple20 = getLeftTuple(20);
        Tuple tuple30 = getLeftTuple(30);
        Tuple tuple40 = getLeftTuple(40);

        treeMemory.add(tuple10);
        treeMemory.add(tuple20);
        treeMemory.add(tuple30);
        treeMemory.add(tuple40);

        // check gets next after 10
        TreeFastIterator it = (TreeFastIterator) treeMemory.fastIterator();
        assertThatEquals(it, 10, 20); // needs to skip the 10

        // checks key after last
        it = (TreeFastIterator) treeMemory.fastIterator();
        assertThatIsNull(it, 50); // doesn't exist

        // checks key before first
        it = (TreeFastIterator) treeMemory.fastIterator();
        Tuple tuple = assertThatEquals(it, 5, 10);
        tuple = assertThatEquals(tuple, it, 20);
        tuple = assertThatEquals(tuple, it, 30);
        tuple = assertThatEquals(tuple, it, 40);
        assertThat(it.next(tuple)).isNull();
    }

    @Test
    public void testGreaterOrEqual() {
        AbstractHashTable.FieldIndex fieldIndex = new AbstractHashTable.FieldIndex(getRightExtractor(), getTupleValueExtractor());
        FastUtilTreeMemory treeMemory = new FastUtilTreeMemory(ConstraintType.GREATER_OR_EQUAL, fieldIndex, !true);

        Tuple tuple10 = getLeftTuple(10);
        Tuple tuple20 = getLeftTuple(20);
        Tuple tuple30 = getLeftTuple(30);
        Tuple tuple40 = getLeftTuple(40);

        treeMemory.add(tuple10);
        treeMemory.add(tuple20);
        treeMemory.add(tuple30);
        treeMemory.add(tuple40);

        // check gets 10
        TreeFastIterator it = (TreeFastIterator) treeMemory.fastIterator();
        assertThatEquals(it, 10, 10);

        // checks key after last
        it = (TreeFastIterator) treeMemory.fastIterator();
        assertThatIsNull(it, 50); // doesn't exist

        // checks key before first
        it = (TreeFastIterator) treeMemory.fastIterator();
        Tuple tuple = assertThatEquals(it, 5, 10);
        tuple = assertThatEquals(tuple, it, 20);
        tuple = assertThatEquals(tuple, it, 30);
        tuple = assertThatEquals(tuple, it, 40);
        assertThat(it.next(tuple)).isNull();
    }

    @Test
    public void testLessOrEqual() {
        AbstractHashTable.FieldIndex fieldIndex = new AbstractHashTable.FieldIndex(getRightExtractor(), getTupleValueExtractor());
        FastUtilTreeMemory treeMemory = new FastUtilTreeMemory(ConstraintType.LESS_OR_EQUAL, fieldIndex, !true);

        Tuple tuple10 = getLeftTuple(10);
        Tuple tuple20 = getLeftTuple(20);
        Tuple tuple30 = getLeftTuple(30);
        Tuple tuple40 = getLeftTuple(40);

        treeMemory.add(tuple10);
        treeMemory.add(tuple20);
        treeMemory.add(tuple30);
        treeMemory.add(tuple40);

        // check key before first
        TreeFastIterator it = (TreeFastIterator) treeMemory.fastIterator();
        assertThatIsNull(it, 5);

        // check gets 10, but nothing more
        it = (TreeFastIterator) treeMemory.fastIterator();
        Tuple tuple = assertThatEquals(it, 10, 10);
        assertThat(it.next(tuple)).isNull();

        // check gets 10 and 20, but nothing more
        it = (TreeFastIterator) treeMemory.fastIterator();
        tuple = assertThatEquals(it, 20, 10);
        tuple = assertThatEquals(tuple, it, 20);
        assertThat(it.next(tuple)).isNull();

        // check gets 10, 20 and 30 but nothing more
        it = (TreeFastIterator) treeMemory.fastIterator();
        tuple = assertThatEquals(it, 30, 10);
        tuple = assertThatEquals(tuple, it, 20);
        tuple = assertThatEquals(tuple, it, 30);
        assertThat(it.next(tuple)).isNull();


        // check gets 10, 20 and 30 but nothing more
        it = (TreeFastIterator) treeMemory.fastIterator();
        tuple = assertThatEquals(it, 35, 10);
        tuple = assertThatEquals(tuple, it, 20);
        tuple = assertThatEquals(tuple, it, 30);
        assertThat(it.next(tuple)).isNull();

        // check key after last
        it = (TreeFastIterator) treeMemory.fastIterator();
        tuple = assertThatEquals(it, 45, 10);
        tuple = assertThatEquals(tuple, it, 20);
        tuple = assertThatEquals(tuple, it, 30);
        tuple = assertThatEquals(tuple, it, 40);
        assertThat(it.next(tuple)).isNull();
    }

    @Test
    public void testLessThan() {
        AbstractHashTable.FieldIndex fieldIndex = new AbstractHashTable.FieldIndex(getRightExtractor(), getTupleValueExtractor());
        FastUtilTreeMemory treeMemory = new FastUtilTreeMemory(ConstraintType.LESS_THAN, fieldIndex, !true);

        Tuple tuple10 = getLeftTuple(10);
        Tuple tuple20 = getLeftTuple(20);
        Tuple tuple30 = getLeftTuple(30);
        Tuple tuple40 = getLeftTuple(40);

        treeMemory.add(tuple10);
        treeMemory.add(tuple20);
        treeMemory.add(tuple30);
        treeMemory.add(tuple40);

        // check key before first
        TreeFastIterator it = (TreeFastIterator) treeMemory.fastIterator();
        assertThatIsNull(it, 5);

        // check still nothing
        it =  (TreeFastIterator) treeMemory.fastIterator();
        assertThatIsNull(it, 10);

        // check gets 10 but nothing more
        it = (TreeFastIterator) treeMemory.fastIterator();
        Tuple tuple = assertThatEquals(it, 20, 10);
        assertThat(it.next(tuple)).isNull();

        // check gets 10 and 20  but nothing more
        it = (TreeFastIterator) treeMemory.fastIterator();
        tuple = assertThatEquals(it, 30, 10);
        tuple = assertThatEquals(tuple, it, 20);
        assertThat(it.next(tuple)).isNull();


        // check gets 10, 20 and 30 but nothing more
        it = (TreeFastIterator) treeMemory.fastIterator();
        tuple = assertThatEquals(it, 35, 10);
        tuple = assertThatEquals(tuple, it, 20);
        tuple = assertThatEquals(tuple, it, 30);
        assertThat(it.next(tuple)).isNull();

        // check key after last
        it = (TreeFastIterator) treeMemory.fastIterator();
        tuple = assertThatEquals(it, 45, 10);
        tuple = assertThatEquals(tuple, it, 20);
        tuple = assertThatEquals(tuple, it, 30);
        tuple = assertThatEquals(tuple, it, 40);
        assertThat(it.next(tuple)).isNull();
    }

    @Test
    public void testSharedFirstBucket() {
        AbstractHashTable.FieldIndex fieldIndex = new AbstractHashTable.FieldIndex(getRightExtractor(), getTupleValueExtractor());
        FastUtilTreeMemory  treeMemory = new FastUtilTreeMemory(ConstraintType.GREATER_THAN, fieldIndex, !true);

        Tuple tuple10_1 = getLeftTuple(10);
        Tuple tuple10_2 = getLeftTuple(10);
        Tuple tuple10_3 = getLeftTuple(10);
        Tuple tuple20 = getLeftTuple(20);
        Tuple tuple30 = getLeftTuple(30);
        Tuple tuple40 = getLeftTuple(40);

        treeMemory.add(tuple10_1);
        treeMemory.add(tuple10_2);
        treeMemory.add(tuple10_3);
        treeMemory.add(tuple20);
        treeMemory.add(tuple30);
        treeMemory.add(tuple40);

        // checks key before first
        TreeFastIterator it = (TreeFastIterator) treeMemory.fastIterator();
        Tuple tuple = assertThatEquals(it, 5, 10);
        assertThat(tuple).isSameAs(tuple10_1);
        tuple = assertThatEquals(tuple, it, 10);
        assertThat(tuple).isSameAs(tuple10_2);
        tuple = assertThatEquals(tuple, it, 10);
        assertThat(tuple).isSameAs(tuple10_3);
        tuple = assertThatEquals(tuple, it, 20);
        tuple = assertThatEquals(tuple, it, 30);
        tuple = assertThatEquals(tuple, it, 40);
        assertThat(it.next(tuple)).isNull();

        // Check full iterator
        FastIterator fullit = treeMemory.fullFastIterator();
        tuple = assertThatEquals(fullit,10);
        assertThat(tuple).isSameAs(tuple10_1);
        tuple = assertThatEquals(tuple, fullit, 10);
        assertThat(tuple).isSameAs(tuple10_2);
        tuple = assertThatEquals(tuple, fullit, 10);
        assertThat(tuple).isSameAs(tuple10_3);
        tuple = assertThatEquals(tuple, fullit, 20);
        tuple = assertThatEquals(tuple, fullit, 30);
        tuple = assertThatEquals(tuple, fullit, 40);
        assertThat(fullit.next(tuple)).isNull();
    }

    @Test
    public void testSharedLastBucket() {
        AbstractHashTable.FieldIndex fieldIndex = new AbstractHashTable.FieldIndex(getRightExtractor(), getTupleValueExtractor());
        FastUtilTreeMemory treeMemory = new FastUtilTreeMemory(ConstraintType.GREATER_THAN, fieldIndex, !true);

        Tuple tuple10 = getLeftTuple(10);
        Tuple tuple20 = getLeftTuple(20);
        Tuple tuple30 = getLeftTuple(30);
        Tuple tuple40_1 = getLeftTuple(40);
        Tuple tuple40_2 = getLeftTuple(40);
        Tuple tuple40_3 = getLeftTuple(40);

        treeMemory.add(tuple10);
        treeMemory.add(tuple20);
        treeMemory.add(tuple30);
        treeMemory.add(tuple40_1);
        treeMemory.add(tuple40_2);
        treeMemory.add(tuple40_3);

        // checks key before first
        TreeFastIterator it = (TreeFastIterator) treeMemory.fastIterator();
        Tuple tuple = assertThatEquals(it, 5, 10);
        tuple = assertThatEquals(tuple, it, 20);
        tuple = assertThatEquals(tuple, it, 30);
        tuple = assertThatEquals(tuple, it, 40);
        assertThat(tuple).isSameAs(tuple40_1);
        tuple = assertThatEquals(tuple, it, 40);
        assertThat(tuple).isSameAs(tuple40_2);
        tuple = assertThatEquals(tuple, it, 40);
        assertThat(tuple).isSameAs(tuple40_3);
        assertThat(it.next(tuple)).isNull();


        // check full iterator
        FastIterator fullit = treeMemory.fullFastIterator();
        tuple = assertThatEquals(fullit, 10);
        tuple = assertThatEquals(tuple, fullit, 20);
        tuple = assertThatEquals(tuple, fullit, 30);
        tuple = assertThatEquals(tuple, fullit, 40);
        assertThat(tuple).isSameAs(tuple40_1);
        tuple = assertThatEquals(tuple, fullit, 40);
        assertThat(tuple).isSameAs(tuple40_2);
        tuple = assertThatEquals(tuple, fullit, 40);
        assertThat(tuple).isSameAs(tuple40_3);
        assertThat(fullit.next(tuple)).isNull();
    }

    private static Tuple assertThatEquals(Tuple tuple, TreeFastIterator it, int expected) {
        tuple = (Tuple) it.next(tuple);
        return assertThatEquals(tuple, expected);
    }

    private static Tuple assertThatEquals(Tuple tuple, FastIterator it, int expected) {
        tuple = (Tuple) it.next(tuple);
        return assertThatEquals(tuple, expected);
    }

    private static Tuple assertThatEquals(TreeFastIterator it, int first, int expected) {
        Tuple tuple =  it.getFirst(getLeftTuple(first));
        return assertThatEquals(tuple, expected);
    }

    private static Tuple assertThatEquals(FastIterator it, int expected) {
        Tuple tuple =  (Tuple) it.next(null);
        return assertThatEquals(tuple, expected);
    }

    private static void assertThatIsNull(TreeFastIterator it, int first) {
        Tuple tuple =  it.getFirst(getLeftTuple(first));
        assertThat(tuple).isNull();
    }

    private static Tuple assertThatEquals(Tuple tuple, int expected) {
        assertThat(tuple.getFactHandle().getObject()).isEqualTo(expected);
        return tuple;
    }

    public static InternalFactHandle getFactHandle(int number) {
        return new DefaultFactHandle(number);
    }

    public static Tuple getLeftTuple(int number) {
        LeftTupleImpl tuple = new LeftTupleImpl();
        tuple.setFactHandle(getFactHandle(number));
        return tuple;
    };

    public static TupleValueExtractor getTupleValueExtractor() {
        TupleValueExtractor extractor = new TupleValueExtractor() {
            @Override
            public ValueType getValueType() {
                return null;
            }

            @Override
            public Object getValue(ReteEvaluator reteEvaluator, Tuple tuple) {
                return tuple.getFactHandle().getObject();
            }

            @Override
            public TupleValueExtractor clone() {
                return null;
            }
        };

        return extractor;
    }

    public static ReadAccessor getRightExtractor() {
        ReadAccessor readAccessor = new BaseObjectClassFieldReader() {
            @Override
            public Object getValue(ReteEvaluator reteEvaluator, Object object) {
                return object;
            }
        };

        return readAccessor;
    }
}
