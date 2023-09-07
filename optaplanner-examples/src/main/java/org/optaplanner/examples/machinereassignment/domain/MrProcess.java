/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.machinereassignment.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.common.persistence.jackson.JacksonUniqueIdGenerator;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonIdentityInfo(generator = JacksonUniqueIdGenerator.class)
public class MrProcess extends AbstractPersistable {

    private MrService service;
    private int moveCost;

    // Order is equal to resourceList so resource.getIndex() can be used
    private List<MrProcessRequirement> processRequirementList;

    public MrProcess() {
    }

    public MrProcess(int moveCost) {
        this.moveCost = moveCost;
    }

    public MrProcess(MrService service) {
        this.service = service;
    }

    public MrProcess(long id, MrService service, int moveCost) {
        super(id);
        this.service = service;
        this.moveCost = moveCost;
    }

    public MrService getService() {
        return service;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public List<MrProcessRequirement> getProcessRequirementList() {
        return processRequirementList;
    }

    public void setProcessRequirementList(List<MrProcessRequirement> processRequirementList) {
        this.processRequirementList = processRequirementList;
    }

    public MrProcessRequirement getProcessRequirement(MrResource resource) {
        return processRequirementList.get(resource.getIndex());
    }

    public long getUsage(MrResource resource) {
        return resource.getIndex() >= processRequirementList.size() ? 0L
                : processRequirementList.get(resource.getIndex()).getUsage();
    }

    @JsonIgnore
    public int getUsageMultiplicand() {
        int multiplicand = 1;
        for (MrProcessRequirement processRequirement : processRequirementList) {
            multiplicand *= processRequirement.getUsage();
        }
        return multiplicand;
    }

}
