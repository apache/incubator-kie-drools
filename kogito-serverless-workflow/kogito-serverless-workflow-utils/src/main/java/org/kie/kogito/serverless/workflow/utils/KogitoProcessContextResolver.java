/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.workflow.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.kie.kogito.internal.process.runtime.KogitoProcessContext;

public class KogitoProcessContextResolver {

    private static KogitoProcessContextResolver instance = new KogitoProcessContextResolver();

    private static final String HEADERS_PREFIX = "headers.";

    public static KogitoProcessContextResolver get() {
        return instance;
    }

    private Map<String, Function<KogitoProcessContext, String>> methods;

    public KogitoProcessContextResolver() {
        methods = new HashMap<>();
        methods.put("instanceId", k -> k.getProcessInstance().getId());
        methods.put("id", k -> k.getProcessInstance().getProcessId());
        methods.put("name", k -> k.getProcessInstance().getProcessName());
    }

    public String readKey(KogitoProcessContext context, String key) {
        if (key.startsWith(HEADERS_PREFIX)) {
            Map<String, List<String>> headersMap = context.getHeaders();
            if (headersMap == null) {
                throw new IllegalArgumentException("No headers available");
            }
            String headerName = key.substring(HEADERS_PREFIX.length());
            List<String> headerValue = headersMap.get(headerName);
            if (headerValue == null) {
                throw new IllegalArgumentException("Header name " + headerName + " does not exist, headers are " + headersMap);
            }
            return headerValue.get(0);
        } else {
            Function<KogitoProcessContext, String> m = methods.get(key);
            if (m == null) {
                throw new IllegalArgumentException("Cannot find key " + key + " in context");
            }
            return m.apply(context);
        }
    }

}
