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
package org.kie.efesto.common.api.model;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * package private Singleton repository to cache generated classes. Accessed by EfestoContext
 */
enum GeneratedClassesRepository {

    INSTANCE;

    private Map<ModelLocalUriId, Map<String, byte[]>> generatedClassesMap = new ConcurrentHashMap<>();

    public void addGeneratedClasses(ModelLocalUriId modelLocalUriId, Map<String, byte[]> generatedClasses) {
        generatedClassesMap.put(modelLocalUriId, generatedClasses);
    }

    public Map<String, byte[]> getGeneratedClasses(ModelLocalUriId modelLocalUriId) {
        return generatedClassesMap.get(modelLocalUriId);
    }

    public Map<String, byte[]> removeGeneratedClasses(ModelLocalUriId modelLocalUriId) {
        return generatedClassesMap.remove(modelLocalUriId);
    }

    public boolean containsKey(ModelLocalUriId modelLocalUriId) {
        return generatedClassesMap.containsKey(modelLocalUriId);
    }

    public Set<ModelLocalUriId> localUriIdKeySet() {
        return generatedClassesMap.keySet();
    }

    public void clear() {
        generatedClassesMap.clear();
    }
}
