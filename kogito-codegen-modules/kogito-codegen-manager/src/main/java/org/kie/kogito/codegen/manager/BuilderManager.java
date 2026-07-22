/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.kie.kogito.codegen.manager;

import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.manager.util.CodeGenManagerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuilderManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuilderManager.class);

    public interface KogitoBuildContextInfo {
        Path projectBaseAbsolutePath();

        CodeGenManagerUtil.Framework framework();

        boolean enablePersistence();

        Set<URI> projectFilesUris();
    }

    public record BuildInfo(Set<URI> projectFilesUris,
            Path projectBaseAbsolutePath, //MUST BE ABSOLUTE
            Path outputDirectory,
            String projectGroupId,
            String projectArtifactId,
            String projectVersion,
            String javaSourceEncoding,
            String javaVersion,
            String jsonSchemaVersion,
            boolean generatePartial,
            boolean enablePersistence,
            boolean keepSources,
            List<String> runtimeClassPathElements,
            CodeGenManagerUtil.Framework framework,
            Map<String, String> properties) implements KogitoBuildContextInfo {
    }

    public static void build(BuildInfo buildInfo) throws MalformedURLException {
        executeConfigurationLog(buildInfo);
        LOGGER.info("Building project: {}:{}:{}", buildInfo.projectGroupId(), buildInfo.projectArtifactId(), buildInfo.projectVersion());
        CodeGenManagerUtil.setSystemProperties(buildInfo.properties());
        ClassLoader projectClassLoader = CodeGenManagerUtil.projectClassLoader(buildInfo.projectFilesUris());
        KogitoGAV kogitoGAV = new KogitoGAV(buildInfo.projectGroupId(), buildInfo.projectArtifactId(), buildInfo.projectVersion());
        KogitoBuildContext kogitoBuildContext = getKogitoBuildContext(projectClassLoader, kogitoGAV, buildInfo);
        GenerateModelHelper.GenerateModelInfo generateModelInfo = new GenerateModelHelper.GenerateModelInfo(projectClassLoader,
                kogitoBuildContext, buildInfo);
        GenerateModelHelper.generateModel(generateModelInfo);
        LOGGER.info("Project build done");
    }

    static void executeConfigurationLog(BuildInfo buildInfo) {
        LOGGER.info("========================================");
        LOGGER.info("  Kogito Code Generation Configuration");
        LOGGER.info("========================================");
        LOGGER.info("  Java Version        : {}", buildInfo.javaVersion());
        LOGGER.info("  Source Encoding     : {}", buildInfo.javaSourceEncoding());
        LOGGER.info("  Base Directory      : {}", buildInfo.projectBaseAbsolutePath());
        LOGGER.info("  Output Directory    : {}", buildInfo.outputDirectory());
        LOGGER.info("  JSON Schema Version : {}", buildInfo.jsonSchemaVersion() != null ? buildInfo.jsonSchemaVersion() : "nd");
        LOGGER.info("  Persistence Enabled : {}", buildInfo.enablePersistence());
        LOGGER.info("  Keep Sources        : {}", buildInfo.keepSources());
        LOGGER.info("  Framework           : {}", buildInfo.framework());
        LOGGER.info("========================================");
    }

    static KogitoBuildContext getKogitoBuildContext(ClassLoader projectClassLoader, KogitoGAV kogitoGAV, KogitoBuildContextInfo kogitoBuildContextInfo) {
        return CodeGenManagerUtil.discoverKogitoRuntimeContext(projectClassLoader, kogitoBuildContextInfo.projectBaseAbsolutePath(), kogitoGAV,
                new CodeGenManagerUtil.ProjectParameters(kogitoBuildContextInfo.framework(), "", "", "", "", kogitoBuildContextInfo.enablePersistence()),
                className -> {
                    try {
                        return CodeGenManagerUtil.isClassNameInUrlClassLoader(kogitoBuildContextInfo.projectFilesUris(), className);
                    } catch (MalformedURLException e) {
                        throw new IllegalArgumentException(e);
                    }
                });
    }

}
