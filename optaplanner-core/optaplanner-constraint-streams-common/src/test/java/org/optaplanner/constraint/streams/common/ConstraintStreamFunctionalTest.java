package org.optaplanner.constraint.streams.common;

/**
 * Defines methods that every constraint stream test must have.
 * These methods are enforced because they test each method of the API for basic functionality.
 */
public interface ConstraintStreamFunctionalTest {

    void filter_entity();

    void filter_consecutive();

    // ************************************************************************
    // Join
    // ************************************************************************

    default void join_0() {
        // Quads don't have joins, so don't force it.
    }

    default void join_1Equal() {
        // Quads don't have joins, so don't force it.
    }

    default void join_2Equal() {
        // Quads don't have joins, so don't force it.
    }

    default void joinAfterGroupBy() {
        // Quads don't have joins, so don't force it.
    }

    // ************************************************************************
    // If (not) exists
    // ************************************************************************

    void ifExists_unknownClass();

    void ifExists_0Joiner0Filter();

    void ifExists_0Join1Filter();

    void ifExists_1Join0Filter();

    void ifExists_1Join1Filter();

    void ifExistsDoesNotIncludeNullVars();

    @Deprecated(forRemoval = true)
    void ifExistsIncludesNullVarsWithFrom();

    void ifNotExists_unknownClass();

    void ifNotExists_0Joiner0Filter();

    void ifNotExists_0Join1Filter();

    void ifNotExists_1Join0Filter();

    void ifNotExists_1Join1Filter();

    void ifNotExistsDoesNotIncludeNullVars();

    @Deprecated(forRemoval = true)
    void ifNotExistsIncludesNullVarsWithFrom();

    void ifExistsAfterGroupBy();

    // ************************************************************************
    // Group by
    // ************************************************************************

    void groupBy_0Mapping1Collector();

    void groupBy_0Mapping2Collector();

    void groupBy_0Mapping3Collector();

    void groupBy_0Mapping4Collector();

    void groupBy_1Mapping0Collector();

    void groupBy_1Mapping1Collector();

    void groupBy_1Mapping2Collector();

    void groupBy_1Mapping3Collector();

    void groupBy_2Mapping0Collector();

    void groupBy_2Mapping1Collector();

    void groupBy_2Mapping2Collector();

    void groupBy_3Mapping0Collector();

    void groupBy_3Mapping1Collector();

    void groupBy_4Mapping0Collector();

    // ************************************************************************
    // Map/flatten/distinct
    // ************************************************************************

    void distinct();

    void mapWithDuplicates();

    void mapWithoutDuplicates();

    void mapAndDistinctWithDuplicates();

    void mapAndDistinctWithoutDuplicates();

    void flattenLastWithDuplicates();

    void flattenLastWithoutDuplicates();

    void flattenLastAndDistinctWithDuplicates();

    void flattenLastAndDistinctWithoutDuplicates();

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    void penalize_Int();

    void penalize_Long();

    void penalize_BigDecimal();

    void penalize_negative();

    void reward_Int();

    void reward_Long();

    void reward_BigDecimal();

    void reward_negative();

}
