/**
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
package org.kie.dmn.core.internal.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;

public class MarshallingStubUtils {

    public static Object stubDMNResult(Object result, Function<Object, Object> stubbingWrapper) {
        if (result instanceof DMNContext) {
            Map<String, Object> stubbedContextValues = new HashMap<>();
            for (Entry<String, Object> kv : ((DMNContext) result).getAll().entrySet()) {
                stubbedContextValues.put(kv.getKey(), stubDMNResult(kv.getValue(), stubbingWrapper));
            }
            return MapBackedDMNContext.of(stubbedContextValues);
        } else if (result instanceof Map<?, ?>) {
            Map<Object, Object> stubbedValues = new HashMap<>();
            for (Entry<?, ?> kv : ((Map<?, ?>) result).entrySet()) {
                stubbedValues.put(kv.getKey(), stubDMNResult(kv.getValue(), stubbingWrapper));
            }
            return stubbedValues;
        } else if (result instanceof List<?>) {
            List<?> stubbedValues = ((List<?>) result).stream().map(r -> stubDMNResult(r, stubbingWrapper)).collect(Collectors.toList());
            return stubbedValues;
        } else if (result instanceof Set<?>) {
            Set<?> stubbedValues = ((Set<?>) result).stream().map(r -> stubDMNResult(r, stubbingWrapper)).collect(Collectors.toSet());
            return stubbedValues;
        } else if (result instanceof ComparablePeriod) {
            return ((ComparablePeriod) result).asPeriod();
        } else if (result != null && result.getClass().getPackage().getName().startsWith("org.kie.dmn")) {
            return stubbingWrapper.apply(result);
        }
        return result;
    }

    private MarshallingStubUtils() {
        // Constructing instances is not allowed for this class
    }
}
