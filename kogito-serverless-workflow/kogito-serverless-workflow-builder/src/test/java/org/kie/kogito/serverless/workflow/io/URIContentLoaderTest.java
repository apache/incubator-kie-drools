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
package org.kie.kogito.serverless.workflow.io;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;
import org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.Builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.builder;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.compoundURI;
import static org.kie.kogito.serverless.workflow.io.URIContentLoaderFactory.readString;

class URIContentLoaderTest {

    @Test
    void testExistingFile() throws URISyntaxException {
        assertThat(readString(builder(Thread.currentThread().getContextClassLoader().getResource("pepe.txt").toURI()))).isEqualTo("my name is javierito");
    }

    @Test
    void testExistingClasspath() {
        assertThat(readString(builder("classpath:pepe.txt"))).isEqualTo("my name is javierito");
    }

    @Test
    void testNotExistingFile() {
        Builder builder = builder("file:/noPepe.txt");
        assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> readString(builder));
    }

    @Test
    void testNotExistingClasspath() {
        Builder builder = builder("classpath:/noPepe.txt");
        assertThatExceptionOfType(UncheckedIOException.class).isThrownBy(() -> readString(builder));
    }

    @Test
    void testCompoundURI() {
        assertThat(compoundURI(URI.create("classpath:pepe.json"), URI.create("pepa.json"))).isEqualTo(URI.create("classpath:/pepa.json"));
        assertThat(compoundURI(URI.create("classpath:pepe.json"), URI.create("file:///pepa.json"))).isEqualTo(URI.create("file:///pepa.json"));
        assertThat(compoundURI(URI.create("classpath:schema/pepe.json"), URI.create("/pepa.json"))).isEqualTo(URI.create("classpath:/pepa.json"));
        assertThat(compoundURI(URI.create("classpath:schema/pepe.json"), URI.create("pepa.json"))).isEqualTo(URI.create("classpath:/schema/pepa.json"));
        assertThat(compoundURI(URI.create("pepe.json"), URI.create("pepa.json"))).isEqualTo(URI.create("file:///pepa.json"));
        assertThat(compoundURI(URI.create("schema/pepe.json"), URI.create("pepa.json"))).isEqualTo(URI.create("file:///schema/pepa.json"));
        assertThat(compoundURI(URI.create("schema/pepe.json"), URI.create("/pepa.json"))).isEqualTo(URI.create("file:///pepa.json"));
        assertThat(compoundURI(URI.create("pepe.json"), URI.create("classpath:pepa.json"))).isEqualTo(URI.create("classpath:pepa.json"));
    }
}
