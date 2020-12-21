/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.context;

import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import java.util.function.Predicate;

public abstract class AbstractKogitoBuildContext implements KogitoBuildContext {

    protected final Predicate<String> classAvailabilityResolver;
    protected DependencyInjectionAnnotator dependencyInjectionAnnotator;

    public AbstractKogitoBuildContext(Predicate<String> classAvailabilityResolver, DependencyInjectionAnnotator dependencyInjectionAnnotator) {
        this.classAvailabilityResolver = classAvailabilityResolver;
        this.dependencyInjectionAnnotator = dependencyInjectionAnnotator;
    }

    @Override
    public boolean hasClassAvailable(String fqcn) {
        return classAvailabilityResolver.test(fqcn);
    }

    @Override
    public DependencyInjectionAnnotator getDependencyInjectionAnnotator() {
        return dependencyInjectionAnnotator;
    }

    @Override
    public void setDependencyInjectionAnnotator(DependencyInjectionAnnotator dependencyInjectionAnnotator) {
        this.dependencyInjectionAnnotator = dependencyInjectionAnnotator;
    }
}