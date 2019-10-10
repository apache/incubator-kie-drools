/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.canonical;

import java.text.MessageFormat;
import java.util.ArrayList;

import org.drools.core.util.StringUtils;

public class NodeValidator {

    public static NodeValidator of(String nodeType, String nodeId) {
        return new NodeValidator(nodeType, nodeId);
    }

    private final String nodeType;
    private final String nodeId;
    private final ArrayList<String> errors = new ArrayList<>();

    private NodeValidator(String nodeType, String nodeId) {
        this.nodeType = nodeType;
        this.nodeId = nodeId;
    }

    public NodeValidator notEmpty(String name, String value) {
        if (StringUtils.isEmpty(value)) {
            this.errors.add(MessageFormat.format("{0} should not be empty", name));
        }
        return this;
    }

    public void validate() {
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(
                    MessageFormat.format("Invalid parameters for {0} \"{1}\": {2}",
                                         nodeType,
                                         nodeId,
                                         String.join(", ", errors)));
        }
    }
}
