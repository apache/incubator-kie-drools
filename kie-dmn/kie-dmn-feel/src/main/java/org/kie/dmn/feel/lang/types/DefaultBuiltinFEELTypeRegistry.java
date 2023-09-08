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
package org.kie.dmn.feel.lang.types;

import java.util.List;
import java.util.stream.Stream;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;

public enum DefaultBuiltinFEELTypeRegistry implements FEELTypeRegistry {
    INSTANCE;

    private static final ScopeImpl BUILTIN_TYPE_SCOPE;
    static {
        BUILTIN_TYPE_SCOPE = new ScopeImpl("typeScope", null); // null intentional 
        Stream.of(BuiltInType.values()).flatMap(b -> b.getSymbols().stream()).forEach(t -> BUILTIN_TYPE_SCOPE.define(t));
    }

    @Override
    public Type resolveFEELType(List<String> qns) {
        if (qns.size() == 1) {
            return BUILTIN_TYPE_SCOPE.resolve(qns.get(0)).getType();
        } else {
            throw new IllegalStateException("Inconsistent state when resolving for qns: " + qns.toString());
        }
    }

    @Override
    public Scope getItemDefScope(Scope parent) {
        return new WrappingScopeImpl(BUILTIN_TYPE_SCOPE, parent);
    }
}
