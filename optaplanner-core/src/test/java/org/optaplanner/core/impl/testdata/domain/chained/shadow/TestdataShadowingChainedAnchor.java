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

package org.optaplanner.core.impl.testdata.domain.chained.shadow;

import org.optaplanner.core.impl.testdata.domain.TestdataObject;

public class TestdataShadowingChainedAnchor extends TestdataObject implements TestdataShadowingChainedObject {

    // Shadow variables
    private TestdataShadowingChainedEntity nextEntity;

    public TestdataShadowingChainedAnchor() {
    }

    public TestdataShadowingChainedAnchor(String code) {
        super(code);
    }

    @Override
    public TestdataShadowingChainedEntity getNextEntity() {
        return nextEntity;
    }

    @Override
    public void setNextEntity(TestdataShadowingChainedEntity nextEntity) {
        this.nextEntity = nextEntity;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
