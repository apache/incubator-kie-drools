package org.drools.compiler;

import java.util.HashMap;
import java.util.Iterator;
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

    public void addImport(String importEntry) {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            Dialect dialect = ( Dialect ) it.next();
            dialect.addImport( importEntry );
        }
    }
    
    public void addStaticImport(String staticImportEntry) {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            Dialect dialect = ( Dialect ) it.next();
            dialect.addStaticImport( staticImportEntry );
        }        
    }
    
}
