package org.drools.compiler.factmodel.traits;

import org.drools.core.factmodel.traits.Entity;
import org.drools.core.factmodel.traits.Traitable;

@Traitable
public class SomeClass extends Entity {
    private String pre;
    public void prepare() {
        pre = "I did ";
    }
    public int getFoo() {
        return 42;
    }
    public String doThis( String arg ) {
        return pre + arg;
    }
}