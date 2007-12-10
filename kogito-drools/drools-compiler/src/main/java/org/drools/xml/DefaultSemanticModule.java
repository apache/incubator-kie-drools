package org.drools.xml;

import java.util.HashMap;
import java.util.Map;

public class DefaultSemanticModule implements SemanticModule {
    public String uri;
    public Map<String, Handler> handlers;
    
    public DefaultSemanticModule(String uri) {
        this.uri = uri;
        this.handlers = new HashMap<String, Handler>();
    }    

    public String getUri() {
        return this.uri;
    }
    
    public void addHandler(String name, Handler handler) {
        this.handlers.put( name, handler );
    }

    public Handler getHandler(String name) {
        return this.handlers.get( name );
    }
            
}
