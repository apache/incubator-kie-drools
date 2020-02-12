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

import java.util.Objects;
import java.util.function.Supplier;

public abstract class KiePMMLIDedNamed extends KiePMMLIDed {

    private static final long serialVersionUID = -6357112249914060778L;
    protected String name;

    protected KiePMMLIDedNamed() {
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "KiePMMLIDedNamed{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
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
        if (!super.equals(o)) {
            return false;
        }

        KiePMMLIDedNamed that = (KiePMMLIDedNamed) o;

        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public static class Builder<T extends KiePMMLIDedNamed> extends KiePMMLIDed.Builder<T> {

        protected Builder(String name, String prefix, Supplier<T> supplier) {
            super(prefix, supplier);
            toBuild.name = name;
        }
    }
}
