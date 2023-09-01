package org.drools.ruleunits.api;

/**
 * A {@link DataSource} of immutable data.
 * By default, this Stream doesn't retain any data and just forwards the facts appended to it to the {@link DataProcessor}s
 * that are registered at the time of insertion. In particular this means that if a fact is inserted into the DataStream declared in a
 * {@link RuleUnitData} before any {@link RuleUnitInstance} has been created from it, this fact's insertion will be simply get lost.
 * It can be optionally buffered and retain a fixed amount of the latest appended facts.
 * @param <T> The type of objects managed by this DataSource.
 */
public interface DataStream<T> extends DataSource<T> {

    /**
     * Append an object to this stream of data.
     */
    void append(T value);
}
