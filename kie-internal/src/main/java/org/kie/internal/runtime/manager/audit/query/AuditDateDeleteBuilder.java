package org.kie.internal.runtime.manager.audit.query;

import java.util.Date;

/**
 * This interface defines methods that are used by all of the Audit delete query builder implementations.
 * @param <T>
 *
 */
public interface AuditDateDeleteBuilder<T> extends AuditDeleteBuilder<T> {

    /**
     * Specify one or more dates as criteria in the query
     * @param date one or more dates
     * @return The current query builder instance
     */
    T date(Date... date);

    /**
     * Specify the begin of a date range to be used as a criteria on the date field.
     * The date range includes the date specified.
     * @param rangeStart the start (early end) of the date range
     * @return The current query builder instance
     */
    T dateRangeStart(Date rangeStart);

    /**
     * Specify the end of a date range to be used as a criteria on the date field.
     * The date range includes this date.
     * @param rangeEnd the end (later end) of the date range
     * @return The current query builder instance
     */
    T dateRangeEnd(Date rangeEnd);
}
