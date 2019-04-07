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

import org.jbpm.casemgmt.api.model.CaseMilestone;


public class CaseMilestoneImpl implements CaseMilestone, Serializable {

    private static final long serialVersionUID = -4703425330438524348L;

    private String id;
    private String name;
    private String achievementCondition;
    private boolean mandatory;
    
    public CaseMilestoneImpl(String id, String name) {
        this.id = id;
        this.name = name;        
    }
    
    public CaseMilestoneImpl(String id, String name, String achievementCondition, boolean mandatory) {
        this.id = id;
        this.name = name;
        this.achievementCondition = achievementCondition;
        this.mandatory = mandatory;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAchievementCondition() {
        return achievementCondition;
    }

    @Override
    public boolean isMandatory() {
        return mandatory;
    }

    
    public void setId(String id) {
        this.id = id;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public void setAchievementCondition(String achievementCondition) {
        this.achievementCondition = achievementCondition;
    }

    
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @Override
    public String toString() {
        return "CaseMilestoneImpl [id=" + id + ", name=" + name + ", achievementCondition=" + achievementCondition + 
                ", mandatory=" + mandatory + "]";
    }

}
