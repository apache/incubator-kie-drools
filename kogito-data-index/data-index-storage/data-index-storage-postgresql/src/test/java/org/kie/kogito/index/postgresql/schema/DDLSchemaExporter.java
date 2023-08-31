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

package org.kie.kogito.index.postgresql.schema;

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
import org.kie.kogito.index.postgresql.model.AttachmentEntity;
import org.kie.kogito.index.postgresql.model.CommentEntity;
import org.kie.kogito.index.postgresql.model.JobEntity;
import org.kie.kogito.index.postgresql.model.MilestoneEntity;
import org.kie.kogito.index.postgresql.model.NodeInstanceEntity;
import org.kie.kogito.index.postgresql.model.ProcessDefinitionEntity;
import org.kie.kogito.index.postgresql.model.ProcessInstanceEntity;
import org.kie.kogito.index.postgresql.model.ProcessInstanceErrorEntity;
import org.kie.kogito.index.postgresql.model.UserTaskInstanceEntity;
import org.kie.kogito.testcontainers.KogitoPostgreSqlContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public class DDLSchemaExporter {

    public static void main(String[] args) {
        try (KogitoPostgreSqlContainer postgresql = new KogitoPostgreSqlContainer()) {
            postgresql.waitingFor(Wait.forListeningPort());
            postgresql.start();
            Map<String, String> settings = new HashMap<>();
            settings.put(Environment.URL, postgresql.getJdbcUrl());
            settings.put(Environment.USER, postgresql.getUsername());
            settings.put(Environment.PASS, postgresql.getPassword());
            settings.put(Environment.PHYSICAL_NAMING_STRATEGY, "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(settings).build();

            MetadataSources metadataSources = new MetadataSources(serviceRegistry);
            metadataSources.addAnnotatedClass(ProcessDefinitionEntity.class);
            metadataSources.addAnnotatedClass(JobEntity.class);
            metadataSources.addAnnotatedClass(MilestoneEntity.class);
            metadataSources.addAnnotatedClass(NodeInstanceEntity.class);
            metadataSources.addAnnotatedClass(ProcessInstanceEntity.class);
            metadataSources.addAnnotatedClass(ProcessInstanceErrorEntity.class);
            metadataSources.addAnnotatedClass(AttachmentEntity.class);
            metadataSources.addAnnotatedClass(CommentEntity.class);
            metadataSources.addAnnotatedClass(UserTaskInstanceEntity.class);
            Metadata metadata = metadataSources.buildMetadata();

            SchemaExport schemaExport = new SchemaExport();
            schemaExport.setDelimiter(";");
            schemaExport.setFormat(true);
            schemaExport.setOverrideOutputFileContent();
            schemaExport.setOutputFile("src/main/resources/data_index_create.sql");
            schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);
            schemaExport.getExceptions().forEach(System.err::println);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
