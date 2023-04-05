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

package org.drools.drl.quarkus.util.deployment;

import java.util.Map;
import java.util.Set;

import io.quarkus.builder.item.SimpleBuildItem;

public final class OtnClassesByPackageBuildItem extends SimpleBuildItem {
    private final Map<String, Set<Class<?>>> otnClasses;
    
    public OtnClassesByPackageBuildItem(Map<String, Set<Class<?>>> otnClasses) {
        this.otnClasses = otnClasses;
    }

    public Map<String, Set<Class<?>>> getOtnClasses() {
        return otnClasses;
    }
}
