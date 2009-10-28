package org.drools.vsm.rio;


import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.vsm.GenericIoWriter;
import org.drools.vsm.Message;

public class RioIoWriter
    implements
    GenericIoWriter {
    private SessionService sessionService;

    public RioIoWriter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void write(Message message) {
        try {
            this.sessionService.write(message);
        } catch (RemoteException ex) {
            Logger.getLogger(RioIoWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
