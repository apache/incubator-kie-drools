/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
