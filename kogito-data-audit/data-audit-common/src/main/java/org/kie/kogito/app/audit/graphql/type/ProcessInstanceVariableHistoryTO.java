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
package org.kie.kogito.app.audit.graphql.type;

import java.util.ArrayList;
import java.util.List;

public class ProcessInstanceVariableHistoryTO {

    private String variableId;

    private String variableName;

    private List<ProcessInstanceVariableTO> logs;

    public ProcessInstanceVariableHistoryTO() {
        logs = new ArrayList<>();
    }

    public String getVariableId() {
        return variableId;
    }

    public void setVariableId(String variableId) {
        this.variableId = variableId;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public List<ProcessInstanceVariableTO> getLogs() {
        return logs;
    }

    public void setLogs(List<ProcessInstanceVariableTO> logs) {
        this.logs = logs;
    }

    public void addLog(ProcessInstanceVariableTO log) {
        logs.add(log);
    }

}
