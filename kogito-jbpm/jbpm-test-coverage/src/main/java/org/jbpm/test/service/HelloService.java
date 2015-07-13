package org.jbpm.test.service;

public class HelloService {

    public void sayHi() {
        System.out.println("Hi");
    }
    
    public void exception(Object obj) {
        throw new RuntimeException("Error");
    }
    
}
