package org.drools.cdi;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class MessageProducers {

    @Produces @Msg1 
    public String getSimple1() {
        return "msg.1";
    }
    
    @Produces @Msg2 
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
