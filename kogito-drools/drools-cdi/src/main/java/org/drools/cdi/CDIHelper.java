/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.cdi;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.drools.compiler.kie.util.BeanCreator;
import org.drools.compiler.kie.util.InjectionHelper;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.QualifierModel;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

public class CDIHelper {

    public static void wireListnersAndWIHs(BeanManager beanManager, KieSessionModel model, KieSession kSession) {
        InjectionHelper.wireListnersAndWIHs( new CDIBeanCreator( beanManager), model, kSession );
    }

    public static void wireListnersAndWIHs(BeanManager beanManager, KieSessionModel model, StatelessKieSession kSession) {
        InjectionHelper.wireListnersAndWIHs(new CDIBeanCreator(beanManager), model, kSession);
    }

    public static BeanCreator getCdiBeanCreator() {
        BeanManager beanManager = lookupBeanManager();
        return beanManager != null ? new CDIBeanCreator(beanManager) : null;
    }

    private static BeanManager lookupBeanManager() {
        try {
            // in an application server
            return InitialContext.doLookup("java:comp/BeanManager");
        } catch (NamingException e) {
            // silently ignore
        }

        try {
            // in a servlet container
            return InitialContext.doLookup("java:comp/env/BeanManager");
        } catch (NamingException e) {
            // silently ignore
        }

        return null;
    }

    private static class CDIBeanCreator implements BeanCreator {
        private final BeanManager beanManager;

        private CDIBeanCreator(BeanManager beanManager) {
            this.beanManager = beanManager;
        }

        public <T> T createBean(ClassLoader cl, String type, QualifierModel qualifier) throws Exception {
            Class<?> beanType = Class.forName(type, true, cl);

            Set<Bean<?>> beans;
            if (qualifier == null) {
                beans = beanManager.getBeans( beanType );
            } else {
                Annotation annotation = getQualifier(qualifier);
                beans = beanManager.getBeans( beanType, annotation );
            }

            Bean<T> bean = (Bean<T>) beans.iterator().next();
            return bean.create( beanManager.createCreationalContext(bean) );
        }

        private Annotation getQualifier(QualifierModel model) throws Exception {
            // TODO ???
            // return Class.forName(model.getType()).newInstance();
            return null;
        }
    }
}
