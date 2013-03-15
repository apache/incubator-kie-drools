package org.drools.compiler.cdi.example;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class MessageProducers2 {        
    @Inject @Msg1
    private String msg1;
    
    @Produces @Msg("chained1") 
    public String getChained1() {
        return "chained.1 " + msg1;
    }    
    
    @Produces @Msg("chained2") 
    public String getChained2(Message m1, @Msg1 String m2, @Msg("named1") String m3) {
        return "chained.2 " + m1.getText() + " " + m2 + " " + m3;
    }      
}
