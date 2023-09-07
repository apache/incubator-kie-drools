/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.domain.common.accessor.gizmo;

import java.lang.reflect.Type;
import java.util.function.Consumer;

import io.quarkus.gizmo.BytecodeCreator;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;

final class GizmoMethodHandler implements GizmoMemberHandler {

    private final Class<?> declaringClass;
    private final MethodDescriptor methodDescriptor;

    GizmoMethodHandler(Class<?> declaringClass, MethodDescriptor methodDescriptor) {
        this.declaringClass = declaringClass;
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public void whenIsField(Consumer<FieldDescriptor> fieldDescriptorConsumer) {
        // Do nothing,
    }

    @Override
    public void whenIsMethod(Consumer<MethodDescriptor> methodDescriptorConsumer) {
        methodDescriptorConsumer.accept(methodDescriptor);
    }

    @Override
    public ResultHandle readMemberValue(BytecodeCreator bytecodeCreator, ResultHandle thisObj) {
        return invokeMemberMethod(declaringClass, bytecodeCreator, methodDescriptor, thisObj);
    }

    @Override
    public boolean writeMemberValue(MethodDescriptor setter, BytecodeCreator bytecodeCreator, ResultHandle thisObj,
            ResultHandle newValue) {
        if (setter == null) {
            return false;
        } else {
            invokeMemberMethod(declaringClass, bytecodeCreator, setter, thisObj, newValue);
            return true;
        }
    }

    private ResultHandle invokeMemberMethod(Class<?> declaringClass, BytecodeCreator creator, MethodDescriptor method,
            ResultHandle bean, ResultHandle... parameters) {
        if (declaringClass.isInterface()) {
            return creator.invokeInterfaceMethod(method, bean, parameters);
        } else {
            return creator.invokeVirtualMethod(method, bean, parameters);
        }
    }

    @Override
    public String getDeclaringClassName() {
        return methodDescriptor.getDeclaringClass();
    }

    @Override
    public String getTypeName() {
        return methodDescriptor.getReturnType();
    }

    @Override
    public Type getType() {
        try {
            return declaringClass.getDeclaredMethod(methodDescriptor.getName()).getGenericReturnType();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "Cannot find method (" + methodDescriptor.getName() + ") on class (" + declaringClass + ").",
                    e);
        }
    }

    @Override
    public String toString() {
        return methodDescriptor.toString();
    }

}
