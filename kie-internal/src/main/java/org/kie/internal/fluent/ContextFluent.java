package org.kie.internal.fluent;

public interface ContextFluent<T, E>{

    /**
     * The last executed command result is set to a name in this executing context. Default Scope is Request
     * @param name
     * @return this
     */
    T set(String name);

    T set(String name, Scope scope);

    T get(String name);

    T get(String name, Scope scope);

    /**
     * This sets an instance, for a given cls key, on the registry for commands to execute against.
     * This method will call "end" if within the context of a given registry command
     * @param name
     * @param cls
     * @param <K>
     * @return
     */
    <K>  K get(String name, Class<K> cls);

    /**
     * The output from the last command should be returned via the out results. It uses the last used name identifer for the previous
     * get or set.
     * @return this
     */
    T out();

    /**
     * The output from the last executed command should be returned and set to the given name in the context. It uses the specified
     * named identifierl
     * @param name
     * @return this
     */
    T out(String name);

    T newApplicationContext(String name);

    T getApplicationContext(String name);

    T startConversation();

    T joinConversation(String uuid);

    T leaveConversation();

    T endConversation(String uuid);

    /**
     * End the scope of the current Command set
     * @return
     */
    E end();
} 
