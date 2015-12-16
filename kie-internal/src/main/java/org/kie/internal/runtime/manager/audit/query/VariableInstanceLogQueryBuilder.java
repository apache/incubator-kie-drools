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

import org.kie.api.runtime.manager.audit.VariableInstanceLog;

public interface VariableInstanceLogQueryBuilder extends AuditLogQueryBuilder<VariableInstanceLogQueryBuilder, VariableInstanceLog> {

    /**
     * Specify one or more dates as criteria in the query.
     * @param date one or more dates
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder date(Date... date);
   
    /**
     * Specify the begin of a date range to be used as a criteria on the date field.
     * The date range includes the date specified.
     * @param rangeStart the start (early end) of the date range
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder dateRangeStart(Date rangeStart);
    
    /**
     * Specify the end of a date range to be used as a criteria on the date field.
     * The date range includes this date. 
     * @param rangeEnd the end (later end) of the date range
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder dateRangeEnd(Date rangeEnd);
    
    /**
     * Specify one or more variable instance ids to use as a criteria.
     * @param variableInstanceId one or more string variable instance ids
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder variableInstanceId(String... variableInstanceId);
   
    /**
     * Specify one or more variable ids to use as a criteria.
     * @param variableId one or more string variable ids
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder variableId(String... variableId);
  
    /**
     * Specify one or more variable values to use as a criteria.
     * @param value one or more string values
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder value(String... value);
   
    /**
     * Specify one or more old (previous) variable values to use as a criteria.
     * @param oldVvalue one or more string old values
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder oldValue(String... oldVvalue);

    /**
     * Specify the value that variable instance logs should have
     * @param variableId the String variable id
     * @param value the String value of the variable
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder variableValue(String variableId, String value);

    /**
     * Specify one or more external ids to use as a criteria. In some cases,
     * the external id is the deployment unit id or runtime manager id.
     * @param externalId one or more string external ids
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder externalId(String... externalId);
    
    /**
     * Only retrieve the most recent ("last") variable instance logs per variable
     * </p>
     * When using this, please make sure that this criteria intersects other criteria. 
     * </p>
     * Otherwise, this criteria will have no effect at all. 
     * @return The current query builder instance
     */
    public VariableInstanceLogQueryBuilder last();
    
}
