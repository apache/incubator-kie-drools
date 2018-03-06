/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.casemgmt.impl.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.event.CaseEventListener;
import org.jbpm.casemgmt.impl.audit.CaseInstanceAuditEventListener;
import org.jbpm.casemgmt.impl.wih.NotifyParentCaseEventListener;
import org.jbpm.runtime.manager.impl.PerCaseRuntimeManager;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.services.api.DeploymentEvent;
import org.jbpm.services.api.DeploymentEventListener;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.kie.internal.identity.IdentityProvider;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.runtime.Closeable;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.ObjectModelResolver;
import org.kie.internal.runtime.conf.ObjectModelResolverProvider;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration listener that must be attached to DeploymentService so it can react 
 * to deployed units so it can register case event listeners per deployment unit.
 *
 */
public class CaseConfigurationDeploymentListener implements DeploymentEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(CaseConfigurationDeploymentListener.class);
    
    private IdentityProvider identityProvider;
    private TransactionalCommandService transactionalCommandService;
    
    public CaseConfigurationDeploymentListener(IdentityProvider identityProvider) {
        this(identityProvider, null);
    }
    
    public CaseConfigurationDeploymentListener(IdentityProvider identityProvider, TransactionalCommandService transactionalCommandService) {
        this.identityProvider = identityProvider;
        this.transactionalCommandService = transactionalCommandService;
    }

    @Override
    public void onDeploy(DeploymentEvent event) {
        InternalRuntimeManager runtimeManager = (InternalRuntimeManager) event.getDeployedUnit().getRuntimeManager();
        if (runtimeManager instanceof PerCaseRuntimeManager) {
            List<CaseEventListener> caseEventListeners = getEventListenerFromDescriptor(runtimeManager);
            logger.debug("Adding following case event listeners {} for deployment {}", caseEventListeners, event.getDeploymentId());
                        
            TransactionalCommandService commandService = transactionalCommandService;
            if (commandService == null) {
                commandService = new TransactionalCommandService(((SimpleRuntimeEnvironment) runtimeManager.getEnvironment()).getEmf());
            }
            
            CaseInstanceAuditEventListener auditEventListener = new CaseInstanceAuditEventListener(commandService);
            caseEventListeners.add(auditEventListener);
            caseEventListeners.add(new NotifyParentCaseEventListener(identityProvider));
            
            CaseEventSupport caseEventSupport = new CaseEventSupport(identityProvider, caseEventListeners);
            ((PerCaseRuntimeManager) runtimeManager).setCaseEventSupport(caseEventSupport);
            logger.debug("CaseEventSupport configured for deployment {}", event.getDeploymentId());
        }
    }

    @Override
    public void onUnDeploy(DeploymentEvent event) {
        InternalRuntimeManager runtimeManager = (InternalRuntimeManager) event.getDeployedUnit().getRuntimeManager();
        if (runtimeManager instanceof PerCaseRuntimeManager) {
            
            CaseEventSupport caseEventSupport = (CaseEventSupport) ((PerCaseRuntimeManager) runtimeManager).getCaseEventSupport();
            if (caseEventSupport != null) {
                caseEventSupport.reset();
                logger.debug("CaseEventSupport disposed for deployment {}", event.getDeploymentId());
            }
        }
    }

    @Override
    public void onActivate(DeploymentEvent event) {
        // no-op
    }

    @Override
    public void onDeactivate(DeploymentEvent event) {
        // no-op
    }
        
    protected List<CaseEventListener>  getEventListenerFromDescriptor(InternalRuntimeManager runtimeManager) {
        List<CaseEventListener> listeners = new ArrayList<CaseEventListener>();
        DeploymentDescriptor descriptor = runtimeManager.getDeploymentDescriptor();
        if (descriptor != null) {
            Map<String, Object> params = getParametersMap(runtimeManager);
            for (ObjectModel model : descriptor.getEventListeners()) {
                ObjectModelResolver resolver = ObjectModelResolverProvider.get(model.getResolver());
                if (resolver == null) {
                    logger.warn("Unable to find ObjectModelResolver for {}", model.getResolver());
                    continue;
                }
                try {
                    Object listenerInstance = resolver.getInstance(model, runtimeManager.getEnvironment().getClassLoader(), params);
                    if (listenerInstance != null && CaseEventListener.class.isAssignableFrom(listenerInstance.getClass())) {
                        listeners.add((CaseEventListener) listenerInstance);
                    } else {
                        // close/cleanup instance as it is not going to be used at the moment, except these that are cacheable
                        if (listenerInstance instanceof Closeable && !(listenerInstance instanceof Cacheable)) {
                            ((Closeable) listenerInstance).close();
                        }
                    }
                } catch (Exception e) {
                    logger.debug("Unable to build listener {}", model);
                }
            }
        }
        
        return listeners;
    }

    protected Map<String, Object> getParametersMap(InternalRuntimeManager runtimeManager) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("runtimeManager", runtimeManager);
        parameters.put("classLoader", runtimeManager.getEnvironment().getClassLoader());
        parameters.put("entityManagerFactory", ((SimpleRuntimeEnvironment)runtimeManager.getEnvironment()).getEmf());
        parameters.put("kieContainer", runtimeManager.getKieContainer());
        
        return parameters;
    }
}
