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

import org.drools.util.StringUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleTemplateTest {
    @Test
    public void testSetContents() {
        RuleTemplate rt = new RuleTemplate("rt1", getTemplateContainer());
        rt.setContents("Test template");
        assertThat(rt.getContents()).isEqualTo("Test template\n");
    }

    private TemplateContainer getTemplateContainer() {
        return new TemplateContainer() {

            public Column getColumn(String name) {
                return null;
            }

            public Column[] getColumns() {
                return null;
            }

            public String getHeader() {
                return null;
            }

            public Map<String, RuleTemplate> getTemplates() {
                return null;
            }

        };
    }

    @Test
    public void testAddColumn() {
        RuleTemplate rt = new RuleTemplate("rt1", getTemplateContainer());
        rt.addColumn("StandardColumn");
        rt.addColumn("!NotColumn");
        rt.addColumn("ColumnCondition == \"test\"");
        rt.addColumn("!NotColumnCondition == \"test2\"");
        rt.addColumn("ArrayColumnCondition[0] == \"test2\"");
        List<TemplateColumn> columns = rt.getColumns();
        assertThat(columns.size()).isEqualTo(5);
        TemplateColumn column1 = columns.get(0);
        assertThat(column1.getName()).isEqualTo("StandardColumn");
        assertThat(column1.isNotCondition()).isFalse();
        assertThat(StringUtils.isEmpty(column1.getCondition())).isTrue();
        TemplateColumn column2 = columns.get(1);
        assertThat(column2.getName()).isEqualTo("NotColumn");
        assertThat(column2.isNotCondition()).isTrue();
        assertThat(StringUtils.isEmpty(column2.getCondition())).isTrue();
        TemplateColumn column3 = columns.get(2);
        assertThat(column3.getName()).isEqualTo("ColumnCondition");
        assertThat(column3.isNotCondition()).isFalse();
        assertThat(column3.getCondition()).isEqualTo("== \"test\"");
        TemplateColumn column4 = columns.get(3);
        assertThat(column4.getName()).isEqualTo("NotColumnCondition");
        assertThat(column4.isNotCondition()).isTrue();
        assertThat(column4.getCondition()).isEqualTo("== \"test2\"");

    }
}
