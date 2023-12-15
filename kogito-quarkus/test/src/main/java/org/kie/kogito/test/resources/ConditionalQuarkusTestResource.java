/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.test.resources;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.kie.kogito.test.quarkus.QuarkusTestProperty;
import org.testcontainers.containers.Container;

import com.google.common.base.Strings;

import io.quarkus.test.common.DevServicesContext;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import jakarta.annotation.Resource;

/**
 * Quarkus resource to be run if and only if it was enabled.
 */
public abstract class ConditionalQuarkusTestResource<T extends TestResource> implements QuarkusTestResourceLifecycleManager, DevServicesContext.ContextAware {

    private final T testResource;
    private final ConditionHolder condition;
    private boolean conditionalEnabled = false;
    private Optional<String> containerNetworkId = Optional.empty();

    public ConditionalQuarkusTestResource(T testResource) {
        this(testResource, new ConditionHolder(testResource.getResourceName()));
    }

    public ConditionalQuarkusTestResource(T testResource, ConditionHolder condition) {
        this.testResource = testResource;
        this.condition = condition;
    }

    public T getTestResource() {
        return testResource;
    }

    public boolean isConditionalEnabled() {
        return conditionalEnabled;
    }

    @Override
    public Map<String, String> start() {
        if (condition.isEnabled()) {
            if (containerNetworkId.isPresent()) {
                ((Container) testResource).withNetworkMode(containerNetworkId.get());
            }
            testResource.start();
            return getProperties();
        }

        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        if (condition.isEnabled()) {
            testResource.stop();
        }
    }

    @Override
    public void setIntegrationTestContext(DevServicesContext context) {
        containerNetworkId = context.containerNetworkId();
    }

    protected String getServerUrl() {
        Container container = (Container) getTestResource();
        Integer port = container.getExposedPorts().get(0);
        if (containerNetworkId.isPresent()) {
            return container.getCurrentContainerInfo().getConfig().getHostName()
                    + ":"
                    + port;
        } else {
            return container.getHost() + ":" + getTestResource().getMappedPort();
        }
    }

    @Override
    public void inject(Object testInstance) {
        Class<?> c = testInstance.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                QuarkusTestProperty quarkusTestProperty = f.getAnnotation(QuarkusTestProperty.class);
                if (quarkusTestProperty != null) {
                    String value = Optional.ofNullable(getProperties().get(quarkusTestProperty.name()))
                            .orElse(quarkusTestProperty.defaultValue());
                    if (!Strings.isNullOrEmpty(value)) {
                        setFieldValue(f, testInstance, value);
                    }
                } else if (f.isAnnotationPresent(Resource.class) && f.getType().isInstance(this)) {
                    setFieldValue(f, testInstance, this);
                }
            }
            c = c.getSuperclass();
        }
    }

    protected abstract Map<String, String> getProperties();

    protected void enableConditional() {
        condition.enableConditional();
        conditionalEnabled = true;
    }

    private void setFieldValue(Field f, Object testInstance, Object value) {
        try {
            f.setAccessible(true);
            f.set(testInstance, value);
            return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
