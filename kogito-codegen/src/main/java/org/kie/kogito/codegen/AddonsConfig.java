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

package org.kie.kogito.codegen;

public class AddonsConfig {

    public static final AddonsConfig DEFAULT = builder()
            .withPersistence(false)
            .withTracing(false)
            .withMonitoring(false)
            .withPrometheusMonitoring(false)
            .withKnativeEventing(false)
            .withCloudEvents(false)
            .build();

    private final boolean usePersistence;
    private final boolean useTracing;
    private final boolean useMonitoring;
    private final boolean usePrometheusMonitoring;
    private final boolean useKnativeEventing;
    private final boolean useCloudEvents;

    private AddonsConfig(boolean usePersistence, boolean useTracing, boolean useMonitoring, boolean usePrometheusMonitoring, boolean useKnativeEventing, boolean useCloudEvents) {
        this.usePersistence = usePersistence;
        this.useTracing = useTracing;
        this.useMonitoring = useMonitoring;
        this.usePrometheusMonitoring = usePrometheusMonitoring;
        this.useKnativeEventing = useKnativeEventing;
        this.useCloudEvents = useCloudEvents;
    }

    public boolean usePersistence() {
        return usePersistence;
    }

    public boolean useTracing() {
        return useTracing;
    }

    public boolean useMonitoring() {
        return useMonitoring;
    }

    public boolean usePrometheusMonitoring() {
        return usePrometheusMonitoring;
    }

    public boolean useKnativeEventing() {
        return useKnativeEventing;
    }

    public boolean useCloudEvents() {
        return useCloudEvents;
    }

    public static AddonsConfigBuilder builder() {
        return new AddonsConfigBuilder();
    }

    @Override
    public String toString() {
        return "AddonsConfig{" +
                "usePersistence=" + usePersistence +
                ", useTracing=" + useTracing +
                ", useMonitoring=" + useMonitoring +
                ", usePrometheusMonitoring=" + usePrometheusMonitoring +
                ", useKnativeEventing=" + useKnativeEventing +
                ", useCloudEvents=" + useCloudEvents +
                '}';
    }

    public static class AddonsConfigBuilder {
        private boolean usePersistence;
        private boolean useTracing;
        private boolean useMonitoring;
        private boolean usePrometheusMonitoring;
        private boolean useKnativeEventing;
        private boolean useCloudEvents;

        private AddonsConfigBuilder() {
        }

        public AddonsConfigBuilder withPersistence(boolean usePersistence) {
            this.usePersistence = usePersistence;
            return this;
        }

        public AddonsConfigBuilder withTracing(boolean useTracing) {
            this.useTracing = useTracing;
            return this;
        }

        public AddonsConfigBuilder withMonitoring(boolean useMonitoring) {
            this.useMonitoring = useMonitoring;
            return this;
        }

        public AddonsConfigBuilder withPrometheusMonitoring(boolean usePrometheusMonitoring) {
            this.usePrometheusMonitoring = usePrometheusMonitoring;
            return this;
        }

        public AddonsConfigBuilder withKnativeEventing(boolean useKnativeEventing) {
            this.useKnativeEventing = useKnativeEventing;
            return this;
        }

        public AddonsConfigBuilder withCloudEvents(boolean useCloudEvents) {
            this.useCloudEvents = useCloudEvents;
            return this;
        }

        public AddonsConfig build() {
            return new AddonsConfig(usePersistence, useTracing, useMonitoring, usePrometheusMonitoring, useKnativeEventing, useCloudEvents);
        }
    }
}
