package org.optaplanner.core.impl.heuristic.selector.move.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.optaplanner.core.config.heuristic.selector.entity.pillar.SubPillarConfigPolicy;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.supply.Demand;
import org.optaplanner.core.impl.domain.variable.supply.SupplyManager;
import org.optaplanner.core.impl.heuristic.selector.entity.EntitySelector;
import org.optaplanner.core.impl.util.MemoizingSupply;

public final class PillarDemand<Solution_> implements Demand<MemoizingSupply<List<List<Object>>>> {

    private final EntitySelector<Solution_> entitySelector;
    private final List<GenuineVariableDescriptor<Solution_>> variableDescriptors;
    private final SubPillarConfigPolicy subpillarConfigPolicy;

    public PillarDemand(EntitySelector<Solution_> entitySelector,
            List<GenuineVariableDescriptor<Solution_>> variableDescriptors, SubPillarConfigPolicy subpillarConfigPolicy) {
        this.entitySelector = entitySelector;
        this.variableDescriptors = variableDescriptors;
        this.subpillarConfigPolicy = subpillarConfigPolicy;
    }

    @Override
    public MemoizingSupply<List<List<Object>>> createExternalizedSupply(SupplyManager supplyManager) {
        Supplier<List<List<Object>>> supplier = () -> {
            long entitySize = entitySelector.getSize();
            if (entitySize > Integer.MAX_VALUE) {
                throw new IllegalStateException("The selector (" + this + ") has an entitySelector ("
                        + entitySelector + ") with entitySize (" + entitySize
                        + ") which is higher than Integer.MAX_VALUE.");
            }
            Stream<Object> entities = StreamSupport.stream(entitySelector.spliterator(), false);
            Comparator<?> comparator = subpillarConfigPolicy.getEntityComparator();
            if (comparator != null) {
                /*
                 * The entity selection will be sorted. This will result in all the pillars being sorted without having to
                 * sort them individually later.
                 */
                entities = entities.sorted((Comparator<? super Object>) comparator);
            }
            // Create all the pillars from a stream of entities; if sorted, the pillars will be sequential.
            Map<List<Object>, List<Object>> valueStateToPillarMap = new LinkedHashMap<>((int) entitySize);
            int variableCount = variableDescriptors.size();
            entities.forEach(entity -> {
                List<Object> valueState = variableCount == 1 ? getSingleVariableValueState(entity, variableDescriptors)
                        : getMultiVariableValueState(entity, variableDescriptors, variableCount);
                List<Object> pillar = valueStateToPillarMap.computeIfAbsent(valueState, key -> new ArrayList<>());
                pillar.add(entity);
            });
            // Store the cache. Exclude pillars of size lower than the minimumSubPillarSize, as we shouldn't select those.
            Collection<List<Object>> pillarLists = valueStateToPillarMap.values();
            int minimumSubPillarSize = subpillarConfigPolicy.getMinimumSubPillarSize();
            return minimumSubPillarSize > 1 ? pillarLists.stream()
                    .filter(pillar -> pillar.size() >= minimumSubPillarSize)
                    .collect(Collectors.toList())
                    : new ArrayList<>(pillarLists);
        };
        return new MemoizingSupply<>(supplier);
    }

    private static <Solution_> List<Object> getSingleVariableValueState(Object entity,
            List<GenuineVariableDescriptor<Solution_>> variableDescriptors) {
        Object value = variableDescriptors.get(0).getValue(entity);
        return Collections.singletonList(value);
    }

    private static <Solution_> List<Object> getMultiVariableValueState(Object entity,
            List<GenuineVariableDescriptor<Solution_>> variableDescriptors, int variableCount) {
        List<Object> valueState = new ArrayList<>(variableCount);
        for (int i = 0; i < variableCount; i++) {
            Object value = variableDescriptors.get(i).getValue(entity);
            valueState.add(value);
        }
        return valueState;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        PillarDemand<?> that = (PillarDemand<?>) other;
        return Objects.equals(entitySelector, that.entitySelector)
                && Objects.equals(variableDescriptors, that.variableDescriptors)
                && Objects.equals(subpillarConfigPolicy, that.subpillarConfigPolicy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entitySelector, variableDescriptors, subpillarConfigPolicy);
    }
}
