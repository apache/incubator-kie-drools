package org.drools.core.marshalling;

import java.util.HashMap;
import java.util.Map;

import org.drools.util.ClassUtils;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;

public class ClassObjectMarshallingStrategyAcceptor implements ObjectMarshallingStrategyAcceptor {
    public static final ClassObjectMarshallingStrategyAcceptor DEFAULT = new ClassObjectMarshallingStrategyAcceptor(new String[] { "*.*" } );
    
    private final Map<String, Object> patterns;
    
    public ClassObjectMarshallingStrategyAcceptor(String[] patterns) {
        this.patterns = new HashMap<>();
        for (String pattern : patterns ) {
            addPattern( pattern );
        }
    }
    
    public ClassObjectMarshallingStrategyAcceptor() {
        this.patterns = new HashMap<>();
    }
    
    private void addPattern(String pattern) {
        
        ClassUtils.addImportStylePatterns( this.patterns, pattern );
    }

    public boolean accept(Object object) {
        return ClassUtils.isMatched( this.patterns, object.getClass().getName() );
    }

    @Override
    public String toString() {
        return "ClassObjectMarshallingStrategyAcceptor for " + patterns.keySet();
    }
}
