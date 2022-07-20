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
package org.kie.efesto.common.api.model;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * package private Singleton repository to cache generated classes. Accessed by EfestoContext
 */
enum GeneratedClassesRepository {

    INSTANCE;

    private Map<FRI, Map<String, byte[]>> generatedClassesMap = new ConcurrentHashMap<>();

    public void addGeneratedClasses(FRI fri, Map<String, byte[]> generatedClasses) {
        generatedClassesMap.put(fri, generatedClasses);
    }

    public Map<String, byte[]> getGeneratedClasses(FRI fri) {
        return generatedClassesMap.get(fri);
    }

    public Map<String, byte[]> removeGeneratedClasses(FRI fri) {
        return generatedClassesMap.remove(fri);
    }

    public boolean containsKey(FRI fri) {
        return generatedClassesMap.containsKey(fri);
    }

    public Set<FRI> friKeySet() {
        return generatedClassesMap.keySet();
    }

    public void clear() {
        generatedClassesMap.clear();
    }
}
