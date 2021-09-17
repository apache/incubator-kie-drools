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
package org.kie.kogito.incubation.predictions;

import org.kie.kogito.incubation.common.Id;
import org.kie.kogito.incubation.common.LocalId;
import org.kie.kogito.incubation.common.LocalUri;
import org.kie.kogito.incubation.common.LocalUriId;

public class LocalPredictionId extends LocalUriId implements Id {
    public static final String PREFIX = "predictions";

    private final String name;

    public LocalPredictionId(String name) {
        super(LocalUri.Root.append(PREFIX).append(name));
        this.name = name;
    }

    public String name() {
        return name;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

}
