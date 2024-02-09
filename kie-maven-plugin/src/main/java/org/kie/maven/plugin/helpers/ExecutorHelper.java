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
package org.kie.maven.plugin.helpers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.plugin.logging.Log;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.kie.memorycompiler.JavaConfiguration;

public class ExecutorHelper {

    private ExecutorHelper() {
    }

    public static void setSystemProperties(Map<String, String> properties, Log log) {

        if (properties != null) {
            log.debug("Additional system properties: " + properties);
            for (Map.Entry<String, String> property : properties.entrySet()) {
                if (property.getKey().equals(JavaConfiguration.JAVA_LANG_LEVEL_PROPERTY)) {
                    log.warn("It seems you are setting `" + 
                            JavaConfiguration.JAVA_LANG_LEVEL_PROPERTY + 
                            "` while building a KJAR in a Maven-based build." +
                            " It is recommended to properly set `maven.compiler.release` instead.");
                }
                System.setProperty(property.getKey(), property.getValue());
            }
            log.debug("Configured system properties were successfully set.");
        }
    }

    public static List<String> getFilesByType(InternalKieModule kieModule, String fileType) {
        return kieModule.getFileNames()
                .stream()
                .filter(f -> f.endsWith(fileType))
                .collect(Collectors.toList());
    }
}
