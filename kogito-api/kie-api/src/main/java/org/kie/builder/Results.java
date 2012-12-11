package org.kie.builder;

import java.util.List;

import org.kie.builder.Message.Level;

public interface Results {
    
    boolean hasMessages(Level... levels);
    
    List<Message>  getMessages(Level... levels);  
    
    List<Message>  getMessages();  
}
