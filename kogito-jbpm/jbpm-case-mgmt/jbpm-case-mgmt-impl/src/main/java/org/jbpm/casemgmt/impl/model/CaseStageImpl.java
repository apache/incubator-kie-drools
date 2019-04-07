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
import java.util.Collection;
import java.util.Collections;

import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.CaseStage;


public class CaseStageImpl implements CaseStage, Serializable {

    private static final long serialVersionUID = 8392150112858214665L;

    private String id;
    private String name; 
    private Collection<AdHocFragment> adHocFragments;
    
    public CaseStageImpl() {
        
    }
    
    public CaseStageImpl(String id, String name, Collection<AdHocFragment> adHocFragments) {
        this.id = id;
        this.name = name;
        this.adHocFragments = adHocFragments == null ? Collections.emptyList() : adHocFragments;
    }
    
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Collection<AdHocFragment> getAdHocFragments() {
        return adHocFragments;
    }
    
    @Override
    public String toString() {
        return "CaseStageImpl [name=" + name + ", adHocFragments=" + adHocFragments + "]";
    }

}
