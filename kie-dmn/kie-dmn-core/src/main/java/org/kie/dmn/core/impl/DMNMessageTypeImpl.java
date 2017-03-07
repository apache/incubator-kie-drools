package org.kie.dmn.core.impl;


public class DMNMessageTypeImpl {

    private final String message;
    private final DMNMessageTypeId id;
    
    public DMNMessageTypeImpl(String message, DMNMessageTypeId id) {
        super();
        this.message = message;
        this.id = id;
    }
    
    public String getMessage() {
        return message;
    }
    
    public DMNMessageTypeId getMessageTypeId() {
        return id;
    }
    
    
}
