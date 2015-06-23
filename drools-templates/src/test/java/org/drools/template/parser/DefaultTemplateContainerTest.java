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

import org.junit.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DefaultTemplateContainerTest {

    @Test
    public void testParseTemplate() {
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_simple.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertEquals( "package This_is_a_ruleset;\n", t.getHeader() );
        assertEquals( 1, t.getColumns().length );
        assertEquals( "name", t.getColumns()[ 0 ].getName() );
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertEquals( 1, templates.size() );
        RuleTemplate template = templates.get( "template1" );
        assertNotNull( template );
        List<TemplateColumn> columns = template.getColumns();
        assertEquals( 1, columns.size() );
        TemplateColumn column = (TemplateColumn) columns.get( 0 );
        assertEquals( "name", column.getName() );
        String contents = template.getContents();
        assertTrue( contents.startsWith( "rule \"How cool is @{name} @{row.rowNumber}\"" ) );
        assertTrue( contents.endsWith( "then\nend\n" ) );
    }

    @Test
    public void testParseTemplateNoPackage() {
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_no_package.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertEquals( "",
                      t.getHeader() );
        assertEquals( 1,
                      t.getColumns().length );
        assertEquals( "name",
                      t.getColumns()[ 0 ].getName() );
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertEquals( 1,
                      templates.size() );
        RuleTemplate template = templates.get( "template1" );
        assertNotNull( template );
        List<TemplateColumn> columns = template.getColumns();
        assertEquals( 1, columns.size() );
        TemplateColumn column = (TemplateColumn) columns.get( 0 );
        assertEquals( "name", column.getName() );
        String contents = template.getContents();
        assertTrue( contents.startsWith( "rule \"Rule_@{row.rowNumber}\"" ) );
        assertTrue( contents.endsWith( "then\nend\n" ) );
    }

    @Test
    public void testParseTemplateNoPackageWithImport() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1147099
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_no_package_with_import.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertEquals( "import org.drools.template.jdbc.Person;\n",
                      t.getHeader() );
        assertEquals( 1,
                      t.getColumns().length );
        assertEquals( "name",
                      t.getColumns()[ 0 ].getName() );
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertEquals( 1,
                      templates.size() );
        RuleTemplate template = templates.get( "template1" );
        assertNotNull( template );
        List<TemplateColumn> columns = template.getColumns();
        assertEquals( 1, columns.size() );
        TemplateColumn column = (TemplateColumn) columns.get( 0 );
        assertEquals( "name", column.getName() );
        String contents = template.getContents();
        assertTrue( contents.startsWith( "rule \"Rule_@{row.rowNumber}\"" ) );
        assertTrue( contents.endsWith( "then\nend\n" ) );
    }

    @Test
    public void testParseTemplatePackageWithImport() {
        //https://bugzilla.redhat.com/show_bug.cgi?id=1147099
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_package_with_import.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertEquals( "package This_is_a_ruleset;\nimport org.drools.template.jdbc.Person;\n",
                      t.getHeader() );
        assertEquals( 1,
                      t.getColumns().length );
        assertEquals( "name",
                      t.getColumns()[ 0 ].getName() );
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertEquals( 1,
                      templates.size() );
        RuleTemplate template = templates.get( "template1" );
        assertNotNull( template );
        List<TemplateColumn> columns = template.getColumns();
        assertEquals( 1, columns.size() );
        TemplateColumn column = (TemplateColumn) columns.get( 0 );
        assertEquals( "name", column.getName() );
        String contents = template.getContents();
        assertTrue( contents.startsWith( "rule \"Rule_@{row.rowNumber}\"" ) );
        assertTrue( contents.endsWith( "then\nend\n" ) );
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
    public void testParseTemplateConditions() {
        InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_conditions.drl" );
        DefaultTemplateContainer t = new DefaultTemplateContainer( is );
        assertEquals( "package This_is_a_ruleset;\n", t.getHeader() );
        assertEquals( 1, t.getColumns().length );
        assertEquals( "name", t.getColumns()[ 0 ].getName() );
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertEquals( 1, templates.size() );
        RuleTemplate template = (RuleTemplate) templates.get( "template1" );
        assertNotNull( template );
        List<TemplateColumn> columns = template.getColumns();
        assertEquals( 1, columns.size() );
        TemplateColumn templateColumn = (TemplateColumn) columns.get( 0 );
        assertEquals( "name", templateColumn.getName() );
        assertEquals( "== \"name1\"", templateColumn.getCondition() );
        String contents = template.getContents();
        assertTrue( contents.startsWith( "rule \"How cool is @{name} @{row.rowNumber}\"" ) );
        assertTrue( contents.endsWith( "then\nend\n" ) );
    }

    @Test
    public void testParseTemplateNoHeader() {
        try {
            InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_invalid1.drl" );
            new DefaultTemplateContainer( is );
            fail( "DecisionTableParseException expected" );
        } catch ( DecisionTableParseException expected ) {
            assertEquals( "Missing header", expected.getMessage() );
        }
    }

    @Test
    public void testParseTemplateNoHeaderColumns() {
        try {
            InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_invalid2.drl" );
            new DefaultTemplateContainer( is );
            fail( "DecisionTableParseException expected" );
        } catch ( DecisionTableParseException expected ) {
            assertEquals( "Missing header columns", expected.getMessage() );
        }
    }

    @Test
    public void testParseTemplateNoTemplates() {
        try {
            InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_invalid3.drl" );
            new DefaultTemplateContainer( is );
            fail( "DecisionTableParseException expected" );
        } catch ( DecisionTableParseException expected ) {
            assertEquals( "Missing templates", expected.getMessage() );
        }
    }

    @Test
    public void testParseTemplateNoEndTemplate() {
        try {
            InputStream is = DefaultTemplateContainerTest.class.getResourceAsStream( "/templates/test_template_invalid4.drl" );
            new DefaultTemplateContainer( is );
            fail( "DecisionTableParseException expected" );
        } catch ( DecisionTableParseException expected ) {
            assertEquals( "Missing end template", expected.getMessage() );
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
        assertEquals( "package This_is_a_ruleset;\n", t.getHeader() );
        Column[] columnList = t.getColumns();
        assertEquals( 5, columnList.length );
        assertEquals( "first_name", columnList[ 0 ].getName() );
        assertEquals( "last_name", columnList[ 1 ].getName() );
        assertEquals( "age", columnList[ 2 ].getName() );
        assertEquals( "city", columnList[ 3 ].getName() );
        assertEquals( "phone", columnList[ 4 ].getName() );
        assertEquals( columnList[ 1 ], t.getColumn( "last_name" ) );
        Map<String, RuleTemplate> templates = t.getTemplates();
        assertEquals( 2, templates.size() );

        RuleTemplate template = (RuleTemplate) templates.get( "template1" );
        assertNotNull( template );
        List<TemplateColumn> columns = template.getColumns();
        assertEquals( 1, columns.size() );
        TemplateColumn column = (TemplateColumn) columns.get( 0 );
        assertEquals( "first_name", column.getName() );

        String contents = template.getContents();
        assertTrue( contents.startsWith( "rule \"How cool is @{first_name} @{row.rowNumber}\"" ) );
        assertTrue( contents.endsWith( "then\nend\n" ) );

        template = (RuleTemplate) templates.get( "template2" );
        assertNotNull( template );
        columns = template.getColumns();
        assertEquals( 2, columns.size() );
        column = (TemplateColumn) columns.get( 0 );
        assertEquals( "first_name", column.getName() );
        TemplateColumn column2 = (TemplateColumn) columns.get( 1 );
        assertEquals( "last_name", column2.getName() );
        contents = template.getContents();
        assertTrue( contents.startsWith( "rule \"How uncool is @{first_name} @{row.rowNumber}\"" ) );
        assertTrue( contents.endsWith( "then\nend\n" ) );
    }

}
