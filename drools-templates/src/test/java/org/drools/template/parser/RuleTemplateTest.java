/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.template.parser;

import org.drools.core.util.StringUtils;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class RuleTemplateTest {
    @Test
    public void testSetContents() {
        RuleTemplate rt = new RuleTemplate("rt1", getTemplateContainer());
        rt.setContents("Test template");
        assertEquals("Test template\n", rt.getContents());
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
        assertEquals(5, columns.size());
        TemplateColumn column1 = (TemplateColumn) columns.get(0);
        assertEquals("StandardColumn", column1.getName());
        assertFalse(column1.isNotCondition());
        assertTrue(StringUtils.isEmpty(column1.getCondition()));
        TemplateColumn column2 = (TemplateColumn) columns.get(1);
        assertEquals("NotColumn", column2.getName());
        assertTrue(column2.isNotCondition());
        assertTrue(StringUtils.isEmpty(column2.getCondition()));
        TemplateColumn column3 = (TemplateColumn) columns.get(2);
        assertEquals("ColumnCondition", column3.getName());
        assertFalse(column3.isNotCondition());
        assertEquals("== \"test\"", column3.getCondition());
        TemplateColumn column4 = (TemplateColumn) columns.get(3);
        assertEquals("NotColumnCondition", column4.getName());
        assertTrue(column4.isNotCondition());
        assertEquals("== \"test2\"", column4.getCondition());

    }
}
