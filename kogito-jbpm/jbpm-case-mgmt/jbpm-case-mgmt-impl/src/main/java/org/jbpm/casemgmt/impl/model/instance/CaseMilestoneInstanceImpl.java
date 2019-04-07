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

package org.jbpm.casemgmt.impl.model.instance;

import java.io.Serializable;
import java.util.Date;

import org.jbpm.casemgmt.api.model.instance.CaseMilestoneInstance;
import org.jbpm.casemgmt.api.model.instance.MilestoneStatus;


public class CaseMilestoneInstanceImpl implements CaseMilestoneInstance, Serializable {

    private static final long serialVersionUID = 2586521906976986355L;

    private String id;
    private String name;
    private boolean achieved;
    private Date achievedAt;
    private MilestoneStatus status = MilestoneStatus.Available;
    
    public CaseMilestoneInstanceImpl(String id, String name, boolean achieved, Date achievedAt) {
        this.id = id;
        this.name = name;
        this.achieved = achieved;
        if (achieved) {
            this.achievedAt = achievedAt;
            this.status = MilestoneStatus.Completed;
        }
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
    public boolean isAchieved() {
        return achieved;
    }

    @Override
    public Date getAchievedAt() {
        return achievedAt;
    }

    @Override
    public MilestoneStatus getStatus() {
        return status;
    }
    
    @Override
    public String toString() {
        return "CaseMilestoneInstanceImpl [id=" + id + ", name=" + name + ", achieved=" + achieved + 
                ", achievedAt=" + achievedAt  + ", status=" + status + "]";
    }

}
