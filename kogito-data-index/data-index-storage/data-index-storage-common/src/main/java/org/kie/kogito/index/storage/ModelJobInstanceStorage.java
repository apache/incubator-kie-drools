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

import org.kie.kogito.index.model.Job;
import org.kie.kogito.persistence.api.Storage;

public class ModelJobInstanceStorage extends ModelStorageFetcher<String, Job> implements JobInstanceStorage {

    public ModelJobInstanceStorage(Storage<String, Job> storage) {
        super(storage, key -> key, key -> key);
    }

    @Override
    public void indexJob(Job job) {
        put(job.getId(), job);
    }

    @Override
    public Job put(String key, Job value) {
        return storage.put(key, value);
    }

    @Override
    public Job remove(String key) {
        return storage.remove(key);
    }

    @Override
    public boolean containsKey(String key) {
        return storage.containsKey(key);
    }

    @Override
    public Map<String, Job> entries() {
        return storage.entries();
    }

    @Override
    public String getRootType() {
        return Job.class.getName();
    }
}
