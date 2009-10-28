/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.vsm.rio;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.drools.vsm.GenericIoWriter;
import org.drools.vsm.Message;

/**
 *
 * @author salaboy
 */
public interface SessionService extends GenericIoWriter {

    public Message rioWrite(Message msg) throws RemoteException;    
    
}
