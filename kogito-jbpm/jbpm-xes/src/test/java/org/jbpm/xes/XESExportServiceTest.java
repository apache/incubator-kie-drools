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

import java.util.Arrays;
import java.util.Date;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.impl.DataSetImpl;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.xes.dataset.DataSetService;
import org.jbpm.xes.mapper.EventTypeMapper;
import org.jbpm.xes.mapper.TraceTypeMapper;
import org.jbpm.xes.model.LogType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.process.ProcessInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.jbpm.xes.mapper.EventTypeMapper.*;
import static org.jbpm.xes.mapper.TraceTypeMapper.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XESExportServiceTest {

    @Mock
    private DataSetService dataSetService;

    @InjectMocks
    private XESExportServiceImpl xesExportService;

    @Test
    public void testExport() throws Exception {
        final DataSetImpl tracesDataSet = new DataSetImpl();
        final Integer processInstanceId = 1;
        tracesDataSet.addColumn(COLUMN_PROCESS_INSTANCE_ID,
                                ColumnType.NUMBER,
                                singletonList(processInstanceId));
        tracesDataSet.addColumn(TraceTypeMapper.COLUMN_ID,
                                ColumnType.NUMBER,
                                singletonList(1));
        tracesDataSet.addColumn(COLUMN_USER_IDENTITY,
                                ColumnType.LABEL,
                                singletonList("admin"));
        tracesDataSet.addColumn(COLUMN_CORRELATION_KEY,
                                ColumnType.LABEL,
                                singletonList(1));
        tracesDataSet.addColumn(COLUMN_PROCESS_VERSION,
                                ColumnType.LABEL,
                                singletonList("1.0"));
        tracesDataSet.addColumn(COLUMN_PROCESS_INSTANCE_DESCRIPTION,
                                ColumnType.LABEL,
                                singletonList("Evaluation"));
        tracesDataSet.addColumn(COLUMN_PARENT_PROCESS_INSTANCE_ID,
                                ColumnType.NUMBER,
                                singletonList(-1));
        tracesDataSet.addColumn(COLUMN_STATUS,
                                ColumnType.NUMBER,
                                singletonList(ProcessInstance.STATE_ACTIVE));
        tracesDataSet.addColumn(COLUMN_SLA_COMPLIANCE,
                                ColumnType.NUMBER,
                                singletonList(0));
        when(dataSetService.findTraces(any())).thenReturn(tracesDataSet);

        final DataSetImpl eventsDataSet = new DataSetImpl();
        eventsDataSet.addColumn(COLUMN_TYPE,
                                ColumnType.NUMBER,
                                Arrays.asList(NodeInstanceLog.TYPE_ENTER,
                                              NodeInstanceLog.TYPE_EXIT));
        eventsDataSet.addColumn(COLUMN_PROCESS_INSTANCE_ID,
                                ColumnType.NUMBER,
                                Arrays.asList(processInstanceId,
                                              processInstanceId));
        eventsDataSet.addColumn(COLUMN_LOG_DATE,
                                ColumnType.DATE,
                                Arrays.asList(new Date(),
                                              new Date()));
        eventsDataSet.addColumn(COLUMN_NODE_NAME,
                                ColumnType.LABEL,
                                Arrays.asList("",
                                              ""));
        eventsDataSet.addColumn(COLUMN_NODE_TYPE,
                                ColumnType.LABEL,
                                Arrays.asList("StartNode",
                                              "StartNode"));
        eventsDataSet.addColumn(COLUMN_NODE_INSTANCE_ID,
                                ColumnType.LABEL,
                                Arrays.asList("1",
                                              "1"));
        eventsDataSet.addColumn(EventTypeMapper.COLUMN_ID,
                                ColumnType.NUMBER,
                                Arrays.asList(1,
                                              2));
        eventsDataSet.addColumn(COLUMN_NODE_ID,
                                ColumnType.LABEL,
                                Arrays.asList("_09AE0EB5-703B-4439-A83D-C92A10C28F63",
                                              "_09AE0EB5-703B-4439-A83D-C92A10C28F63"));
        eventsDataSet.addColumn(COLUMN_WORK_ITEM_ID,
                                ColumnType.NUMBER,
                                Arrays.asList(null,
                                              null));

        when(dataSetService.findEvents(anyVararg())).thenReturn(eventsDataSet);

        String xml = xesExportService.export(XESProcessFilter.builder().withProcessId("processId").build());

        final XESLogMarshaller marshaller = new XESLogMarshaller();

        LogType log = marshaller.unmarshall(xml);
        assertNotNull(log);
        assertEquals(4, log.getExtension().size());
        assertEquals(3, log.getStringOrDateOrInt().size());
        assertEquals(2, log.getGlobal().size());
        assertEquals(4, log.getClassifier().size());
        assertEquals(1, log.getTrace().size());
        assertEquals(2, log.getTrace().get(0).getEvent().size());
    }
}
