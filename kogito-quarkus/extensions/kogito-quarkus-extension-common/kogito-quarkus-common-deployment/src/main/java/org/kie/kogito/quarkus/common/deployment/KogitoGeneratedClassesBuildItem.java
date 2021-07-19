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
package org.kie.kogito.quarkus.common.deployment;

import java.util.Collections;
import java.util.Map;

import org.jboss.jandex.IndexView;

import io.quarkus.builder.item.MultiBuildItem;

public final class KogitoGeneratedClassesBuildItem extends MultiBuildItem {

    private final IndexView indexedClasses;
    private final Map<String, byte[]> generatedClasses;

    public KogitoGeneratedClassesBuildItem(IndexView indexedClasses, Map<String, byte[]> generatedClasses) {
        this.indexedClasses = indexedClasses;
        this.generatedClasses = Collections.unmodifiableMap(generatedClasses);
    }

    public IndexView getIndexedClasses() {
        return indexedClasses;
    }

    public Map<String, byte[]> getGeneratedClasses() {
        return generatedClasses;
    }
}
