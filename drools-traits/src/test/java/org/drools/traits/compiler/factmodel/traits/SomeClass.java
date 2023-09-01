package org.drools.traits.compiler.factmodel.traits;

import org.drools.traits.core.factmodel.Entity;
import org.drools.base.factmodel.traits.Traitable;

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