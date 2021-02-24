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

import io.quarkus.builder.item.MultiBuildItem;
import org.jboss.jandex.IndexView;

public final class KogitoGeneratedClassesBuildItem extends MultiBuildItem {

    private final IndexView indexedClasses;

    public KogitoGeneratedClassesBuildItem(IndexView indexedClasses) {
        this.indexedClasses = indexedClasses;
    }

    public IndexView getIndexedClasses() {
        return indexedClasses;
    }
}
