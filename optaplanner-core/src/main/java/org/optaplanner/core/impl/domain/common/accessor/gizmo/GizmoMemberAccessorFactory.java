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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;

public class GizmoMemberAccessorFactory {
    // GizmoMemberAccessors are stateless, and thus can be safely reused across multiple instances
    private static Map<String, MemberAccessor> memberAccessorMap = new ConcurrentHashMap<>();

    // These fields and methods are here instead of GizmoMemberAccessorImplementor since
    // any calls to GizmoMemberAccessorImplementor will try to load Gizmo,
    // which is bad if Gizmo not on the classpath (which is the case for
    // Quarkus, where Gizmo is only on the build path)

    /**
     * Stores the generic type of a member (required
     * as we cannot hard code a non-primitive object
     * instance in Gizmo code); will be accessed
     * from generated Gizmo code via getGenericTypeFor
     */
    static Map<String, Type> gizmoMemberAccessorNameToGenericType = new ConcurrentHashMap<>();
    /**
     * Stores the annotated element of a member (required
     * as we cannot hard code a non-primitive object
     * instance in Gizmo code); will be accessed
     * from generated Gizmo code via getAnnotatedElementFor
     */
    static Map<String, AnnotatedElement> gizmoMemberAccessorNameToAnnotatedElement = new ConcurrentHashMap<>();

    /**
     * Returns the generated class name for a given member.
     * (Here as accessing any method of GizmoMemberAccessorImplementor
     * will try to load Gizmo code)
     *
     * @param member The member to get the generated class name for
     * @return The generated class name for member
     */
    public static String getGeneratedClassName(Member member) {
        return member.getDeclaringClass().getPackage().getName() + ".$optaplanner$__"
                + member.getDeclaringClass().getSimpleName() + "$__" + member.getName();
    }

    /**
     * Returns the Generic Type that a particular
     * MemberAccessor should return. Used in generated
     * Gizmo code.
     *
     * @param gizmoMemberAccessorName The MemberAccessor that is being queried
     * @return The generic type gizmoMemberAccessorName should return
     *         in MemberAccessor.getGenericType
     */
    public static Type getGenericTypeFor(String gizmoMemberAccessorName) {
        return gizmoMemberAccessorNameToGenericType.get(gizmoMemberAccessorName);
    }

    /**
     * Returns the AnnotatedElement that a particular
     * MemberAccessor should return. Used in generated
     * Gizmo code.
     *
     * @param gizmoMemberAccessorName The MemberAccessor that is being queried
     * @return The AnnotatedElement gizmoMemberAccessorName should return
     *         in MemberAccessor.getAnnotatedElement
     */
    public static AnnotatedElement getAnnotatedElementFor(String gizmoMemberAccessorName) {
        return gizmoMemberAccessorNameToAnnotatedElement.get(gizmoMemberAccessorName);
    }

    public static void usePregeneratedMaps(Map<String, MemberAccessor> memberAccessorMap,
            Map<String, Type> gizmoMemberAccessorNameToGenericType,
            Map<String, AnnotatedElement> gizmoMemberAccessorNameToAnnotatedElement) {
        GizmoMemberAccessorFactory.memberAccessorMap = memberAccessorMap;
        GizmoMemberAccessorFactory.gizmoMemberAccessorNameToGenericType = gizmoMemberAccessorNameToGenericType;
        GizmoMemberAccessorFactory.gizmoMemberAccessorNameToAnnotatedElement = gizmoMemberAccessorNameToAnnotatedElement;
    }

    public static MemberAccessor buildGizmoMemberAccessor(Member member, Class<? extends Annotation> annotationClass) {
        String gizmoMemberAccessorClassName = getGeneratedClassName(member);
        if (memberAccessorMap.containsKey(gizmoMemberAccessorClassName)) {
            return memberAccessorMap.get(gizmoMemberAccessorClassName);
        } else {
            try {
                // Check if Gizmo on the classpath by verifying we can access one of its classes
                Class.forName("io.quarkus.gizmo.ClassCreator", false,
                        Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("When using the domainAccessType (" +
                        DomainAccessType.GIZMO +
                        ") the classpath or modulepath must contain io.quarkus.gizmo:gizmo.\n" +
                        "Maybe add a dependency to io.quarkus.gizmo:gizmo.");
            }
            MemberAccessor accessor = GizmoMemberAccessorImplementor.createAccessorFor(member, annotationClass);
            memberAccessorMap.put(gizmoMemberAccessorClassName, accessor);
            return accessor;
        }
    }

    private GizmoMemberAccessorFactory() {
    }
}
