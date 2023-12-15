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
package org.kie.kogito.persistence.schema;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.kie.kogito.persistence.oracle.model.CacheEntity;
import org.kie.kogito.testcontainers.Constants;
import org.kie.kogito.testcontainers.KogitoOracleSqlContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class DDLSchemaExporter {

    public static void main(String[] args) {
        try (KogitoOracleSqlContainer oracle = new KogitoOracleSqlContainer()) {
            oracle.waitingFor(Wait.forListeningPort()).withStartupTimeout(Constants.CONTAINER_START_TIMEOUT);
            oracle.start();
            Map<String, Object> settings = new HashMap<>();
            settings.put(Environment.URL, oracle.getJdbcUrl());
            settings.put(Environment.USER, oracle.getUsername());
            settings.put(Environment.PASS, oracle.getPassword());

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(settings).build();

            MetadataSources metadataSources = new MetadataSources(serviceRegistry);
            metadataSources.addAnnotatedClass(CacheEntity.class);
            Metadata metadata = metadataSources.buildMetadata();

            SchemaExport schemaExport = new SchemaExport();
            schemaExport.setDelimiter(";");
            schemaExport.setFormat(true);
            schemaExport.setOutputFile("src/main/resources/cache_entity_create.sql");
            schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
            schemaExport.getExceptions().forEach(System.err::println);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
