/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.domain;

import java.util.List;

import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("MrService")
public class MrService extends AbstractPersistable {

    private List<MrService> toDependencyServiceList;
    private List<MrService> fromDependencyServiceList;

    private int locationSpread;

    public MrService() {
    }

    public MrService(long id) {
        super(id);
    }

    public List<MrService> getToDependencyServiceList() {
        return toDependencyServiceList;
    }

    public void setToDependencyServiceList(List<MrService> toDependencyServiceList) {
        this.toDependencyServiceList = toDependencyServiceList;
    }

    public List<MrService> getFromDependencyServiceList() {
        return fromDependencyServiceList;
    }

    public void setFromDependencyServiceList(List<MrService> fromDependencyServiceList) {
        this.fromDependencyServiceList = fromDependencyServiceList;
    }

    public int getLocationSpread() {
        return locationSpread;
    }

    public void setLocationSpread(int locationSpread) {
        this.locationSpread = locationSpread;
    }

}
