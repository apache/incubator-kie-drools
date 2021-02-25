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

import java.util.Objects;

import org.kie.kogito.codegen.api.ApplicationSection;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

/**
 * Base implementation for an {@link ApplicationSection}.
 * <p>
 * It provides a skeleton for a "section" in the Application generated class.
 * Subclasses may extend this base class and decorate the provided
 * simple implementations of the interface methods with custom logic.
 */
public abstract class AbstractApplicationSection implements ApplicationSection {

    protected final KogitoBuildContext context;
    private final String sectionClassName;

    public AbstractApplicationSection(KogitoBuildContext context, String sectionClassName) {
        Objects.requireNonNull(context, "context cannot be null");
        this.sectionClassName = sectionClassName;
        this.context = context;
    }

    @Override
    public String sectionClassName() {
        return sectionClassName;
    }
}
