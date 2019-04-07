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

package org.jbpm.casemgmt.impl.model;

import java.io.Serializable;

import org.jbpm.casemgmt.api.model.CaseRole;


public class CaseRoleImpl implements CaseRole, Serializable {

    private static final long serialVersionUID = -2640423715855846985L;

    private String name;    
    private Integer cardinality;
    
    public CaseRoleImpl() {
    }
    
    public CaseRoleImpl(String name) {
        this(name, -1);
    }
    
    public CaseRoleImpl(String name, Integer cardinality) {
        this.name = name;
        this.cardinality = cardinality;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getCardinality() {
        return cardinality;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public void setCardinality(Integer cardinality) {
        this.cardinality = cardinality;
    }

    @Override
    public String toString() {
        return "CaseRoleImpl [name=" + name + ", cardinality=" + cardinality + "]";
    }

}
