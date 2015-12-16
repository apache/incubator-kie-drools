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

public interface ProcessInstanceLogDeleteBuilder extends AuditDeleteBuilder<ProcessInstanceLogDeleteBuilder> {

    /**
     * Specify one more statuses (in the form of an int) as criteria.
     * @param status one or more int statuses
     * @return The current instance of this query builder
     */
    public ProcessInstanceLogDeleteBuilder status(int... status);
   
    /**
     * Specify one or more (process instance) outcomes as a criteria.
     * @param outcome one or more string outcomes
     * @return The current instance of this query builder
     */
    public ProcessInstanceLogDeleteBuilder outcome(String... outcome);
    
    
    /**
     * Specify one or more identiies (who started the process instance) as a criteria
     * @param identity one or more string identities
     * @return The current instance of this query builder
     */
    public ProcessInstanceLogDeleteBuilder identity(String... identity);
    
    /**
     * Specify one or more process versions as a criteria
     * @param version one or more string process versions
     * @return The current instance of this query builder
     */
    public ProcessInstanceLogDeleteBuilder processVersion(String... version);
   
    /**
     * Specify one or more process names as a criteria
     * @param processName one or more string process names
     * @return The current instance of this query builder
     */
    public ProcessInstanceLogDeleteBuilder processName(String... processName);
    
    /**
     * Specify one or more (process instance) start dates as a criteria
     * @param date one or more {@link Date} start dates
     * @return The current instance of this query builder
     */
    public ProcessInstanceLogDeleteBuilder startDate(Date... date);
    
    /**
     * Specify the begin of a date range to be used as a criteria on the start date field.
     * The date range includes the date specified.
     * @param rangeStart the start (early end) of the date range
     * @return The current query builder instance
     */
    public ProcessInstanceLogDeleteBuilder startDateRangeStart(Date rangeStart);
    
    /**
     * Specify the end of a date range to be used as a criteria on the start date field.
     * The date range includes this date. 
     * @param rangeEnd the end (later end) of the date range
     * @return The current query builder instance
     */
    public ProcessInstanceLogDeleteBuilder startDateRangeEnd(Date rangeEnd);
    
    /**
     * Specify one or more (process instance) end dates as a criteria
     * @param date one or more {@link Date} end dates
     * @return The current instance of this query builder
     */
    public ProcessInstanceLogDeleteBuilder endDate(Date... date);
    
    /**
     * Specify the begin of a date range to be used as a criteria on the end date field.
     * The date range includes this date. 
     * @param date the begin (later end) of the date range
     * @return The current query builder instance
     */
    public ProcessInstanceLogDeleteBuilder endDateRangeStart(Date rangeStart);
    
    /**
     * Specify the end of a date range to be used as a criteria on the end date field.
     * The date range includes this date. 
     * @param date the end (later end) of the date range
     * @return The current query builder instance
     */
    public ProcessInstanceLogDeleteBuilder endDateRangeEnd(Date rangeEnd);
    
    /**
     * Specify externalId to be used as criteria on the externalId field.
     * @param externalId identifier that defines custom id
     * @return The current query builder instance
     */
    public ProcessInstanceLogDeleteBuilder externalId(String... externalId);


}
