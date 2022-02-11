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
package org.kie.pmml.models.regression.model;

import java.util.List;

import org.kie.pmml.commons.model.KiePMMLExtension;

public final class KiePMMLRegressionTable extends AbstractKiePMMLTable {

    private static final long serialVersionUID = -7899446939844650691L;

    public static Builder builder(String name, List<KiePMMLExtension> extensions) {
        return new Builder(name, extensions);
    }

    private KiePMMLRegressionTable(String name, List<KiePMMLExtension> extensions) {
        // Keeping private to implement Builder pattern
        super(name, extensions);
    }

    public static class Builder extends AbstractKiePMMLTable.Builder<KiePMMLRegressionTable> {

        protected Builder(String name, List<KiePMMLExtension> extensions) {
            super("KiePMMLRegressionTable-", () -> new KiePMMLRegressionTable(name, extensions));
        }
    }
}
