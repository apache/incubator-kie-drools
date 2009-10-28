/**
 * 
 */
package org.drools.vsm;


public class BlockingGenericIoWriter implements GenericIoWriter {
    
    private Message msg;

    public void write(Message message) {
        this.msg = message;
    }
    
    public Message getMessage() {
        return this.msg;
    }
    
}