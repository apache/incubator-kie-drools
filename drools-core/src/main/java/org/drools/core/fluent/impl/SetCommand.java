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

public class SetCommand<T> implements ExecutableCommand<T> {
    private String name;
    private Scope scope = Scope.REQUEST;

    public SetCommand(String name) {
        this.name = name;
    }

    public SetCommand(String name, Scope scope) {
        this.name = name;
        this.scope = scope;
    }

    @Override
    public T execute(Context context) {
        RequestContextImpl reqContext = (RequestContextImpl)context;
        T returned = (T) reqContext.getResult();

        if ( scope == Scope.REQUEST ) {
            reqContext.set(name, returned);
        } else if ( scope == Scope.CONVERSATION ) {
            if ( reqContext.getConversationContext() == null ) {
                throw new IllegalStateException("No Conversation Context Exists");
            }
            reqContext.getConversationContext().set(name, returned);
        } else  if ( scope == Scope.APPLICATION ) {
            if ( reqContext.getApplicationContext() == null ) {
                throw new IllegalStateException("No Application Context Exists");
            }
            reqContext.getApplicationContext().set(name, returned);
        }

        ((RequestContextImpl)context).setLastSetOrGet(name);
        return returned;
    }

    @Override
    public String toString() {
        return "SetCommand{" +
               "name='" + name + '\'' +
               ", scope=" + scope +
               '}';
    }
}
