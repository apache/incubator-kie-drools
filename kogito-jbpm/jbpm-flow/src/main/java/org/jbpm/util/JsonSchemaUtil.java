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
package org.jbpm.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemHandler;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.ProcessInstanceNotFoundException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.workitem.LifeCyclePhase;
import org.kie.kogito.process.workitem.Policy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonSchemaUtil {

    private JsonSchemaUtil() {
    }

    private static ObjectMapper mapper = new ObjectMapper();
    private static Path jsonDir = Paths.get("META-INF", "jsonSchema");

    public static String getJsonSchemaName(String processId, String taskName) {
        return (processId + "_" + taskName).replace('.', '#').replaceAll("\\s", "_");
    }

    public static Path getJsonDir() {
        return jsonDir;
    }

    public static String getFileName(String key) {
        return key + ".json";
    }

    public static Map<String, Object> load(ClassLoader cl, String processId, String taskName) {
        Path jsonFile = jsonDir.resolve(getFileName(getJsonSchemaName(processId, taskName)));
        try (InputStream in = cl.getResourceAsStream(jsonFile.toString())) {
            if (in == null) {
                throw new IllegalArgumentException("Cannot find file " + jsonFile + " in classpath");
            }
            return load(in);
        } catch (IOException io) {
            throw new IllegalStateException("Error loading schema " + jsonFile, io);
        }
    }

    public static Map<String, Object> load(InputStream in) throws IOException {
        return mapper.readValue(in, new TypeReference<Map<String, Object>>() {
        });
    }

    public static <T> Map<String, Object> addPhases(Process<T> process,
            Application application,
            String processInstanceId,
            String workItemId,
            Policy<?>[] policies,
            Map<String, Object> jsonSchema) {
        return process.instances().findById(processInstanceId, ProcessInstanceReadMode.READ_ONLY).map(pi -> {
            jsonSchema
                    .put(
                            "phases",
                            allowedPhases(
                                    application.config().get(ProcessConfig.class).workItemHandlers().forName("Human Task"),
                                    pi.workItem(workItemId, policies)));
            return jsonSchema;
        }).orElseThrow(() -> new ProcessInstanceNotFoundException(processInstanceId));
    }

    public static Set<String> allowedPhases(WorkItemHandler handler, WorkItem workItem) {
        return HumanTaskWorkItemHandler.allowedPhases(handler, workItem.getPhase()).map(LifeCyclePhase::id).collect(Collectors.toSet());
    }
}
