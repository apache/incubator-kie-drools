/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.domain.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;

public class DescriptorPolicy {

    private Map<String, MemberAccessor> fromSolutionValueRangeProviderMap = new LinkedHashMap<>();
    private Map<String, MemberAccessor> fromEntityValueRangeProviderMap = new LinkedHashMap<>();

    public void addFromSolutionValueRangeProvider(MemberAccessor memberAccessor) {
        String id = extractValueRangeProviderId(memberAccessor);
        fromSolutionValueRangeProviderMap.put(id, memberAccessor);
    }

    public boolean hasFromSolutionValueRangeProvider(String id) {
        return fromSolutionValueRangeProviderMap.containsKey(id);
    }

    public MemberAccessor getFromSolutionValueRangeProvider(String id) {
        return fromSolutionValueRangeProviderMap.get(id);
    }

    public void addFromEntityValueRangeProvider(MemberAccessor memberAccessor) {
        String id = extractValueRangeProviderId(memberAccessor);
        fromEntityValueRangeProviderMap.put(id, memberAccessor);
    }

    public boolean hasFromEntityValueRangeProvider(String id) {
        return fromEntityValueRangeProviderMap.containsKey(id);
    }

    public MemberAccessor getFromEntityValueRangeProvider(String id) {
        return fromEntityValueRangeProviderMap.get(id);
    }

    private String extractValueRangeProviderId(MemberAccessor memberAccessor) {
        ValueRangeProvider annotation = memberAccessor.getAnnotation(ValueRangeProvider.class);
        String id = annotation.id();
        if (StringUtils.isEmpty(id)) {
            throw new IllegalStateException("The " + ValueRangeProvider.class.getSimpleName()
                    + " annotated member (" + memberAccessor + ")'s id (" + id + ") must not be empty.");
        }
        validateUniqueValueRangeProviderId(id, memberAccessor);
        return id;
    }

    private void validateUniqueValueRangeProviderId(String id, MemberAccessor memberAccessor) {
        MemberAccessor duplicate = fromSolutionValueRangeProviderMap.get(id);
        if (duplicate != null) {
            throw new IllegalStateException("2 members (" + duplicate + ", " + memberAccessor
                    + ") with a " + ValueRangeProvider.class.getSimpleName()
                    + " annotation must not have the same id (" + id + ").");
        }
        duplicate = fromEntityValueRangeProviderMap.get(id);
        if (duplicate != null) {
            throw new IllegalStateException("2 members (" + duplicate + ", " + memberAccessor
                    + ") with a " + ValueRangeProvider.class.getSimpleName()
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
