/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.kie.builder.impl;

import java.util.Optional;

import org.drools.core.reteoo.Rete;
import org.kie.api.conf.Option;

public class KieBaseUpdatersContext {

    private final KieBaseUpdaterOptions options;
    private final Rete rete;
    private final ClassLoader classLoader;

    public KieBaseUpdatersContext(KieBaseUpdaterOptions options,
                                  Rete rete,
                                  ClassLoader classLoader) {
        this.options = options;
        this.rete = rete;
        this.classLoader = classLoader;
    }

    public Optional<Option> getOption(Class<? extends Option> optionClazz) {
        return options.getOption(optionClazz);
    }

    public Rete getRete() {
        return rete;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
