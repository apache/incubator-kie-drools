package org.optaplanner.core.impl.domain.solution.mutation;

import java.util.Iterator;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class MutationCounter<Solution_> {

    protected final SolutionDescriptor<Solution_> solutionDescriptor;

    public MutationCounter(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    /**
     *
     * @param a never null
     * @param b never null
     * @return {@code >= 0}, the number of planning variables that have a different value in {@code a} and {@code b}.
     */
    public int countMutations(Solution_ a, Solution_ b) {
        int mutationCount = 0;
        for (EntityDescriptor<Solution_> entityDescriptor : solutionDescriptor.getGenuineEntityDescriptors()) {
            List<Object> aEntities = entityDescriptor.extractEntities(a);
            List<Object> bEntities = entityDescriptor.extractEntities(b);
            for (Iterator<Object> aIt = aEntities.iterator(), bIt = bEntities.iterator(); aIt.hasNext() && bIt.hasNext();) {
                Object aEntity = aIt.next();
                Object bEntity = bIt.next();
                for (GenuineVariableDescriptor<Solution_> variableDescriptor : entityDescriptor
                        .getGenuineVariableDescriptorList()) {
                    // TODO broken if the value is an entity, because then it's never the same
                    // But we don't want to depend on value/entity equals() => use surrogate entity IDs to compare
                    // https://issues.redhat.com/browse/PLANNER-170
                    if (variableDescriptor.getValue(aEntity) != variableDescriptor.getValue(bEntity)) {
                        mutationCount++;
                    }
                }
            }
            if (aEntities.size() != bEntities.size()) {
                mutationCount += Math.abs(aEntities.size() - bEntities.size())
                        * entityDescriptor.getGenuineVariableDescriptorList().size();
            }
        }
        return mutationCount;
    }

    @Override
    public String toString() {
        return "MutationCounter(" + solutionDescriptor + ")";
    }

}
