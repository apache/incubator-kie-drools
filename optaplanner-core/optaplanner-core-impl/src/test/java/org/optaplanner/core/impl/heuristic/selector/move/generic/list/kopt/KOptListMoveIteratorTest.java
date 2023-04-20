package org.optaplanner.core.impl.heuristic.selector.move.generic.list.kopt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.variable.descriptor.ListVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.index.IndexVariableSupply;
import org.optaplanner.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import org.optaplanner.core.impl.heuristic.selector.value.EntityIndependentValueSelector;

public class KOptListMoveIteratorTest {

    private static class KOptListMoveIteratorMockData {
        int minK;
        int maxK;
        int[] pickedKDistribution;

        int distributionSum;

        KOptListMoveIterator<Object, Object> kOptListMoveIterator;
        Random workingRandom;
        ListVariableDescriptor<Object> listVariableDescriptor;
        SingletonInverseVariableSupply inverseVariableSupply;
        IndexVariableSupply indexVariableSupply;
        EntityIndependentValueSelector<Object> originSelector;
        EntityIndependentValueSelector<Object> valueSelector;
    }

    @SuppressWarnings("unchecked")
    private KOptListMoveIteratorMockData createMockKOptListMoveIterator(int minK, int maxK, int[] pickedKDistribution) {
        KOptListMoveIteratorMockData result = new KOptListMoveIteratorMockData();
        result.minK = minK;
        result.maxK = maxK;
        result.pickedKDistribution = pickedKDistribution;
        result.distributionSum = 0;
        for (int i = 0; i < pickedKDistribution.length; i++) {
            result.distributionSum += pickedKDistribution[i];
        }
        result.workingRandom = mock(Random.class);
        result.listVariableDescriptor = mock(ListVariableDescriptor.class);
        result.inverseVariableSupply = mock(SingletonInverseVariableSupply.class);
        result.indexVariableSupply = mock(IndexVariableSupply.class);
        result.originSelector = mock(EntityIndependentValueSelector.class);
        result.valueSelector = mock(EntityIndependentValueSelector.class);
        result.kOptListMoveIterator = new KOptListMoveIterator<>(
                result.workingRandom,
                result.listVariableDescriptor,
                result.inverseVariableSupply,
                result.indexVariableSupply,
                result.originSelector,
                result.valueSelector,
                minK,
                maxK,
                pickedKDistribution);

        return result;
    }

    private Iterator<Object> iteratorForValues(Object... values) {
        return Arrays.stream(values).iterator();
    }

    private static class KOptMoveInfo {
        List<Object> removedEdgeList;
        int[] addedEdgeIndexToOtherEndpoint;
        List<Object> entityList;

        public void verify(KOptListMove<?> kOptListMove) {
            KOptDescriptor<?> descriptor = kOptListMove.getDescriptor();
            assertThat(descriptor.getK()).isEqualTo(removedEdgeList.size() / 2);
            List<Object> expectedRemoveEdges = new ArrayList<>(descriptor.getK() * 2 + 1);
            expectedRemoveEdges.add(null);
            expectedRemoveEdges.addAll(removedEdgeList);
            assertThat((Object[]) descriptor.getRemovedEdges()).containsExactly(expectedRemoveEdges.toArray());

            assertThat(descriptor.getAddedEdgeToOtherEndpoint()).containsExactly(addedEdgeIndexToOtherEndpoint);
        }
    }

    /**
     * @return The expected KOptMoveInfo
     */
    private KOptMoveInfo setupValidOddSequentialKOptMove(KOptListMoveIteratorMockData mocks, int k, Object... entities) {
        if (entities.length != k) {
            throw new IllegalArgumentException("Expected (" + k + ") arguments");
        }
        if (k % 2 != 1) {
            throw new IllegalArgumentException("Function can only be used for odd k (" + k + " is not odd).");
        }
        int randomValue = 0;
        for (int i = mocks.minK; i < k; i++) {
            randomValue += mocks.pickedKDistribution[i - mocks.minK];
        }
        when(mocks.workingRandom.nextInt(mocks.distributionSum)).thenReturn(randomValue);

        Object[] data = new Object[2 * k];
        for (int i = 0; i < data.length; i++) {
            data[i] = "v" + i;
        }

        Map<Object, Integer> entityToListSize = Arrays.stream(entities)
                .collect(Collectors.toMap(Function.identity(),
                        entity -> 2,
                        Integer::sum));
        Map<Object, List<Object>> entityToList = new HashMap<>();
        Map<Object, Integer> entityToOffset = new HashMap<>();

        int offset = 0;
        for (Map.Entry<Object, Integer> entityAndListSize : entityToListSize.entrySet()) {
            Object entity = entityAndListSize.getKey();
            int listSize = entityAndListSize.getValue();
            List<Object> entityList = new ArrayList<>(Arrays.asList(data).subList(offset, offset + listSize));
            for (int i = 0; i < listSize; i++) {
                entityList.add(2 * i + 1, entity + "-extra-" + i);
            }
            entityList.add(0, entity + "-start");
            entityList.add(entity + "-end");
            when(mocks.listVariableDescriptor.getListVariable(entity)).thenReturn(entityList);

            entityToList.put(entity, entityList);
            entityToOffset.put(entity, 1);

            for (int i = 0; i < entityList.size(); i++) {
                when(mocks.inverseVariableSupply.getInverseSingleton(entityList.get(i))).thenReturn(entity);
                when(mocks.indexVariableSupply.getIndex(entityList.get(i))).thenReturn(i);
            }
            when(mocks.listVariableDescriptor.getListSize(entity)).thenReturn(entityList.size());
            offset += listSize;
        }

        when(mocks.workingRandom.nextBoolean()).thenReturn(true);

        Object firstPicked = entityToList.get(entities[0]).get(1);
        Object firstSuccessor = entityToList.get(entities[0]).get(2);
        entityToOffset.merge(entities[0], 3, Integer::sum);

        when(mocks.originSelector.iterator()).thenReturn(iteratorForValues(firstPicked));
        Object[] remainingPicked = new Object[k - 1];
        Object[] remainingPickedSuccessor = new Object[k - 1];

        List<Object> pickedValueList = new ArrayList<>();
        for (int i = 1; i < k; i++) {
            int index = entityToOffset.get(entities[i]);
            Object value = entityToList.get(entities[i]).get(index);
            Object valueSuccessor = entityToList.get(entities[i]).get(index + 1);
            entityToOffset.merge(entities[i], 3, Integer::sum);
            pickedValueList.add(0, value);

            remainingPicked[remainingPicked.length - i] = value;
            remainingPickedSuccessor[remainingPicked.length - i] = valueSuccessor;
        }
        when(mocks.valueSelector.iterator()).thenReturn(pickedValueList.iterator());

        KOptMoveInfo out = new KOptMoveInfo();

        out.removedEdgeList = new ArrayList<>(2 * k);
        out.entityList = Arrays.stream(entities).distinct().collect(Collectors.toList());
        out.removedEdgeList.add(firstPicked);
        out.removedEdgeList.add(firstSuccessor);

        for (int i = 0; i < remainingPicked.length; i++) {
            out.removedEdgeList.add(remainingPicked[i]);
            out.removedEdgeList.add(remainingPickedSuccessor[i]);
        }

        Object[] tourData = new Object[out.removedEdgeList.size() + 1];
        for (int i = 0; i < out.removedEdgeList.size(); i++) {
            tourData[i + 1] = out.removedEdgeList.get(i);
        }
        out.addedEdgeIndexToOtherEndpoint = KOptDescriptor.computeInEdgesForSequentialMove(tourData);
        return out;
    }

    /**
     * @return The expected KOptMoveInfo
     */
    private KOptMoveInfo setupValidNonsequential4OptMove(KOptListMoveIteratorMockData mocks, Object... entities) {
        int k = 4;
        if (entities.length != (k + 1)) {
            throw new IllegalArgumentException("Expected (" + (k + 1) + ") arguments");
        }
        int randomValue = 0;
        for (int i = mocks.minK; i < k; i++) {
            randomValue += mocks.pickedKDistribution[i - mocks.minK];
        }
        when(mocks.workingRandom.nextInt(mocks.distributionSum)).thenReturn(randomValue);

        Object[] data = new Object[2 * k + 8];
        for (int i = 0; i < data.length; i++) {
            data[i] = "v" + i;
        }

        Map<Object, Integer> entityToListSize = Arrays.stream(entities)
                .collect(Collectors.toMap(Function.identity(),
                        entity -> 2,
                        Integer::sum));
        Map<Object, List<Object>> entityToList = new HashMap<>();
        Map<Object, Integer> entityToOffset = new HashMap<>();

        int offset = 0;
        for (Map.Entry<Object, Integer> entityAndListSize : entityToListSize.entrySet()) {
            Object entity = entityAndListSize.getKey();
            int listSize = entityAndListSize.getValue();
            List<Object> entityList = new ArrayList<>(Arrays.asList(data).subList(offset, offset + listSize));
            for (int i = 0; i < listSize; i++) {
                entityList.add(2 * i + 1, entity + "-extra-" + i);
            }
            entityList.add(0, entity + "-start");
            entityList.add(entity + "-end");
            when(mocks.listVariableDescriptor.getListVariable(entity)).thenReturn(entityList);

            entityToList.put(entity, entityList);
            entityToOffset.put(entity, 1);

            for (int i = 0; i < entityList.size(); i++) {
                when(mocks.inverseVariableSupply.getInverseSingleton(entityList.get(i))).thenReturn(entity);
                when(mocks.indexVariableSupply.getIndex(entityList.get(i))).thenReturn(i);
            }
            when(mocks.listVariableDescriptor.getListSize(entity)).thenReturn(entityList.size());
            offset += listSize;
        }

        when(mocks.workingRandom.nextBoolean()).thenReturn(true);

        Object firstPicked = entityToList.get(entities[0]).get(1);
        Object firstSuccessor = entityToList.get(entities[0]).get(2);
        entityToOffset.merge(entities[0], 3, Integer::sum);

        when(mocks.originSelector.iterator()).thenReturn(iteratorForValues(firstPicked));
        Object[] remainingPicked = new Object[k];
        Object[] remainingPickedSuccessor = new Object[k];

        List<Object> pickedValueList = new ArrayList<>();
        for (int i = 1; i < k + 1; i++) {
            int index = entityToOffset.get(entities[i]);
            Object value = entityToList.get(entities[i]).get(index);
            Object valueSuccessor = entityToList.get(entities[i]).get(index + 1);
            entityToOffset.merge(entities[i], 3, Integer::sum);
            pickedValueList.add(0, value);

            if (i != 1) {
                remainingPicked[remainingPicked.length - i] = value;
                remainingPickedSuccessor[remainingPicked.length - i] = valueSuccessor;
            } else {
                remainingPicked[remainingPicked.length - i] = "e1-extra-0";
                remainingPickedSuccessor[remainingPicked.length - i] = "v1";
            }
        }

        int finalValueIndex = entityToOffset.get(entities[k]);
        List<Object> distinctEntityList = Arrays.stream(entities).distinct().collect(Collectors.toList());
        int entityListIndex = distinctEntityList.indexOf(entities[k]);
        when(mocks.workingRandom.ints(0, distinctEntityList.size()))
                .thenReturn(IntStream.iterate(entityListIndex, value -> value));
        when(mocks.workingRandom.nextInt(entityToList.get(distinctEntityList.get(entityListIndex)).size()))
                .thenReturn(finalValueIndex);
        when(mocks.valueSelector.iterator()).thenReturn(pickedValueList.iterator());

        KOptMoveInfo out = new KOptMoveInfo();

        out.removedEdgeList = new ArrayList<>(2 * k);
        out.entityList = Arrays.stream(entities).distinct().collect(Collectors.toList());

        out.removedEdgeList.add(firstPicked);
        out.removedEdgeList.add(firstSuccessor);

        for (int i = 0; i < remainingPicked.length; i++) {
            out.removedEdgeList.add(remainingPicked[i]);
            out.removedEdgeList.add(remainingPickedSuccessor[i]);
        }
        out.removedEdgeList.add(entityToList.get(distinctEntityList.get(entityListIndex)).get(finalValueIndex));
        out.removedEdgeList.add(entityToList.get(distinctEntityList.get(entityListIndex)).get(finalValueIndex + 1));

        // Hack since calculating the exact removed and added edge is complicated
        if (distinctEntityList.size() > 1) {
            out.removedEdgeList.clear();
            out.removedEdgeList.addAll(Arrays.asList(
                    "v0",
                    "e1-extra-0",
                    "v3",
                    "e1-extra-3",
                    "e2-extra-1",
                    "v8",
                    "e1-extra-1",
                    "v2",
                    "v8",
                    "e2-extra-2",
                    "e1-extra-4",
                    "v5"));
        }

        out.addedEdgeIndexToOtherEndpoint = new int[] {
                0, 8, 3, 2, 5, 4, 7, 6, 1, 12, 11, 10, 9, 15, 16, 13, 14
        };
        return out;
    }

    @Test
    void testSequentialKOptOnSameEntity() {
        KOptListMoveIteratorMockData mocks = createMockKOptListMoveIterator(2, 4, new int[] { 1, 1, 1, 1 });

        KOptMoveInfo kOptMoveInfo = setupValidOddSequentialKOptMove(mocks, 3, "e1", "e1", "e1");
        KOptListMove<?> kOptListMove = (KOptListMove<?>) mocks.kOptListMoveIterator.createUpcomingSelection();
        kOptMoveInfo.verify(kOptListMove);

        kOptMoveInfo = setupValidOddSequentialKOptMove(mocks, 5, "e1", "e1", "e1", "e1", "e1");
        kOptListMove = (KOptListMove<?>) mocks.kOptListMoveIterator.createUpcomingSelection();
        kOptMoveInfo.verify(kOptListMove);
    }

    @Test
    void testSequentialKOptOnDifferentEntities() {
        KOptListMoveIteratorMockData mocks = createMockKOptListMoveIterator(2, 4, new int[] { 1, 1, 1, 1 });

        KOptMoveInfo kOptMoveInfo = setupValidOddSequentialKOptMove(mocks, 5, "e1", "e2", "e1", "e2", "e1");
        KOptListMove<?> kOptListMove = (KOptListMove<?>) mocks.kOptListMoveIterator.createUpcomingSelection();
        kOptMoveInfo.verify(kOptListMove);
    }

    @Test
    void testNonsequentialKOptOnSameEntity() {
        KOptListMoveIteratorMockData mocks = createMockKOptListMoveIterator(2, 4, new int[] { 1, 1, 1, 1 });

        KOptMoveInfo kOptMoveInfo = setupValidNonsequential4OptMove(mocks, "e1", "e1", "e1", "e1", "e1");
        KOptListMove<?> kOptListMove = (KOptListMove<?>) mocks.kOptListMoveIterator.createUpcomingSelection();
        kOptMoveInfo.verify(kOptListMove);
    }

    @Test
    void testNonsequentialKOptOnDifferentEntity() {
        KOptListMoveIteratorMockData mocks = createMockKOptListMoveIterator(2, 4, new int[] { 1, 1, 1, 1 });

        KOptMoveInfo kOptMoveInfo = setupValidNonsequential4OptMove(mocks, "e1", "e2", "e1", "e2", "e1");
        KOptListMove<?> kOptListMove = (KOptListMove<?>) mocks.kOptListMoveIterator.createUpcomingSelection();
        kOptMoveInfo.verify(kOptListMove);
    }

}
