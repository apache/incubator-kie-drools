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
package org.jbpm.services.cdi.test.humantaskservice;

import javax.enterprise.inject.Produces;

import org.drools.core.impl.EnvironmentFactory;
import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.services.cdi.producer.HumanTaskServiceProducer;
import org.jbpm.services.task.HumanTaskConfigurator;
import org.jbpm.services.task.impl.command.CommandBasedTaskService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

public class CustomHumanTaskServiceProducer extends HumanTaskServiceProducer {

    @Produces
    @CustomHumanTaskService
    @Override
    public CommandBasedTaskService produceTaskService() {
        CommandBasedTaskService taskServiceMock = Mockito.mock(CommandBasedTaskService.class);
        Mockito.when(taskServiceMock.execute(Mockito.any())).thenAnswer((InvocationOnMock invocation) -> {
            throw new CustomTaskServiceInUse();
        });
        return taskServiceMock;
    }

    @Override
    protected void configureHumanTaskConfigurator(HumanTaskConfigurator configurator) {
        Environment environment = EnvironmentFactory.newEnvironment();
        environment.set(EnvironmentName.TRANSACTION_MANAGER, new CustomTransactionManager());
        super.configureHumanTaskConfigurator(configurator.environment(environment));
    }

    public static class CustomTransactionManager extends JtaTransactionManager {
        public CustomTransactionManager() {
            super(null, null, null);
        }
    }


    /**
     * Exception throw to show the custom service task is in use.
     */
    @SuppressWarnings("serial")
    public static class CustomTaskServiceInUse extends RuntimeException {}

}
