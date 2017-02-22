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
import java.lang.reflect.Method;
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

/**
 * This class is thread-safe.
 */
public class LocationStrategyResolver {

    private final LocationStrategyType locationStrategyType;

    private final ConcurrentMap<Class<?>, LocationStrategy> decisionCache = new ConcurrentHashMap<>();

    public LocationStrategyResolver(LocationStrategyType locationStrategyType) {
        this.locationStrategyType = locationStrategyType;
        decisionCache.put(Boolean.class, new ImmutableLocationStrategy());
        decisionCache.put(Byte.class, new ImmutableLocationStrategy());
        decisionCache.put(Short.class, new ImmutableLocationStrategy());
        decisionCache.put(Integer.class, new ImmutableLocationStrategy());
        decisionCache.put(Long.class, new ImmutableLocationStrategy());
        decisionCache.put(Float.class, new ImmutableLocationStrategy());
        decisionCache.put(Double.class, new ImmutableLocationStrategy());
        decisionCache.put(BigInteger.class, new ImmutableLocationStrategy());
        decisionCache.put(BigDecimal.class, new ImmutableLocationStrategy());
        decisionCache.put(Character.class, new ImmutableLocationStrategy());
        decisionCache.put(String.class, new ImmutableLocationStrategy());
        decisionCache.put(LocalDate.class, new ImmutableLocationStrategy());
        decisionCache.put(LocalTime.class, new ImmutableLocationStrategy());
        decisionCache.put(LocalDateTime.class, new ImmutableLocationStrategy());
    }

    /**
     * This method is thread-safe.
     * @param object never null
     * @return never null
     */
    public LocationStrategy determineLocationStrategy(Object object) {
        Class<?> objectClass = object.getClass();
        return decisionCache.computeIfAbsent(objectClass, key -> {
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
                    Method equalsMethod;
                    Method hashCodeMethod;
                    try {
                        equalsMethod = object.getClass().getMethod("equals", Object.class);
                        hashCodeMethod = object.getClass().getMethod("hashCode");
                    } catch (NoSuchMethodException e) {
                        throw new IllegalStateException(
                                "Impossible state because equals() and hashCode() always exist.", e);
                    }
                    if (equalsMethod.getDeclaringClass().equals(Object.class)) {
                        throw new IllegalArgumentException("The class (" + object.getClass().getSimpleName()
                                + ") doesn't override the equals() method, neither does any superclass.");
                    }
                    if (hashCodeMethod.getDeclaringClass().equals(Object.class)) {
                        throw new IllegalArgumentException("The class (" + object.getClass().getSimpleName()
                                + ") overrides equals() but neither it nor any superclass"
                                + " overrides the hashCode() method.");
                    }
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
            throw new IllegalArgumentException("The class (" + clazz
                    + ") has " + memberList.size() + " members (" + memberList + ") with a "
                    + PlanningId.class.getSimpleName() + " annotation.");
        }
        Member member = memberList.get(0);
        return ConfigUtils.buildMemberAccessor(member, FIELD_OR_READ_METHOD, PlanningId.class);
    }

}
