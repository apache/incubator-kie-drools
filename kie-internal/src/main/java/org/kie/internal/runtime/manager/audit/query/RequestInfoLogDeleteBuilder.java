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

import org.kie.api.executor.STATUS;
import org.kie.internal.query.ParametrizedUpdate;
import org.kie.internal.task.api.AuditTask;

public interface RequestInfoLogDeleteBuilder extends AuditDeleteBuilder<RequestInfoLogDeleteBuilder>{

    /**
     * Specify one or more dates as criteria in the query
     * @param date one or more dates
     * @return The current query builder instance
     */
    public RequestInfoLogDeleteBuilder date(Date... date);
   
    /**
     * Specify the begin of a date range to be used as a criteria on the date field.
     * The date range includes the date specified.
     * @param date the start (early end) of the date range
     * @return The current query builder instance
     */
    public RequestInfoLogDeleteBuilder dateRangeStart(Date rangeStart);
    
    /**
     * Specify the end of a date range to be used as a criteria on the date field.
     * The date range includes this date. 
     * @param date the end (later end) of the date range
     * @return The current query builder instance
     */
    public RequestInfoLogDeleteBuilder dateRangeEnd(Date rangeStart);

    /**
     * Specify one or more deployment ids to use as a criteria.
     * @param deploymentId one or more string deployment ids
     * @return The current query builder instance
     */
    public RequestInfoLogDeleteBuilder deploymentId(String... deploymentId);
    
    /**
     * Specify one more statuses (in the form of an int) as criteria.
     * @param status one or more int statuses
     * @return The current instance of this query builder
     */
    public RequestInfoLogDeleteBuilder status(STATUS... status);
    
    /**
     * Create the {@link ParametrizedUpdate} instance that can be used
     * to execute update/delete of {@link AuditTask} instances.
     * </p>
     * Further modifications to the {@link ProcessInstanceLogDeleteBuilder} instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedUpdate} 
     * produced by this method.
     * @return The results of the update/delete
     */
    public ParametrizedUpdate build();

}
