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

package org.kie.kogito.codegen;

import java.util.Objects;

import org.kie.kogito.codegen.context.KogitoBuildContext;

public abstract class AbstractGenerator implements Generator {

    public static final GeneratedFileType REST_TYPE = GeneratedFileType.of("REST", GeneratedFileType.Category.SOURCE, true, true);
    public static final GeneratedFileType MODEL_TYPE = GeneratedFileType.of("MODEL", GeneratedFileType.Category.SOURCE, true, true);

    private final ConfigGenerator configGenerator;
    private final KogitoBuildContext context;

    protected AbstractGenerator(KogitoBuildContext context) {
        this(context, null);
    }

    protected AbstractGenerator(KogitoBuildContext context, ConfigGenerator configGenerator) {
        Objects.requireNonNull(context, "context cannot be null");
        this.context = context;
        this.configGenerator = configGenerator;
    }

    @Override
    public KogitoBuildContext context() {
        return this.context;
    }

    protected String applicationCanonicalName() {
        return context.getPackageName() + ".Application";
    }

    @Override
    public void updateConfig(ApplicationConfigGenerator cfg) {
        if (configGenerator != null) {
            cfg.withConfigGenerator(configGenerator);
        }
    }
}
