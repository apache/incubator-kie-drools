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

package org.optaplanner.quarkus.nativeimage;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory;
import org.optaplanner.core.impl.domain.common.accessor.ReflectionBeanPropertyMemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.ReflectionFieldMemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.ReflectionMethodMemberAccessor;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

@TargetClass(className = "org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory")
public final class Substitute_MemberAccessorFactory {

    @Substitute
    public static MemberAccessor buildMemberAccessor(Member member, MemberAccessorFactory.MemberAccessorType memberAccessorType,
            Class<? extends Annotation> annotationClass) {
        if (member instanceof Field) {
            Field field = (Field) member;
            return new ReflectionFieldMemberAccessor(field);
        } else if (member instanceof Method) {
            Method method = (Method) member;
            MemberAccessor memberAccessor;
            switch (memberAccessorType) {
                case FIELD_OR_READ_METHOD:
                    if (!ReflectionHelper.isGetterMethod(method)) {
                        ReflectionHelper.assertReadMethod(method, annotationClass);
                        memberAccessor = new ReflectionMethodMemberAccessor(method);
                        break;
                    }
                    // Intentionally fall through (no break)
                case FIELD_OR_GETTER_METHOD:
                case FIELD_OR_GETTER_METHOD_WITH_SETTER:
                    boolean getterOnly =
                            memberAccessorType != MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER;
                    ReflectionHelper.assertGetterMethod(method, annotationClass);
                    memberAccessor = new ReflectionBeanPropertyMemberAccessor(method, getterOnly);
                    break;
                default:
                    throw new IllegalStateException("The memberAccessorType (" + memberAccessorType
                            + ") is not implemented.");
            }
            if (memberAccessorType == MemberAccessorFactory.MemberAccessorType.FIELD_OR_GETTER_METHOD_WITH_SETTER
                    && !memberAccessor.supportSetter()) {
                throw new IllegalStateException("The class (" + method.getDeclaringClass()
                        + ") has a " + annotationClass.getSimpleName()
                        + " annotated getter method (" + method
                        + "), but lacks a setter for that property (" + memberAccessor.getName() + ").");
            }
            return memberAccessor;
        } else {
            throw new IllegalStateException("Impossible state: the member (" + member + ")'s type is not a "
                    + Field.class.getSimpleName() + " or a " + Method.class.getSimpleName() + ".");
        }
    }

}
