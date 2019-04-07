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

package org.jbpm.xes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.jbpm.xes.dataset.DataSetService;
import org.jbpm.xes.mapper.*;
import org.jbpm.xes.model.LogType;
import org.jbpm.xes.model.TraceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.dashbuilder.dataset.filter.FilterFactory.equalsTo;
import static org.dashbuilder.dataset.filter.FilterFactory.in;
import static org.jbpm.xes.dataset.DataSetUtils.getColumnLongValue;
import static org.jbpm.xes.mapper.EventTypeMapper.COLUMN_NODE_TYPE;
import static org.jbpm.xes.mapper.EventTypeMapper.COLUMN_TYPE;
import static org.jbpm.xes.mapper.TraceTypeMapper.COLUMN_PROCESS_INSTANCE_ID;

public class XESExportServiceImpl implements XESExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(XESExportServiceImpl.class);

    private DataSetService dataSetService;
    private XESLogMarshaller marshaller = new XESLogMarshaller();

    public void setDataSetService(DataSetService dataSetService) {
        this.dataSetService = dataSetService;
    }

    @Override
    public String export(final XESProcessFilter filter) throws Exception {
        if (filter == null || isNullOrEmpty(filter.getProcessId())) {
            throw new RuntimeException("Process Id must be provided for filtering the logs");
        }

        LOGGER.info("Starting XES export...");
        LOGGER.debug("XES filter: {}", filter);
//      TODO: Include variables from tasks and processes

        final LocalDateTime start = LocalDateTime.now();

        final List<ColumnFilter> filters = new ColumnFilterMapper().apply(filter);
        final DataSet tracesDataSet = dataSetService.findTraces(filters.toArray(new ColumnFilter[filters.size()]));
        LOGGER.debug("Found {} process instances to export.", tracesDataSet.getRowCount());

        if (tracesDataSet.getRowCount() == 0) {
            LOGGER.warn("Could not find any process instance to export, please review filter: {}", filter);
            return null;
        }

        final LogType log = new LogTypeMapper().apply(null, filter.getProcessId());

        final Map<Long, TraceType> instances = new HashMap<>();
        List<TraceType> traces = IntStream.range(0, tracesDataSet.getRowCount()).boxed().map(row -> {
            Long pId = getColumnLongValue(tracesDataSet, COLUMN_PROCESS_INSTANCE_ID, row);
            TraceType trace = new TraceTypeMapper().apply(tracesDataSet, row);
            instances.put(pId, trace);
            return trace;
        }).collect(toList());

        log.getTrace().addAll(traces);

        final List<ColumnFilter> eventFilters = getEventsColumnFilter(new ArrayList<>(instances.keySet()), filter);
        final DataSet eventsDataSet = dataSetService.findEvents(eventFilters.toArray(new ColumnFilter[eventFilters.size()]));

        LOGGER.debug("Found {} events to export.", eventsDataSet.getRowCount());
        IntStream.range(0, eventsDataSet.getRowCount()).boxed().forEach(row -> {
            Long pId = getColumnLongValue(eventsDataSet, COLUMN_PROCESS_INSTANCE_ID, row);
            instances.get(pId).getEvent().add(new EventTypeMapper().apply(eventsDataSet, row));
        });

        final String xml = marshaller.marshall(log);

        LOGGER.info("XES exported finished in {} seconds", Duration.between(start, LocalDateTime.now()).getSeconds());

        return xml;
    }

    protected List<ColumnFilter> getEventsColumnFilter(final List<Long> pInstances, final XESProcessFilter filter) {
        List<ColumnFilter> filters = new ArrayList<>();
        filters.add(in(COLUMN_PROCESS_INSTANCE_ID, pInstances));
        if (filter.isAllNodeTypes() == false) {
            filters.add(in(COLUMN_NODE_TYPE, asList("HumanTaskNode",
                                                    "WorkItemNode",
                                                    "RuleSetNode",
                                                    "SubProcessNode",
                                                    "MilestoneNode",
                                                    "ActionNode")));
        }
        if (filter.getNodeInstanceLogType() != null) {
            filters.add(equalsTo(COLUMN_TYPE, filter.getNodeInstanceLogType()));
        }
        return filters;
    }
}
