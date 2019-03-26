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
import org.jbpm.xes.model.TraceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jbpm.xes.dataset.DataSetUtils.*;

public class TraceTypeMapper implements BiFunction<DataSet, Integer, TraceType> {

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String COLUMN_PROCESS_ID = "processId";
    public static final String COLUMN_USER_IDENTITY = "user_identity";
    public static final String COLUMN_CORRELATION_KEY = "correlationKey";
    public static final String COLUMN_PROCESS_VERSION = "processVersion";
    public static final String COLUMN_PROCESS_INSTANCE_DESCRIPTION = "processInstanceDescription";
    public static final String COLUMN_PARENT_PROCESS_INSTANCE_ID = "parentProcessInstanceId";
    public static final String COLUMN_START_DATE = "start_date";
    public static final String COLUMN_END_DATE = "end_date";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_SLA_DUE_DATE = "sla_due_date";
    public static final String COLUMN_SLA_COMPLIANCE = "slaCompliance";
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceTypeMapper.class);

    @Override
    public TraceType apply(DataSet dataSet,
                           Integer row) {
        final Long processInstanceId = getColumnLongValue(dataSet,
                                                          COLUMN_PROCESS_INSTANCE_ID,
                                                          row);

        final TraceType trace = new TraceType();

        trace.addStringType(
                "concept:name",
                processInstanceId.toString());

//        Custom jBPM attributes
        trace.addStringType(
                "jbpm:initiator",
                getColumnStringValue(dataSet,
                                     COLUMN_USER_IDENTITY,
                                     row));

        trace.addIntegerType(
                "jbpm:logid",
                getColumnLongValue(dataSet,
                                   COLUMN_ID,
                                   row));
        trace.addStringType(
                "jbpm:correlationkey",
                getColumnStringValue(dataSet,
                                     COLUMN_CORRELATION_KEY,
                                     row));

        trace.addStringType(
                "jbpm:version",
                getColumnStringValue(dataSet,
                                     COLUMN_PROCESS_VERSION,
                                     row));
        trace.addStringType(
                "jbpm:description",
                getColumnStringValue(dataSet,
                                     COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                     row));
        trace.addIntegerType(
                "jbpm:instanceid",
                processInstanceId);
        Long parent = getColumnLongValue(dataSet,
                                         COLUMN_PARENT_PROCESS_INSTANCE_ID,
                                         row);
        if (parent != -1) {
            trace.addIntegerType(
                    "jbpm:parentinstanceid",
                    parent);
        }
        trace.addDateType(
                "jbpm:start",
                getColumnDateValue(dataSet,
                                   COLUMN_START_DATE,
                                   row));

        trace.addDateType(
                "jbpm:end",
                getColumnDateValue(dataSet,
                                   COLUMN_END_DATE,
                                   row));
        trace.addStringType(
                "jbpm:status",
                new ProcessInstanceStatusMapper().apply(getColumnIntValue(dataSet,
                                                                          COLUMN_STATUS,
                                                                          row)));
        trace.addDateType(
                "jbpm:sladuedate",
                getColumnDateValue(dataSet,
                                   COLUMN_SLA_DUE_DATE,
                                   row));
        trace.addIntegerType(
                "jbpm:slacompliance",
                getColumnIntValue(dataSet,
                                  COLUMN_SLA_COMPLIANCE,
                                  row));

        LOGGER.debug("Generated trace object: {}",
                     trace);
        return trace;
    }
}
