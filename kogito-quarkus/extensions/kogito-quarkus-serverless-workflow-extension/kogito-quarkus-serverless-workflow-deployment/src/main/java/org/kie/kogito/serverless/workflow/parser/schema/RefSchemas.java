/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.serverless.workflow.parser.schema;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.media.Schema;

class RefSchemas {

    private RefSchemas() {
    }

    private static class ThreadInfo {
        private final String id;
        private final Map<String, Schema> map = new HashMap<>();
        private int counter;
        private String baseURI;

        private ThreadInfo(String id) {
            this.id = id;
        }
    }

    private static ThreadLocal<ThreadInfo> threadInfo = new ThreadLocal<>();

    public static void init(String id) {
        threadInfo.set(new ThreadInfo(id));
    }

    public static Map<String, Schema> get() {
        return threadInfo.get().map;
    }

    public static void baseURI(String baseURI) {
        if (baseURI != null) {
            int lastIndexOf = baseURI.lastIndexOf('/');
            threadInfo.get().baseURI = lastIndexOf != -1 ? baseURI.substring(0, lastIndexOf) : baseURI;
        }
    }

    public static String getBaseURI() {
        return threadInfo.get().baseURI;
    }

    public static String getKey() {
        ThreadInfo t = threadInfo.get();
        return t.id + "_nested_" + ++t.counter;
    }

    public static void reset() {
        threadInfo.remove();
    }
}
