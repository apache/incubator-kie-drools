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
package org.drools.template.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;

import static org.assertj.core.api.Assertions.assertThat;

public class ExternalSheetListenerTest {

    private TemplateDataListener esl;

    private List<Row> assertedRows = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        esl = new TemplateDataListener(2, 2, new TestTemplateContainer(), new TestGenerator());
    }

    @Test
    public void testRenderDrl() {
        String drl = esl.renderDRL();
        assertThat(drl).isEqualTo("Test Template Header\nTest Generated DRL\n");
    }

    @Test
    public void testRowHandling() {
        esl.newRow(0, 3);
        esl.newCell(0, 0, "row0col0", 0);
        esl.newCell(0, 1, "row0col1", 0);
        esl.newCell(0, 2, "row0col2", 0);
        esl.newRow(1, 3);
        esl.newCell(1, 0, "row1col0", 0);
        esl.newCell(1, 1, "row1col1", 0);
        esl.newCell(1, 2, "row1col2", 0);
        esl.newRow(2, 3);
        esl.newCell(2, 0, "row2col0", 0);
        esl.newCell(2, 1, "row2col1", 0);
        esl.newCell(2, 2, "row2col2", 0);
        esl.finishSheet();
        assertThat(assertedRows.size()).isEqualTo(2);
    }

    @Test
    public void testRowHandlingBlankRows() {
        esl.newRow(0, 3);
        esl.newCell(0, 0, "row0col0", 0);
        esl.newCell(0, 1, "row0col1", 0);
        esl.newCell(0, 2, "row0col2", 0);
        esl.newRow(1, 3);
        esl.newCell(1, 0, "row1col0", 0);
        esl.newCell(1, 1, "row1col1", 0);
        esl.newCell(1, 2, "row1col2", 0);
        esl.newRow(2, 3);
        esl.newCell(2, 0, "row2col0", 0);
        esl.newCell(2, 1, "row2col1", 0);
        esl.newCell(2, 2, "row2col2", 0);
        esl.newRow(3, 3);
        esl.newCell(3, 0, "", 0);
        esl.newCell(3, 1, "", 0);
        esl.newCell(3, 2, "", 0);
        esl.newRow(4, 3);
        esl.newCell(4, 0, "", 0);
        esl.newCell(4, 1, "", 0);
        esl.newCell(4, 2, "", 0);

        esl.finishSheet();
        assertThat(assertedRows.size()).isEqualTo(2);
    }

    private class TestGenerator implements Generator {

        public void generate(String templateName, Row row) {
            assertedRows.add(row);
        }

        public String getDrl() {
            return "Test Generated DRL";
        }

    }

    private class TestTemplateContainer implements TemplateContainer {

        public void addColumn(Column c) {
        }

        public void addTemplate(RuleTemplate template) {
        }

        public Column[] getColumns() {
            return new Column[] { new StringColumn("Pattern 1"),
                    new StringColumn("Pattern 2"), new StringColumn("Pattern 3") };
        }

        public String getHeader() {
            return "Test Template Header";
        }

        public Map<String, RuleTemplate> getTemplates() {
            return Map.of("template", new RuleTemplate("template", this));
        }

        public void setHeader(String head) {
        }

        public void setTemplates(Map<String, RuleTemplate> templates) {

        }

        public Column getColumn(String name) {
            return null;
        }
    }

}
