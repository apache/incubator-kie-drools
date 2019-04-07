/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
    public T date(Date... date);

    /**
     * Specify the begin of a date range to be used as a criteria on the date field.
     * The date range includes the date specified.
     * @param rangeStart the start (early end) of the date range
     * @return The current query builder instance
     */
    public T dateRangeStart(Date rangeStart);

    /**
     * Specify the end of a date range to be used as a criteria on the date field.
     * The date range includes this date.
     * @param rangeEnd the end (later end) of the date range
     * @return The current query builder instance
     */
    public T dateRangeEnd(Date rangeEnd);;
}
