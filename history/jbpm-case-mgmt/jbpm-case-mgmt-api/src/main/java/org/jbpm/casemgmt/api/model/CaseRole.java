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

/**
 * Represents single role assigned to a given case. 
 * Optionally can specify cardinality to restrict number of assigned individuals to given role within given case.
 */
public interface CaseRole {

    /**
     * Returns name of the role
     * @return
     */
    String getName();
    
    /**
     * Returns maximum cardinality for this role - maximum assigned entities per case instance.
     * Needs to be set to value greater than 0 to be taken into account.
     * @return
     */
    Integer getCardinality();
}
