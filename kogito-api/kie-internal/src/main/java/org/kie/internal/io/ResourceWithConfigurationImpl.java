package org.kie.internal.io;

import java.util.function.Consumer;

import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;

public class ResourceWithConfigurationImpl implements ResourceWithConfiguration {

        private final Resource resource;
        private final ResourceConfiguration resourceConfiguration;
        private final Consumer<Object> beforeAdd;
        private final Consumer<Object> afterAdd;

        /**
         * 
         * @param resource 
         * @param resourceConfiguration
         * @param beforeAdd callback executed on `kbuilder` as a paramenter, which will be executed before performing {@link KieAssemblerService#addResource(Object, Resource, ResourceType, ResourceConfiguration)} for the given resource {@link #resource}
         * @param afterAdd callback executed on `kbuilder` as a paramenter, which will be executed after performing {@link KieAssemblerService#addResource(Object, Resource, ResourceType, ResourceConfiguration)} for the given resource {@link #resource}
         */
        public ResourceWithConfigurationImpl(Resource resource, ResourceConfiguration resourceConfiguration, Consumer<Object> beforeAdd, Consumer<Object> afterAdd) {
            this.resource = resource;
            this.resourceConfiguration = resourceConfiguration;
            this.beforeAdd = beforeAdd;
            this.afterAdd = afterAdd;
        }

        @Override
        public Resource getResource() {
            return resource;
        }

        @Override
        public ResourceConfiguration getResourceConfiguration() {
            return resourceConfiguration;
        }

        @Override
        public Consumer<Object> getBeforeAdd() {
            return beforeAdd;
        }

        @Override
        public Consumer<Object> getAfterAdd() {
            return afterAdd;
        }

    }