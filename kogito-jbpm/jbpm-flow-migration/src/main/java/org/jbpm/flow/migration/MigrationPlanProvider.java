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
package org.jbpm.flow.migration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jbpm.flow.migration.model.MigrationPlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrationPlanProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MigrationPlanProvider.class);

    private List<MigrationPlanFileReader> fileReaders;
    private List<MigrationPlanFileProvider> fileProviders;

    public MigrationPlanProvider() {
        fileReaders = new ArrayList<>();
        fileProviders = new ArrayList<>();
    }

    public List<MigrationPlan> findMigrationPlans() {
        String[] extensions = fileReaders.stream().map(MigrationPlanFileReader::getFileExtension).toArray(String[]::new);

        List<MigrationPlanFile> migrationPlanFiles = new ArrayList<>();
        for (MigrationPlanFileProvider provider : fileProviders) {
            migrationPlanFiles.addAll(provider.listMigrationPlanFiles(extensions));
        }

        List<MigrationPlan> migrationPlans = new ArrayList<>();
        for (MigrationPlanFile mpf : migrationPlanFiles) {
            try {
                migrationPlans.add(readMigrationPlan(mpf));
            } catch (IOException e) {
                LOGGER.error("Error trying to read migration plan {}", mpf.getPath(), e);
            }
        }

        return migrationPlans;
    }

    private MigrationPlan readMigrationPlan(MigrationPlanFile mpf) throws IOException {
        try (InputStream is = new ByteArrayInputStream(mpf.getContent())) {
            for (MigrationPlanFileReader reader : fileReaders) {
                if (reader.accept(mpf.getPath())) {
                    return reader.read(is);
                }
            }
        }
        throw new MigrationPlanFileFormatException("Not file format reader found for " + mpf.getPath());

    }

    public static class MigrationPlanProviderBuilder {

        private MigrationPlanProvider migrationPlanProvider;

        public MigrationPlanProviderBuilder() {
            this.migrationPlanProvider = new MigrationPlanProvider();
        }

        public MigrationPlanProviderBuilder withEnvironmentDefaults() {
            migrationPlanProvider.fileReaders.addAll(MigrationPlanFileReader.findMigrationPlanFileReaders());
            migrationPlanProvider.fileProviders.addAll(MigrationPlanFileProvider.findMigrationPlanFileProviders());
            return this;
        }

        public MigrationPlanProviderBuilder withMigrationPlanFileReader(MigrationPlanFileReader migrationPlanFileReader) {
            migrationPlanProvider.fileReaders.add(migrationPlanFileReader);
            return this;
        }

        public MigrationPlanProviderBuilder withMigrationPlanFileProvider(MigrationPlanFileProvider MigrationPlanFileProvider) {
            migrationPlanProvider.fileProviders.add(MigrationPlanFileProvider);
            return this;
        }

        public MigrationPlanProvider build() {
            return migrationPlanProvider;
        }

    }

    public static MigrationPlanProviderBuilder newMigrationPlanProviderBuilder() {
        return new MigrationPlanProviderBuilder();
    }

}
