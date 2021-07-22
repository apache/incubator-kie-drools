/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.persistence.inmemory.postgresql.runtime;

import java.io.IOException;

import org.eclipse.microprofile.config.spi.ConfigSourceProvider;
import org.jboss.logging.Logger;

import io.quarkus.runtime.RuntimeValue;
import io.quarkus.runtime.annotations.Recorder;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;

import static org.kie.kogito.persistence.inmemory.postgresql.runtime.InmemoryPostgreSQLConfigSourceProvider.*;

@Recorder
public class InmemoryPostgreSQLRecorder {

    private static final Logger log = Logger.getLogger(InmemoryPostgreSQLRecorder.class);

    public RuntimeValue<Integer> startPostgres() throws IOException {
        EmbeddedPostgres pg = EmbeddedPostgres.start();
        log.infov("Embedded Postgres started at port \"{0,number,#}\" with database \"{1}\", user \"{2}\" and password \"{3}\"",
                pg.getPort(), DEFAULT_DATABASE, DEFAULT_USERNAME, DEFAULT_PASSWORD);
        return new RuntimeValue<>(pg.getPort());
    }

    public RuntimeValue<ConfigSourceProvider> configSources(RuntimeValue<Integer> port) {
        return new RuntimeValue<>(new InmemoryPostgreSQLConfigSourceProvider(port.getValue()));
    }
}
