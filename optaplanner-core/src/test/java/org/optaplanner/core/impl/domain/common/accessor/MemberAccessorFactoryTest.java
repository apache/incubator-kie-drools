/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.common.accessor;

import org.junit.Test;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.reflect.accessmodifier.TestdataVisibilityModifierSolution;
import org.optaplanner.core.impl.testdata.domain.reflect.field.TestdataFieldAnnotatedEntity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.*;

public class MemberAccessorFactoryTest {

    @Test
    public void fieldAnnotatedEntity() throws NoSuchFieldException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                TestdataFieldAnnotatedEntity.class.getDeclaredField("value"),
                MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER, PlanningVariable.class);
        assertInstanceOf(ReflectionFieldMemberAccessor.class, memberAccessor);
        assertEquals("value", memberAccessor.getName());
        assertEquals(TestdataValue.class, memberAccessor.getType());

        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataFieldAnnotatedEntity e1 = new TestdataFieldAnnotatedEntity("e1", v1);
        assertSame(v1, memberAccessor.executeGetter(e1));
        memberAccessor.executeSetter(e1, v2);
        assertSame(v2, e1.getValue());
    }


    @Test
    public void privateField() throws NoSuchFieldException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                TestdataVisibilityModifierSolution.class.getDeclaredField("privateField"),
                MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class);
        assertInstanceOf(ReflectionFieldMemberAccessor.class, memberAccessor);
        assertEquals("privateField", memberAccessor.getName());
        assertEquals(String.class, memberAccessor.getType());

        TestdataVisibilityModifierSolution s1 = new TestdataVisibilityModifierSolution("s1",
                "firstValue", "n/a",
                "n/a", "n/a", "n/a", "n/a");
        assertEquals("firstValue", memberAccessor.executeGetter(s1));
        memberAccessor.executeSetter(s1, "secondValue");
        assertEquals("secondValue", memberAccessor.executeGetter(s1));
    }

    @Test
    public void publicField() throws NoSuchFieldException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                TestdataVisibilityModifierSolution.class.getDeclaredField("publicField"),
                MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class);
        assertInstanceOf(ReflectionFieldMemberAccessor.class, memberAccessor);
        assertEquals("publicField", memberAccessor.getName());
        assertEquals(String.class, memberAccessor.getType());

        TestdataVisibilityModifierSolution s1 = new TestdataVisibilityModifierSolution("s1",
                "n/a", "firstValue",
                "n/a", "n/a", "n/a", "n/a");
        assertEquals("firstValue", memberAccessor.executeGetter(s1));
        memberAccessor.executeSetter(s1, "secondValue");
        assertEquals("secondValue", memberAccessor.executeGetter(s1));
    }

    @Test
    public void publicProperty() throws NoSuchMethodException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                TestdataVisibilityModifierSolution.class.getDeclaredMethod("getPublicProperty"),
                MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class);
        assertInstanceOf(LambdaBeanPropertyMemberAccessor.class, memberAccessor);
        assertEquals("publicProperty", memberAccessor.getName());
        assertEquals(String.class, memberAccessor.getType());

        TestdataVisibilityModifierSolution s1 = new TestdataVisibilityModifierSolution("s1",
                "n/a", "n/a",
                "n/a", "n/a", "n/a", "firstValue");
        assertEquals("firstValue", memberAccessor.executeGetter(s1));
        memberAccessor.executeSetter(s1, "secondValue");
        assertEquals("secondValue", memberAccessor.executeGetter(s1));
    }

}
