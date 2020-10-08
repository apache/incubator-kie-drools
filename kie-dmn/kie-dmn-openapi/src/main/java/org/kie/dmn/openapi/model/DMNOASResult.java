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

package org.kie.dmn.openapi.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.openapi.NamingPolicy;

public class DMNOASResult {

    public final ObjectNode jsonSchemaNode;
    public final List<DMNModelIOSets> ioSets;
    public final Map<DMNType, Schema> schemas;
    public final NamingPolicy namingPolicy;

    public DMNOASResult(ObjectNode jsonSchemaNode, List<DMNModelIOSets> ioSets, Map<DMNType, Schema> schemas, NamingPolicy namingPolicy) {
        this.jsonSchemaNode = jsonSchemaNode.deepCopy();
        this.ioSets = Collections.unmodifiableList(ioSets);
        this.schemas = Collections.unmodifiableMap(schemas);
        this.namingPolicy = namingPolicy;
    }

    public DMNModelIOSets lookupIOSetsByModel(DMNModel model) {
        return ioSets.stream().filter(ioset -> ioset.getModel().equals(model)).findFirst().orElseThrow(IllegalArgumentException::new);
    }

}
