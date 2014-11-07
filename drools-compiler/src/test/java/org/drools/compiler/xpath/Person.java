package org.drools.compiler.xpath;

import org.drools.core.phreak.ReactiveObject;

public abstract class Person extends ReactiveObject {

    private final String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        notifyModification();
    }

    @Override
    public String toString() {
        return name;
    }
}
