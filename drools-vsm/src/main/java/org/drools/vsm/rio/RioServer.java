package org.drools.vsm.rio;


import java.io.File;
import java.io.IOException;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.vsm.AcceptorService;
import org.rioproject.cybernode.StaticCybernode;

public class RioServer
    implements
    AcceptorService {
    private SessionService sessionService;
    private String opstring = "src/test/resources/org/drools/sessionService.groovy";

    public RioServer() {
       
    }

    public synchronized void start() throws IOException {
        try {
            StaticCybernode cybernode = new StaticCybernode();
            Map<String, Object> map = cybernode.activate(new File(opstring));
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String beanName = entry.getKey();
                Object beanImpl = entry.getValue();
                if (beanName.equals("SessionService")) {
                    sessionService = (SessionService) beanImpl;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(RioServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public synchronized void stop() {
        //do nothing
    }
    public SessionService getSessionService(){
        return this.sessionService;
    }
    

}