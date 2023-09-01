package org.kie.internal.command;

import java.util.function.Function;

import org.kie.api.runtime.Context;

public interface RegistryContext extends Context {

    <T> RegistryContext register(Class<T> clazz, T instance);

    <T> T lookup(Class<T> clazz);

    default <T> T computeIfAbsent(Class<T> clazz, Function<? super Class<T>, ? extends  T> mappingFunction) {
        T element = lookup(clazz);
        if (element == null) {
            element = mappingFunction.apply(clazz);
            register(clazz, element);
        }
        return element;
    }

    ContextManager getContextManager();

    static RegistryContext create() {
        try {
            return (RegistryContext) Class.forName( "org.drools.commands.impl.ContextImpl" ).getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance RegistryContext, please add org.drools:drools-commands to your classpath", e);
        }
    }
}
