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

import org.kie.pmml.commons.model.KiePMMLExtension;

public abstract class KiePMMLExtensionedNamed extends KiePMMLNamed {

    private static final long serialVersionUID = 1452775408881599004L;
    protected final List<KiePMMLExtension> extensions;

    public KiePMMLExtensionedNamed(String name, List<KiePMMLExtension> extensions) {
        super(name);
        this.extensions = extensions;
    }

    public List<KiePMMLExtension> getExtensions() {
        return extensions;
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

        KiePMMLExtensionedNamed that = (KiePMMLExtensionedNamed) o;

        return extensions != null ? extensions.equals(that.extensions) : that.extensions == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (extensions != null ? extensions.hashCode() : 0);
        return result;
    }
}
