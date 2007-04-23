package org.drools.jsr94.rules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class IteratorToList {
    public static List convert(Iterator it) {
        List list = new ArrayList();
        while ( it.hasNext() ) {
            list.add( it.next() );
        }
        return list;
    }    
}
