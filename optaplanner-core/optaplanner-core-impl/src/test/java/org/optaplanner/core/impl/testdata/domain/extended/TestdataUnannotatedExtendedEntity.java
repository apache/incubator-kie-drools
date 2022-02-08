/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.testdata.domain.extended;

import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

public class TestdataUnannotatedExtendedEntity extends TestdataEntity {

    private Object extraObject;

    public TestdataUnannotatedExtendedEntity() {
    }

    public TestdataUnannotatedExtendedEntity(String code) {
        super(code);
    }

    public TestdataUnannotatedExtendedEntity(String code, TestdataValue value) {
        super(code, value);
    }

    public TestdataUnannotatedExtendedEntity(String code, TestdataValue value, Object extraObject) {
        super(code, value);
        this.extraObject = extraObject;
    }

    public Object getExtraObject() {
        return extraObject;
    }

    public void setExtraObject(Object extraObject) {
        this.extraObject = extraObject;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
