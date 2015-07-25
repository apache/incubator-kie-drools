/*
 * Copyright 2015 JBoss Inc
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

import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.internal.query.ExtendedParametrizedQueryBuilder;

public interface RequestInfoQueryBuilder extends ExtendedParametrizedQueryBuilder<RequestInfoQueryBuilder, RequestInfo> {

    /**
     * Specify one or more request command names to use as a criteria
     * @param commandName one or more strings
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder commandName(String... commandName);

    /**
     * Specify one or more deployment ids to use as a criteria
     * @param deploymentId one or more strings
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder deploymentId(String... deploymentId);

    /**
     * Specify one or more number of executions to use as a criteria
     * @param executions one or more ints
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder executions(int... executions);

    /**
     * Specify one or more task event ids to use as a criteria 
     * @param id one or more task event entity ids
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder id(long... id);

    /**
     * Specify one or more request keys to use as a criteria
     * @param key one or more strings
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder key(String... key);

    /**
     * Specify one or more request owner ids to use as a criteria
     * @param owner one or more strings
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder owner(String... owner);

    /**
     * Specify one or more message strings as criteria in the query.
     * @param message one or more strings
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder message(String... message);

    /**
     * Specify one or more number of retries to use as a criteria
     * @param retries one or more ints
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder retries(int... retries);

    /**
     * Specify one or more request statuses to use as a criteria
     * @param status one or more {@link STATUS} values
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder status(STATUS... status);

    /**
     * Specify one or more dates to use as a criteria 
     * for the error info
     * @param time one or more dates
     * @return The current query builder instance
     */
    public RequestInfoQueryBuilder time(Date... time);
  
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
    public RequestInfoQueryBuilder timeRange(Date timeMin, Date timeMax);
    
    /**
     * Specify which field to use when ordering the results, in ascending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order 
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public RequestInfoQueryBuilder ascending( OrderBy field );
    
    /**
     * Specify which field to use when ordering the results, in descending order.
     * </p>
     * If this method is not used, the results will be ordered in ascending order 
     * by the id field.
     * @param field the field by which the query results should be ordered
     * @return The current instance of this query builder
     */
    public RequestInfoQueryBuilder descending( OrderBy field );
    
    public static enum OrderBy { 
        // order by id
        id, 
        // order by process id
        time,
        // order by deployment id
        deploymentId,
        // order by request executions
        executions,
        // order by request retries
        retries,
        // order by request retries
        status,
    }
}
