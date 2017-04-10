/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang.types;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;

public class BuiltInTypeSymbol
        extends BaseSymbol
        implements Type {

    public BuiltInTypeSymbol(String id, Type type) {
        super( id, type );
    }

    public BuiltInTypeSymbol(String id, Type type, Scope scope) {
        super( id, type, scope );
    }

    public String getName() {
        return getId();
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return getType().isInstanceOf(o);
    }

}
