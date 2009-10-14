/**
 * 
 */
package org.drools.vsm;

public interface MessageResponseHandler {
    public void setError(RuntimeException error);

    public void receive(Message message);
}