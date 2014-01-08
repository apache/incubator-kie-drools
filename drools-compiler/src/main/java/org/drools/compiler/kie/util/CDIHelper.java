package org.drools.compiler.kie.util;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.drools.core.util.MVELSafeHelper;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.builder.model.QualifierModel;
import org.kie.api.builder.model.WorkItemHandlerModel;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemHandler;

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

        for (ListenerModel listenerModel : model.getListenerModels()) {
            Object listener;
            try {
                listener = beanCreator.createBean(listenerModel.getType(), listenerModel.getQualifierModel());
            } catch (Exception e) {
                throw new RuntimeException("Cannot instance listener " + listenerModel.getType(), e);
            }
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
                wih = beanCreator.createBean(wihModel.getType(), wihModel.getQualifierModel());
            } catch (Exception e) {
                throw new RuntimeException("Cannot instance WorkItemHandler " + wihModel.getType(), e);
            }
            kSession.getWorkItemManager().registerWorkItemHandler(wihModel.getName(), wih );
        }

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
        <T> T createBean(String type, QualifierModel qualifier) throws Exception;
    }

    private static class CDIBeanCreator implements BeanCreator {
        private final BeanManager beanManager;

        private CDIBeanCreator(BeanManager beanManager) {
            this.beanManager = beanManager;
        }

        public <T> T createBean(String type, QualifierModel qualifier) throws Exception {
            Class<?> beanType = Class.forName(type);

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

        public <T> T createBean(String type, QualifierModel qualifier) throws Exception {
            if (qualifier != null) {
                throw new IllegalArgumentException("Cannot use a qualifier without a CDI container");
            }
            return (T)Class.forName(type).newInstance();
        }
    }

    private static class MVELBeanCreator implements BeanCreator {

        private Map<String, Object> parameters;

        private MVELBeanCreator(Map<String, Object> parameters) {
            this.parameters = parameters;
        }
        public <T> T createBean(String type, QualifierModel qualifier) throws Exception {
            if (qualifier != null) {
                throw new IllegalArgumentException("Cannot use a qualifier without a CDI container");
            }
            return (T)MVELSafeHelper.getEvaluator().eval( type, parameters );
        }
    }
}
