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
import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;

public abstract class KiePMMLExtensioned implements Serializable {

    private static final long serialVersionUID = 7584716149775970999L;
    protected List<KiePMMLExtension> extensions;

    public KiePMMLExtensioned() {
        // Serialization
    }

    public KiePMMLExtensioned(List<KiePMMLExtension> extensions) {
        this.extensions = extensions;
    }

    public List<KiePMMLExtension> getExtensions() {
        return extensions;
    }
}
