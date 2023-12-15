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
package $Package$;

@org.springframework.web.context.annotation.ApplicationScope
@org.springframework.stereotype.Component
public class Processes implements org.kie.kogito.process.Processes {

    @org.springframework.beans.factory.annotation.Autowired
    java.util.Collection<org.kie.kogito.process.Process<? extends org.kie.kogito.Model>> processes;

    private java.util.Map<String, org.kie.kogito.process.Process<? extends org.kie.kogito.Model>> mappedProcesses = new java.util.HashMap<>();

    @jakarta.annotation.PostConstruct
    public void setup() {
        for (org.kie.kogito.process.Process<? extends org.kie.kogito.Model> process : processes) {
            mappedProcesses.put(process.id(), process);
        }
    }

    public org.kie.kogito.process.Process<? extends org.kie.kogito.Model> processById(String processId) {
        return mappedProcesses.get(processId);
    }

    public java.util.Collection<String> processIds() {
        return mappedProcesses.keySet();
    }
}
