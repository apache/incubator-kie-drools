/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.services.cdi.test.humantaskservice;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Qualifier;

import org.drools.core.impl.EnvironmentFactory;
import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.services.cdi.producer.HumanTaskServiceProducer;
import org.jbpm.services.task.HumanTaskConfigurator;
import org.jbpm.services.task.impl.command.CommandBasedTaskService;
import org.jbpm.services.task.persistence.JPATaskPersistenceContextManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;

/**
 *
 *
 */
public class CustomHumanTaskServiceProducer extends HumanTaskServiceProducer {

    @Produces
    @CustomHumanTaskService
    @Override
    public CommandBasedTaskService produceTaskService() {
        return super.produceTaskService();
    }

    @Override
    protected void configureHumanTaskConfigurator(HumanTaskConfigurator configurator) {
        Environment environment = EnvironmentFactory.newEnvironment();
        environment.set(EnvironmentName.TASK_PERSISTENCE_CONTEXT_MANAGER, new CustomTaskPersistenceContextManager());
        environment.set(EnvironmentName.TRANSACTION_MANAGER, new CustomTransactionManager());
        super.configureHumanTaskConfigurator(configurator.environment(environment));
    }

    public static class CustomTransactionManager extends JtaTransactionManager {
        public CustomTransactionManager() {
            super(null, null, null);
        }
    }


    public static class CustomTaskPersistenceContextManager extends JPATaskPersistenceContextManager {
        public CustomTaskPersistenceContextManager() {
            super(EnvironmentFactory.newEnvironment());
        }

        @Override
        public void beginCommandScopedEntityManager() {
            throw new CustomTaskPersistenceContextManagerInUse();
        }
    }

    /**
     * Exception throw to show the CustomTaskPersistenceContextManager is in use.
     */
    @SuppressWarnings("serial")
    public static class CustomTaskPersistenceContextManagerInUse extends RuntimeException {}

}
