/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.drools.core.data;

import org.kie.kogito.rules.DataHandle;
import org.kie.kogito.rules.DataStore;

public class ListDataStore<T> extends org.drools.ruleunits.impl.datasources.ListDataStore<T> implements DataStore<T> {

    @Override
    public DataHandle add(T t) {
        return (DataHandle) super.add(t);
    }

    @Override
    protected DataHandle createDataHandle(T t) {
        return new DataHandleImpl(t);
    }

    @Override
    public DataHandle findHandle(long id) {
        return (DataHandle) super.findHandle(id);
    }
}
