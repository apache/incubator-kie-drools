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
package org.kie.kogito.grafana;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;

import static org.assertj.core.api.Assertions.assertThat;

public class GrafanaConfigurationWriterTest {

    @Test
    public void testGrafanaDashboardName() {
        KogitoGAV gav = new KogitoGAV("groupId", "artifactId", "versionId");
        String handlerName = "myHandler";
        String expected = "artifactId_versionId - myHandler";

        String dashboardName = GrafanaConfigurationWriter.buildDashboardName(Optional.of(gav), handlerName);

        assertThat(dashboardName).isEqualTo(expected);
    }

    @Test
    public void testGrafanaDashboardNameWithEmptyGav() {
        String handlerName = "myHandler";
        String expected = "myHandler";

        String dashboardName = GrafanaConfigurationWriter.buildDashboardName(Optional.empty(), handlerName);

        assertThat(dashboardName).isEqualTo(expected);
    }
}
