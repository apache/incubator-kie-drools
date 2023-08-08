/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.common.deployment;

import java.util.Optional;

import org.jboss.jandex.IndexView;

import io.quarkus.builder.item.SimpleBuildItem;

public final class LiveReloadExecutionBuildItem extends SimpleBuildItem {

    private final IndexView indexView;
    private final ClassLoader classLoader;

    public LiveReloadExecutionBuildItem(IndexView indexView) {
        this(indexView, null);
    }

    public LiveReloadExecutionBuildItem(IndexView indexView, ClassLoader classLoader) {
        this.indexView = indexView;
        this.classLoader = classLoader;
    }

    public IndexView getIndexView() {
        return indexView;
    }

    public Optional<ClassLoader> getClassLoader() {
        return Optional.ofNullable(classLoader);
    }
}
