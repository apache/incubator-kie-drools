/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.lookup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEnum;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectIntegerId;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectMultipleIds;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectNoId;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectPrimitiveIntId;

public class LookUpStrategyIdOrFailTest {

    private LookUpManager lookUpManager;

    @BeforeEach
    public void setUpLookUpManager() {
        lookUpManager = new LookUpManager(new LookUpStrategyResolver(LookUpStrategyType.PLANNING_ID_OR_FAIL_FAST));
        lookUpManager.resetWorkingObjects(Collections.emptyList());
    }

    @Test
    public void addRemoveWithIntegerId() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(0);
        lookUpManager.addWorkingObject(object);
        lookUpManager.removeWorkingObject(object);
        // The removed object cannot be looked up
        assertThat(lookUpManager.lookUpWorkingObjectOrReturnNull(object)).isNull();
    }

    @Test
    public void addRemoveWithPrimitiveIntId() {
        TestdataObjectPrimitiveIntId object = new TestdataObjectPrimitiveIntId(0);
        lookUpManager.addWorkingObject(object);
        lookUpManager.removeWorkingObject(object);
        // The removed object cannot be looked up
        assertThat(lookUpManager.lookUpWorkingObjectOrReturnNull(object)).isNull();
    }

    @Test
    public void addRemoveEnum() {
        TestdataObjectEnum object = TestdataObjectEnum.THIRD_VALUE;
        lookUpManager.addWorkingObject(object);
        lookUpManager.removeWorkingObject(object);
    }

    @Test
    public void addWithNullId() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(null);
        assertThatIllegalArgumentException().isThrownBy(() -> lookUpManager.addWorkingObject(object));
    }

    @Test
    public void removeWithNullId() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(null);
        assertThatIllegalArgumentException().isThrownBy(() -> lookUpManager.removeWorkingObject(object));
    }

    @Test
    public void addWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        assertThatIllegalArgumentException().isThrownBy(() -> lookUpManager.addWorkingObject(object));
    }

    @Test
    public void removeWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        assertThatIllegalArgumentException().isThrownBy(() -> lookUpManager.removeWorkingObject(object));
    }

    @Test
    public void addSameIdTwice() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(2);
        lookUpManager.addWorkingObject(object);
        assertThatIllegalStateException()
                .isThrownBy(() -> lookUpManager.addWorkingObject(new TestdataObjectIntegerId(2)))
                .withMessageContaining(" have the same planningId ")
                .withMessageContaining(object.toString());
    }

    @Test
    public void removeWithoutAdding() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(0);
        assertThatIllegalStateException()
                .isThrownBy(() -> lookUpManager.removeWorkingObject(object))
                .withMessageContaining("differ");
    }

    @Test
    public void lookUpWithId() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(1);
        lookUpManager.addWorkingObject(object);
        assertThat(lookUpManager.lookUpWorkingObject(new TestdataObjectIntegerId(1))).isSameAs(object);
    }

    @Test
    public void lookUpWithoutId() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.lookUpWorkingObject(object))
                .withMessageContaining("does not have a " + PlanningId.class.getSimpleName());
    }

    @Test
    public void lookUpWithoutAdding() {
        TestdataObjectIntegerId object = new TestdataObjectIntegerId(0);
        assertThat(lookUpManager.lookUpWorkingObjectOrReturnNull(object)).isNull();
    }

    @Test
    public void addWithTwoIds() {
        TestdataObjectMultipleIds object = new TestdataObjectMultipleIds();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.addWorkingObject(object))
                .withMessageContaining("3 members")
                .withMessageContaining(PlanningId.class.getSimpleName());
    }

    @Test
    public void removeWithTwoIds() {
        TestdataObjectMultipleIds object = new TestdataObjectMultipleIds();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.removeWorkingObject(object))
                .withMessageContaining("3 members")
                .withMessageContaining(PlanningId.class.getSimpleName());
    }
}
