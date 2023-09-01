package org.drools.model;

import java.util.function.BiPredicate;

public interface ConstraintOperator {
    <T, V> BiPredicate<T, V> asPredicate();
}
