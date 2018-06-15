/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.rule;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ActionCallMethodTest {

    @Test
    public void testBothNamesNull() {
        final ActionCallMethod action1 = new ActionCallMethod();
        final ActionCallMethod action2 = new ActionCallMethod();

        Assertions.assertThat(action1).isEqualTo(action2);
        Assertions.assertThat(action2).isEqualTo(action1);
        Assertions.assertThat(action1.hashCode()).isEqualTo(action2.hashCode());
        Assertions.assertThat(action2.hashCode()).isEqualTo(action1.hashCode());
    }

    @Test
    public void testFirstNameIsNull() {
        final ActionCallMethod action1 = new ActionCallMethod();
        final ActionCallMethod action2 = new ActionCallMethod("invokedMethod");

        Assertions.assertThat(action1).isNotEqualTo(action2);
        Assertions.assertThat(action2).isNotEqualTo(action1);
        Assertions.assertThat(action1.hashCode()).isNotEqualTo(action2.hashCode());
        Assertions.assertThat(action2.hashCode()).isNotEqualTo(action1.hashCode());
    }

    @Test
    public void testSecondNameIsNull() {
        final ActionCallMethod action1 = new ActionCallMethod("invokedMethod");
        final ActionCallMethod action2 = new ActionCallMethod();

        Assertions.assertThat(action1).isNotEqualTo(action2);
        Assertions.assertThat(action2).isNotEqualTo(action1);
        Assertions.assertThat(action1.hashCode()).isNotEqualTo(action2.hashCode());
        Assertions.assertThat(action2.hashCode()).isNotEqualTo(action1.hashCode());
    }

    @Test
    public void testNamesAreDifferent() {
        final ActionCallMethod action1 = new ActionCallMethod("invokedMethod1");
        final ActionCallMethod action2 = new ActionCallMethod("invokedMethod2");

        Assertions.assertThat(action1).isNotEqualTo(action2);
        Assertions.assertThat(action2).isNotEqualTo(action1);
        Assertions.assertThat(action1.hashCode()).isNotEqualTo(action2.hashCode());
        Assertions.assertThat(action2.hashCode()).isNotEqualTo(action1.hashCode());
    }

    @Test
    public void testBothNamesAreSame() {
        final ActionCallMethod action1 = new ActionCallMethod("invokedMethod");
        final ActionCallMethod action2 = new ActionCallMethod("invokedMethod");

        Assertions.assertThat(action1).isEqualTo(action2);
        Assertions.assertThat(action2).isEqualTo(action1);
        Assertions.assertThat(action1.hashCode()).isEqualTo(action2.hashCode());
        Assertions.assertThat(action2.hashCode()).isEqualTo(action1.hashCode());
    }
}
