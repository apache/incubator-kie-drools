/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.runtime.manager;

import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.task.UserGroupCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface RuntimeEnvironmentBuilder {

    public RuntimeEnvironmentBuilder persistence(boolean persistenceEnabled);

    public RuntimeEnvironmentBuilder entityManagerFactory(Object emf);

    public RuntimeEnvironmentBuilder addAsset(Resource asset, ResourceType type);

    public RuntimeEnvironmentBuilder addEnvironmentEntry(String name, Object value);

    public RuntimeEnvironmentBuilder addConfiguration(String name, String value);

    public RuntimeEnvironmentBuilder knowledgeBase(KieBase kbase);

    public RuntimeEnvironmentBuilder userGroupCallback(UserGroupCallback callback);

    public RuntimeEnvironmentBuilder registerableItemsFactory(RegisterableItemsFactory factory);

    public RuntimeEnvironment get();

    public RuntimeEnvironmentBuilder classLoader(ClassLoader cl);

    public RuntimeEnvironmentBuilder schedulerService(Object globalScheduler);

    public static class Factory implements RuntimeEnvironmentBuilderFactory {
        private static RuntimeEnvironmentBuilderFactory INSTANCE;
        private static Logger logger = LoggerFactory.getLogger(Factory.class);

        static {
            try {
                INSTANCE = ( RuntimeEnvironmentBuilderFactory )
                        Class.forName( "org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder").newInstance();
            } catch (Exception e) {
                logger.error("Unable to instance RuntimeEnvironmentBuilderFactory due to " + e.getMessage());
            }
        }

        /**
         * Returns a reference to the RuntimeEnvironmentBuilderFactory singleton
         */
        public static RuntimeEnvironmentBuilderFactory get() {
            if (INSTANCE == null) {
                throw new RuntimeException("RuntimeEnvironmentBuilder was not initialized, see previous errors");
            }
            return INSTANCE;
        }

        @Override
        public RuntimeEnvironmentBuilder newEmptyBuilder() {
            return ((RuntimeEnvironmentBuilderFactory) get()).newEmptyBuilder();
        }

        @Override
        public RuntimeEnvironmentBuilder newDefaultBuilder() {
            return ((RuntimeEnvironmentBuilderFactory) get()).newDefaultBuilder();
        }

        @Override
        public RuntimeEnvironmentBuilder newDefaultInMemoryBuilder() {
            return ((RuntimeEnvironmentBuilderFactory) get()).newDefaultInMemoryBuilder();
        }

        @Override
        public RuntimeEnvironmentBuilder newDefaultBuilder(String groupId, String artifactId, String version) {
            return ((RuntimeEnvironmentBuilderFactory) get()).newDefaultBuilder(groupId, artifactId, version);
        }

        @Override
        public RuntimeEnvironmentBuilder newDefaultBuilder(String groupId, String artifactId, String version, String kbaseName, String ksessionName) {
            return ((RuntimeEnvironmentBuilderFactory) get()).newDefaultBuilder(groupId, artifactId, version, kbaseName, ksessionName);
        }

        @Override
        public RuntimeEnvironmentBuilder newDefaultBuilder(ReleaseId releaseId) {
            return ((RuntimeEnvironmentBuilderFactory) get()).newDefaultBuilder(releaseId);
        }

        @Override
        public RuntimeEnvironmentBuilder newDefaultBuilder(ReleaseId releaseId, String kbaseName, String ksessionName) {
            return ((RuntimeEnvironmentBuilderFactory) get()).newDefaultBuilder(releaseId, kbaseName, ksessionName);
        }

        @Override
        public RuntimeEnvironmentBuilder newClasspathKmoduleDefaultBuilder() {
            return ((RuntimeEnvironmentBuilderFactory) get()).newClasspathKmoduleDefaultBuilder();
        }

        @Override
        public RuntimeEnvironmentBuilder newClasspathKmoduleDefaultBuilder(String kbaseName, String ksessionName) {
            return ((RuntimeEnvironmentBuilderFactory) get()).newClasspathKmoduleDefaultBuilder(kbaseName, ksessionName);
        }


    }
}
