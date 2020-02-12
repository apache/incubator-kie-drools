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

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import org.kie.pmml.commons.model.KiePMMLExtension;

public abstract class KiePMMLIDedExtensioned extends KiePMMLIDed {

    private static final long serialVersionUID = 7584716149775970999L;
    protected List<KiePMMLExtension> extensions;

    protected KiePMMLIDedExtensioned() {
    }

    public List<KiePMMLExtension> getExtensions() {
        return extensions;
    }

    @Override
    public String toString() {
        return "KiePMMLIDedExtensioned{" +
                "extensions=" + extensions +
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
        KiePMMLIDedExtensioned that = (KiePMMLIDedExtensioned) o;
        return Objects.equals(extensions, that.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), extensions);
    }

    public static class Builder<T extends KiePMMLIDedExtensioned> extends KiePMMLIDed.Builder<T> {

        protected Builder(List<KiePMMLExtension> extensions, String prefix, Supplier<T> supplier) {
            super(prefix, supplier);
            toBuild.extensions = extensions;
        }
    }
}
