package org.drools.traits.compiler.factmodel.traits;

public class SomethingImpl<K> implements IDoSomething<K> {



    private ISomethingWithBehaviour<K> arg;

    public SomethingImpl( ISomethingWithBehaviour<K> arg ) {
        this.arg = arg;
    }


    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String doSomething( int x ) {
        return "" + (arg.getAge() + x);
    }

    public void doAnotherTask() {
        System.out.println("X");
    }
}
