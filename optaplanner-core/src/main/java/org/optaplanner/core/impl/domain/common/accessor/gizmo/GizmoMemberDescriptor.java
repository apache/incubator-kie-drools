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
package org.optaplanner.core.impl.domain.common.accessor.gizmo;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.function.Consumer;

import org.optaplanner.core.impl.domain.common.ReflectionHelper;

import io.quarkus.gizmo.BytecodeCreator;
import io.quarkus.gizmo.DescriptorUtils;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;

/**
 * Describe and provide simplified/unified access for a Member
 */
public class GizmoMemberDescriptor {

    /**
     * The name of a member. For a field, it the field name.
     * For a method, if it is a getter, the method name without "get"/"is"
     * and the first letter lowercase; otherwise, the method name.
     */
    String name;

    /**
     * If the member is a field, the FieldDescriptor of the member accessor
     * If the member is a method, the MethodDescriptor of the member accessor
     */
    Object memberDescriptor;

    /**
     * If the member is a field, the FieldDescriptor of the member
     * If the member is a method, the MethodDescriptor of the member
     *
     * Should only be used for metadata (i.e. Generic Type and Annotated Element).
     */
    Object metadataDescriptor;

    /**
     * If the member is a normal member, the class that declared it
     * If the member is from Jandex, the Jandex ClassInfo of the class that declared it
     */
    Class<?> declaringClass;

    /**
     * The MethodDescriptor of the corresponding setter. Is empty if not present.
     */
    Optional<MethodDescriptor> setter;

    public GizmoMemberDescriptor(Member member) {
        declaringClass = member.getDeclaringClass();
        if (!Modifier.isPublic(member.getModifiers())) {
            throw new IllegalStateException("Member (" + member.getName() + ") of class (" +
                    member.getDeclaringClass().getName() + ") is not public and domainAccessType is GIZMO.\n" +
                    ((member instanceof Field) ? "Maybe put the annotations onto the public getter of the field.\n" : "") +
                    "Maybe use domainAccessType REFLECTION instead of GIZMO.");
        }
        if (member instanceof Field) {
            memberDescriptor = FieldDescriptor.of((Field) member);
            name = member.getName();
            setter = lookupSetter(memberDescriptor, declaringClass, name);
        } else if (member instanceof Method) {
            memberDescriptor = MethodDescriptor.ofMethod((Method) member);
            if (ReflectionHelper.isGetterMethod((Method) member)) {
                name = ReflectionHelper.getGetterPropertyName(member);
            } else {
                name = member.getName();
            }
            setter = lookupSetter(memberDescriptor, declaringClass, name);
        } else {
            throw new IllegalArgumentException(member + " is not a Method or a Field.");
        }

        metadataDescriptor = memberDescriptor;
    }

    // For Quarkus
    // (Cannot move to Quarkus module; get runtime
    //  exception since objects created here use classes
    //  from another ClassLoader).
    public GizmoMemberDescriptor(String name, Object memberDescriptor, Object metadataDescriptor, Class<?> declaringClass) {
        this(name, memberDescriptor, metadataDescriptor, declaringClass,
                lookupSetter(memberDescriptor, declaringClass, name).orElse(null));
    }

    public GizmoMemberDescriptor(String name, Object memberDescriptor, Object metadataDescriptor, Class<?> declaringClass,
            MethodDescriptor setterDescriptor) {
        this.name = name;
        this.memberDescriptor = memberDescriptor;
        this.metadataDescriptor = metadataDescriptor;
        this.declaringClass = declaringClass;
        setter = Optional.ofNullable(setterDescriptor);
    }

    /**
     * If the member accessor is a field, pass the member's field descriptor to the
     * provided consumer. Otherwise, do nothing. Returns self for chaining.
     *
     * @param fieldDescriptorConsumer What to do if the member a field.
     * @return this
     */
    public GizmoMemberDescriptor whenIsField(Consumer<FieldDescriptor> fieldDescriptorConsumer) {
        if (memberDescriptor instanceof FieldDescriptor) {
            fieldDescriptorConsumer.accept((FieldDescriptor) memberDescriptor);
        }
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
        if (memberDescriptor instanceof MethodDescriptor) {
            methodDescriptorConsumer.accept((MethodDescriptor) memberDescriptor);
        }
        return this;
    }

    /**
     * If the member metadata is on a field, pass the member's field descriptor to the
     * provided consumer. Otherwise, do nothing. Returns self for chaining.
     *
     * @param fieldDescriptorConsumer What to do if the member a field.
     * @return this
     */
    public GizmoMemberDescriptor whenMetadataIsOnField(Consumer<FieldDescriptor> fieldDescriptorConsumer) {
        if (metadataDescriptor instanceof FieldDescriptor) {
            fieldDescriptorConsumer.accept((FieldDescriptor) metadataDescriptor);
        }
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
        if (metadataDescriptor instanceof MethodDescriptor) {
            methodDescriptorConsumer.accept((MethodDescriptor) metadataDescriptor);
        }
        return this;
    }

    /**
     * Returns the declaring class name of the member in descriptor format.
     * For instance, the declaring class name of Object.toString() is "java/lang/Object".
     *
     * @return Returns the declaring class name of the member in descriptor format
     */
    public String getDeclaringClassName() {
        if (memberDescriptor instanceof FieldDescriptor) {
            return ((FieldDescriptor) memberDescriptor).getDeclaringClass();
        } else if (memberDescriptor instanceof MethodDescriptor) {
            return ((MethodDescriptor) memberDescriptor).getDeclaringClass();
        } else {
            throw new IllegalStateException("memberDescriptor not a fieldDescriptor or a methodDescriptor");
        }
    }

    /**
     * Returns true iff the getter is from an interface.
     *
     * @return true iff the getter is from an interface
     */
    public boolean isInterfaceMethod() {
        if (memberDescriptor instanceof MethodDescriptor) {
            return declaringClass.isInterface();
        } else {
            return false;
        }
    }

    public ResultHandle invokeMemberMethod(BytecodeCreator creator, MethodDescriptor method, ResultHandle bean,
            ResultHandle... parameters) {
        if (isInterfaceMethod()) {
            return creator.invokeInterfaceMethod(method, bean, parameters);
        } else {
            return creator.invokeVirtualMethod(method, bean, parameters);
        }
    }

    public Optional<MethodDescriptor> getSetter() {
        return setter;
    }

    private static Optional<MethodDescriptor> lookupSetter(Object memberDescriptor,
            Class<?> declaringClass,
            String name) {
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
     * The name does not include generic infomation.
     */
    public String getTypeName() {
        String[] holder = new String[1];
        whenMetadataIsOnField(fd -> {
            holder[0] = fd.getType();
        });

        whenMetadataIsOnMethod(md -> {
            holder[0] = md.getReturnType();
        });

        if (DescriptorUtils.isPrimitive(holder[0])) {
            switch (holder[0]) {
                case "I":
                    return int.class.getName();
                case "B":
                    return byte.class.getName();
                case "C":
                    return char.class.getName();
                case "J":
                    return long.class.getName();
                case "F":
                    return float.class.getName();
                case "S":
                    return short.class.getName();
                case "D":
                    return double.class.getName();
                case "Z":
                    return boolean.class.getName();
                case "V":
                    return void.class.getName();
                default:
                    throw new IllegalStateException("Unknown primitive type (" + holder[0] + ").");
            }
        }

        String typeName = DescriptorUtils.getTypeStringFromDescriptorFormat(holder[0]).replace('/', '.');
        int genericStart = typeName.indexOf('<');
        boolean isGeneric = genericStart != -1;
        if (isGeneric) {
            int genericEnd = typeName.lastIndexOf('>');
            return typeName.substring(0, genericStart) + typeName.substring(genericEnd + 1);
        } else {
            return typeName;
        }
    }

    @Override
    public String toString() {
        return memberDescriptor.toString();
    }
}
