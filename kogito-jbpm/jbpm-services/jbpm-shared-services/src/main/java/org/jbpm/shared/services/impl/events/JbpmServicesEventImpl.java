/*
 * Copyright 2013 JBoss by Red Hat.
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
package org.jbpm.shared.services.impl.events;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;

/**
 *
 * @author salaboy
 */
public class JbpmServicesEventImpl<T> implements Event<T>, Serializable {

    List<JbpmServicesEventListener> listeners = new CopyOnWriteArrayList<JbpmServicesEventListener>();
    private List<String> filters = new CopyOnWriteArrayList<String>();

    @Override
    public void fire(T t) {
        Map<JbpmServicesEventListener, List<Method>> invokeMethods = new ConcurrentHashMap<JbpmServicesEventListener, List<Method>>();
        for (JbpmServicesEventListener listener : listeners) {
            Map<Method, List<Annotation>> observerMethods = listener.getObserverMethods();
            for (Method m : observerMethods.keySet()) {
                for (Annotation a : observerMethods.get(m)) {
                    boolean filtered = false;
                    for(String filter : filters){
                        if(a.annotationType().getCanonicalName().equals(filter)){
                            filtered = true;
                        }
                        
                    }
                    if(filtered){
                            if(invokeMethods.get(listener) == null){
                                invokeMethods.put(listener, new CopyOnWriteArrayList<Method>());
                            }
                            invokeMethods.get(listener).add(m);

                   }
                   
                }

            }
            
        }
        filters.clear();
        for(JbpmServicesEventListener listener : invokeMethods.keySet())
            for(Method m : invokeMethods.get(listener)){
                try {   
                    m.invoke(listener, t);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(JbpmServicesEventImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                    Logger.getLogger(JbpmServicesEventImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                    Logger.getLogger(JbpmServicesEventImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
       }
       invokeMethods.clear();
    }

    public boolean addListener(JbpmServicesEventListener e) {
        return listeners.add(e);
    }
    
    public void clearListeners(){
        listeners.clear();
    }

    @Override
    public Event<T> select(Annotation... antns) {
        for (Annotation a : antns) {
            filters.add(a.annotationType().getCanonicalName());
        }
        return this;
    }

    @Override
    public <U extends T> Event<U> select(Class<U> type, Annotation... antns) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <U extends T> Event<U> select(TypeLiteral<U> tl, Annotation... antns) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
