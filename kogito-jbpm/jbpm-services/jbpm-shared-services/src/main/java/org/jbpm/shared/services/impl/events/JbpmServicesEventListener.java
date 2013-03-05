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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Observes;

/**
 *
 * @author salaboy
 */
public abstract class JbpmServicesEventListener<T> {
    public Map<Method, List<Annotation>> getObserverMethods(){
        Method[] methods = this.getClass().getMethods();
        Map<Method, List<Annotation>> observerMethods = new HashMap<Method, List<Annotation>>();
        for(Method m: methods){
            if(m.getParameterAnnotations().length > 0){
                Annotation[][] annotations = m.getParameterAnnotations();
                for(Annotation[] a : annotations){
                    for(Annotation b : a){
                        if(b.annotationType().getCanonicalName().equals(Observes.class.getCanonicalName())){
                            observerMethods.put(m, Arrays.asList(a));
                        }
                    }
                }
            }
        }
        return observerMethods;
    }
}
