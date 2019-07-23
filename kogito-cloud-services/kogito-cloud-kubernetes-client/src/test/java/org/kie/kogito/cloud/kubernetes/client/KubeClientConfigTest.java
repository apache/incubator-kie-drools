/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.cloud.kubernetes.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class KubeClientConfigTest {

    public static final Path KUBE_CONFIG_PATH = Paths.get(System.getProperty("user.home") + "/.kube/config");
    public static final Path KUBE_CONFIG_DIR = Paths.get(System.getProperty("user.home") + "/.kube");

    @BeforeEach
    public void setup() throws IOException {
        if (!Files.exists(KUBE_CONFIG_PATH)) {
            if (!Files.exists(KUBE_CONFIG_DIR)) {
                Files.createDirectories(KUBE_CONFIG_DIR);
            }
            Files.createFile(KUBE_CONFIG_PATH);
        }
    }

    @AfterEach
    public void teardown() throws IOException {
        if (Files.exists(KUBE_CONFIG_PATH) && Files.readAllBytes(KUBE_CONFIG_PATH).length == 0) {
            Files.delete(KUBE_CONFIG_PATH);
            Files.delete(KUBE_CONFIG_DIR);
        }
        System.clearProperty("kubernetes.master");
    }

    @Test
    public void whenCreateNewConfigurationIgnoresKubeConfigFile() {
        final KogitoKubeConfig config = new KogitoKubeConfig();
        assertThat(config.getHttpClient(), notNullValue());
        assertThat(config.getMasterUrl().toString(), containsString("kubernetes.default.svc"));
    }

    @Test
    public void whenGetFromSystemProps() {
        System.setProperty("kubernetes.master", "localhost");
        final KogitoKubeConfig config = new KogitoKubeConfig();
        assertThat(config.getMasterUrl().toString(), containsString("localhost"));
    }

}
