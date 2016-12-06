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

import org.drools.core.command.*;
import org.kie.api.command.Command;
import org.kie.api.runtime.builder.ContextFluent;
import org.kie.api.runtime.builder.Scope;

public class BaseBatchFluent<T, E> implements ContextFluent<T, E> {
    protected ExecutableImpl fluentCtx;

    public BaseBatchFluent(ExecutableImpl fluentCtx) {
        this.fluentCtx = fluentCtx;
    }

    public ExecutableImpl getFluentContext() {
        return fluentCtx;
    }

    public T addCommand(Command command) {
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
        fluentCtx.addCommand( new OutCommand<Object>());
        return (T) this;
    }

    @Override
    public T out(String name) {
        fluentCtx.addCommand( new OutCommand<Object>(name));
        return (T) this;
    }


    @Override
    public T set(String name, Scope scope) {
        fluentCtx.addCommand( new SetCommand<Object>(name, scope));
        return (T) this;
    }

    @Override
    public T set(String name) {
        fluentCtx.addCommand( new SetCommand<Object>(name));
        return (T) this;
    }

    @Override
    public T get(String name) {
        fluentCtx.addCommand( new GetCommand(name));
        return (T) this;
    }

    @Override
    public T get(String name, Scope scope) {
        fluentCtx.addCommand( new GetCommand(name, scope));
        return (T) this;
    }

    @Override
    public <K> K get(String name, Class<K> cls) {
        String fluentTarget = getFluentContext().getFactory().getFluentTarget(cls.getName());
        addCommand(new SetVarAsRegistryEntry(fluentTarget, name));

        K object = null;
        try {
            // @TODO We really should use a component factory for these, but for now use impl lookup
            Class imlpCls = getFluentContext().getFactory().getImplClass(cls.getName());
            object = (K) imlpCls.getDeclaredConstructor(ExecutableImpl.class).newInstance(getFluentContext());
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate fluent " + cls.getName(), e) ;
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
    public T leaveConversation() {
        fluentCtx.addCommand(new LeaveConversationCommand());
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
