package org.drools.xml;

import java.util.HashMap;
import java.util.Map;

public class DefaultSemanticModule implements SemanticModule {
    public String uri;
    public Map<String, Handler> handlers;
    public Map<Class<?>, Handler> handlersByClass;
    
    public DefaultSemanticModule(String uri) {
        this.uri = uri;
        this.handlers = new HashMap<String, Handler>();
        this.handlersByClass = new HashMap<Class<?>, Handler>();
    }    

    public String getUri() {
        return this.uri;
    }
    
    public void addHandler(String name, Handler handler) {
        this.handlers.put( name, handler );
        if (handler != null && handler.generateNodeFor() != null) {
        	this.handlersByClass.put( handler.generateNodeFor(), handler );
        }
    }

    public Handler getHandler(String name) {
        return this.handlers.get( name );
    }
    
    public Handler getHandlerByClass(Class<?> clazz) {
        while (clazz != null) {
        	Handler handler = this.handlersByClass.get( clazz );
            if (handler != null) {
            	return handler;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }
            
}
