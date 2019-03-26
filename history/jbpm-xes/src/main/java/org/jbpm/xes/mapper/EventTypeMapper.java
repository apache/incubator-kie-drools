/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.xes.mapper;

import java.util.function.BiFunction;

import org.dashbuilder.dataset.DataSet;
import org.jbpm.xes.model.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.jbpm.xes.dataset.DataSetUtils.*;

public class EventTypeMapper implements BiFunction<DataSet, Integer, EventType> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventTypeMapper.class);

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LOG_DATE = "log_date";
    public static final String COLUMN_NODE_NAME = "nodeName";
    public static final String COLUMN_NODE_TYPE = "nodeType";
    public static final String COLUMN_NODE_INSTANCE_ID = "nodeInstanceId";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NODE_ID = "nodeId";
    public static final String COLUMN_WORK_ITEM_ID = "workItemId";
    public static final String COLUMN_CREATED_BY = "createdBy";
    public static final String COLUMN_ACTUAL_OWNER = "actualOwner";

    @Override
    public EventType apply(DataSet dataSet,
                           Integer row) {
        EventType event = new EventType();
        event.addDateType(
                "time:timestamp",
                getColumnDateValue(dataSet,
                                   COLUMN_LOG_DATE,
                                   row));

        String nodeName = getColumnStringValue(dataSet,
                                               COLUMN_NODE_NAME,
                                               row);

        String nodeType = getColumnStringValue(dataSet,
                                               COLUMN_NODE_TYPE,
                                               row);

        event.addStringType(
                "concept:name",
                nodeName.trim().isEmpty() ? nodeType : nodeName);

        String nodeInstanceId = getColumnStringValue(dataSet,
                                                     COLUMN_NODE_INSTANCE_ID,
                                                     row);

        event.addStringType(
                "concept:instance",
                nodeInstanceId);

        Integer type = getColumnIntValue(dataSet,
                                         COLUMN_TYPE,
                                         row);

        event.addStringType(
                "lifecycle:transition",
                type == 0 ? "start" : "complete");

//        Custom jBPM attributes
        event.addStringType(
                "jbpm:nodeinstanceid",
                nodeInstanceId);
        event.addIntegerType(
                "jbpm:logid",
                getColumnLongValue(dataSet,
                                   COLUMN_ID,
                                   row));
        event.addStringType(
                "jbpm:nodeid",
                getColumnStringValue(dataSet,
                                     COLUMN_NODE_ID,
                                     row));
        event.addStringType(
                "jbpm:nodename",
                nodeName);
        Integer workItemId = getColumnIntValue(dataSet,
                                               COLUMN_WORK_ITEM_ID,
                                               row);
        event.addIntegerType(
                "jbpm:workitemid",
                workItemId);
        event.addStringType(
                "jbpm:nodetype",
                nodeType);

        String resource = null;
        if ("HumanTaskNode".equals(nodeType) && workItemId != null) {
            resource = type == 0 ?
                    getColumnStringValue(dataSet,
                                         COLUMN_CREATED_BY,
                                         row) :
                    getColumnStringValue(dataSet,
                                         COLUMN_ACTUAL_OWNER,
                                         row);
        }

        event.addStringType("org:resource",
                            isNullOrEmpty(resource) ? "jbpm" : resource);

        LOGGER.debug("Generated event object: {}",
                     event);
        return event;
    }
}
