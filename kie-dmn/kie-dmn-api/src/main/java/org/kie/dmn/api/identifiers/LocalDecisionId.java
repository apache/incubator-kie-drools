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
package org.kie.dmn.api.identifiers;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class LocalDecisionId extends LocalUriId implements Id {
    public static final String PREFIX = "decisions";

    private final String namespace;
    private final String name;

    public LocalDecisionId(String namespace, String name) {
        super(makeLocalUri(namespace, name));
        this.namespace = namespace;
        this.name = name;
    }

    public String namespace() {
        return namespace;
    }

    public String name() {
        return name;
    }

    public DecisionServiceIds services() {
        return new DecisionServiceIds(this);
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    private static LocalUri makeLocalUri(String namespace, String name) {
        String fullId = String.format("%s#%s", namespace, name);
        return LocalUri.Root.append(PREFIX).append(fullId);
    }

}
