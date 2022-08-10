package org.optaplanner.core.impl.domain.common.accessor;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.impl.domain.common.accessor.gizmo.GizmoMemberAccessorFactory;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.reflect.accessmodifier.TestdataVisibilityModifierSolution;
import org.optaplanner.core.impl.testdata.domain.reflect.field.TestdataFieldAnnotatedEntity;

class MemberAccessorFactoryTest {

    @Test
    void fieldAnnotatedEntity() throws NoSuchFieldException {
        MemberAccessor memberAccessor =
                MemberAccessorFactory.buildMemberAccessor(TestdataFieldAnnotatedEntity.class.getDeclaredField("value"),
                        MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER, PlanningVariable.class,
                        DomainAccessType.REFLECTION);
        assertThat(memberAccessor)
                .isInstanceOf(ReflectionFieldMemberAccessor.class);
        assertThat(memberAccessor.getName()).isEqualTo("value");
        assertThat(memberAccessor.getType()).isEqualTo(TestdataValue.class);

        TestdataValue v1 = new TestdataValue("v1");
        TestdataValue v2 = new TestdataValue("v2");
        TestdataFieldAnnotatedEntity e1 = new TestdataFieldAnnotatedEntity("e1", v1);
        assertThat(memberAccessor.executeGetter(e1)).isSameAs(v1);
        memberAccessor.executeSetter(e1, v2);
        assertThat(e1.getValue()).isSameAs(v2);
    }

    @Test
    void privateField() throws NoSuchFieldException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                TestdataVisibilityModifierSolution.class.getDeclaredField("privateField"),
                MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class,
                DomainAccessType.REFLECTION);
        assertThat(memberAccessor)
                .isInstanceOf(ReflectionFieldMemberAccessor.class);
        assertThat(memberAccessor.getName()).isEqualTo("privateField");
        assertThat(memberAccessor.getType()).isEqualTo(String.class);

        TestdataVisibilityModifierSolution s1 = new TestdataVisibilityModifierSolution("s1",
                "firstValue", "n/a",
                "n/a", "n/a", "n/a", "n/a");
        assertThat(memberAccessor.executeGetter(s1)).isEqualTo("firstValue");
        memberAccessor.executeSetter(s1, "secondValue");
        assertThat(memberAccessor.executeGetter(s1)).isEqualTo("secondValue");
    }

    @Test
    void publicField() throws NoSuchFieldException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                TestdataVisibilityModifierSolution.class.getDeclaredField("publicField"),
                MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class,
                DomainAccessType.REFLECTION);
        assertThat(memberAccessor)
                .isInstanceOf(ReflectionFieldMemberAccessor.class);
        assertThat(memberAccessor.getName()).isEqualTo("publicField");
        assertThat(memberAccessor.getType()).isEqualTo(String.class);

        TestdataVisibilityModifierSolution s1 = new TestdataVisibilityModifierSolution("s1",
                "n/a", "firstValue",
                "n/a", "n/a", "n/a", "n/a");
        assertThat(memberAccessor.executeGetter(s1)).isEqualTo("firstValue");
        memberAccessor.executeSetter(s1, "secondValue");
        assertThat(memberAccessor.executeGetter(s1)).isEqualTo("secondValue");
    }

    @Test
    void publicProperty() throws NoSuchMethodException {
        MemberAccessor memberAccessor = MemberAccessorFactory.buildMemberAccessor(
                TestdataVisibilityModifierSolution.class.getDeclaredMethod("getPublicProperty"),
                MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class,
                DomainAccessType.REFLECTION);
        assertThat(memberAccessor)
                .isInstanceOf(LambdaBeanPropertyMemberAccessor.class);
        assertThat(memberAccessor.getName()).isEqualTo("publicProperty");
        assertThat(memberAccessor.getType()).isEqualTo(String.class);

        TestdataVisibilityModifierSolution s1 = new TestdataVisibilityModifierSolution("s1",
                "n/a", "n/a",
                "n/a", "n/a", "n/a", "firstValue");
        assertThat(memberAccessor.executeGetter(s1)).isEqualTo("firstValue");
        memberAccessor.executeSetter(s1, "secondValue");
        assertThat(memberAccessor.executeGetter(s1)).isEqualTo("secondValue");
    }

    @Test
    void shouldUseGeneratedMemberAccessorIfExists() throws NoSuchMethodException {
        Member member = TestdataVisibilityModifierSolution.class.getDeclaredMethod("getPublicProperty");
        MemberAccessor mockMemberAccessor = Mockito.mock(MemberAccessor.class);

        Map<String, MemberAccessor> preexistingMemberAccessors = new HashMap<>();
        preexistingMemberAccessors.put(GizmoMemberAccessorFactory.getGeneratedClassName(member), mockMemberAccessor);
        MemberAccessorFactory memberAccessorFactory = new MemberAccessorFactory(preexistingMemberAccessors);
        MemberAccessor memberAccessor = memberAccessorFactory.buildAndCacheMemberAccessor(member,
                MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER, ProblemFactProperty.class,
                DomainAccessType.REFLECTION);
        assertThat(memberAccessor)
                .isSameAs(mockMemberAccessor);
    }
}
