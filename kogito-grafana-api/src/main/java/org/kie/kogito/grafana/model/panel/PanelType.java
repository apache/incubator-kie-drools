/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.grafana.model.panel;

public enum PanelType {
    GRAPH {
        @Override
        public String toString() {
            return "graph";
        }
    },
    STAT {
        @Override
        public String toString() {
            return "stat";
        }
    },
    SINGLESTAT {
        @Override
        public String toString() {
            return "singleStat";
        }
    },
    TABLE {
        @Override
        public String toString() {
            return "table";
        }
    },
    HEATMAP {
        @Override
        public String toString() {
            return "heatmap";
        }
    },
    GAUGE {
        @Override
        public String toString() {
            return "gauge";
        }
    }
}
