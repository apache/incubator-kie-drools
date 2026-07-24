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
package org.kie.kogito.app.audit.jpa.queries.mapper;

import java.util.ArrayList;
import java.util.List;

import org.kie.kogito.app.audit.graphql.type.ProcessInstanceVariableHistoryTO;
import org.kie.kogito.app.audit.graphql.type.ProcessInstanceVariableTO;
import org.kie.kogito.app.audit.jpa.queries.DataMapper;

import graphql.com.google.common.base.Objects;

public class ProcessInstanceVariableHistoryTOMapper implements DataMapper<ProcessInstanceVariableHistoryTO, Object[]> {

    PojoMapper<ProcessInstanceVariableTO> mapper;

    public ProcessInstanceVariableHistoryTOMapper() {
        mapper = new PojoMapper<>(ProcessInstanceVariableTO.class);
    }

    @Override
    public List<ProcessInstanceVariableHistoryTO> produce(List<Object[]> rows) {
        List<ProcessInstanceVariableTO> data = mapper.produce(rows);
        List<ProcessInstanceVariableHistoryTO> transformedData = new ArrayList<>();
        ProcessInstanceVariableHistoryTO current = null;
        Object currentIndex = null;
        for (int idx = 0; idx < data.size(); idx++) {
            ProcessInstanceVariableTO row = data.get(idx);
            if (!Objects.equal(currentIndex, row.getVariableId())) {
                current = new ProcessInstanceVariableHistoryTO();
                current.setVariableId(row.getVariableId());
                current.setVariableName(row.getVariableName());
                currentIndex = row.getVariableId();
                transformedData.add(current);
            }
            current.addLog(row);
        }

        return transformedData;
    }
}
