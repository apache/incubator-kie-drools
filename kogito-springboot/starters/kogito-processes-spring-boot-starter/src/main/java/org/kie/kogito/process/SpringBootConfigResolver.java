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
package org.kie.kogito.process;

import java.util.Optional;

import org.kie.kogito.process.workitems.impl.ConfigResolver;
import org.kie.kogito.process.workitems.impl.ConfigResolverHolder;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SpringBootConfigResolver implements ConfigResolver, ApplicationListener<ApplicationStartedEvent> {

    private final Environment environment;

    public SpringBootConfigResolver(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        ConfigResolverHolder.setConfigResolver(this);
    }

    @Override
    public <T> Optional<T> getConfigProperty(String name, Class<T> clazz) {
        return Optional.ofNullable(environment.getProperty(name, clazz));
    }
}
