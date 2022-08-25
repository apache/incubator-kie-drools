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
package org.kie.drl.api.identifiers;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class LocalComponentIdDrlSession extends LocalUriId implements Id {

    public static final String PREFIX = "drl";
    private final String basePath;
    private final long identifier;

    public LocalComponentIdDrlSession(String basePath, long identifier) {
        super(LocalUri.Root.append(PREFIX).append(basePath).append(String.valueOf(identifier)));
        this.basePath = basePath;
        this.identifier = identifier;
    }

    public String getBasePath() {
        return basePath;
    }

    public long getIdentifier() {
        return identifier;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

}
