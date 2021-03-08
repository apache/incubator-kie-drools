/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.trusty.service.common.messaging.incoming;

import java.util.Objects;

/**
 * Makes an identifier for a Model. The format is "name:namespace".
 */
public class ModelIdentifier {

    //TODO GAV components are provided but unused. See https://issues.redhat.com/browse/FAI-239
    private static final String IDENTIFIER_TEMPLATE = "%s:%s";

    private String identifier;

    public ModelIdentifier(final String groupId,
            final String artifactId,
            final String version,
            final String name,
            final String namespace) {
        this.identifier = makeIdentifier(groupId, artifactId, version, name, namespace);
    }

    public String getIdentifier() {
        return identifier;
    }

    private String makeIdentifier(final String groupId,
            final String artifactId,
            final String version,
            final String name,
            final String namespace) {
        return String.format(IDENTIFIER_TEMPLATE,
                nullable(name),
                nullable(namespace));
    }

    private String nullable(final String value) {
        return Objects.isNull(value) ? "" : value;
    }
}
