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
package org.kie.kogito.index.storage;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.persistence.api.Storage;

public class ModelProcessDefinitionStorage extends ModelStorageFetcher<ProcessDefinitionKey, ProcessDefinition> implements Storage<ProcessDefinitionKey, ProcessDefinition> {

    private static final String VERSION_SEPARATOR = "$v:";

    static ProcessDefinitionKey fromString(String key) {
        int indexOf = key.indexOf(VERSION_SEPARATOR);
        return indexOf == -1 ? new ProcessDefinitionKey(key, null)
                : new ProcessDefinitionKey(key.substring(0, indexOf), key.substring(indexOf + VERSION_SEPARATOR.length()));
    }

    static String toString(ProcessDefinitionKey key) {
        String id = key.getId();
        String version = key.getVersion();
        return version == null ? id : id + VERSION_SEPARATOR + version;
    }

    public ModelProcessDefinitionStorage(Storage<String, ProcessDefinition> storage) {
        super(storage, ModelProcessDefinitionStorage::toString, ModelProcessDefinitionStorage::fromString);
    }

    @Override
    public ProcessDefinition put(ProcessDefinitionKey key, ProcessDefinition value) {
        return storage.put(toString(key), value);
    }

    @Override
    public ProcessDefinition remove(ProcessDefinitionKey key) {
        return storage.remove(toString(key));
    }

    @Override
    public boolean containsKey(ProcessDefinitionKey key) {
        return storage.containsKey(toString(key));
    }

    @Override
    public Map<ProcessDefinitionKey, ProcessDefinition> entries() {
        return storage.entries().entrySet().stream().collect(Collectors.toMap(e -> fromString(e.getKey()), Entry::getValue));
    }

    @Override
    public String getRootType() {
        return ProcessDefinition.class.getName();
    }
}
