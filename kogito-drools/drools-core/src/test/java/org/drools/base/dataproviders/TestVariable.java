package org.drools.base.dataproviders;

import java.util.ArrayList;
import java.util.List;

public class TestVariable {

    public String helloWorld(final String a1,
                             final int a2,
                             final String a3) {
        return a1 + a2 + a3;
    }

    public List otherMethod() {
        final List list = new ArrayList();
        list.add( "boo" );
        return list;
    }

    public String helloWorld() {
        return "another one";
    }

}
