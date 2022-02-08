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

import org.optaplanner.core.impl.testdata.domain.TestdataObject;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;

/**
 * This POJO does not depend on OptaPlanner:
 * it has no OptaPlanner imports (annotations, score, ...) except for test imports.
 */
public class TestdataThirdPartyEntityPojo extends TestdataObject {

    private TestdataValue value;

    public TestdataThirdPartyEntityPojo() {
    }

    public TestdataThirdPartyEntityPojo(String code) {
        super(code);
    }

    public TestdataThirdPartyEntityPojo(String code, TestdataValue value) {
        this(code);
        this.value = value;
    }

    public TestdataValue getValue() {
        return value;
    }

    public void setValue(TestdataValue value) {
        this.value = value;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
