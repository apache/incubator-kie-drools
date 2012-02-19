/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formbuilder.server;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlRootElementProvider;
import org.jboss.resteasy.plugins.providers.jaxb.XmlJAXBContextFinder;

public class FormBuilderResteasy extends Application {

    private final Set<Class<?>> classes = new HashSet<Class<?>>();
    
    private final Set<Object> singletons = new HashSet<Object>();
    
    public FormBuilderResteasy() {
        classes.add(XmlJAXBContextFinder.class);
        classes.add(JAXBXmlRootElementProvider.class);
        
        singletons.add(new RESTMenuService());
        singletons.add(new RESTFormService());
        singletons.add(new RESTIoService());
        singletons.add(new RESTFileService());
        singletons.add(new RESTUserService());
        singletons.add(new XmlJAXBContextFinder());
        singletons.add(new JAXBXmlRootElementProvider());
    }
    
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
    
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
