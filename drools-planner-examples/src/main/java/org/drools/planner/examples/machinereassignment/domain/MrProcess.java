/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.planner.examples.machinereassignment.domain;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("MrProcess")
public class MrProcess extends AbstractPersistable {

    private MrService service;
    private int moveCost;

    private Map<MrResource, MrProcessRequirement> processRequirementMap;

    public MrService getService() {
        return service;
    }

    public void setService(MrService service) {
        this.service = service;
    }

    public int getMoveCost() {
        return moveCost;
    }

    public void setMoveCost(int moveCost) {
        this.moveCost = moveCost;
    }

    public Map<MrResource, MrProcessRequirement> getProcessRequirementMap() {
        return processRequirementMap;
    }

    public void setProcessRequirementMap(Map<MrResource, MrProcessRequirement> processRequirementMap) {
        this.processRequirementMap = processRequirementMap;
    }

    public MrProcessRequirement getProcessRequirement(MrResource resource) {
        return processRequirementMap.get(resource);
    }

}
