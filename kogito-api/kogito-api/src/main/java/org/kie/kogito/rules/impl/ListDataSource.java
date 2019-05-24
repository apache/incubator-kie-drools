/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.rules.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.rules.DataSource;

public class ListDataSource<T> implements DataSource<T> {
    ArrayList<T> values = new ArrayList<>();

    public FactHandle add(T t) {
        values.add(t);
        return null;
    }

    @Override
    public void update(FactHandle handle, T object) {

    }

    @Override
    public void remove(FactHandle handle) {

    }

    public void addAll(Collection<? extends T> ts) {
        values.addAll(ts);
    }

    public void drainInto(Consumer<Object> sink) {
        values.forEach( sink );
        values.clear();
    }
}
