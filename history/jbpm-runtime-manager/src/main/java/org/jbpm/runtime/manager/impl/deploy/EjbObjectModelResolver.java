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

package org.jbpm.runtime.manager.impl.deploy;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;

import org.kie.api.KieServices;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.task.TaskService;
import org.kie.api.executor.ExecutorService;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EjbObjectModelResolver extends ReflectionObjectModelResolver {
	
	private static final Logger logger = LoggerFactory.getLogger(EjbObjectModelResolver.class);
	
	public static final String ID = "ejb";
	
	private Map<String, Class<?>> knownContextParamMapping = new HashMap<String, Class<?>>();
	
	public EjbObjectModelResolver() {
		knownContextParamMapping.put("entityManagerFactory", EntityManagerFactory.class);
		knownContextParamMapping.put("runtimeManager", RuntimeManager.class);
		knownContextParamMapping.put("kieSession", KieServices.class);
		knownContextParamMapping.put("taskService", TaskService.class);
		knownContextParamMapping.put("executorService", ExecutorService.class);
		knownContextParamMapping.put("classLoader", ClassLoader.class);
	}

	@Override
	public Object getInstance(ObjectModel model, ClassLoader cl, Map<String, Object> contextParams) {

		Class<?> clazz = getClassObject(model.getIdentifier(), cl);
		Object instance = null;
		InternalRuntimeManager manager = null;
		if (contextParams.containsKey("runtimeManager")) {
			manager = (InternalRuntimeManager) contextParams.get("runtimeManager");
			instance = manager.getCacheManager().get(clazz.getName());
			if (instance != null) {
				return instance;
			}
		}
		if (model.getParameters() == null || model.getParameters().isEmpty()) {
			logger.debug("About to create instance of {} with no arg constructor", model.getIdentifier());
			// no parameters then use no arg constructor
			try {
				instance = clazz.newInstance();
			} catch (Exception e) {
				throw new IllegalArgumentException("Unable to create instance (no arg constructor) of type "
									+ model.getIdentifier() + " due to " + e.getMessage(), e);
			}
		} else {
			logger.debug("About to create instance of {} with {} parameters", model.getIdentifier(), model.getParameters().size());
			// process parameter instances
			Class<?>[] parameterTypes = new Class<?>[model.getParameters().size()];
			Object[] paramInstances = new Object[model.getParameters().size()];
			
			int index = 0;
			for (Object param : model.getParameters()) {
				
				if (param instanceof ObjectModel) {
					logger.debug("Parameter is of type ObjectModel (id: {}), trying to create instance based on that model",
							((ObjectModel) param).getIdentifier());
					Class<?> paramclazz = getClassObject(((ObjectModel)param).getIdentifier(), cl);
					parameterTypes[index] = paramclazz;
					
					paramInstances[index] = getInstance(((ObjectModel)param), cl, contextParams);
				} else {
					if (contextParams.containsKey(param)) {
						logger.debug("Parametr references context parametr with name {}", param);
						Object contextValue = contextParams.get(param);
						Class<?> paramClass = contextValue.getClass();
						if (knownContextParamMapping.containsKey(param)) {
							paramClass = knownContextParamMapping.get(param);
						}
						parameterTypes[index] = paramClass;
						paramInstances[index] = contextValue;
					} else {
						if (param.toString().startsWith("jndi:")) {
							
							logger.debug("Parameter is jndi lookup type - {}", param);
							// remove the jndi: prefix
							String lookupName = param.toString().substring(5);
							try {
								Object jndiObject = InitialContext.doLookup(lookupName);
								parameterTypes[index] = Object.class;
								paramInstances[index] = jndiObject;
							} catch (NamingException e) {
								throw new IllegalArgumentException("Unable to look up object from jndi using name " + lookupName, e);
							}
							
						} else {
						
							logger.debug("Parameter is simple type (string) - {}", param);
							parameterTypes[index] = param.getClass();
							paramInstances[index] = param;
						}
					}
				}
				
				index++;
			}
			try {	
				logger.debug("Creating instance of class {} with parameter types {} and parameter instances {}",
						clazz, parameterTypes, paramInstances);
				Constructor<?> constructor = clazz.getConstructor(parameterTypes);
				instance = constructor.newInstance(paramInstances);
			} catch (Exception e) {
				throw new IllegalArgumentException("Unable to create instance (" + parameterTypes + " constructor) of type "
									+ model.getIdentifier() + " due to " + e.getMessage(), e);
			}
		}
		logger.debug("Created instance : {}", instance);
		
		if (manager != null && instance instanceof Cacheable) {
			manager.getCacheManager().add(instance.getClass().getName(), instance);
		}
		return instance;
	}

	@Override
	public boolean accept(String resolverId) {
		if (ID.equals(resolverId)) {
			return true;
		}
		logger.debug("Resolver id {} is not accepted by {}", resolverId, this.getClass());
		return false;
	}

}
