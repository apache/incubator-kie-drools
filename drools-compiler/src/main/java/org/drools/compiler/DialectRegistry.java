package org.drools.compiler;

import java.util.HashMap;
import java.util.Map;

import org.drools.rule.builder.Dialect;

public class DialectRegistry {
    private Map map;

    public DialectRegistry() {
        this.map = new HashMap();
    }

    public void addDialect(final String name,
                           final Dialect dialect) {
        this.map.put( name,
                      dialect );
    }

    public Dialect getDialect(final String name) {
        return (Dialect) this.map.get( name );
    }

}
