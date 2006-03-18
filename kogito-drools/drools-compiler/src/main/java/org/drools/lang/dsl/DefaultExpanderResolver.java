package org.drools.lang.dsl;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.drools.lang.Expander;
import org.drools.lang.ExpanderResolver;

/**
 * The default expander resolver will provide instances of the DefaultExpander.
 * The DefaultExpander uses templates to provide DSL and pseudo
 * natural language support.
 * 
 * @author Michael Neale
 */
public class DefaultExpanderResolver
    implements
    ExpanderResolver {

    private final Map expanders = new HashMap();
    
    /**
     * Create an empty resolver, which you will then
     * call addExpander multiple times, to map a specific expander
     * with a name that will be found in the drl after the expander keyword.
     */
    public DefaultExpanderResolver() {
    }
    
    /**
     * This will load up a DSL from the reader specified.
     * This will make the expander available to any parser 
     * regardless of name.
     * 
     * The DSL expander will be the default expander.
     * 
     * This is the constructor most people should use.
     */
    public DefaultExpanderResolver(Reader reader) {
        DefaultExpander expander = new DefaultExpander(reader);
        expanders.put( "*", expander );
    }
    
    /**
     * Add an expander with the given name, which will be used
     * by looking for the "expander" keyword in the DRL.
     * 
     * If a default expander is installed, it will always be returned
     * if none matching the given name can be found.
     * 
     * You don't need to use this unless you have multiple expanders/DSLs
     * involved when compiling multiple rule packages at the same time.
     * If you don't know what that sentence means, you probably don't need to use this method.
     */
    public void addExpander(String name, Expander expander) {
        this.expanders.put( name, expander );
    }
    
    public Expander get(String name,
                        String config) {
        if (expanders.containsKey( name )) {
            return (Expander) expanders.get( name );
        } else {
            Expander exp = (Expander) expanders.get( "*" );
            if (exp == null) 
                throw new IllegalArgumentException("Unable to provide an expander for " + name + " or a default expander.");
            return exp;
        }
    }

}
