package org.drools.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DialectRegistry {
    private Map map;

    public DialectRegistry() {
        this.map = new HashMap();
    }

    public void addDialectConfiguration(final String name,
                           final DialectConfiguration dialect) {
        this.map.put( name,
                      dialect );
    }

    public DialectConfiguration getDialectConfiguration(final String name) {
        return (DialectConfiguration) this.map.get( name );
    }
    
    public void initAll(PackageBuilder builder) {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            DialectConfiguration dialect = ( DialectConfiguration ) it.next();
            dialect.getDialect().init( builder );
        }        
    }
    
    public void compileAll() {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            DialectConfiguration dialect = ( DialectConfiguration ) it.next();
            dialect.getDialect().compileAll();
        }
    }
    
    public Iterator iterator() {
        return this.map.values().iterator();
    }
    
    public List addResults(List list) {
        if ( list == null ) {
            list = new ArrayList();
        }
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            DialectConfiguration dialect = ( DialectConfiguration ) it.next();
            List results = dialect.getDialect().getResults();
            if ( results != null ) {
                list.addAll( results );
            }
        }        
        return list;
    }

    public void addImport(String importEntry) {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            DialectConfiguration dialect = ( DialectConfiguration ) it.next();
            dialect.getDialect().addImport( importEntry );
        }
    }
    
    public void addStaticImport(String staticImportEntry) {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            DialectConfiguration dialect = ( DialectConfiguration ) it.next();
            dialect.getDialect().addStaticImport( staticImportEntry );
        }        
    }
    
}
