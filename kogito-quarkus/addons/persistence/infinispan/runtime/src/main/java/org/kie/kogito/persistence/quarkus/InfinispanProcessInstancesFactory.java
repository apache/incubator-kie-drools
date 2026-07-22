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
package org.kie.kogito.persistence.quarkus;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.kie.kogito.infinispan.AbstractProcessInstancesFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class InfinispanProcessInstancesFactory extends AbstractProcessInstancesFactory {

    public InfinispanProcessInstancesFactory() {
        super(null, null, null);
    }

    @Inject
    public InfinispanProcessInstancesFactory(RemoteCacheManager cacheManager,
            @ConfigProperty(name = "kogito.persistence.optimistic.lock", defaultValue = "false") Boolean lock,
            @ConfigProperty(name = "kogito.persistence.infinispan.template") Optional<String> templateName) {
        super(cacheManager, lock, templateName.orElse(null));
    }

}
