package org.optaplanner.constraint.streams.bavet.common;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.common.AbstractJoiner;
import org.optaplanner.constraint.streams.tri.DefaultTriJoiner;

public final class JoinerUtils {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    private JoinerUtils() {

    }

    public static <A, B> Function<A, Object[]> combineLeftMappings(DefaultBiJoiner<A, B> joiner) {
        int joinerCount = joiner.getJoinerCount();
        if (joinerCount == 0) {
            return (A a) -> EMPTY_OBJECT_ARRAY;
        } else if (joinerCount == 1) {
            Function<A, Object> mapping = joiner.getLeftMapping(0);
            return (A a) -> new Object[] { mapping.apply(a) };
        } else {
            return (A a) -> {
                Object[] result = new Object[joinerCount];
                for (int i = 0; i < joinerCount; i++) {
                    result[i] = joiner.getLeftMapping(i).apply(a);
                }
                return result;
            };
        }
    }

    public static <A, B, C> BiFunction<A, B, Object[]> combineLeftMappings(DefaultTriJoiner<A, B, C> joiner) {
        int joinerCount = joiner.getJoinerCount();
        if (joinerCount == 0) {
            return (A a, B b) -> EMPTY_OBJECT_ARRAY;
        } else if (joinerCount == 1) {
            BiFunction<A, B, Object> mapping = joiner.getLeftMapping(0);
            return (A a, B b) -> new Object[] { mapping.apply(a, b) };
        } else {
            return (A a, B b) -> {
                Object[] result = new Object[joinerCount];
                for (int i = 0; i < joinerCount; i++) {
                    result[i] = joiner.getLeftMapping(i).apply(a, b);
                }
                return result;
            };
        }
    }

    public static <Right_> Function<Right_, Object[]> combineRightMappings(AbstractJoiner<Right_> joiner) {
        int joinerCount = joiner.getJoinerCount();
        if (joinerCount == 0) {
            return (Right_ x) -> EMPTY_OBJECT_ARRAY;
        } else if (joinerCount == 1) {
            Function<Right_, Object> mapping = joiner.getRightMapping(0);
            return (Right_ x) -> new Object[] { mapping.apply(x) };
        } else {
            return (Right_ x) -> {
                Object[] result = new Object[joinerCount];
                for (int i = 0; i < joinerCount; i++) {
                    result[i] = joiner.getRightMapping(i).apply(x);
                }
                return result;
            };
        }
    }
}
