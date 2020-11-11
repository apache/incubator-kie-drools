/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.api.io;

import java.util.function.Consumer;

import org.kie.api.internal.assembler.KieAssemblerService;

/**
 * Represent a tuple of a {@link Resource} with associated {@link ResourceConfiguration}, along with necessary kbuilder callbacks, to be used in in {@link KieAssemblerService}.
 */
public interface ResourceWithConfiguration {

    Resource getResource();

    ResourceConfiguration getResourceConfiguration();

    /**
     * callback executed on `kbuilder` as a parameter in {@link KieAssemblerService}, which will be executed before performing {@link KieAssemblerService#addResource(Object, Resource, ResourceType, ResourceConfiguration)} for the given resource {@link #getResource()}.
     */
    Consumer<Object> getBeforeAdd();

    /**
     * callback executed on `kbuilder` as a parameter in {@link KieAssemblerService}, which will be executed after performing {@link KieAssemblerService#addResource(Object, Resource, ResourceType, ResourceConfiguration)} for the given resource {@link #getResource()}.
     */
    Consumer<Object> getAfterAdd();

}
