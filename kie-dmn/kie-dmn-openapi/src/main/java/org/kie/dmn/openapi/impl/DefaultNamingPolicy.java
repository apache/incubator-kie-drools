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
package org.kie.dmn.openapi.impl;

import java.net.URI;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.openapi.NamingPolicy;

public class DefaultNamingPolicy implements NamingPolicy {

    private final String refPrefix;

    public DefaultNamingPolicy(String refPrefix) {
        this.refPrefix = refPrefix;
    }

    @Override
    public String getName(DMNType type) {
        String name = type.getName();
        DMNType belongingType = ((BaseDMNTypeImpl) type).getBelongingType(); // internals for anonymous inner types.
        while (belongingType != null) {
            name = belongingType.getName() + "_" + name;
            belongingType = ((BaseDMNTypeImpl) belongingType).getBelongingType();
        }
        name = CodegenStringUtil.escapeIdentifier(name);
        return name;
    }

    @Override
    public String getRef(DMNType type) {
        String namePart;
        try {
            URI uri = new URI(null, null, getName(type), null);
            namePart = uri.toString();
        } catch (Exception e) {
            namePart = type.getName();
        }
        return refPrefix + namePart;
    }
}
