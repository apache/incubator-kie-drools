/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.api.model.instance;

import java.util.Date;
import java.util.Map;

import org.kie.api.runtime.process.CaseData;

/**
 * Represent contextual data of a given Case
 *
 */
public interface CaseFileInstance extends CaseData {
    
    /**
     * Returns case id this case file is associated with.
     * @return
     */
    String getCaseId();
    
    /**
     * Returns start date of the associated case
     * @return
     */
    Date getCaseStartDate();
    
    /**
     * Returns end date of the associated case
     * @return
     */
    Date getCaseEndDate();    
    
    /**
     * Returns reopen date of the associated case
     * @return
     */
    Date getCaseReopenDate();   
    
    /**
     * Returns filtered available case data for given case.
     * @return
     */
    Map<String, Object> getData(CaseFileDataFilter filter);
    
    /**
     * Adds all elements of data collection to existing case file 
     * (replacing already existing data that matches with input)
     * @param data
     */
    void addAll(Map<String, Object> data);

    
    /**
     * Remove permanently all data from existing case file that matches given filter
     * @param filter
     */
    void remove(CaseFileDataFilter filter);
    
    /**
     * Removes permanently all data, which is essentially a clear of the entire case file
     */
    void removeAll();
    
}
