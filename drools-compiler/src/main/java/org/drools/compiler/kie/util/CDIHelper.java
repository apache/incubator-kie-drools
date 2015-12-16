/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.kie.util;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.util.MVELSafeHelper;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.builder.model.QualifierModel;
import org.kie.api.builder.model.WorkItemHandlerModel;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.process.WorkItemHandler;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

public class CDIHelper {

    public static void wireListnersAndWIHs(KieSessionModel model, KieSession kSession) {
        wireListnersAndWIHs(BeanCreatorHolder.beanCreator, model, kSession);
    }

    public static void wireListnersAndWIHs(BeanManager beanManager, KieSessionModel model, KieSession kSession) {
        wireListnersAndWIHs(new CDIBeanCreator(beanManager), model, kSession);
    }

    public static void wireListnersAndWIHs(KieSessionModel model, KieSession kSession, Map<String, Object> parameters) {
        wireListnersAndWIHs(new MVELBeanCreator(parameters), model, kSession);
    }

    private static void wireListnersAndWIHs(BeanCreator beanCreator, KieSessionModel model, KieSession kSession) {
        BeanCreator fallbackBeanCreator = new ReflectionBeanCreator();
        ClassLoader cl = ((InternalKnowledgeBase)kSession.getKieBase()).getRootClassLoader();

        for (ListenerModel listenerModel : model.getListenerModels()) {
            Object listener = createListener( beanCreator, fallbackBeanCreator, cl, listenerModel );
            switch(listenerModel.getKind()) {
                case AGENDA_EVENT_LISTENER:
                    kSession.addEventListener((AgendaEventListener)listener);
                    break;
                case RULE_RUNTIME_EVENT_LISTENER:
                    kSession.addEventListener((RuleRuntimeEventListener)listener);
                    break;
                case PROCESS_EVENT_LISTENER:
                    kSession.addEventListener((ProcessEventListener)listener);
                    break;
            }
        }
        for (WorkItemHandlerModel wihModel : model.getWorkItemHandlerModels()) {
            WorkItemHandler wih;
            try {
                wih = beanCreator.createBean(cl, wihModel.getType(), wihModel.getQualifierModel());
            } catch (Exception e) {
                try {
                    wih = fallbackBeanCreator.createBean(cl, wihModel.getType(), wihModel.getQualifierModel() );
                } catch (Exception ex) {
                    throw new RuntimeException("Cannot instance WorkItemHandler " + wihModel.getType(), e);
                }
            }
            kSession.getWorkItemManager().registerWorkItemHandler(wihModel.getName(), wih );
        }
    }

    public static void wireListnersAndWIHs(KieSessionModel model, StatelessKieSession kSession) {
        wireListnersAndWIHs(BeanCreatorHolder.beanCreator, model, kSession);
    }

    public static void wireListnersAndWIHs(BeanManager beanManager, KieSessionModel model, StatelessKieSession kSession) {
        wireListnersAndWIHs(new CDIBeanCreator(beanManager), model, kSession);
    }

    public static void wireListnersAndWIHs(KieSessionModel model, StatelessKieSession kSession, Map<String, Object> parameters) {
        wireListnersAndWIHs(new MVELBeanCreator(parameters), model, kSession);
    }

    private static void wireListnersAndWIHs(BeanCreator beanCreator, KieSessionModel model, StatelessKieSession kSession) {
        BeanCreator fallbackBeanCreator = new ReflectionBeanCreator();
        ClassLoader cl = ((InternalKnowledgeBase)kSession.getKieBase()).getRootClassLoader();

        for (ListenerModel listenerModel : model.getListenerModels()) {
            Object listener = createListener( beanCreator, fallbackBeanCreator, cl, listenerModel );
            switch(listenerModel.getKind()) {
                case AGENDA_EVENT_LISTENER:
                    kSession.addEventListener((AgendaEventListener)listener);
                    break;
                case RULE_RUNTIME_EVENT_LISTENER:
                    kSession.addEventListener((RuleRuntimeEventListener)listener);
                    break;
                case PROCESS_EVENT_LISTENER:
                    kSession.addEventListener((ProcessEventListener)listener);
                    break;
            }
        }
    }

    private static Object createListener( BeanCreator beanCreator, BeanCreator fallbackBeanCreator, ClassLoader cl, ListenerModel listenerModel ) {
        Object listener;
        try {
            listener = beanCreator.createBean(cl, listenerModel.getType(), listenerModel.getQualifierModel());
        } catch (Exception e) {
            try {
                listener = fallbackBeanCreator.createBean(cl, listenerModel.getType(), listenerModel.getQualifierModel());
            } catch (Exception ex) {
                throw new RuntimeException("Cannot instance listener " + listenerModel.getType(), e);
            }

        }
        return listener;
    }

    private static class BeanCreatorHolder {
        private static final BeanCreator beanCreator = loadBeanCreator();

        private static BeanCreator loadBeanCreator() {
            BeanManager beanManager = lookupBeanManager();
            return beanManager != null ? new CDIBeanCreator(beanManager) : new ReflectionBeanCreator();
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
    }

    private interface BeanCreator {
        <T> T createBean(ClassLoader cl, String type, QualifierModel qualifier) throws Exception;
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

    private static class ReflectionBeanCreator implements BeanCreator {

        public <T> T createBean(ClassLoader cl, String type, QualifierModel qualifier) throws Exception {
            if (qualifier != null) {
                throw new IllegalArgumentException("Cannot use a qualifier without a CDI container");
            }
            return (T)Class.forName(type, true, cl).newInstance();
        }
    }

    private static class MVELBeanCreator implements BeanCreator {

        private Map<String, Object> parameters;

        private MVELBeanCreator(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
        public <T> T createBean(ClassLoader cl, String type, QualifierModel qualifier) throws Exception {
            if (qualifier != null) {
                throw new IllegalArgumentException("Cannot use a qualifier without a CDI container");
            }

            ParserConfiguration config = new ParserConfiguration();
            config.setClassLoader(cl);
            ParserContext ctx = new ParserContext(config);
            if (parameters != null) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    ctx.addVariable(entry.getKey(), entry.getValue().getClass());
                }
            }

            Object compiledExpression = MVEL.compileExpression(type, ctx);
            return (T)MVELSafeHelper.getEvaluator().executeExpression( compiledExpression, parameters );
        }
    }
}
