/*
 * Copyright 2013 JBoss Inc
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

package org.jbpm.shared.services.cdi;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.inject.Singleton;

/**
 * CDI extension that is responsible to eagerly bootstrap CDI beans that
 * are annotated with <code>@BootOnLoad</code>. That mainly means that
 * beans annotated with <code>@BootOnLoad</code> will be invoked (their)
 * <code>@PostConstruct</code> method on CDI container initialization.
 * This extension will only take effect on beans marked as:
 * <ul>
 * 	<li><code>@javax.enterprise.context.ApplicationScoped</code></li>
 * 	<li><code>@javax.inject.Singleton</code></li>
 * </ul>
 */
public class BootOnLoadExtension implements Extension {

	private final List<Bean<?>> startupBootstrapBeans = new LinkedList<Bean<?>>();

	public <X> void processBean(@Observes final ProcessBean<X> event) {
		if (event.getAnnotated().isAnnotationPresent(BootOnLoad.class)
				&& (event.getAnnotated().isAnnotationPresent(ApplicationScoped.class) 
						|| event.getAnnotated().isAnnotationPresent(Singleton.class))) {
			startupBootstrapBeans.add(event.getBean());

		}
	}

	public void afterDeploymentValidation(
			final @Observes AfterDeploymentValidation event,
			final BeanManager manager) {
		// Force execution of Bootstrap bean's @PostConstruct methods first
		runPostConstruct(manager, startupBootstrapBeans);

	}

	private void runPostConstruct(final BeanManager manager,
			final List<Bean<?>> orderedBeans) {

		for (Bean<?> bean : orderedBeans) {
			// the call to toString() is a cheat to force the bean to be initialized			
			manager.getReference(bean, bean.getBeanClass(),
					manager.createCreationalContext(bean)).toString();
		}
	}


}
