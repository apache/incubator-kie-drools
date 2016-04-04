/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;


/**
 *
 * @author salaboy
 */
public class KieBusinessScopeContext implements Context, Serializable {

    private Map<String, Object> somewhere = new HashMap<String, Object>();

    public KieBusinessScopeContext() {
        System.out.println(">>> Creating a new context instance");
    }

    
  // Get the scope type of the context object.
    @Override
    public Class<? extends Annotation> getScope() {
        return KieBusinessScoped.class;
    }

    // Return an existing instance of certain contextual type or create a new instance by calling
    // javax.enterprise.context.spi.Contextual.create(CreationalContext) and return the new instance.
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean bean = (Bean) contextual;
        // you can store the bean somewhere
        if (somewhere.containsKey(bean.getName())) {
            return (T) somewhere.get(bean.getName());
        } else {
            T t = (T) bean.create(creationalContext);
            somewhere.put(bean.getName(), t);
            return t;
        }
    }

    // Return an existing instance of a certain contextual type or a null value.
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Contextual<T> contextual) {
        Bean bean = (Bean) contextual;
        // you can store the bean somewhere
        if (somewhere.containsKey(bean.getName())) {
            return (T) somewhere.get(bean.getName());
        } else {
            return null;
        }
    }

  // Determines if the context object is active.
    @Override
    public boolean isActive() {
        return true;
    }

   

}
