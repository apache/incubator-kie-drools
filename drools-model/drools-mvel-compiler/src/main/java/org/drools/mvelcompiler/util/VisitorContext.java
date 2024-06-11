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

package org.drools.mvelcompiler.util;

import org.drools.mvelcompiler.ast.TypedExpression;

import java.lang.reflect.Type;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class VisitorContext {
    private final Optional<TypedExpression> scope;

    public VisitorContext(final TypedExpression scope) {
        this.scope = ofNullable(scope);
    }

    public Optional<Type> getScopeType() {
        return scope.flatMap(TypedExpression::getType);
    }

    public Optional<TypedExpression> getScope() {
        return scope;
    }
}
