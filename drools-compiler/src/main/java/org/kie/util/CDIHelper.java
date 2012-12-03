package org.kie.util;

import org.kie.builder.KieSessionModel;
import org.kie.builder.ListenerModel;
import org.kie.builder.QualifierModel;
import org.kie.builder.WorkItemHandlerModel;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.rule.AgendaEventListener;
import org.kie.event.rule.WorkingMemoryEventListener;
import org.kie.runtime.KieSession;
import org.kie.runtime.process.WorkItemHandler;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.annotation.Annotation;
import java.util.Set;

public class CDIHelper {

    public static void wireListnersAndWIHs(KieSessionModel model, KieSession kSession) {
        wireListnersAndWIHs(BeanCreatorHolder.beanCreator, model, kSession);
    }

    public static void wireListnersAndWIHs(BeanManager beanManager, KieSessionModel model, KieSession kSession) {
        wireListnersAndWIHs(new CDIBeanCreator(beanManager), model, kSession);
    }

    private static void wireListnersAndWIHs(BeanCreator beanCreator, KieSessionModel model, KieSession kSession) {
        for (ListenerModel listenerModel : model.getListenerModels()) {
            Object listener;
            try {
                listener = beanCreator.createBean(listenerModel.getType(), listenerModel.getQualifierModel());
            } catch (Exception e) {
                throw new RuntimeException("Cannot instance listener " + listenerModel.getType(), e);
            }
            if (listener instanceof WorkingMemoryEventListener) {
                kSession.addEventListener((WorkingMemoryEventListener)listener);
            } else if (listener instanceof AgendaEventListener) {
                kSession.addEventListener((AgendaEventListener)listener);
            } else if (listener instanceof ProcessEventListener) {
                kSession.addEventListener((ProcessEventListener)listener);
            }
        }

        for (WorkItemHandlerModel wihModel : model.getWorkItemHandelerModels()) {
            WorkItemHandler wih;
            try {
                wih = beanCreator.createBean(wihModel.getType(), wihModel.getQualifierModel());
            } catch (Exception e) {
                throw new RuntimeException("Cannot instance WorkItemHandler " + wihModel.getType(), e);
            }
            kSession.getWorkItemManager().registerWorkItemHandler( "???", wih );
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
                return (BeanManager) InitialContext.doLookup("java:comp/BeanManager");
            } catch (NamingException e) {
                // silently ignore
            }

            try {
                // in a servlet container
                return (BeanManager) InitialContext.doLookup("java:comp/env/BeanManager");
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
}
