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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.function.Consumer;

import io.quarkus.gizmo.BytecodeCreator;
import io.quarkus.gizmo.FieldDescriptor;
import io.quarkus.gizmo.MethodDescriptor;
import io.quarkus.gizmo.ResultHandle;

interface GizmoMemberHandler {

    /**
     * Creates handler for a {@link Field}.
     *
     * @param declaringClass never null, class that declares the {@link Field} in question
     * @param name never null, name of the field
     * @param fieldDescriptor never null, descriptor of the {@link Field} in question
     * @param ignoreFinalChecks true if Quarkus will make the field non-final for us
     * @return never null
     */
    static GizmoMemberHandler of(Class<?> declaringClass, String name, FieldDescriptor fieldDescriptor,
            boolean ignoreFinalChecks) {
        try {
            Field field = declaringClass.getField(name);
            return new GizmoFieldHandler(declaringClass, fieldDescriptor,
                    ignoreFinalChecks || !Modifier.isFinal(field.getModifiers()));
        } catch (NoSuchFieldException e) { // The field is only used for its metadata and never actually called.
            return new GizmoFieldHandler(declaringClass, fieldDescriptor, false);
        }
    }

    /**
     * Creates handler for a {@link Method}.
     *
     * @param declaringClass never null, class that declares the {@link Method} in question
     * @param methodDescriptor never null, descriptor of the {@link Method} in question
     * @return never null
     */
    static GizmoMemberHandler of(Class<?> declaringClass, MethodDescriptor methodDescriptor) {
        return new GizmoMethodHandler(declaringClass, methodDescriptor);
    }

    void whenIsField(Consumer<FieldDescriptor> fieldDescriptorConsumer);

    void whenIsMethod(Consumer<MethodDescriptor> methodDescriptorConsumer);

    ResultHandle readMemberValue(BytecodeCreator bytecodeCreator, ResultHandle thisObj);

    boolean writeMemberValue(MethodDescriptor setter, BytecodeCreator bytecodeCreator, ResultHandle thisObj,
            ResultHandle newValue);

    String getDeclaringClassName();

    String getTypeName();

    Type getType();

}
