/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import java.io.Serializable;
import java.util.UUID;
import javax.enterprise.inject.spi.PassivationCapable;

/**
 *
 * @author salaboy
 */
public class MyBean implements PassivationCapable, Serializable {

    public MyBean() {
    }

    public String getId() {
        return "MyBean-"+UUID.randomUUID().toString();
    }
    
    
    
    public String doSomething(String text){
        System.out.println("Doing Something with: "+text);
        return text + " processed!";
    }
}
