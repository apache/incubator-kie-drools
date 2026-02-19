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

import org.kie.kogito.app.audit.graphql.type.UserTaskInstanceDeadlineTO;
import org.kie.kogito.app.audit.jpa.queries.DataMapper;

import graphql.com.google.common.base.Objects;

import static org.kie.kogito.app.audit.jpa.queries.mapper.DateTimeUtil.toDateTime;

public class UserTaskInstanceDeadlineTOMapper implements DataMapper<UserTaskInstanceDeadlineTO, Object[]> {

    @Override
    public List<UserTaskInstanceDeadlineTO> produce(List<Object[]> data) {
        List<UserTaskInstanceDeadlineTO> transformedData = new ArrayList<>();
        UserTaskInstanceDeadlineTO current = null;
        Object currentIndex = null;
        for (int idx = 0; idx < data.size(); idx++) {
            Object[] row = data.get(idx);
            if (!Objects.equal(currentIndex, row[0])) {
                current = new UserTaskInstanceDeadlineTO();
                currentIndex = row[0];
                transformedData.add(current);
            }
            current.setEventId((String) row[0]);
            current.setEventDate(toDateTime(row[1]));
            current.setUserTaskDefinitionId((String) row[2]);
            current.setUserTaskInstanceId((String) row[3]);
            current.setProcessInstanceId((String) row[4]);
            current.setBusinessKey((String) row[5]);
            current.setEventType((String) row[6]);
            current.addNotification((String) row[7], (String) row[8]);

        }

        return transformedData;
    }
}
