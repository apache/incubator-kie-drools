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

package org.optaplanner.core.impl.domain.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessorFactory;

public class DescriptorPolicy {
    private Map<String, SolutionCloner> generatedSolutionClonerMap = new LinkedHashMap<>();
    private final Map<String, MemberAccessor> fromSolutionValueRangeProviderMap = new LinkedHashMap<>();
    private final Set<MemberAccessor> anonymousFromSolutionValueRangeProviderSet = new LinkedHashSet<>();
    private final Map<String, MemberAccessor> fromEntityValueRangeProviderMap = new LinkedHashMap<>();
    private final Set<MemberAccessor> anonymousFromEntityValueRangeProviderSet = new LinkedHashSet<>();
    private DomainAccessType domainAccessType = DomainAccessType.REFLECTION;
    private MemberAccessorFactory memberAccessorFactory;

    public void addFromSolutionValueRangeProvider(MemberAccessor memberAccessor) {
        String id = extractValueRangeProviderId(memberAccessor);
        if (id == null) {
            anonymousFromSolutionValueRangeProviderSet.add(memberAccessor);
        } else {
            fromSolutionValueRangeProviderMap.put(id, memberAccessor);
        }
    }

    public boolean isFromSolutionValueRangeProvider(MemberAccessor memberAccessor) {
        return fromSolutionValueRangeProviderMap.containsValue(memberAccessor)
                || anonymousFromSolutionValueRangeProviderSet.contains(memberAccessor);
    }

    public boolean hasFromSolutionValueRangeProvider(String id) {
        return fromSolutionValueRangeProviderMap.containsKey(id);
    }

    public MemberAccessor getFromSolutionValueRangeProvider(String id) {
        return fromSolutionValueRangeProviderMap.get(id);
    }

    public Set<MemberAccessor> getAnonymousFromSolutionValueRangeProviderSet() {
        return anonymousFromSolutionValueRangeProviderSet;
    }

    public void addFromEntityValueRangeProvider(MemberAccessor memberAccessor) {
        String id = extractValueRangeProviderId(memberAccessor);
        if (id == null) {
            anonymousFromEntityValueRangeProviderSet.add(memberAccessor);
        } else {
            fromEntityValueRangeProviderMap.put(id, memberAccessor);
        }
    }

    public boolean isFromEntityValueRangeProvider(MemberAccessor memberAccessor) {
        return fromEntityValueRangeProviderMap.containsValue(memberAccessor)
                || anonymousFromEntityValueRangeProviderSet.contains(memberAccessor);
    }

    public boolean hasFromEntityValueRangeProvider(String id) {
        return fromEntityValueRangeProviderMap.containsKey(id);
    }

    public Set<MemberAccessor> getAnonymousFromEntityValueRangeProviderSet() {
        return anonymousFromEntityValueRangeProviderSet;
    }

    /**
     * @return never null
     */
    public DomainAccessType getDomainAccessType() {
        return domainAccessType;
    }

    public void setDomainAccessType(DomainAccessType domainAccessType) {
        this.domainAccessType = domainAccessType;
    }

    /**
     * @return never null
     */
    public Map<String, SolutionCloner> getGeneratedSolutionClonerMap() {
        return generatedSolutionClonerMap;
    }

    public void setGeneratedSolutionClonerMap(Map<String, SolutionCloner> generatedSolutionClonerMap) {
        this.generatedSolutionClonerMap = generatedSolutionClonerMap;
    }

    public MemberAccessorFactory getMemberAccessorFactory() {
        return memberAccessorFactory;
    }

    public void setMemberAccessorFactory(MemberAccessorFactory memberAccessorFactory) {
        this.memberAccessorFactory = memberAccessorFactory;
    }

    public MemberAccessor getFromEntityValueRangeProvider(String id) {
        return fromEntityValueRangeProviderMap.get(id);
    }

    private String extractValueRangeProviderId(MemberAccessor memberAccessor) {
        ValueRangeProvider annotation = memberAccessor.getAnnotation(ValueRangeProvider.class);
        String id = annotation.id();
        if (id == null || id.isEmpty()) {
            return null;
        }
        validateUniqueValueRangeProviderId(id, memberAccessor);
        return id;
    }

    private void validateUniqueValueRangeProviderId(String id, MemberAccessor memberAccessor) {
        MemberAccessor duplicate = fromSolutionValueRangeProviderMap.get(id);
        if (duplicate != null) {
            throw new IllegalStateException("2 members (" + duplicate + ", " + memberAccessor
                    + ") with a @" + ValueRangeProvider.class.getSimpleName()
                    + " annotation must not have the same id (" + id + ").");
        }
        duplicate = fromEntityValueRangeProviderMap.get(id);
        if (duplicate != null) {
            throw new IllegalStateException("2 members (" + duplicate + ", " + memberAccessor
                    + ") with a @" + ValueRangeProvider.class.getSimpleName()
                    + " annotation must not have the same id (" + id + ").");
        }
    }

    public Collection<String> getValueRangeProviderIds() {
        List<String> valueRangeProviderIds = new ArrayList<>(
                fromSolutionValueRangeProviderMap.size() + fromEntityValueRangeProviderMap.size());
        valueRangeProviderIds.addAll(fromSolutionValueRangeProviderMap.keySet());
        valueRangeProviderIds.addAll(fromEntityValueRangeProviderMap.keySet());
        return valueRangeProviderIds;
    }

}
