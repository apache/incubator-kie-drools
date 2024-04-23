/**
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
package org.drools.model.codegen.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;

import static org.drools.compiler.kie.builder.impl.KieBuilderImpl.setDefaultsforEmptyKieModule;

/**
 * Utility class to discover/interact with KieModuleModel.
 *
 */
public class KieModuleModelWrapper {
    private KieModuleModel kieModuleModel;

    public KieModuleModelWrapper(KieModuleModel kieModuleModel) {
        this.kieModuleModel = kieModuleModel;
        setDefaultsforEmptyKieModule(kieModuleModel);
    }

    static KieModuleModelWrapper fromResourcePaths(Path[] resourcePaths) {
        return new KieModuleModelWrapper(lookupKieModuleModel(resourcePaths));
    }

    private static KieModuleModel lookupKieModuleModel(Path[] resourcePaths) {
        for (Path resourcePath : resourcePaths) {
            if (resourcePath.toString().endsWith(".jar")) {
                InputStream inputStream = fromJarFile(resourcePath);
                if (inputStream != null) {
                    return KieModuleModelImpl.fromXML(inputStream);
                }
            } else {
                Path moduleXmlPath = resourcePath.resolve(KieModuleModelImpl.KMODULE_JAR_PATH.asString());
                if (Files.exists(moduleXmlPath)) {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(Files.readAllBytes(moduleXmlPath))) {
                        return KieModuleModelImpl.fromXML(bais);
                    } catch (IOException e) {
                        throw new UncheckedIOException("Impossible to open " + moduleXmlPath, e);
                    }
                }
            }
        }

        return new KieModuleModelImpl();
    }

    static boolean hasKieModule(Path[] resourcePaths) {
        for (Path resourcePath : resourcePaths) {
            if (resourcePath.toString().endsWith(".jar")) {
                InputStream inputStream = fromJarFile(resourcePath);
                if (inputStream != null) {
                    return true;
                }
            } else {
                Path moduleXmlPath = resourcePath.resolve(KieModuleModelImpl.KMODULE_JAR_PATH.asString());
                if (Files.exists(moduleXmlPath)) {
                    return true;
                }
            }
        }
        return false;
    }

    /*
     * This is really a modified duplicate of org.drools.quarkus.deployment.ResourceCollector#fromJarFile(java.nio.file.Path).
     * TODO: Refactor https://issues.redhat.com/browse/DROOLS-7254
     */
    public static InputStream fromJarFile(Path jarPath) {
        try (ZipFile zipFile = new ZipFile(jarPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith("kmodule.xml")) {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    return new ByteArrayInputStream(inputStream.readAllBytes());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return null; // cannot find such file
    }

    Map<String, KieBaseModel> kieBaseModels() {
        return kieModuleModel.getKieBaseModels();
    }


}
