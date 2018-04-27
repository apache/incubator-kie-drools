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

package org.jbpm.services.cdi.impl.form;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jbpm.kie.services.impl.form.FormProvider;
import org.jbpm.kie.services.impl.form.FormProviderServiceImpl;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.RuntimeDataService;
import org.kie.api.task.TaskService;
import org.kie.internal.identity.IdentityProvider;

@ApplicationScoped
public class FormProvidesServiceCDIImpl extends FormProviderServiceImpl {

	@Inject
    @Any
    private Instance<FormProvider> providersInjected;

    @PostConstruct
    public void prepare() {
    	Set<FormProvider> providers = new TreeSet<FormProvider>(new Comparator<FormProvider>() {

            @Override
            public int compare(FormProvider o1, FormProvider o2) {
                return o1.getPriority() - o2.getPriority();
            }
        });
        for (FormProvider p : providersInjected) {
            providers.add(p);
        }

        super.setProviders(providers);
    }

    @Inject
	@Override
	public void setTaskService(TaskService taskService) {

		super.setTaskService(taskService);
	}

    @Inject
	@Override
	public void setBpmn2Service(DefinitionService bpmn2Service) {

		super.setBpmn2Service(bpmn2Service);
	}

    @Inject
	@Override
	public void setDataService(RuntimeDataService dataService) {

		super.setDataService(dataService);
	}

    @Inject
	@Override
	public void setDeploymentService(DeploymentService deploymentService) {

		super.setDeploymentService(deploymentService);
	}

	@Inject
    @Override
    public void setIdentityProvider(IdentityProvider identityProvider) {
        super.setIdentityProvider(identityProvider);
    }
}
