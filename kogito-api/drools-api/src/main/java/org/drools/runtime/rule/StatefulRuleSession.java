package org.drools.runtime.rule;

import java.util.concurrent.Future;


public interface StatefulRuleSession extends WorkingMemory {      

    Future<Object> asyncInsert( Object object );    
    
    Future<Object> asyncInsert( Object[] array );
    
    Future<Object> asyncInsert( Iterable<?> iterable );
    
    void fireAllRules();
    
    Future<?> asyncFireAllRules();    
      
}
