package org.drools.core.fluent.impl;

import org.drools.core.command.*;
import org.kie.api.command.Command;
import org.kie.internal.fluent.ContextFluent;
import org.kie.internal.fluent.Scope;

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
    public T joinConversation(long id) {
        fluentCtx.addCommand(new JoinConversationCommand(id));
        return (T) this;
    }

    @Override
    public T leaveConversation() {
        fluentCtx.addCommand(new LeaveConversationCommand());
        return (T) this;
    }

    @Override
    public T endConversation(long id) {
        fluentCtx.addCommand(new EndConversationCommand(id));
        return (T) this;
    }

    @Override
    public E end() {
        return (E) fluentCtx.getFluentBuilder();
    }

}
