/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.vsm.rio;

import java.rmi.RemoteException;
import org.drools.vsm.Message;

/**
 *
 * @author salaboy
 */
public interface SessionService {

    public Message write(Message msg) throws RemoteException;
    
    
}
