package org.drools.marshalling.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.util.ClassUtils;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;

public class ClassObjectMarshallingStrategyAcceptor implements ObjectMarshallingStrategyAcceptor {
    public static final ClassObjectMarshallingStrategyAcceptor DEFAULT = new ClassObjectMarshallingStrategyAcceptor(new String[] { "*.*" } );
    
    private final Map<String, Object> patterns;
    
    public ClassObjectMarshallingStrategyAcceptor(String[] patterns) {
        this.patterns = new HashMap<String, Object>();
        for (String pattern : patterns ) {
            addPattern( pattern );
        }
    }
    
    public ClassObjectMarshallingStrategyAcceptor() {
        this.patterns = new HashMap<String, Object>();
    }
    
    private void addPattern(String pattern) {
        
        ClassUtils.addImportStylePatterns( this.patterns, pattern );
    }

    public boolean accept(Object object) {
        return ClassUtils.isMatched( this.patterns, object.getClass().getName() );
    }

}
