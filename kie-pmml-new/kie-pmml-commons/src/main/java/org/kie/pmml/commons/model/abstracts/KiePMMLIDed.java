/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons.model.abstracts;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class KiePMMLIDed implements Serializable {

    private static final long serialVersionUID = -2153680489671276928L;
    protected String id;
    protected String parentId;

    protected KiePMMLIDed() {
    }

    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "KiePMMLIDed{" +
                "id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KiePMMLIDed that = (KiePMMLIDed) o;

        if (!Objects.equals(id, that.id)) {
            return false;
        }
        return Objects.equals(parentId, that.parentId);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        return result;
    }

    public static class Builder<T extends KiePMMLIDed> {

        private static final AtomicInteger counter = new AtomicInteger(1);
        protected T toBuild;

        protected Builder(String prefix, Supplier<T> supplier) {
            this.toBuild = supplier.get();
            this.toBuild.id = prefix + counter.getAndAdd(1);
        }

        public T build() {
            return toBuild;
        }
    }
}
