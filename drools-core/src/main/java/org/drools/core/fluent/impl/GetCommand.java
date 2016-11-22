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

package org.drools.core.fluent.impl;

import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.impl.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.fluent.Scope;

public class GetCommand<T> implements ExecutableCommand<T> {
    private String name;
    private Scope scope;

    public GetCommand(String name) {
        this.name = name;
    }

    public  GetCommand(String name, Scope scope) {
        this.name = name;
        this.scope = scope;
    }

    @Override
    public T execute(Context context) {
        RequestContextImpl reqContext = (RequestContextImpl)context;

        T object = null;
        if ( reqContext.has(name)) {
            object = (T) reqContext.get(name);
            reqContext.setLastSetOrGet(name);
        }

        return object;
    }

    @Override
    public String toString() {
        return "SetCommand{" +
               "name='" + name + '\'' +
               ", scope=" + scope +
               '}';
    }
}
