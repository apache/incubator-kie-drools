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
package org.kie.pmml.api.identifiers;

import java.util.HashMap;
import java.util.Map;

import org.kie.efesto.common.api.identifiers.AppRoot;
import org.kie.efesto.common.api.identifiers.ComponentRoot;

public class KiePmmlAppRoot extends AppRoot {

    private static final String PREFIX = LocalComponentIdPmml.PREFIX + "-app";
    private static final Map<Class<? extends ComponentRoot>, ComponentRoot> INSTANCES;

    static {
        INSTANCES = new HashMap<>();
        INSTANCES.put(PmmlIdFactory.class, new PmmlIdFactory());
        INSTANCES.put(PmmlIdRedirectFactory.class, new PmmlIdRedirectFactory());
    }

    public KiePmmlAppRoot() {
        super(PREFIX);
    }

    @Override
    public <T extends ComponentRoot> T get(Class<T> providerId) {
        return (T) INSTANCES.get(providerId);
    }
}
