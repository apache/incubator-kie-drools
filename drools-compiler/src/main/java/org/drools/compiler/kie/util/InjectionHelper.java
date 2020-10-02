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

package org.drools.compiler.kie.util;

import java.util.List;
import java.util.Map;

import org.drools.compiler.rule.builder.ConstraintBuilder;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.builder.model.ChannelModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.builder.model.WorkItemHandlerModel;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.process.WorkItemHandler;

public class InjectionHelper {

    private static class BeanCreatorHolder {
        private static final BeanCreator beanCreator = loadBeanCreator();

        private static BeanCreator loadBeanCreator() {
            BeanCreator beanCreator = lookupCdiBeanCreator();
            return beanCreator != null ? beanCreator : new ReflectionBeanCreator();
        }

        private static BeanCreator lookupCdiBeanCreator() {
            try {
                return (BeanCreator) Class.forName( "org.drools.cdi.CDIHelper" )
                                          .getMethod( "getCdiBeanCreator" ).invoke( null );
            } catch (Exception e) {
                return null;
            }
        }
    }

    public static void wireSessionComponents(KieSessionModel model, KieSession kSession) {
        wireSessionComponents(BeanCreatorHolder.beanCreator, model, kSession);
    }

    public static void wireSessionComponents(KieSessionModel model, StatelessKieSession kSession) {
        wireSessionComponents(BeanCreatorHolder.beanCreator, model, kSession);
    }

    public static void wireSessionComponents( KieSessionModel model, KieSession kSession, Map<String, Object> parameters ) {
        wireSessionComponents( ConstraintBuilder.get().createMVELBeanCreator( parameters ), model, kSession );
    }

    public static void wireSessionComponents(KieSessionModel model, StatelessKieSession kSession, Map<String, Object> parameters) {
        wireSessionComponents( ConstraintBuilder.get().createMVELBeanCreator(parameters), model, kSession);
    }

    public static void wireSessionComponents(BeanCreator beanCreator, KieSessionModel model, KieSession kSession) {
        BeanCreator fallbackBeanCreator = new ReflectionBeanCreator();
        ClassLoader cl = ((InternalKnowledgeBase)kSession.getKieBase()).getRootClassLoader();
        wireListeners(beanCreator, fallbackBeanCreator, cl, model.getListenerModels(), kSession);
        wireWIHs(beanCreator, fallbackBeanCreator, cl, model.getWorkItemHandlerModels(), kSession);
        wireChannels(beanCreator, fallbackBeanCreator, cl, model.getChannelModels(), kSession);
    }
    
    public static void wireSessionComponents(BeanCreator beanCreator, KieSessionModel model, StatelessKieSession kSession ) {
        BeanCreator fallbackBeanCreator = new ReflectionBeanCreator();
        ClassLoader cl = ((InternalKnowledgeBase)kSession.getKieBase()).getRootClassLoader();
        wireListeners(beanCreator, fallbackBeanCreator, cl, model.getListenerModels(), kSession);
        wireChannels(beanCreator, fallbackBeanCreator, cl, model.getChannelModels(), kSession);
    }
      
    private static void wireWIHs(BeanCreator beanCreator, BeanCreator fallbackBeanCreator, ClassLoader cl, List<WorkItemHandlerModel> wihModels, KieSession kSession) { 
     	 for (WorkItemHandlerModel wihModel : wihModels) {
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
    
    private static void wireListeners(BeanCreator beanCreator, BeanCreator fallbackBeanCreator, ClassLoader cl, List<ListenerModel> listenerModels, KieRuntimeEventManager kRuntimeEventManager) {
    	for (ListenerModel listenerModel : listenerModels) {
            Object listener = createListener( beanCreator, fallbackBeanCreator, cl, listenerModel );
            switch(listenerModel.getKind()) {
                case AGENDA_EVENT_LISTENER:
                    kRuntimeEventManager.addEventListener((AgendaEventListener)listener);
                    break;
                case RULE_RUNTIME_EVENT_LISTENER:
                    kRuntimeEventManager.addEventListener((RuleRuntimeEventListener)listener);
                    break;
                case PROCESS_EVENT_LISTENER:
                    kRuntimeEventManager.addEventListener((ProcessEventListener)listener);
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
    
    private static void wireChannels(BeanCreator beanCreator, BeanCreator fallbackBeanCreator, ClassLoader cl, List<ChannelModel> channelModels, KieSession kSession) {
    	wireSessionChannels(beanCreator, fallbackBeanCreator, cl, channelModels, kSession);
    }
    
    private static void wireChannels(BeanCreator beanCreator, BeanCreator fallbackBeanCreator, ClassLoader cl, List<ChannelModel> channelModels, StatelessKieSession kSession) {
    	wireSessionChannels(beanCreator, fallbackBeanCreator, cl, channelModels, kSession);
    }
    
    private static void wireSessionChannels(BeanCreator beanCreator, BeanCreator fallbackBeanCreator, ClassLoader cl, List<ChannelModel> channelModels, Object kSession) {
    	for (ChannelModel channelModel : channelModels) {
            Channel channel;
            try {
                channel = beanCreator.createBean(cl, channelModel.getType(), channelModel.getQualifierModel());
            } catch (Exception e) {
                try {
                    channel = fallbackBeanCreator.createBean(cl, channelModel.getType(), channelModel.getQualifierModel() );
                } catch (Exception ex) {
                    throw new RuntimeException("Cannot instance Channel " + channelModel.getType(), e);
                }
            }
            if (kSession instanceof KieSession) {
            	((KieSession) kSession).registerChannel(channelModel.getName(), channel);
            } else if (kSession instanceof StatelessKieSession) {
            	((StatelessKieSession) kSession).registerChannel(channelModel.getName(), channel);
            } else {
            	throw new IllegalArgumentException("kSession not of correct type. Expected KieSession or StatelessKieSession but was: " + kSession.getClass().getCanonicalName());
            }
        }
    }
    
}
