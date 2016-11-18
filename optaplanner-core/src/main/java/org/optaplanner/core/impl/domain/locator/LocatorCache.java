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

package org.optaplanner.core.impl.domain.locator;

import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.optaplanner.core.api.domain.locator.LocationStrategyType;
import org.optaplanner.core.api.domain.locator.PlanningId;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;

import static org.optaplanner.core.config.util.ConfigUtils.MemberAccessorType.FIELD_OR_READ_METHOD;

public class LocatorCache {

    private final LocationStrategyType locationStrategyType;

    private final ConcurrentMap<Class, LocationStrategy> decisionClassCache = new ConcurrentHashMap<>();

    public LocatorCache(LocationStrategyType locationStrategyType) {
        this.locationStrategyType = locationStrategyType;
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
            switch (locationStrategyType) {
                case PLANNING_ID_OR_NONE:
                    MemberAccessor memberAccessor1 = findPlanningIdMemberAccessor(objectClass);
                    if (memberAccessor1 == null) {
                        return new NoneLocationStrategy();
                    }
                    return new PlanningIdLocationStrategy(memberAccessor1);
                case PLANNING_ID_OR_FAIL_FAST:
                    MemberAccessor memberAccessor2 = findPlanningIdMemberAccessor(objectClass);
                    if (memberAccessor2 == null) {
                        throw new IllegalArgumentException("The class (" + objectClass
                                + ") does not have a " + PlanningId.class.getSimpleName() + " annotation,"
                                + " but the locationStrategyType (" + locationStrategyType + ") requires it.\n"
                                + "Maybe add the " + PlanningId.class.getSimpleName() + " annotation"
                                + " or change the " + PlanningSolution.class.getSimpleName() + " annotation's "
                                + LocationStrategyType.class.getSimpleName() + ".");
                    }
                    return new PlanningIdLocationStrategy(memberAccessor2);
                case EQUALITY:
                    return new EqualsLocationStrategy();
                case NONE:
                    return new NoneLocationStrategy();
                default:
                    throw new IllegalStateException("The locationStrategyType (" + locationStrategyType
                            + ") is not implemented.");
            }
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
