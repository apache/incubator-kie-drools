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

package org.jbpm.casemgmt.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Simple view on individual CaseFile data item. 
 * It provides basic information that is most useful on data lists to avoid fetching
 * actual case file data as they might come from various sources.
 */
public interface CaseFileItem extends Serializable {

    /**
     * Returns unique case identifier that this item belongs to
     */
    String getCaseId();
    
    /**
     * Returns name of the data item
     */
    String getName();
    
    /**
     * Returns "toString" version of the latest value of the data item
     */
    String getValue();
    
    /**
     * Returns type of the data item - FQCN
     */
    String getType();
    
    /**
     * Returns user id who made the last modification of this data item
     */
    String getLastModifiedBy();
    
    /**
     * Returns last modification timestamp for this data item
     */
    Date getLastModified();
}
