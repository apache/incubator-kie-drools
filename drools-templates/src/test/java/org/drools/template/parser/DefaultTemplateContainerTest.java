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

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class DefaultTemplateContainerTest {

    @Test
    public void testParseTemplate() {
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_simple.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertThat(t.getHeader()).isEqualTo("package This_is_a_ruleset;\n");
        assertThat(t.getColumns().length).isEqualTo(1);
        assertThat(t.getColumns()[0].getName()).isEqualTo("name");
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertThat(templates.size()).isEqualTo(1);
        RuleTemplate template = templates.get( "template1" );
        assertThat(template).isNotNull();
        List<TemplateColumn> columns = template.getColumns();
        assertThat(columns.size()).isEqualTo(1);
        TemplateColumn column = columns.get(0);
        assertThat(column.getName()).isEqualTo("name");
        String contents = template.getContents();
        assertThat(contents.startsWith("rule \"How cool is @{name} @{row.rowNumber}\"")).isTrue();
        assertThat(contents.endsWith("then\nend\n")).isTrue();
    }

    @Test
    public void testParseTemplateNoPackage() {
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_no_package.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertThat(t.getHeader()).isEqualTo("");
        assertThat(t.getColumns().length).isEqualTo(1);
        assertThat(t.getColumns()[0].getName()).isEqualTo("name");
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertThat(templates.size()).isEqualTo(1);
        RuleTemplate template = templates.get( "template1" );
        assertThat(template).isNotNull();
        List<TemplateColumn> columns = template.getColumns();
        assertThat(columns.size()).isEqualTo(1);
        TemplateColumn column = columns.get(0);
        assertThat(column.getName()).isEqualTo("name");
        String contents = template.getContents();
        assertThat(contents.startsWith("rule \"Rule_@{row.rowNumber}\"")).isTrue();
        assertThat(contents.endsWith("then\nend\n")).isTrue();
    }

    @Test
    public void testParseTemplateNoPackageWithImport() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1147099
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_no_package_with_import.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertThat(t.getHeader()).isEqualTo("import org.drools.template.jdbc.Person;\n");
        assertThat(t.getColumns().length).isEqualTo(1);
        assertThat(t.getColumns()[0].getName()).isEqualTo("name");
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertThat(templates.size()).isEqualTo(1);
        RuleTemplate template = templates.get( "template1" );
        assertThat(template).isNotNull();
        List<TemplateColumn> columns = template.getColumns();
        assertThat(columns.size()).isEqualTo(1);
        TemplateColumn column = columns.get(0);
        assertThat(column.getName()).isEqualTo("name");
        String contents = template.getContents();
        assertThat(contents.startsWith("rule \"Rule_@{row.rowNumber}\"")).isTrue();
        assertThat(contents.endsWith("then\nend\n")).isTrue();
    }

    @Test
    public void testParseTemplatePackageWithImport() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1147099
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_package_with_import.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertThat(t.getHeader()).isEqualTo("package This_is_a_ruleset;\nimport org.drools.template.jdbc.Person;\n");
        assertThat(t.getColumns().length).isEqualTo(1);
        assertThat(t.getColumns()[0].getName()).isEqualTo("name");
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertThat(templates.size()).isEqualTo(1);
        RuleTemplate template = templates.get( "template1" );
        assertThat(template).isNotNull();
        List<TemplateColumn> columns = template.getColumns();
        assertThat(columns.size()).isEqualTo(1);
        TemplateColumn column = columns.get(0);
        assertThat(column.getName()).isEqualTo("name");
        String contents = template.getContents();
        assertThat(contents.startsWith("rule \"Rule_@{row.rowNumber}\"")).isTrue();
        assertThat(contents.endsWith("then\nend\n")).isTrue();
    }

    /*
     * Smoke-test to verify it's possible to load a template containing 
     * indented keywords without exception
     */
    @Test
    public void testParseTemplateIndentedKeywords() {
        InputStream is = DefaultTemplateContainerTest.class
                .getResourceAsStream( "/templates/rule_template_indented.drl" );
        new DefaultTemplateContainer( is );
    }

    @Test
    public void testParseTemplateWithKeywordVariableNames() {
        // https://issues.jboss.org/browse/DROOLS-1623
        InputStream is = DefaultTemplateContainerTest.class
                .getResourceAsStream( "/templates/test_template_with_keyword_variable_names.drl" );
        new DefaultTemplateContainer( is );
    }

    @Test
    public void testParseTemplateConditions() {
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_conditions.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertThat(t.getHeader()).isEqualTo("package This_is_a_ruleset;\n");
        assertThat(t.getColumns().length).isEqualTo(1);
        assertThat(t.getColumns()[0].getName()).isEqualTo("name");
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertThat(templates.size()).isEqualTo(1);
        RuleTemplate template = templates.get("template1");
        assertThat(template).isNotNull();
        List<TemplateColumn> columns = template.getColumns();
        assertThat(columns.size()).isEqualTo(1);
        TemplateColumn templateColumn = columns.get(0);
        assertThat(templateColumn.getName()).isEqualTo("name");
        assertThat(templateColumn.getCondition()).isEqualTo("== \"name1\"");
        String contents = template.getContents();
        assertThat(contents.startsWith("rule \"How cool is @{name} @{row.rowNumber}\"")).isTrue();
        assertThat(contents.endsWith("then\nend\n")).isTrue();
    }

    @Test
    public void testParseTemplateNoHeader() {
        try {
            InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_invalid1.drl" );
            new DefaultTemplateContainer( is );
            fail( "DecisionTableParseException expected" );
        } catch ( DecisionTableParseException expected ) {
            assertThat(expected.getMessage()).isEqualTo("Missing header");
        }
    }

    @Test
    public void testParseTemplateNoHeaderColumns() {
        try {
            InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_invalid2.drl" );
            new DefaultTemplateContainer( is );
            fail( "DecisionTableParseException expected" );
        } catch ( DecisionTableParseException expected ) {
            assertThat(expected.getMessage()).isEqualTo("Missing header columns");
        }
    }

    @Test
    public void testParseTemplateNoTemplates() {
        try {
            InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_invalid3.drl" );
            new DefaultTemplateContainer( is );
            fail( "DecisionTableParseException expected" );
        } catch ( DecisionTableParseException expected ) {
            assertThat(expected.getMessage()).isEqualTo("Missing templates");
        }
    }

    @Test
    public void testParseTemplateNoEndTemplate() {
        try {
            InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_invalid4.drl" );
            new DefaultTemplateContainer( is );
            fail( "DecisionTableParseException expected" );
        } catch ( DecisionTableParseException expected ) {
            assertThat(expected.getMessage()).isEqualTo("Missing end template");
        }
    }

    @Test
    public void testNullInputStream() {
        try {
            new DefaultTemplateContainer( (InputStream) null );
            fail( "NullPointerException expected" );
        } catch ( NullPointerException expected ) {
        }
    }

    @Test
    public void testInvalidTemplatePath() {
        try {
            new DefaultTemplateContainer( "invalid path" );
            fail( "NullPointerException expected" );
        } catch ( NullPointerException expected ) {
        }
    }

    @Test
    public void testParseComplexTemplate() {
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_complex.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertThat(t.getHeader()).isEqualTo("package This_is_a_ruleset;\n");
        Column[] columnList = t.getColumns();
        assertThat(columnList.length).isEqualTo(5);
        assertThat(columnList[0].getName()).isEqualTo("first_name");
        assertThat(columnList[1].getName()).isEqualTo("last_name");
        assertThat(columnList[2].getName()).isEqualTo("age");
        assertThat(columnList[3].getName()).isEqualTo("city");
        assertThat(columnList[4].getName()).isEqualTo("phone");
        assertThat(t.getColumn("last_name")).isEqualTo(columnList[1]);
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertThat(templates.size()).isEqualTo(2);

        RuleTemplate template = templates.get("template1");
        assertThat(template).isNotNull();
        List<TemplateColumn> columns = template.getColumns();
        assertThat(columns.size()).isEqualTo(1);
        TemplateColumn column = columns.get(0);
        assertThat(column.getName()).isEqualTo("first_name");

        String contents = template.getContents();
        assertThat(contents.startsWith("rule \"How cool is @{first_name} @{row.rowNumber}\"")).isTrue();
        assertThat(contents.endsWith("then\nend\n")).isTrue();

        template = templates.get("template2");
        assertThat(template).isNotNull();
        columns = template.getColumns();
        assertThat(columns.size()).isEqualTo(2);
        column = columns.get(0);
        assertThat(column.getName()).isEqualTo("first_name");
        TemplateColumn column2 = columns.get(1);
        assertThat(column2.getName()).isEqualTo("last_name");
        contents = template.getContents();
        assertThat(contents.startsWith("rule \"How uncool is @{first_name} @{row.rowNumber}\"")).isTrue();
        assertThat(contents.endsWith("then\nend\n")).isTrue();
    }

    @Test
    public void testParseTemplateWithComments() {
        // BZ-1242010
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_with_comment.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        Map<String, RuleTemplate> templates = t.getTemplates();
        RuleTemplate template = templates.get("template1");
        List<TemplateColumn> columns = template.getColumns();
        TemplateColumn templateColumn = columns.get(0);
        String contents = template.getContents();
        assertThat(contents.contains("@{name}")).isTrue();
        assertThat(contents.contains("@{invalidName}")).isFalse();
    }
}
