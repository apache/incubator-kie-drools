/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.kie.kogito.resources;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.support.TestPropertySourceUtils;

/**
 *
 * Spring Boot test resource.
 *
 */
public abstract class ConditionalSpringBootTestResource implements ApplicationContextInitializer<ConfigurableApplicationContext>, ApplicationListener<ContextClosedEvent> {

    private final TestResource testResource;
    private final ConditionHolder condition;
    private boolean conditionalEnabled = false;

    public ConditionalSpringBootTestResource(TestResource testResource) {
        this(testResource, new ConditionHolder(testResource.getResourceName()));
    }

    public ConditionalSpringBootTestResource(TestResource testResource, ConditionHolder condition) {
        this.testResource = testResource;
        this.condition = condition;
    }

    public TestResource getTestResource() {
        return testResource;
    }

    public boolean isConditionalEnabled() {
        return conditionalEnabled;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (condition.isEnabled()) {
            testResource.start();
            updateBeanFactory(applicationContext.getBeanFactory());
            updateContextProperty(applicationContext, getKogitoProperty(), getKogitoPropertyValue());
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (condition.isEnabled()) {
            testResource.stop();
        }
    }

    protected abstract String getKogitoProperty();

    protected String getKogitoPropertyValue() {
        return "localhost:" + testResource.getMappedPort();
    }

    protected void enableConditional() {
        condition.enableConditional();
        conditionalEnabled = true;
    }

    protected void updateBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.registerSingleton(testResource.getResourceName() + "ShutDownHook", this);
    }

    protected void updateContextProperty(ConfigurableApplicationContext applicationContext, String key, String value) {
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext, key + "=" + value);
    }
}
