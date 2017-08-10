package org.kie.dmn.feel.codegen.feel11;

import java.util.HashMap;
import java.util.Map;

public class CompiledContextImpl implements CompiledContext {

    private Map<String, Object> store = new HashMap<>();
    
    @Override
    public void set(String name, Object value) {
        store.put(name, value);
    }

    @Override
    public Object get(String name) {
        return store.get(name);
    }

    @Override
    public Object accept(CompiledFEELExpression expression) {
        return expression.apply(this);
    }
}
