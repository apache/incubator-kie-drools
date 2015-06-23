/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.xml;

public class WrapperSemanticModule
    implements
    SemanticModule {
    private String uri;
    private SemanticModule module;

    public WrapperSemanticModule(String uri,
                                 SemanticModule module) {
        this.uri = uri;
        this.module = module;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public void addHandler(String name,
                           Handler handler) {
        module.addHandler( name,
                           handler );
    }
    
    public Handler getHandler(String name) {
        return module.getHandler( name );
    }
    
    public Handler getHandlerByClass(Class< ? > clazz) {
        return module.getHandlerByClass( clazz );
    }
    
    public SemanticModule getModule() {
        return module;
    }
    
    public void setModule(SemanticModule module) {
        this.module = module;
    }

    
    
}
