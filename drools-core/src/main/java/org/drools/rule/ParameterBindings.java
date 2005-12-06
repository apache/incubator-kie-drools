package org.drools.rule;

public class ParameterBindings
{
    private Declaration[] parameters;
    
    public ParameterBindings(Declaration[] parameters)
    {
        this.parameters = parameters;
    }
    
    public Declaration getDecleration(int index)
    {
        return parameters[index];
    }
}
