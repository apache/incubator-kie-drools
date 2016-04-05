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

package org.optaplanner.core.impl.testdata.domain.extended.thirdparty;

import java.util.List;

import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

/**
 * This POJO does not depend on OptaPlanner:
 * it has no OptaPlanner imports (annotations, score, ...) except for test imports.
 */
public class TestdataThirdPartySolutionPojo extends TestdataObject {

    private List<TestdataValue> valueList;
    private List<TestdataThirdPartyEntityPojo> entityList;

    public TestdataThirdPartySolutionPojo() {
    }

    public TestdataThirdPartySolutionPojo(String code) {
        super(code);
    }

    public List<TestdataValue> getValueList() {
        return valueList;
    }

    public void setValueList(List<TestdataValue> valueList) {
        this.valueList = valueList;
    }

    public List<TestdataThirdPartyEntityPojo> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<TestdataThirdPartyEntityPojo> entityList) {
        this.entityList = entityList;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
