/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.kie.kogito.codegen.api.ConfigGenerator;
import org.kie.kogito.codegen.api.GeneratedFile;
import org.kie.kogito.codegen.api.Generator;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

public abstract class AbstractGenerator implements Generator {

    private final ConfigGenerator configGenerator;
    private final KogitoBuildContext context;
    private final String name;

    protected AbstractGenerator(KogitoBuildContext context, String name) {
        this(context, name, null);
    }

    protected AbstractGenerator(KogitoBuildContext context, String name, ConfigGenerator configGenerator) {
        Objects.requireNonNull(context, "context cannot be null");
        this.name = name;
        this.context = context;
        this.configGenerator = configGenerator;
    }

    @Override
    public KogitoBuildContext context() {
        return this.context;
    }

    @Override
    public String name() {
        return name;
    }

    protected String applicationCanonicalName() {
        return context.getPackageName() + ".Application";
    }

    @Override
    public Optional<ConfigGenerator> configGenerator() {
        return Optional.ofNullable(configGenerator);
    }

    @Override
    public final Collection<GeneratedFile> generate() {
        if (isEmpty()) {
            return Collections.emptySet();
        }
        return internalGenerate();
    }

    protected abstract Collection<GeneratedFile> internalGenerate();
}
