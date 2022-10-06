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
package org.kie.kogito.event;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.kie.kogito.MappableToModel;
import org.kie.kogito.Model;

public class DummyModel implements Model,
        MappableToModel<DummyModel> {

    private Object dummyEvent;

    @Override
    public DummyModel fromMap(Map<String, Object> params) {
        this.dummyEvent = params.get("dummyEvent");
        return this;
    }

    @Override
    public Map<String, Object> toMap() {
        return Collections.singletonMap("dummyEvent", dummyEvent);
    }

    public DummyModel(Object dummyEvent) {
        this.dummyEvent = dummyEvent;
    }

    @Override
    public void update(Map<String, Object> params) {
        fromMap(params);
    }

    @Override
    public DummyModel toModel() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DummyModel)) {
            return false;
        }
        DummyModel that = (DummyModel) o;
        return Objects.equals(dummyEvent, that.dummyEvent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dummyEvent);
    }
}
