package org.optaplanner.core.impl.domain.common.accessor.gizmo;

import java.lang.reflect.Type;
import java.util.function.Consumer;

import io.quarkus.gizmo.BytecodeCreator;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;

final class GizmoFieldHandler implements GizmoMemberHandler {

    private final Class<?> declaringClass;
    private final FieldDescriptor fieldDescriptor;
    private final boolean canBeWritten;

    GizmoFieldHandler(Class<?> declaringClass, FieldDescriptor fieldDescriptor, boolean canBeWritten) {
        this.declaringClass = declaringClass;
        this.fieldDescriptor = fieldDescriptor;
        this.canBeWritten = canBeWritten;
    }

    @Override
    public void whenIsField(Consumer<FieldDescriptor> fieldDescriptorConsumer) {
        fieldDescriptorConsumer.accept(fieldDescriptor);
    }

    @Override
    public void whenIsMethod(Consumer<MethodDescriptor> methodDescriptorConsumer) {
        // Do nothing.
    }

    @Override
    public ResultHandle readMemberValue(BytecodeCreator bytecodeCreator, ResultHandle thisObj) {
        return bytecodeCreator.readInstanceField(fieldDescriptor, thisObj);
    }

    @Override
    public boolean writeMemberValue(MethodDescriptor setter, BytecodeCreator bytecodeCreator, ResultHandle thisObj,
            ResultHandle newValue) {
        if (canBeWritten) {
            bytecodeCreator.writeInstanceField(fieldDescriptor, thisObj, newValue);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getDeclaringClassName() {
        return fieldDescriptor.getDeclaringClass();
    }

    @Override
    public String getTypeName() {
        return fieldDescriptor.getType();
    }

    @Override
    public Type getType() {
        try {
            return declaringClass.getDeclaredField(fieldDescriptor.getName()).getGenericType();
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(
                    "Cannot find field (" + fieldDescriptor.getName() + ") on class (" + declaringClass + ").",
                    e);
        }
    }

    @Override
    public String toString() {
        return fieldDescriptor.toString();
    }

}
