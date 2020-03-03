/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.hacep.core.infra.consumer;

import java.io.Serializable;

public class ItemToProcess implements Serializable {

    private String key;
    private Serializable object;
    private long offset;

    public ItemToProcess(String key,
                         long offset,
                         Serializable object) {
        this.key = key;
        this.object = object;
        this.offset = offset;
    }

    public String getKey() {
        return key;
    }

    public Object getObject() {
        return object;
    }

    public long getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ItemToProcess{");
        sb.append("key='").append(key).append('\'');
        sb.append(", object=").append(object);
        sb.append(", offset=").append(offset);
        sb.append('}');
        return sb.toString();
    }
}
