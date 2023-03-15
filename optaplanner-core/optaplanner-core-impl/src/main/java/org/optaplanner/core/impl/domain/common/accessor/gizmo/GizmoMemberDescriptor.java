package org.optaplanner.core.impl.domain.common.accessor.gizmo;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Consumer;

import org.optaplanner.core.impl.domain.common.ReflectionHelper;

import io.quarkus.gizmo.BytecodeCreator;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;

/**
 * Describe and provide simplified/unified access for {@link Member}.
 */
public final class GizmoMemberDescriptor {

    /**
     * The name of a member.
     * For a field, it is the field name.
     * For a method,
     * if it is a getter, the method name without "get"/"is" and the first letter lowercase;
     * otherwise, the method name.
     */
    private final String name;

    private final GizmoMemberHandler memberHandler;

    /**
     * Should only be used for metadata (i.e. Generic Type and Annotated Element).
     */
    private final GizmoMemberHandler metadataHandler;

    /**
     * The MethodDescriptor of the corresponding setter. Empty if not present.
     */
    private final MethodDescriptor setter;

    public GizmoMemberDescriptor(Member member) {
        Class<?> declaringClass = member.getDeclaringClass();
        if (!Modifier.isPublic(member.getModifiers())) {
            throw new IllegalStateException("Member (" + member.getName() + ") of class (" +
                    member.getDeclaringClass().getName() + ") is not public and domainAccessType is GIZMO.\n" +
                    ((member instanceof Field) ? "Maybe put the annotations onto the public getter of the field.\n" : "") +
                    "Maybe use domainAccessType REFLECTION instead of GIZMO.");
        }
        if (member instanceof Field) {
            FieldDescriptor fieldDescriptor = FieldDescriptor.of((Field) member);
            this.name = member.getName();
            this.memberHandler = GizmoMemberHandler.of(declaringClass, name, fieldDescriptor, false);
            this.setter = null;
        } else if (member instanceof Method) {
            MethodDescriptor methodDescriptor = MethodDescriptor.ofMethod((Method) member);
            this.name = ReflectionHelper.isGetterMethod((Method) member) ? ReflectionHelper.getGetterPropertyName(member)
                    : member.getName();
            this.memberHandler = GizmoMemberHandler.of(declaringClass, methodDescriptor);
            this.setter = lookupSetter(methodDescriptor, declaringClass, name).orElse(null);
        } else {
            throw new IllegalArgumentException(member + " is not a Method or a Field.");
        }
        this.metadataHandler = this.memberHandler;
    }

    public GizmoMemberDescriptor(String name, FieldDescriptor fieldDescriptor, Class<?> declaringClass) {
        this.name = name;
        this.memberHandler = GizmoMemberHandler.of(declaringClass, name, fieldDescriptor, true);
        this.metadataHandler = this.memberHandler;
        this.setter = null;
    }

    public GizmoMemberDescriptor(String name, MethodDescriptor memberDescriptor, MethodDescriptor metadataDescriptor,
            Class<?> declaringClass, MethodDescriptor setterDescriptor) {
        this.name = name;
        this.memberHandler = GizmoMemberHandler.of(declaringClass, memberDescriptor);
        this.metadataHandler = memberDescriptor == metadataDescriptor ? this.memberHandler
                : GizmoMemberHandler.of(declaringClass, metadataDescriptor);
        this.setter = setterDescriptor;
    }

    public GizmoMemberDescriptor(String name, MethodDescriptor memberDescriptor, Class<?> declaringClass,
            MethodDescriptor setterDescriptor) {
        this.name = name;
        this.memberHandler = GizmoMemberHandler.of(declaringClass, memberDescriptor);
        this.metadataHandler = this.memberHandler;
        this.setter = setterDescriptor;
    }

    public GizmoMemberDescriptor(String name, MethodDescriptor memberDescriptor, FieldDescriptor metadataDescriptor,
            Class<?> declaringClass, MethodDescriptor setterDescriptor) {
        this.name = name;
        this.memberHandler = GizmoMemberHandler.of(declaringClass, memberDescriptor);
        this.metadataHandler = GizmoMemberHandler.of(declaringClass, name, metadataDescriptor, true);
        this.setter = setterDescriptor;
    }

    /**
     * If the member accessor is a field, pass the member's field descriptor to the
     * provided consumer. Otherwise, do nothing. Returns self for chaining.
     *
     * @param fieldDescriptorConsumer What to do if the member a field.
     * @return this
     */
    public GizmoMemberDescriptor whenIsField(Consumer<FieldDescriptor> fieldDescriptorConsumer) {
        memberHandler.whenIsField(fieldDescriptorConsumer);
        return this;
    }

    /**
     * If the member accessor is a method, pass the member's method descriptor to the
     * provided consumer. Otherwise, do nothing. Returns self for chaining.
     *
     * @param methodDescriptorConsumer What to do if the member a method.
     * @return this
     */
    public GizmoMemberDescriptor whenIsMethod(Consumer<MethodDescriptor> methodDescriptorConsumer) {
        memberHandler.whenIsMethod(methodDescriptorConsumer);
        return this;
    }

    public ResultHandle readMemberValue(BytecodeCreator bytecodeCreator, ResultHandle thisObj) {
        return memberHandler.readMemberValue(bytecodeCreator, thisObj);
    }

    /**
     * Write the bytecode for writing to this member. If there is no setter,
     * it write the bytecode for throwing the exception. Return true if
     * it was able to write the member value.
     *
     * @param bytecodeCreator the bytecode creator to use
     * @param thisObj the bean to write the new value to
     * @param newValue to new value of the member
     * @return True if it was able to write the member value, false otherwise
     */
    public boolean writeMemberValue(BytecodeCreator bytecodeCreator, ResultHandle thisObj, ResultHandle newValue) {
        return memberHandler.writeMemberValue(setter, bytecodeCreator, thisObj, newValue);
    }

    /**
     * If the member metadata is on a field, pass the member's field descriptor to the
     * provided consumer. Otherwise, do nothing. Returns self for chaining.
     *
     * @param fieldDescriptorConsumer What to do if the member a field.
     * @return this
     */
    public GizmoMemberDescriptor whenMetadataIsOnField(Consumer<FieldDescriptor> fieldDescriptorConsumer) {
        metadataHandler.whenIsField(fieldDescriptorConsumer);
        return this;
    }

    /**
     * If the member metadata is on a method, pass the member's method descriptor to the
     * provided consumer. Otherwise, do nothing. Returns self for chaining.
     *
     * @param methodDescriptorConsumer What to do if the member a method.
     * @return this
     */
    public GizmoMemberDescriptor whenMetadataIsOnMethod(Consumer<MethodDescriptor> methodDescriptorConsumer) {
        metadataHandler.whenIsMethod(methodDescriptorConsumer);
        return this;
    }

    /**
     * Returns the declaring class name of the member in descriptor format.
     * For instance, the declaring class name of Object.toString() is "java/lang/Object".
     *
     * @return Returns the declaring class name of the member in descriptor format
     */
    public String getDeclaringClassName() {
        return memberHandler.getDeclaringClassName();
    }

    public Optional<MethodDescriptor> getSetter() {
        return Optional.ofNullable(setter);
    }

    private static Optional<MethodDescriptor> lookupSetter(Object memberDescriptor, Class<?> declaringClass, String name) {
        if (memberDescriptor instanceof MethodDescriptor) {
            return Optional.ofNullable(ReflectionHelper.getSetterMethod(declaringClass, name))
                    .map(MethodDescriptor::ofMethod);
        } else {
            return Optional.empty();
        }
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the member type (for fields) / return type (for methods) name.
     * The name does not include generic information.
     */
    public String getTypeName() {
        String typeName = metadataHandler.getTypeName();
        return org.objectweb.asm.Type.getType(typeName).getClassName();
    }

    public Type getType() {
        return metadataHandler.getType();
    }

    @Override
    public String toString() {
        return memberHandler.toString();
    }
}
