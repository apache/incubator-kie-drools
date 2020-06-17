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
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEquals;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEqualsNoHashCode;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectEqualsSubclass;
import org.optaplanner.core.impl.testdata.domain.clone.lookup.TestdataObjectNoId;

public class LookUpStrategyEqualityTest {

    private LookUpManager lookUpManager;

    @BeforeEach
    public void setUpLookUpManager() {
        lookUpManager = new LookUpManager(new LookUpStrategyResolver(LookUpStrategyType.EQUALITY));
        lookUpManager.resetWorkingObjects(Collections.emptyList());
    }

    @Test
    public void addRemoveWithEquals() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        lookUpManager.addWorkingObject(object);
        lookUpManager.removeWorkingObject(object);
        // The removed object cannot be looked up
        assertThat(lookUpManager.lookUpWorkingObjectOrReturnNull(object)).isNull();
    }

    @Test
    public void addWithEqualsInSuperclass() {
        TestdataObjectEqualsSubclass object = new TestdataObjectEqualsSubclass(0);
        lookUpManager.addWorkingObject(object);
        assertThat(lookUpManager.lookUpWorkingObject(object)).isSameAs(object);
    }

    @Test
    public void addWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.addWorkingObject(object))
                .withMessageContaining("override the equals() method")
                .withMessageContaining(TestdataObjectNoId.class.getSimpleName());
    }

    @Test
    public void addWithoutHashCode() {
        TestdataObjectEqualsNoHashCode object = new TestdataObjectEqualsNoHashCode(0);
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.addWorkingObject(object))
                .withMessageContaining("overrides the hashCode() method")
                .withMessageContaining(TestdataObjectEqualsNoHashCode.class.getSimpleName());
    }

    @Test
    public void removeWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.removeWorkingObject(object))
                .withMessageContaining("override the equals() method")
                .withMessageContaining(TestdataObjectNoId.class.getSimpleName());
    }

    @Test
    public void addEqualObjects() {
        TestdataObjectEquals object = new TestdataObjectEquals(2);
        lookUpManager.addWorkingObject(object);
        assertThatIllegalStateException()
                .isThrownBy(() -> lookUpManager.addWorkingObject(new TestdataObjectEquals(2)))
                .withMessageContaining(object.toString());
    }

    @Test
    public void removeWithoutAdding() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        assertThatIllegalStateException()
                .isThrownBy(() -> lookUpManager.removeWorkingObject(object))
                .withMessageContaining("differ");
    }

    @Test
    public void lookUpWithEquals() {
        TestdataObjectEquals object = new TestdataObjectEquals(1);
        lookUpManager.addWorkingObject(object);
        assertThat(lookUpManager.lookUpWorkingObject(new TestdataObjectEquals(1))).isSameAs(object);
    }

    @Test
    public void lookUpWithoutEquals() {
        TestdataObjectNoId object = new TestdataObjectNoId();
        assertThatIllegalArgumentException()
                .isThrownBy(() -> lookUpManager.lookUpWorkingObject(object))
                .withMessageContaining("override the equals() method");
    }

    @Test
    public void lookUpWithoutAdding() {
        TestdataObjectEquals object = new TestdataObjectEquals(0);
        assertThat(lookUpManager.lookUpWorkingObjectOrReturnNull(object)).isNull();
    }
}
