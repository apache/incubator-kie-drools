package org.mvel.integration.impl;

import java.util.Map;

public class LocalVariableResolverFactory extends MapVariableResolverFactory {
    public LocalVariableResolverFactory(Map<String, Object> variables) {
        super(variables);
    }
}
