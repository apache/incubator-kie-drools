package org.kie.ci.test.dep;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }

    public String someCustomMethod() {
        return "This is a custom method!";
    }

    @Override
    public String toString() {
        return "Main{}";
    }
}