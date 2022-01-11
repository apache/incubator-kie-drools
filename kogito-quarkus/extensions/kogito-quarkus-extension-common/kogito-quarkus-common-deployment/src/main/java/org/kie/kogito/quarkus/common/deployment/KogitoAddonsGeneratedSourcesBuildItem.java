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

import java.util.Collection;

import org.kie.kogito.codegen.api.GeneratedFile;

import io.quarkus.builder.item.MultiBuildItem;

public final class KogitoAddonsGeneratedSourcesBuildItem extends MultiBuildItem implements Comparable<KogitoAddonsGeneratedSourcesBuildItem> {

    private static int counter;
    private final Collection<GeneratedFile> generatedFiles;
    private final int order;

    public KogitoAddonsGeneratedSourcesBuildItem(Collection<GeneratedFile> generatedFiles) {
        this.generatedFiles = generatedFiles;
        this.order = counter++;
    }

    public Collection<GeneratedFile> getGeneratedFiles() {
        return generatedFiles;
    }

    @Override
    public int compareTo(KogitoAddonsGeneratedSourcesBuildItem o) {
        return order - o.order;
    }
}
