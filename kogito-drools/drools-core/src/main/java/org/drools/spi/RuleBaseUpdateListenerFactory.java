package org.drools.spi;

import java.util.HashMap;
import java.util.Map;

import org.drools.StatefulSession;

public class RuleBaseUpdateListenerFactory {
    private Map classes = new HashMap();
    
    public RuleBaseUpdateListener createListener(String className, StatefulSession session) {       
        Class cls = (Class) classes.get( className );
        if ( cls == null ) {
            try {
                cls = Class.forName( className );
                this.classes.put(  className, cls );
            } catch ( Throwable e ) {
                throw new RuntimeException("Unable to load RuleBaseUpdateListener '" + className + "'", e );
            }            
        }
        
        RuleBaseUpdateListener listener;
        try {
            listener = ( RuleBaseUpdateListener ) cls.newInstance();
            listener.setSession( session );
            
            return listener;            
        } catch ( Throwable e ) {
            throw new RuntimeException("Unable to instantiate RuleBaseUpdateListener '" + className + "'", e );
        }        
    }
}
