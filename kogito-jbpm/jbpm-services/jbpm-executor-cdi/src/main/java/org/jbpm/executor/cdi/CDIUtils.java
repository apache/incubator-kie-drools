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

package org.jbpm.executor.cdi;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.kie.api.executor.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDIUtils {

	private static final Logger logger = LoggerFactory.getLogger(CDIUtils.class);
	private static final String[] BEAN_MANAGER_NAMES = {"java:comp/BeanManager", "java:comp/env/BeanManager", System.getProperty("org.jbpm.cdi.bm")};
	
	public static BeanManager lookUpBeanManager(CommandContext ctx) {
		BeanManager beanManager = null;
		for (String jndiName : BEAN_MANAGER_NAMES) {
			if (jndiName == null) {
				continue;
			}
			try {
				beanManager = InitialContext.doLookup(jndiName);
				logger.debug("Found bean manager under {} jndi name", jndiName);
				break;
			} catch (NamingException e) {
				logger.debug("No bean manager under {} jndi name", jndiName);
			}
		}
		if (beanManager == null) {
			beanManager = (BeanManager) ctx.getData("BeanManager");
		}
		return beanManager;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T createBean(Class<T> beanType, BeanManager beanManager, Annotation... bindings) throws Exception {

        Set<Bean<?>> beans = beanManager.getBeans( beanType, bindings );
   
        if (beans != null && !beans.isEmpty()) {
	        Bean<T> bean = (Bean<T>) beans.iterator().next();

	        return (T) beanManager.getReference(bean, beanType, beanManager.createCreationalContext(bean));
        }
        
        return null;
    }
}