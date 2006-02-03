package org.drools.util.proxy;

import java.io.PrintWriter;
import java.util.List;



public class Person {
    public final Blah SOME_CONST = new Blah();
    
    private String name;
    
    private int age;

    //private so not a "field"
    private Integer getZoom() {
        return null;
    }
    
    //first field
    public String getName() {
        return name;
    }        
    
    //second field
    public int getAge() {
        return age;
    }
    
    //not a field, as it takes a parameter.. move along...
    public boolean getNothing(int something) {
        return true;
    }
    
    //third field
    public boolean isHappy() {
        return true;
    }
    
    public void setAge(int age) {
        this.age = age;
    }



    public void setName(String name) {
        this.name = name;
    }
    
    public void printNameAndAge(PrintWriter out) {
        out.println(name + age);
    }

    public void doSomethingComplex(List list,
                                   int num, String s, char c, long l, boolean b) {
        
        
    }
    
    public void changeName(String newName) {
        this.name = newName;
    }
    
    public int moreComplex(Blah b) {
        return 42;
    }
    
    static class Blah {
        
    }
}
