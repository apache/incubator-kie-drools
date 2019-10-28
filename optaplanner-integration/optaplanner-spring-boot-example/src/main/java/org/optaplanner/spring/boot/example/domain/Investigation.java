/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.spring.boot.example.domain;

import java.time.Duration;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Investigation {

    @PlanningId
    private long id;
    private Duration estimatedDuration;

    @PlanningVariable(valueRangeProviderRefs = "detectiveRange")
    private Detective detective;

    private Investigation() {
    }

    public Investigation(long id, Duration estimatedDuration) {
        this.id = id;
        this.estimatedDuration = estimatedDuration;
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public long getId() {
        return id;
    }

    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    public Detective getDetective() {
        return detective;
    }

}
