/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.testdata.domain.clone.lookup;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class TestdataObjectMultipleIds {

    @PlanningId
    private final Integer id;
    @PlanningId
    private final String name;
    @PlanningId
    private final Boolean bool;

    public TestdataObjectMultipleIds() {
        this.id = 0;
        this.name = "";
        this.bool = false;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getBool() {
        return bool;
    }

}
