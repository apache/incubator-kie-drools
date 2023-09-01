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
package org.drools.commands.fluent;

import org.drools.commands.EndConversationCommand;
import org.drools.commands.JoinConversationCommand;
import org.drools.commands.OutCommand;
import org.drools.commands.StartConversationCommand;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Executable;
import org.kie.internal.builder.fluent.ContextFluent;
import org.kie.internal.builder.fluent.Scope;

public class BaseBatchFluent<T, E> implements ContextFluent<T, E> {

    protected ExecutableImpl fluentCtx;

    public BaseBatchFluent(Executable fluentCtx) {
        this.fluentCtx = (ExecutableImpl) fluentCtx;
    }

    public ExecutableImpl getFluentContext() {
        return fluentCtx;
    }

    @Override
    public T addCommand(ExecutableCommand command) {
        fluentCtx.addCommand(command);
        return (T) this;
    }

    public T after(long distance) {
        fluentCtx.addBatch(new BatchImpl(distance));
        return (T) this;
    }

    public T relativeAfter(long duration) {
        return (T) this;
    }

    @Override
    public T out() {
        fluentCtx.addCommand(new OutCommand<Object>());
        return (T) this;
    }

    @Override
    public T out(String name) {
        fluentCtx.addCommand(new OutCommand<Object>(name));
        return (T) this;
    }

    @Override
    public T set(String name, Scope scope) {
        fluentCtx.addCommand(new SetCommand<Object>(name, scope));
        return (T) this;
    }

    @Override
    public T set(String name) {
        fluentCtx.addCommand(new SetCommand<Object>(name));
        return (T) this;
    }

    @Override
    public T get(String name) {
        fluentCtx.addCommand(new GetCommand(name));
        return (T) this;
    }

    @Override
    public T get(String name, Scope scope) {
        fluentCtx.addCommand(new GetCommand(name, scope));
        return (T) this;
    }

    @Override
    public <K> K get(String name, Class<K> cls) {
        String fluentTarget = getFluentContext().getFactory().getFluentTarget(cls.getName());
        addCommand(new SetVarAsRegistryEntry(fluentTarget, name));

        K object;
        try {
            // @TODO We really should use a component factory for these, but for now use impl lookup
            Class imlpCls = getFluentContext().getFactory().getImplClass(cls.getName());
            object = (K) imlpCls.getDeclaredConstructor(ExecutableImpl.class).newInstance(getFluentContext());
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate fluent " + cls.getName(), e);
        }

        return object;
    }

    @Override
    public T newApplicationContext(String name) {
        addCommand(new NewContextCommand(name));
        return (T) this;
    }

    @Override
    public T getApplicationContext(String name) {
        addCommand(new GetContextCommand(name));
        return (T) this;
    }

    @Override
    public T startConversation() {
        fluentCtx.addCommand(new StartConversationCommand());
        return (T) this;
    }

    @Override
    public T joinConversation(String uuid) {
        fluentCtx.addCommand(new JoinConversationCommand(uuid));
        return (T) this;
    }

    @Override
    public T endConversation(String uuid) {
        fluentCtx.addCommand(new EndConversationCommand(uuid));
        return (T) this;
    }

    @Override
    public E end() {
        return (E) fluentCtx.getExecutableBuilder();
    }
}
