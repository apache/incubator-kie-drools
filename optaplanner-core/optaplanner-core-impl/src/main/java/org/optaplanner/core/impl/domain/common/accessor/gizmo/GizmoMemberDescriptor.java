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
     * The class that declared this member
     */
    Class<?> declaringClass;

    /**
     * The MethodDescriptor of the corresponding setter. Is empty if not present.
     */
    Optional<MethodDescriptor> setter;

    /**
     * If final checks should be ignored due to Quarkus transformations
     */
    boolean ignoreFinalChecks = false;

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
        ignoreFinalChecks = true;
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

    public ResultHandle readMemberValue(BytecodeCreator bytecodeCreator, ResultHandle thisObj) {
        if (memberDescriptor instanceof FieldDescriptor) {
            FieldDescriptor fd = (FieldDescriptor) memberDescriptor;
            return bytecodeCreator.readInstanceField(fd, thisObj);
        } else if (memberDescriptor instanceof MethodDescriptor) {
            MethodDescriptor md = (MethodDescriptor) memberDescriptor;
            return invokeMemberMethod(bytecodeCreator, md, thisObj);
        } else {
            throw new IllegalStateException(
                    "memberDescriptor (" + memberDescriptor + ") is neither a field descriptor or a method descriptor.");
        }
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
        if (memberDescriptor instanceof FieldDescriptor) {
            FieldDescriptor fd = (FieldDescriptor) memberDescriptor;
            try {
                Field field = declaringClass.getField(name);
                if (!ignoreFinalChecks && Modifier.isFinal(field.getModifiers())) {
                    throw new IllegalStateException(
                            "Field (" + name + ") of class (" + declaringClass + ") is final and cannot be modified.");
                } else {
                    bytecodeCreator.writeInstanceField(fd, thisObj, newValue);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException("Field (" + name + ") of class (" + declaringClass + ") does not exist.", e);
            }
            return true;
        } else if (memberDescriptor instanceof MethodDescriptor) {
            MethodDescriptor md = (MethodDescriptor) memberDescriptor;
            Optional<MethodDescriptor> maybeSetter = getSetter();
            if (!maybeSetter.isPresent()) {
                return false;
            } else {
                invokeMemberMethod(bytecodeCreator, maybeSetter.get(), thisObj, newValue);
                return true;
            }
        } else {
            throw new IllegalStateException(
                    "memberDescriptor (" + memberDescriptor + ") is neither a field descriptor or a method descriptor.");
        }
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
        String typeName;
        if (metadataDescriptor instanceof FieldDescriptor) {
            typeName = ((FieldDescriptor) metadataDescriptor).getType();
        } else {
            // Must be a method descriptor if it not a field descriptor
            typeName = ((MethodDescriptor) metadataDescriptor).getReturnType();
        }
        return org.objectweb.asm.Type.getType(typeName).getClassName();
    }

    public Type getType() {
        if (metadataDescriptor instanceof FieldDescriptor) {
            FieldDescriptor fieldDescriptor = (FieldDescriptor) metadataDescriptor;
            try {
                return declaringClass.getDeclaredField(fieldDescriptor.getName()).getGenericType();
            } catch (NoSuchFieldException e) {
                throw new IllegalStateException(
                        "Cannot find field (" + fieldDescriptor.getName() + ") on class (" + declaringClass + ").",
                        e);
            }
        } else {
            // Must be a method descriptor if it not a field descriptor
            MethodDescriptor methodDescriptor = (MethodDescriptor) metadataDescriptor;
            try {
                return declaringClass.getDeclaredMethod(methodDescriptor.getName()).getGenericReturnType();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "Cannot find method (" + methodDescriptor.getName() + ") on class (" + declaringClass + ").",
                        e);
            }
        }
    }

    @Override
    public String toString() {
        return memberDescriptor.toString();
    }
}
