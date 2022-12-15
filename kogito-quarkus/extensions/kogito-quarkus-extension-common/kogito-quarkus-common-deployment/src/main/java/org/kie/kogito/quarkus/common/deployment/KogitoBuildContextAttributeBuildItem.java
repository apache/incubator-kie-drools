/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import io.quarkus.builder.item.MultiBuildItem;

/**
 * {@link MultiBuildItem} for {@link KogitoBuildContext} attributes.
 */
public final class KogitoBuildContextAttributeBuildItem extends MultiBuildItem {

    private final String name;
    private final Object value;

    public KogitoBuildContextAttributeBuildItem(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
