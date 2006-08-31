package org.drools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FromTestClass {
    public List toList(Object object1, Object object2, String object3, int integer, Map map) {
        List list = new ArrayList();
        list.add( object1 );
        list.add( object2 );
        list.add( object3 );
        list.add( new Integer( integer ) );
        list.add( map );
        return list;
    }
}
