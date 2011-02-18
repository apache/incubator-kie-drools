package org.drools.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.rule.Package;

/**
 * A Registry of DialectConfigurations. It is also responsible for issueing actions to all registered
 * dialects.
 * This Class api is subject to change.
 *
 */
public class DialectCompiletimeRegistry {
    private Package              pkg;

    
    private Map<String, Dialect> map;

    public DialectCompiletimeRegistry(Package pkg) {
        this.pkg = pkg;
        this.map = new HashMap<String, Dialect>();
    }
    
    /**
     * Add a DialectConfiguration to the registry
     * @param name
     * @param dialect
     */
    public void addDialect(final String name,
                           final Dialect dialect) {
        this.map.put( name,
                      dialect );
    }

    /**
     * Get a DialectConfiguration for a named dialect
     * @param name
     * @return
     */
    public Dialect getDialect(final String name) {
        return (Dialect) this.map.get( name );
    }

    /**
     * Instruct all registered dialects to compile what ever they have attempted to build.
     *
     */
    public void compileAll() {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            Dialect dialect = (Dialect) it.next();
            dialect.compileAll();
        }
    }

    /**
     * Return an Iterator of DialectConfigurations
     * @return
     */
    public Iterator iterator() {
        return this.map.values().iterator();
    }

    /**
     * Add all registered Dialect results to the provided List.
     * @param list
     * @return
     */
    public List addResults(List list) {
        if ( list == null ) {
            list = new ArrayList();
        }
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            Dialect dialect = (Dialect) it.next();
            List results = dialect.getResults();
            if ( results != null ) {
                list.addAll( results );
            }
        }
        return list;
    }

    /**
     * Iterates all registered dialects, informing them of an import added to the PackageBuilder
     * @param importEntry
     */
    public void addImport(String importEntry) {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            Dialect dialect = (Dialect) it.next();
            dialect.addImport( importEntry );
        }
    }

    /**
     * Iterates all registered dialects, informing them of a static imports added to the PackageBuilder
     * @param staticImportEntry
     */
    public void addStaticImport(String staticImportEntry) {
        for ( Iterator it = this.map.values().iterator(); it.hasNext(); ) {
            Dialect dialect = (Dialect) it.next();
            dialect.addStaticImport( staticImportEntry );
        }
    }
    
}
