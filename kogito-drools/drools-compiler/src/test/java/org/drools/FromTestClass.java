package org.drools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FromTestClass {
    public List toList(final Object object1,
                       final Object object2,
                       final String object3,
                       final int integer,
                       final Map map,
                       final List inputList) {
        final List list = new ArrayList();
        list.add( object1 );
        list.add( object2 );
        list.add( object3 );
        list.add( new Integer( integer ) );
        list.add( map );
        list.add( inputList );
        return list;
    }
}
