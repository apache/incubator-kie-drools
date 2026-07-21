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
package org.kie.kogito.codegen.api;

public class AddonsConfig {

    public static final AddonsConfig DEFAULT = builder()
            .withPersistence(false)
            .withTracing(false)
            .withMonitoring(false)
            .withPrometheusMonitoring(false)
            .withCloudEvents(false)
            .withExplainability(false)
            .withEventDrivenDecisions(false)
            .build();

    private final boolean usePersistence;
    private final boolean useTracing;
    private final boolean useMonitoring;
    private final boolean usePrometheusMonitoring;
    private final boolean useCloudEvents;
    private final boolean useExplainability;
    private final boolean useProcessSVG;
    private final boolean useEventDrivenDecisions;
    private final boolean useEventDrivenRules;
    private final boolean useSourceFiles;

    private AddonsConfig(boolean usePersistence, boolean useTracing, boolean useMonitoring, boolean usePrometheusMonitoring, boolean useCloudEvents,
            boolean useExplainability, boolean useProcessSVG, boolean useEventDrivenDecisions, boolean useEventDrivenRules, boolean useSourceFiles) {
        this.usePersistence = usePersistence;
        this.useTracing = useTracing;
        this.useMonitoring = useMonitoring;
        this.usePrometheusMonitoring = usePrometheusMonitoring;
        this.useCloudEvents = useCloudEvents;
        this.useExplainability = useExplainability;
        this.useProcessSVG = useProcessSVG;
        this.useEventDrivenDecisions = useEventDrivenDecisions;
        this.useEventDrivenRules = useEventDrivenRules;
        this.useSourceFiles = useSourceFiles;
    }

    public static AddonsConfigBuilder builder() {
        return new AddonsConfigBuilder();
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

    public boolean useCloudEvents() {
        return useCloudEvents;
    }

    public boolean useExplainability() {
        return useExplainability;
    }

    public boolean useProcessSVG() {
        return useProcessSVG;
    }

    public boolean useEventDrivenDecisions() {
        return useEventDrivenDecisions;
    }

    public boolean useEventDrivenRules() {
        return useEventDrivenRules;
    }

    public boolean useSourceFiles() {
        return useSourceFiles;
    }

    @Override
    public String toString() {
        return "AddonsConfig{" +
                "usePersistence=" + usePersistence +
                ", useTracing=" + useTracing +
                ", useMonitoring=" + useMonitoring +
                ", usePrometheusMonitoring=" + usePrometheusMonitoring +
                ", useCloudEvents=" + useCloudEvents +
                ", useExplainability=" + useExplainability +
                ", useProcessSVG=" + useProcessSVG +
                ", useEventDrivenDecisions=" + useEventDrivenDecisions +
                ", useEventDrivenRules=" + useEventDrivenRules +
                ", useProcessSources=" + useSourceFiles +
                '}';
    }

    public static class AddonsConfigBuilder {

        private boolean usePersistence;
        private boolean useTracing;
        private boolean useMonitoring;
        private boolean usePrometheusMonitoring;
        private boolean useCloudEvents;
        private boolean useExplainability;
        private boolean useProcessSVG;
        private boolean useEventDrivenDecisions;
        private boolean useEventDrivenRules;
        private boolean useSourceFiles;

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

        public AddonsConfigBuilder withCloudEvents(boolean useCloudEvents) {
            this.useCloudEvents = useCloudEvents;
            return this;
        }

        public AddonsConfigBuilder withExplainability(boolean useExplainability) {
            this.useExplainability = useExplainability;
            return this;
        }

        public AddonsConfigBuilder withProcessSVG(boolean useProcessSVG) {
            this.useProcessSVG = useProcessSVG;
            return this;
        }

        public AddonsConfigBuilder withEventDrivenDecisions(boolean useEventDrivenDecisions) {
            this.useEventDrivenDecisions = useEventDrivenDecisions;
            return this;
        }

        public AddonsConfigBuilder withEventDrivenRules(boolean useEventDrivenRules) {
            this.useEventDrivenRules = useEventDrivenRules;
            return this;
        }

        public AddonsConfigBuilder withSourceFiles(boolean useSourceFiles) {
            this.useSourceFiles = useSourceFiles;
            return this;
        }

        public AddonsConfig build() {
            return new AddonsConfig(usePersistence, useTracing, useMonitoring, usePrometheusMonitoring, useCloudEvents, useExplainability, useProcessSVG, useEventDrivenDecisions, useEventDrivenRules,
                    useSourceFiles);
        }
    }
}
