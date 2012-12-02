package org.drools.cdi.example;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class MessageProducers {

    @Produces @Msg1 
    public String getSimple1() {
        return "msg.1";
    }
    
    @Msg2 @Produces 
    public String getSimple2() {
        return "msg.2";
    }
    
    @Produces @Msg("named1") 
    public String getNamed1() {
        return "msg.named1";
    }
    
    @Produces @Msg("named2") 
    public String getNamed2() {
        return "msg.named2";
    }   
         
}
