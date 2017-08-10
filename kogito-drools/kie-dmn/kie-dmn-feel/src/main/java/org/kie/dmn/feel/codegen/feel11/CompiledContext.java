package org.kie.dmn.feel.codegen.feel11;


public interface CompiledContext {

    void set(String name, Object value);
    
    Object get(String name);
    
    Object accept( CompiledFEELExpression expression );
}
