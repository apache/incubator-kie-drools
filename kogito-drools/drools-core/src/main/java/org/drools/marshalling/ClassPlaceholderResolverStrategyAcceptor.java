package org.drools.marshalling;

import java.util.HashMap;
import java.util.Map;

import org.drools.util.ClassUtils;

public class ClassPlaceholderResolverStrategyAcceptor implements PlaceholderResolverStrategyAcceptor {
    private final Map<String, Object> patterns;
    
    public ClassPlaceholderResolverStrategyAcceptor(String str) {
        this.patterns = new HashMap<String, Object>();
        addPatterns(str);
    }
    
    public ClassPlaceholderResolverStrategyAcceptor() {
        this.patterns = new HashMap<String, Object>();
    }
    
    public void addPatterns(String str) {
        
        ClassUtils.addImportStylePatterns( this.patterns, str );
    }

    public boolean accept(Object object) {
        return ClassUtils.isMatched( this.patterns, object.getClass().getName() );
    }

}
