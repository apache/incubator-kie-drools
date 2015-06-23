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

import org.kie.internal.query.ParametrizedUpdate;

public interface NodeInstanceLogDeleteBuilder extends AuditDeleteBuilder<NodeInstanceLogDeleteBuilder> {

    /**
     * Specify one or more work item ids associated with a node to use as a criteria.
     * @param nodeInstanceId one or more long work item ids
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder workItemId(long... workItemId);
    
    /**
     * Specify one or more node instance ids to use as a criteria.
     * @param nodeInstanceId one or more string node instance ids
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder nodeInstanceId(String... nodeInstanceId);
    
    /**
     * Specify one or more node ids to use as a criteria.
     * @param nodeId one or more string node ids
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder nodeId(String... nodeId);
    
    /**
     * Specify one or more node names to use as a criteria.
     * @param nodeInstanceId one or more string node names
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder nodeName(String... name);
    
    /**
     * Specify one or more dates as criteria in the query.
     * @param date one or more dates
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder date(Date... date);
   
    /**
     * Specify the begin of a date range to be used as a criteria on the date field.
     * The date range includes the date specified.
     * @param date the start (early end) of the date range
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder dateRangeStart(Date rangeStart);
    
    /**
     * Specify the end of a date range to be used as a criteria on the date field.
     * The date range includes this date. 
     * @param date the end (later end) of the date range
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder dateRangeEnd(Date rangeStart);
    
    /**
     * Specify externalId to be used as criteria on the externalId field.
     * @param externalId identifier that defines custom id
     * @return The current query builder instance
     */
    public NodeInstanceLogDeleteBuilder externalId(String... externalId);
    
    /**
     * Create the {@link ParametrizedUpdate} instance that can be used
     * to execute update/delete of {@link List<NodeInstanceLog>} instances.
     * </p>
     * Further modifications to the {@link NodeInstanceLogDeleteBuilder} instance
     * will <em>not</em> affect the query criteria used in the {@link ParametrizedUpdate} 
     * produced by this method.
     * @return The results of the update/delete
     */
    public ParametrizedUpdate build();
}
