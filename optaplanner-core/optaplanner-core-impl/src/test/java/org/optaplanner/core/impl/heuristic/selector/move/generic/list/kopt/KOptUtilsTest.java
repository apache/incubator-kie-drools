package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt.KOptUtils.getBetweenPredicate;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.util.Pair;

public class KOptUtilsTest {

    private final TestdataListValue v1 = new TestdataListValue("1");
    private final TestdataListValue v2 = new TestdataListValue("2");
    private final TestdataListValue v3 = new TestdataListValue("3");
    private final TestdataListValue v4 = new TestdataListValue("4");
    private final TestdataListValue v5 = new TestdataListValue("5");
    private final TestdataListValue v6 = new TestdataListValue("6");
    private final TestdataListValue v7 = new TestdataListValue("7");
    private final TestdataListValue v8 = new TestdataListValue("8");
    private final TestdataListValue v9 = new TestdataListValue("9");
    private final TestdataListValue v10 = new TestdataListValue("10");
    private final TestdataListValue v11 = new TestdataListValue("11");
    private final TestdataListValue v12 = new TestdataListValue("12");

    @Test
    void testGetRemovedEdgesList() {
        List<TestdataListValue> originalTour = List.of(v1, v2, v3, v4, v5, v6, v7, v8);
        List<TestdataListValue> removedEdges = List.of(v1, v2,
                v3, v4,
                v5, v6,
                v7, v8);
        List<TestdataListValue> addedEdges = List.of(v1, v4,
                v2, v7,
                v5, v3,
                v6, v8);

        KOptDescriptor<?, TestdataListValue> kOptDescriptor = fromRemovedAndAddedEdges(originalTour,
                removedEdges,
                addedEdges);

        assertThat(KOptUtils.getRemovedEdgeList(kOptDescriptor))
                .containsExactlyInAnyOrder(Pair.of(v1, v2),
                        Pair.of(v3, v4),
                        Pair.of(v5, v6),
                        Pair.of(v7, v8));
    }

    @Test
    void testGetAddedEdgesList() {
        List<TestdataListValue> originalTour = List.of(v1, v2, v3, v4, v5, v6, v7, v8);
        List<TestdataListValue> removedEdges = List.of(v1, v2,
                v3, v4,
                v5, v6,
                v7, v8);
        List<TestdataListValue> addedEdges = List.of(v1, v4,
                v2, v7,
                v5, v3,
                v6, v8);

        KOptDescriptor<?, TestdataListValue> kOptDescriptor = fromRemovedAndAddedEdges(originalTour,
                removedEdges,
                addedEdges);

        assertThat(KOptUtils.getAddedEdgeList(kOptDescriptor))
                .containsExactlyInAnyOrder(Pair.of(v1, v4),
                        Pair.of(v2, v7),
                        Pair.of(v5, v3),
                        Pair.of(v6, v8));
    }

    @Test
    void testGetCyclesForPermutationOneCycle() {
        List<TestdataListValue> originalTour = List.of(v1, v2, v3, v4, v5, v6, v7, v8);
        List<TestdataListValue> removedEdges = List.of(v1, v2,
                v3, v4,
                v5, v6,
                v7, v8);
        List<TestdataListValue> addedEdges = List.of(v1, v4,
                v2, v7,
                v5, v3,
                v6, v8);

        KOptDescriptor<?, TestdataListValue> kOptDescriptor = fromRemovedAndAddedEdges(originalTour,
                removedEdges,
                addedEdges);
        KOptCycle cycle = KOptUtils.getCyclesForPermutation(kOptDescriptor);
        assertThat(cycle.cycleCount).isEqualTo(1);

        // Cycles:
        // v1 -> v4 -> v5 -> v3 -> v2 -> v7 -> v6 -> v8
        assertThat(cycle.indexToCycleIdentifier).containsExactly(0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Test
    void testGetCyclesForPermutationTwoCycle() {
        List<TestdataListValue> originalTour = List.of(v1, v2, v3, v4, v5, v6, v7, v8);
        List<TestdataListValue> removedEdges = List.of(v1, v2,
                v3, v4,
                v5, v6,
                v7, v8);
        List<TestdataListValue> addedEdges = List.of(v1, v4,
                v3, v6,
                v5, v8,
                v7, v2);

        KOptDescriptor<?, TestdataListValue> kOptDescriptor = fromRemovedAndAddedEdges(originalTour,
                removedEdges,
                addedEdges);
        KOptCycle cycleInfo = KOptUtils.getCyclesForPermutation(kOptDescriptor);
        assertThat(cycleInfo.cycleCount).isEqualTo(2);

        // Cycles:
        // v1 -> v4 -> v5 -> v8
        // v2 -> v3 -> v6 -> v7
        assertThat(cycleInfo.indexToCycleIdentifier).containsExactly(0, 0, 1, 1, 0, 0, 1, 1, 0);
    }

    @Test
    void testGetCyclesForPermutationThreeCycle() {
        List<TestdataListValue> originalTour = List.of(v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12);
        List<TestdataListValue> removedEdges = List.of(v1, v2,
                v3, v4,
                v5, v6,
                v7, v8,
                v9, v10,
                v11, v12);
        List<TestdataListValue> addedEdges = List.of(v1, v4,
                v5, v12,
                v3, v6,
                v7, v2,
                v8, v10,
                v11, v9);

        KOptDescriptor<?, TestdataListValue> kOptDescriptor = fromRemovedAndAddedEdges(originalTour,
                removedEdges,
                addedEdges);
        KOptCycle cycleInfo = KOptUtils.getCyclesForPermutation(kOptDescriptor);
        assertThat(cycleInfo.cycleCount).isEqualTo(3);

        // Cycles:
        // v1 -> v4 -> v5 -> v12
        // v2 -> v7 -> v6 -> v3
        // v8 -> v10 -> v11 -> v9
        assertThat(cycleInfo.indexToCycleIdentifier).containsExactly(0, 0, 1, 1, 0, 0, 1, 1, 2, 2, 2, 2, 0);
    }

    @Test
    void flipSubarray() {
        int[] array = new int[] { 1, 2, 5, 4, 3, 6, 7, 8 };

        KOptUtils.flipSubarray(array, 2, 5);
        assertThat(array).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);

        KOptUtils.flipSubarray(array, 2, 5);
        assertThat(array).containsExactly(1, 2, 5, 4, 3, 6, 7, 8);
    }

    @Test
    void flipSubarraySecondEndsBeforeFirst() {
        int[] array = new int[] { 8, 7, 3, 4, 5, 6, 2, 1 };

        KOptUtils.flipSubarray(array, 6, 2);
        assertThat(array).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);

        KOptUtils.flipSubarray(array, 6, 2);
        assertThat(array).containsExactly(8, 7, 3, 4, 5, 6, 2, 1);
    }

    @Test
    void flipSubarraySecondEndsBeforeFirstUnbalanced() {
        int[] array = new int[] { 6, 5, 3, 4, 2, 1, 8, 7 };

        KOptUtils.flipSubarray(array, 4, 2);
        assertThat(array).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);

        KOptUtils.flipSubarray(array, 4, 2);
        assertThat(array).containsExactly(6, 5, 3, 4, 2, 1, 8, 7);
    }

    @Test
    void flipSubarrayFirstEndsBeforeSecondUnbalanced() {
        int[] array = new int[] { 2, 1, 8, 7, 5, 6, 4, 3 };

        KOptUtils.flipSubarray(array, 6, 4);
        assertThat(array).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);

        KOptUtils.flipSubarray(array, 6, 4);
        assertThat(array).containsExactly(2, 1, 8, 7, 5, 6, 4, 3);
    }

    @Test
    void testGetPureKOptMoveTypes() {
        assertThat(KOptUtils.getPureKOptMoveTypes(2)).isEqualTo(1L);
        assertThat(KOptUtils.getPureKOptMoveTypes(3)).isEqualTo(4L);
        assertThat(KOptUtils.getPureKOptMoveTypes(4)).isEqualTo(25L);
        assertThat(KOptUtils.getPureKOptMoveTypes(5)).isEqualTo(208L);
        assertThat(KOptUtils.getPureKOptMoveTypes(6)).isEqualTo(2121L);
        assertThat(KOptUtils.getPureKOptMoveTypes(7)).isEqualTo(25828L);
        assertThat(KOptUtils.getPureKOptMoveTypes(8)).isEqualTo(365457L);
    }

    private static <Solution_> KOptDescriptor<Solution_, TestdataListValue> fromRemovedAndAddedEdges(
            List<TestdataListValue> originalTour,
            List<TestdataListValue> removedEdgeList,
            List<TestdataListValue> addedEdgeList) {

        if (addedEdgeList.size() != removedEdgeList.size()) {
            throw new IllegalArgumentException(
                    "addedEdgeList (" + addedEdgeList + ") and removedEdgeList (" + removedEdgeList + ") have the same size");
        }

        if ((addedEdgeList.size() % 2) != 0) {
            throw new IllegalArgumentException(
                    "addedEdgeList and removedEdgeList are invalid: there is an odd number of endpoints.");
        }

        if (!addedEdgeList.containsAll(removedEdgeList)) {
            throw new IllegalArgumentException("addedEdgeList (" + addedEdgeList + ") is invalid; it contains endpoints "
                    + "that are not included in the removedEdgeList (" + removedEdgeList + ").");
        }

        Function<TestdataListValue, TestdataListValue> successorFunction =
                item -> originalTour.get((originalTour.indexOf(item) + 1) % originalTour.size());

        for (int i = 0; i < removedEdgeList.size(); i += 2) {
            if (successorFunction.apply(removedEdgeList.get(i)) != removedEdgeList.get(i + 1)
                    && successorFunction.apply(removedEdgeList.get(i + 1)) != removedEdgeList.get(i)) {
                throw new IllegalArgumentException("removedEdgeList (" + removedEdgeList + ") contains an invalid edge ((" +
                        removedEdgeList.get(i) + ", " + removedEdgeList.get(i + 1) + ")).");
            }
        }

        TestdataListValue[] tourArray = new TestdataListValue[removedEdgeList.size() + 1];
        int[] incl = new int[removedEdgeList.size() + 1];
        for (int i = 0; i < removedEdgeList.size(); i += 2) {
            tourArray[i + 1] = removedEdgeList.get(i);
            tourArray[i + 2] = removedEdgeList.get(i + 1);
            int addedEdgeIndex = identityIndexOf(addedEdgeList, removedEdgeList.get(i));

            if (addedEdgeIndex % 2 == 0) {
                incl[i + 1] = identityIndexOf(removedEdgeList, addedEdgeList.get(addedEdgeIndex + 1)) + 1;
            } else {
                incl[i + 1] = identityIndexOf(removedEdgeList, addedEdgeList.get(addedEdgeIndex - 1)) + 1;
            }

            addedEdgeIndex = identityIndexOf(addedEdgeList, removedEdgeList.get(i + 1));
            if (addedEdgeIndex % 2 == 0) {
                incl[i + 2] = identityIndexOf(removedEdgeList, addedEdgeList.get(addedEdgeIndex + 1)) + 1;
            } else {
                incl[i + 2] = identityIndexOf(removedEdgeList, addedEdgeList.get(addedEdgeIndex - 1)) + 1;
            }
        }

        return new KOptDescriptor<>(tourArray,
                incl,
                item -> originalTour.get((originalTour.indexOf(item) + 1) % originalTour.size()),
                getBetweenPredicate(originalTour::indexOf));
    }

    private static int identityIndexOf(List<TestdataListValue> sourceList, TestdataListValue query) {
        for (int i = 0; i < sourceList.size(); i++) {
            if (sourceList.get(i) == query) {
                return i;
            }
        }
        return -1;
    }
}
