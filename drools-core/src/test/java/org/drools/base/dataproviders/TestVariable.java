package org.drools.base.dataproviders;

import java.util.ArrayList;
import java.util.List;

public class TestVariable {
    
    
    public String helloWorld(String a1, int a2, String a3) {
        return a1 + a2 + a3;
    }
    
    public List otherMethod() {
        List list = new ArrayList();
        list.add( "boo" );
        return list;
    }
    
    public String helloWorld() {
        return "another one";
    }

}
