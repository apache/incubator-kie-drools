package org.optaplanner.core.impl.score.stream;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;

public enum JoinerType {
    EQUAL(Objects::equals),
    LESS_THAN((a, b) -> ((Comparable) a).compareTo(b) < 0),
    LESS_THAN_OR_EQUAL((a, b) -> ((Comparable) a).compareTo(b) <= 0),
    GREATER_THAN((a, b) -> ((Comparable) a).compareTo(b) > 0),
    GREATER_THAN_OR_EQUAL((a, b) -> ((Comparable) a).compareTo(b) >= 0),
    CONTAINING((a, b) -> ((Collection) a).contains(b)),
    INTERSECTING((a, b) -> intersecting((Collection) a, (Collection) b)),
    DISJOINT((a, b) -> disjoint((Collection) a, (Collection) b));

    private final BiPredicate<Object, Object> matcher;

    JoinerType(BiPredicate<Object, Object> matcher) {
        this.matcher = matcher;
    }

    public JoinerType flip() {
        switch (this) {
            case LESS_THAN:
                return GREATER_THAN;
            case LESS_THAN_OR_EQUAL:
                return GREATER_THAN_OR_EQUAL;
            case GREATER_THAN:
                return LESS_THAN;
            case GREATER_THAN_OR_EQUAL:
                return LESS_THAN_OR_EQUAL;
            default:
                throw new IllegalStateException("The joinerType (" + this + ") cannot be flipped.");
        }
    }

    public boolean matches(Object left, Object right) {
        return matcher.test(left, right);
    }

    private static boolean disjoint(Collection leftCollection, Collection rightCollection) {
        return leftCollection.stream().noneMatch(rightCollection::contains) &&
                rightCollection.stream().noneMatch(leftCollection::contains);
    }

    private static boolean intersecting(Collection leftCollection, Collection rightCollection) {
        return leftCollection.stream().anyMatch(rightCollection::contains) ||
                rightCollection.stream().anyMatch(leftCollection::contains);
    }

}
