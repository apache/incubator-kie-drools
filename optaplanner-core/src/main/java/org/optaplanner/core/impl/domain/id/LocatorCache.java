/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.id;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.optaplanner.core.api.domain.id.PlanningId;
import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.common.accessor.FieldMemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MethodMemberAccessor;

import static org.optaplanner.core.config.util.ConfigUtils.MemberAccessorType.FIELD_OR_READ_METHOD;

public class LocatorCache {

    private final ConcurrentMap<Class, LocationStrategy> decisionClassCache = new ConcurrentHashMap<>();

    public LocatorCache() {
        decisionClassCache.put(Boolean.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Byte.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Short.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Integer.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Long.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Float.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Double.class, new ImmutableLocationStrategy());
        decisionClassCache.put(BigInteger.class, new ImmutableLocationStrategy());
        decisionClassCache.put(BigDecimal.class, new ImmutableLocationStrategy());
        decisionClassCache.put(Character.class, new ImmutableLocationStrategy());
        decisionClassCache.put(String.class, new ImmutableLocationStrategy());
        decisionClassCache.put(LocalDate.class, new ImmutableLocationStrategy());
        decisionClassCache.put(LocalTime.class, new ImmutableLocationStrategy());
        decisionClassCache.put(LocalDateTime.class, new ImmutableLocationStrategy());
    }

    protected LocationStrategy retrieveLocationStrategy(Object object) {
        Class<?> objectClass = object.getClass();
        return decisionClassCache.computeIfAbsent(objectClass, key -> {
            MemberAccessor memberAccessor = findPlanningIdMemberAccessor(objectClass);
            if (memberAccessor != null) {
                return new PlanningIdLocationStrategy(memberAccessor);
            }
            throw new UnsupportedOperationException("" + objectClass); // TODO
//            return new EqualsLocationStrategy();
        });
    }

    protected <C> MemberAccessor findPlanningIdMemberAccessor(Class<C> clazz) {
        List<Member> memberList = ConfigUtils.getAllMembers(clazz, PlanningId.class);
        if (memberList.isEmpty()) {
            return null;
        }
        if (memberList.size() > 1) {
            throw new IllegalStateException("The class (" + clazz
                    + ") has " +  memberList.size() + " members (" + memberList + ") with a "
                    + PlanningId.class.getSimpleName() + " annotation.");
        }
        Member member = memberList.get(0);
        return ConfigUtils.buildMemberAccessor(member, FIELD_OR_READ_METHOD, PlanningId.class);
    }

}
