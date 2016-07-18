/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.util;

import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.internal.KnowledgeBaseFactory;

/**
 * Represents various tested KieBase configurations.
 */
public enum KieBaseTestConfiguration implements KieBaseModelProvider {

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     */
    CLOUD_IDENTITY {
        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel();
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.CLOUD</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     */
    CLOUD_EQUALITY {
        @Override
        public KieBaseModel getKieBaseModel(final KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel();
            kieBaseModel.setEventProcessingMode(EventProcessingOption.CLOUD);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.CLOUD);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.IDENTITY</code> options set.
     */
    STREAM_IDENTITY {
        @Override
        public KieBaseModel getKieBaseModel(KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel();
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.IDENTITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.IDENTITY);
            return kieBaseConfiguration;
        }
    },

    /**
     * Represents KieBase configuration with
     * <code>EventProcessingOption.STREAM</code> and
     * <code>EqualityBehaviorOption.EQUALITY</code> options set.
     */
    STREAM_EQUALITY {
        @Override
        public KieBaseModel getKieBaseModel(KieModuleModel kieModuleModel) {
            final KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel();
            kieBaseModel.setEventProcessingMode(EventProcessingOption.STREAM);
            kieBaseModel.setEqualsBehavior(EqualityBehaviorOption.EQUALITY);
            kieBaseModel.setDefault(true);
            return kieBaseModel;
        }

        @Override
        public KieBaseConfiguration getKieBaseConfiguration() {
            final KieBaseConfiguration kieBaseConfiguration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
            kieBaseConfiguration.setOption(EventProcessingOption.STREAM);
            kieBaseConfiguration.setOption(EqualityBehaviorOption.EQUALITY);
            return kieBaseConfiguration;
        }
    }
}
