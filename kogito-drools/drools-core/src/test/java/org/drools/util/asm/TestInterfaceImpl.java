package org.drools.util.asm;

public class TestInterfaceImpl
    implements
    TestInterface {

    public String getSomething() {
        return "foo";
    }

    public int getAnother() {
        return 42;
    }

}
