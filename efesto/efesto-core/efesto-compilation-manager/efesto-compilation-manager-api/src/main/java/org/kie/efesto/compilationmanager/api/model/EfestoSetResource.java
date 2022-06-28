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
package org.kie.efesto.compilationmanager.api.model;

import java.util.Set;

public abstract class EfestoSetResource<T> implements EfestoResource<Set<T>> {

    private final Set<T> resources;
    private String model;

    private String basePath;

    protected EfestoSetResource(Set<T> resources, String model, String basePath) {
        this.resources = resources;
        this.model = model;
        this.basePath = basePath;
    }

    @Override
    public Set<T> getContent() {
        return resources;
    }

    public String getModelType() {
        return model;
    }

    /**
     * This should return the string used as <b>base path</b> in the generated <code>FRI</code>
     * @return
     */
    public String getBasePath() {
        return basePath;
    }

}
