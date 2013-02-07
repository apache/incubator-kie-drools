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
package org.droolsjbpm.services.impl.util;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;

public class StartupBeanExtension implements Extension {
    private final Set<Bean<?>> startupBeans = new LinkedHashSet<Bean<?>>();
    
    public <X> void processBean(@Observes ProcessBean<X> event) {
        if (event.getAnnotated().isAnnotationPresent(Startup.class)
                && event.getAnnotated().isAnnotationPresent( ApplicationScoped.class)) {
            startupBeans.add(event.getBean());            
        }
    }

    public void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager manager) {
        for (Bean<?> bean : startupBeans) {
            // the call to toString() is a cheat to force the bean to be
            // initialized
            manager.getReference(bean, bean.getBeanClass(),
                    manager.createCreationalContext(bean)).toString();
        }
    }    
}
