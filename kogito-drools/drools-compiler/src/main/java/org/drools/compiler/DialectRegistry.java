package org.drools.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
    
    public void compileAll() {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            Dialect dialect = ( Dialect ) it.next();
            dialect.compileAll();
        }
    }
    
    public List addResults(List list) {
        if ( list == null ) {
            list = new ArrayList();
        }
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            Dialect dialect = ( Dialect ) it.next();
            list.addAll( dialect.getResults() );
        }        
        return list;
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
