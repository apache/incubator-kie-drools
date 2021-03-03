package org.optaplanner.core.impl.domain.common.accessor.gizmo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.Collection;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.gizmo.GizmoTestdataEntity;
import org.optaplanner.core.impl.testdata.domain.reflect.accessmodifier.TestdataVisibilityModifierSolution;
import org.optaplanner.core.impl.testdata.domain.reflect.field.TestdataFieldAnnotatedEntity;

import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodDescriptor;

public class GizmoMemberDescriptorTest {

    @Test
    public void testThatCreatingDescriptorForPrivateMembersFail() {
        assertThatCode(() -> new GizmoMemberDescriptor(TestdataFieldAnnotatedEntity.class.getDeclaredField("value")))
                .hasMessage("Member (" + "value" + ") of class (" +
                        TestdataFieldAnnotatedEntity.class.getName() + ") is not public and domainAccessType is GIZMO.\n" +
                        "Maybe put the annotations onto the public getter of the field.\n" +
                        "Maybe use domainAccessType REFLECTION instead of GIZMO.");

        assertThatCode(() -> new GizmoMemberDescriptor(
                TestdataVisibilityModifierSolution.class.getDeclaredMethod("getPrivateProperty")))
                        .hasMessage("Member (" + "getPrivateProperty" + ") of class (" +
                                TestdataVisibilityModifierSolution.class.getName()
                                + ") is not public and domainAccessType is GIZMO.\n" +
                                "Maybe use domainAccessType REFLECTION instead of GIZMO.");
    }

    @Test
    public void testThatWhenIsMethodExecuteConsumerIffMemberIsMethod() throws Exception {
        GizmoMemberDescriptor methodMemberDescriptor = new GizmoMemberDescriptor(GizmoTestdataEntity.class.getMethod("getId"));
        Consumer<MethodDescriptor> methodDescriptorConsumer = Mockito.mock(Consumer.class);
        methodMemberDescriptor.whenIsMethod(methodDescriptorConsumer);
        Mockito.verify(methodDescriptorConsumer).accept(Mockito.any());

        Mockito.reset(methodDescriptorConsumer);

        GizmoMemberDescriptor fieldMemberDescriptor = new GizmoMemberDescriptor(GizmoTestdataEntity.class.getField("value"));
        fieldMemberDescriptor.whenIsMethod(methodDescriptorConsumer);
        Mockito.verifyNoInteractions(methodDescriptorConsumer);
    }

    @Test
    public void testThatWhenIsFieldExecuteConsumerIffMemberIsField() throws Exception {
        GizmoMemberDescriptor fieldMemberDescriptor = new GizmoMemberDescriptor(GizmoTestdataEntity.class.getField("value"));
        Consumer<FieldDescriptor> methodDescriptorConsumer = Mockito.mock(Consumer.class);
        fieldMemberDescriptor.whenIsField(methodDescriptorConsumer);
        Mockito.verify(methodDescriptorConsumer).accept(Mockito.any());

        Mockito.reset(methodDescriptorConsumer);

        GizmoMemberDescriptor methodMemberDescriptor = new GizmoMemberDescriptor(GizmoTestdataEntity.class.getMethod("getId"));
        methodMemberDescriptor.whenIsField(methodDescriptorConsumer);
        Mockito.verifyNoInteractions(methodDescriptorConsumer);
    }

    @Test
    public void testThatGetDeclaringClassNameIsCorrect() throws Exception {
        GizmoMemberDescriptor fieldMemberDescriptor = new GizmoMemberDescriptor(GizmoTestdataEntity.class.getField("value"));
        assertThat(fieldMemberDescriptor.getDeclaringClassName())
                .isEqualTo(GizmoTestdataEntity.class.getName().replace('.', '/'));

        GizmoMemberDescriptor methodMemberDescriptor = new GizmoMemberDescriptor(GizmoTestdataEntity.class.getMethod("getId"));
        assertThat(methodMemberDescriptor.getDeclaringClassName())
                .isEqualTo(GizmoTestdataEntity.class.getName().replace('.', '/'));
    }

    @Test
    public void testMemberDescriptorNameIsCorrect() throws Exception {
        GizmoMemberDescriptor fieldMemberDescriptor = new GizmoMemberDescriptor(GizmoTestdataEntity.class.getField("value"));
        assertThat(fieldMemberDescriptor.getName()).isEqualTo("value");

        GizmoMemberDescriptor getterMethodMemberDescriptor =
                new GizmoMemberDescriptor(GizmoTestdataEntity.class.getMethod("getId"));
        assertThat(getterMethodMemberDescriptor.getName()).isEqualTo("id");

        GizmoMemberDescriptor readMethodMemberDescriptor =
                new GizmoMemberDescriptor(GizmoTestdataEntity.class.getMethod("readMethod"));
        assertThat(readMethodMemberDescriptor.getName()).isEqualTo("readMethod");
    }

    @Test
    public void testMemberDescriptorSetterIsCorrect() throws Exception {
        GizmoMemberDescriptor fieldMemberDescriptor = new GizmoMemberDescriptor(GizmoTestdataEntity.class.getField("value"));
        assertThat(fieldMemberDescriptor.getSetter()).isEmpty();

        GizmoMemberDescriptor getterMethodMemberDescriptorWithoutSetter =
                new GizmoMemberDescriptor(GizmoTestdataEntity.class.getMethod("getId"));
        assertThat(getterMethodMemberDescriptorWithoutSetter.getSetter()).isEmpty();

        GizmoMemberDescriptor getterMethodMemberDescriptorWithSetter =
                new GizmoMemberDescriptor(GizmoTestdataEntity.class.getMethod("getValue"));
        assertThat(getterMethodMemberDescriptorWithSetter.getSetter())
                .hasValue(MethodDescriptor.ofMethod(GizmoTestdataEntity.class.getMethod("setValue", TestdataValue.class)));

        GizmoMemberDescriptor readMethodMemberDescriptor =
                new GizmoMemberDescriptor(GizmoTestdataEntity.class.getMethod("readMethod"));
        assertThat(readMethodMemberDescriptor.getSetter()).isEmpty();
    }

    @Test
    public void testMemberDescriptorTypeNameIsCorrect() throws Exception {
        GizmoMemberDescriptor fieldMemberDescriptor = new GizmoMemberDescriptor(GizmoTestdataEntity.class.getField("value"));
        assertThat(fieldMemberDescriptor.getTypeName()).isEqualTo(TestdataValue.class.getName());

        GizmoMemberDescriptor getterMethodMemberDescriptor =
                new GizmoMemberDescriptor(GizmoTestdataEntity.class.getMethod("getId"));
        assertThat(getterMethodMemberDescriptor.getTypeName()).isEqualTo(String.class.getName());

        GizmoMemberDescriptor genericFieldMemberDescriptor =
                new GizmoMemberDescriptor(GizmoTestdataEntity.class.getField("genericField"));
        assertThat(genericFieldMemberDescriptor.getTypeName()).isEqualTo(Collection.class.getName());
    }

}
