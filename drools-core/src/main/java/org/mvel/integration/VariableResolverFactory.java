package org.mvel.integration;

public interface VariableResolverFactory {
    /**
     * Creates a new variable.  This probably doesn't need to be implemented in most scenarios.  This is
     * used for variable assignment.
     * @param name - name of the variable being created
     * @param value - value of the variable
     * @return instance of the variable resolver associated with the variable
     */
    public VariableResolver createVariable(String name, Object value);

    /**
     * Returns the next factory in the factory chain.  MVEL uses a hierarchical variable resolution strategy,
     * much in the same way as Classloaders in Java.   For performance reasons, it is the responsibility of
     * the individual VariableResolverFactory to pass off to the next one.
     * @return instance of the next factory - null if none.
     */
    public VariableResolverFactory getNextFactory();

    /**
     * Sets the next factory in the chain. Proper implementation:
     * <code>
     *
     *      return this.nextFactory = resolverFactory;
     * </code>
     * @param resolverFactory - instance of next resolver factory
     * @return - instance of next resolver factory
     */
    public VariableResolverFactory setNextFactory(VariableResolverFactory resolverFactory);

    /**
     * Return a variable resolver for the specified variable name.  This method is expected to traverse the
     * heirarchy of ResolverFactories.
     * @param name - variable name
     * @return - instance of the VariableResolver for the specified variable
     */
    public VariableResolver getVariableResolver(String name);


    /**
     * Deterimines whether or not the current VariableResolverFactory is the physical target for the actual
     * variable.
     * @param name - variable name
     * @return - boolean indicating whether or not factory is the physical target
     */
    public boolean isTarget(String name);


    /**
     * Determines whether or not the variable is resolver in the chain of factories.
     * @param name - variable name
     * @return - boolean
     */
    public boolean isResolveable(String name);
}
