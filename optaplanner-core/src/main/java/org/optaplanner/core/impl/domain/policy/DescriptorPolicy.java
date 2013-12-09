/*
 * Copyright 2013 JBoss Inc
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.util.StringUtils;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;

public class DescriptorPolicy {

    private Map<String, Method> fromSolutionValueRangeProviderMap = new LinkedHashMap<String, Method>();
    private Map<String, Method> fromEntityValueRangeProviderMap = new LinkedHashMap<String, Method>();

    public void addFromSolutionValueRangeProvider(Method method) {
        String id = extractValueRangeProviderId(method);
        fromSolutionValueRangeProviderMap.put(id, method);
    }

    public boolean hasFromSolutionValueRangeProvider(String id) {
        return fromSolutionValueRangeProviderMap.containsKey(id);
    }

    public Method getFromSolutionValueRangeProvider(String id) {
        return fromSolutionValueRangeProviderMap.get(id);
    }

    public void addFromEntityValueRangeProvider(Method method) {
        String id = extractValueRangeProviderId(method);
        fromEntityValueRangeProviderMap.put(id, method);
    }

    public boolean hasFromEntityValueRangeProvider(String id) {
        return fromEntityValueRangeProviderMap.containsKey(id);
    }

    public Method getFromEntityValueRangeProvider(String id) {
        return fromEntityValueRangeProviderMap.get(id);
    }

    private String extractValueRangeProviderId(Method method) {
        ValueRangeProvider annotation = method.getAnnotation(ValueRangeProvider.class);
        String id = annotation.id();
        if (StringUtils.isEmpty(id)) {
            throw new IllegalStateException("The " + ValueRangeProvider.class.getSimpleName()
                    + " annotated method (" + method + ")'s id (" + id + ") must not be empty.");
        }
        validateUniqueValueRangeProviderId(id, method);
        return id;
    }

    private void validateUniqueValueRangeProviderId(String id, Method method) {
        Method duplicate = fromSolutionValueRangeProviderMap.get(id);
        if (duplicate != null) {
            throw new IllegalStateException("2 methods (" + duplicate + ", " + method
                    + ") with a " + ValueRangeProvider.class.getSimpleName()
                    + " annotation must not have the same id (" + id + ").");
        }
        duplicate = fromEntityValueRangeProviderMap.get(id);
        if (duplicate != null) {
            throw new IllegalStateException("2 methods (" + duplicate + ", " + method
                    + ") with a " + ValueRangeProvider.class.getSimpleName()
                    + " annotation must not have the same id (" + id + ").");
        }
    }

    public Collection<String> getValueRangeProviderIds() {
        List<String> valueRangeProviderIds = new ArrayList<String>(
                fromSolutionValueRangeProviderMap.size() + fromEntityValueRangeProviderMap.size());
        valueRangeProviderIds.addAll(fromSolutionValueRangeProviderMap.keySet());
        valueRangeProviderIds.addAll(fromEntityValueRangeProviderMap.keySet());
        return valueRangeProviderIds;
    }

}
