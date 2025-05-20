/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.quarkus.deployment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.kie.api.io.ResourceType;

public class DroolsCompilationProvider extends AbstractCompilationProvider {

    private static final Set<String> MANAGED_EXTENSIONS = initExtensions();

    private static Set<String> initExtensions() {
        Set<String> extensions = new HashSet<>();
        extensions.addAll(ResourceType.DRL.getAllExtensions());
        extensions.addAll(ResourceType.DTABLE.getAllExtensions());
        extensions.addAll(ResourceType.YAML.getAllExtensions());
        return Collections.unmodifiableSet(extensions);
    }

    @Override
    public Set<String> handledExtensions() {
        return MANAGED_EXTENSIONS;
    }
}
