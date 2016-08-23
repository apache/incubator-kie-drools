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

import org.kie.api.executor.ErrorInfo;
import org.kie.internal.query.ExtendedParametrizedQueryBuilder;
import org.kie.internal.runtime.manager.audit.query.AuditLogQueryBuilder.OrderBy;

public interface ErrorInfoQueryBuilder extends ExtendedParametrizedQueryBuilder<ErrorInfoQueryBuilder, ErrorInfo> {

    /**
     * Specify one or more message strings as criteria in the query.
     * @param message one or more strings
     * @return The current query builder instance
     */
    public ErrorInfoQueryBuilder message(String... message);

    /**
     * Specify one or more task event ids to use as a criteria
     * @param id one or more task event entity ids
     * @return The current query builder instance
     */
    public ErrorInfoQueryBuilder id(long... id);

    /**
     * Specify one or more dates to use as a criteria
     * for the error info
     * @param time one or more dates
     * @return The current query builder instance
     */
    public ErrorInfoQueryBuilder time(Date... time);

    /**
     * Specify an inclusive range of error info times to use as a criteria
     * </p>
     * If the lower or upper end of the range is given as null,
     * then an open-ended range using the non-null range end is used
     * as the criteria
     * @param timeMin the minimal (lower) date to use in the range
     * @param timeMax the max (upper) date to use in the range
     * @return The current query builder instance
     */
    public ErrorInfoQueryBuilder timeRange(Date timeMin, Date timeMax);

    /**
     * Specify one or more string regular expressions (only . and * accepted)
     * to use as a criteria for error info entities.
     * @param stackTraceRegex one or more dates
     * @return The current query builder instance
     */
    public ErrorInfoQueryBuilder stackTraceRegex(String... stackTraceRegex);

    /**
     * Specify which field to use when ordering the results, in ascending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public ErrorInfoQueryBuilder ascending( OrderBy field );

    /**
     * Specify which field to use when ordering the results, in descending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public ErrorInfoQueryBuilder descending( OrderBy field );

    public static enum OrderBy {
        // order by id
        id,
        // order by process id
        time,
    }

}
