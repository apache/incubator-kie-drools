/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.impl.testdata.domain.TestdataSolution;

public class TestdataUnannotatedExtendedSolution extends TestdataSolution {

    private Object extraObject;

    public TestdataUnannotatedExtendedSolution() {
    }

    public TestdataUnannotatedExtendedSolution(String code) {
        super(code);
    }

    public TestdataUnannotatedExtendedSolution(String code, Object extraObject) {
        super(code);
        this.extraObject = extraObject;
    }

    public TestdataUnannotatedExtendedSolution(TestdataSolution other) {
        super(other.getCode());
        setValueList(other.getValueList());
        setEntityList(other.getEntityList());
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
