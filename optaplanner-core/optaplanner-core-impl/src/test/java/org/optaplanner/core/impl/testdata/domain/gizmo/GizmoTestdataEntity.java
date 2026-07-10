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

package org.optaplanner.core.impl.testdata.domain.gizmo;

import java.util.Collection;
import java.util.Map;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

@PlanningEntity
public class GizmoTestdataEntity {

    private String id;

    @PlanningVariable
    public TestdataValue value;

    public boolean isPinned;

    public Collection<Map<String, String>> genericField;

    public GizmoTestdataEntity(String id, TestdataValue value, boolean isPinned) {
        this.id = id;
        this.value = value;
        this.isPinned = isPinned;
    }

    @PlanningId
    public String getId() {
        return id;
    }

    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    @PlanningPin
    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public String readMethod() {
        return "Read Method";
    }

    public String methodWithParameters(String parameter) {
        return parameter;
    }

    public void getVoid() {
    }

    public void voidMethod() {
    }

    public String isAMethodThatHasABadName() {
        return "It should start with get not is.";
    }

    private String getBadMethod() {
        return "Creating a Member Descriptor for this method should throw as it is private.";
    }
}
