package org.optaplanner.core.api.score.constraint;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;

final class ConstraintJustificationComparator implements Comparator<Object> {

    private static final Comparator<Object> CLASS_NAME_COMPARATOR =
            Comparator.comparing(o -> o.getClass().getCanonicalName());
    private final Map<Class<?>, MemberAccessor> accessorCache = new HashMap<>(0);

    @Override
    public int compare(Object o1, Object o2) {
        // First make sure different classes are never equal.
        final int compareClassedByName = CLASS_NAME_COMPARATOR.compare(o1, o2);
        if (compareClassedByName != 0) {
            return compareClassedByName;
        }
        // Same class, see if it's comparable.
        if (o1 instanceof Comparable) {
            return ((Comparable) o1).compareTo(o2);
        }
        MemberAccessor memberAccessor = accessorCache.computeIfAbsent(o1.getClass(),
                ConfigUtils::findPlanningIdMemberAccessor);
        if (memberAccessor == null) {
            // Same class, not comparable, does not have planning ID => don't sort. Not consistent with equals().
            return 0;
        }
        // Compare planning IDs; Comparable guaranteed by MemberAccessor.
        Comparable id1 = (Comparable) memberAccessor.executeGetter(o1);
        Comparable id2 = (Comparable) memberAccessor.executeGetter(o2);
        return id1.compareTo(id2);
    }
}
