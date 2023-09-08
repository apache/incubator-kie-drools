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
package org.drools.decisiontable;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.drools.decisiontable.parser.DefaultRuleSheetListener;
import org.drools.decisiontable.parser.RuleMatrixSheetListener;
import org.drools.decisiontable.parser.RuleSheetParserUtil;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.template.model.Global;
import org.drools.template.model.Import;
import org.drools.template.parser.DataListener;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.decisiontable.InputType.CSV;
import static org.drools.decisiontable.InputType.XLS;

/**
 * Some basic unit tests for converter utility. Note that some of this may still
 * use the drools 2.x syntax, as it is not compiled, only tested that it
 * generates DRL in the correct structure (not that the DRL itself is correct).
 */
public class SpreadsheetCompilerUnitTest {

    
    private SpreadsheetCompiler converter;

    @Before
    public void setUp() {
        converter = new SpreadsheetCompiler();
    }
    
    @Test
    public void testLoadFromClassPath() {
        String drl = converter.compile("/data/MultiSheetDST.drl.xls", XLS);

        assertThat(drl).isNotNull();

        assertThat(drl.indexOf("rule \"How cool am I_12\"") > drl.indexOf("rule \"How cool am I_11\"")).isTrue();
        assertThat(drl).contains("import example.model.User;");
        assertThat(drl).contains("import example.model.Car;");
        assertThat(drl).contains("package ");
        
        InputStream ins = this.getClass().getResourceAsStream("/data/MultiSheetDST.drl.xls");

        drl = converter.compile(false, ins, XLS);

        assertThat(drl).isNotNull();

        assertThat(drl.indexOf("rule \"How cool am I_12\"") > 0).isTrue();
        assertThat(drl).contains("import example.model.User;");
        assertThat(drl).contains("import example.model.Car;");
        assertThat(drl).doesNotContain("package ");

    }

    @Test
    public void testMultilineActions() {
        final InputStream stream = this.getClass().getResourceAsStream("MultiLinesInAction.drl.xls");
        final String drl = converter.compile(stream, XLS);

        assertThat(drl).doesNotContain("\\n");
    }

    @Test
    public void testMultilineActionsAndMultiLineInConstant() {
      
        final InputStream stream = this.getClass().getResourceAsStream("MultiLinesInActionAndMultiLineInConstant.drl.xls");
        final String drl = converter.compile(stream, XLS);

        assertThat(drl).contains(" **\n** ");
    }

    @Test
    public void testMultilineActionsAndMultiLineAsConstant() {
      
        final InputStream stream = this.getClass().getResourceAsStream("MultiLinesInActionAndMultiLineAsConstant.drl.xls");
        final String drl = converter.compile(stream, XLS);

        assertThat(drl).contains(" **\\n** ");
    }

    @Test
    public void testMultilineCommentsInDescription() {
      
        final InputStream stream = this.getClass().getResourceAsStream("/data/Multiline comment example.drl.xls");
        final String drl = converter.compile(stream, XLS);

        assertThat(drl).containsSubsequence("/* Say", "Hello */", "/* Say", "Goobye */");
        assertThat(drl).doesNotContain("// Say");
    }

    @Test
    public void testMultilineComplexCommentsInDescription() {
      
        final InputStream stream = this.getClass().getResourceAsStream("/data/Multiline comment example complex.drl.xls");
        final String drl = converter.compile(stream, XLS);

        assertThat(drl).containsSubsequence("/* Do these actions:",
                                                    "- Print Greeting",
                                                    "- Set params: {message:'bye cruel world', status:'bye'} */",
                                                    "/* Print message: \"Bye!\"",
                                                    "Author: james@company.org */");

        assertThat(drl).doesNotContain("* - Print Greeting");
        assertThat(drl).doesNotContain("* - Set params: {message:'bye cruel world', status:'bye'}");
        assertThat(drl).doesNotContain("* Author: james@company.org");
    }

    @Test
    public void testLoadSpecificWorksheet() {
      
        final InputStream stream = this.getClass().getResourceAsStream("/data/MultiSheetDST.drl.xls");
        final String drl = converter.compile(stream, "Another Sheet");
        assertThat(drl).isNotNull();
    }

    @Test
    public void testLoadCustomListener() {
        final InputStream stream = this.getClass().getResourceAsStream("/data/CustomWorkbook.drl.xls");
        
        final String drl = converter.compile(stream, XLS, new RuleMatrixSheetListener());

        assertThat(drl).isNotNull();
        assertThat(drl.indexOf("\"matrix\"") != -1).isTrue();
        assertThat(drl.indexOf("$v : FundVisibility") != -1).isTrue();
        assertThat(drl.indexOf("FundType") != -1).isTrue();
        assertThat(drl.indexOf("Role") != -1).isTrue();
    }

    @Test
    public void testLoadCsv() {
      
        final InputStream stream = this.getClass().getResourceAsStream("/data/ComplexWorkbook.drl.csv");
        final String drl = converter.compile(stream, CSV);
        assertThat(drl).isNotNull();

        //        System.out.println(drl);

        assertThat(drl.indexOf("myObject.setIsValid(1, 2)") > 0).isTrue();
        assertThat(drl.indexOf("myObject.size () > 50") > 0).isTrue();

        assertThat(drl.indexOf("Foo(myObject.getColour().equals(red), myObject.size () > 1)") > 0).isTrue();
    }

    @Test
    public void testLoadBasicWithMergedCells() {
      
        final InputStream stream = this.getClass().getResourceAsStream("/data/BasicWorkbook.drl.xls");
        final String drl = converter.compile(stream, XLS);

        assertThat(drl).isNotNull();

        Pattern p = Pattern.compile(".*setIsValid\\(Y\\).*setIsValid\\(Y\\).*setIsValid\\(Y\\).*",
                                     Pattern.DOTALL | Pattern.MULTILINE);
        Matcher m = p.matcher(drl);
        assertThat(m.matches()).isTrue();

        assertThat(drl).contains("This is a function block");
        assertThat(drl).contains("global Class1 obj1;");
        assertThat(drl).contains("myObject.setIsValid(10-Jul-1974)");
        assertThat(drl).contains("myObject.getColour().equals(blue)");
        assertThat(drl).contains("Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")");

        assertThat(drl).contains("b: Bar() eval(myObject.size() < 3)");
        assertThat(drl).contains("b: Bar() eval(myObject.size() < 9)");

        assertThat(drl.indexOf("Foo(myObject.getColour().equals(red), myObject.size () > 1)") < drl.indexOf("b: Bar() eval(myObject.size() < 3)")).isTrue();

        assertThat(drl).contains("myObject.setIsValid(\"19-Jul-1992\")");
        

    }

    @Test
    public void testDeclaresXLS() {
      
        String drl = converter.compile("DeclaresWorkbook.drl.xls", XLS);

        assertThat(drl).isNotNull();

        assertThat(drl).contains("declare Smurf name : String end");
    }

    @Test
    public void testDeclaresCSV() {
      
        String drl = converter.compile("DeclaresWorkbook.drl.csv", CSV);

        assertThat(drl).isNotNull();

        assertThat(drl).contains("declare Smurf name : String end");
    }

    @Test
    public void testAttributesXLS() {
      
        String drl = converter.compile("Attributes.drl.xls", XLS);

        assertThat(drl).isNotNull();

        assertThat(drl).contains("rule \"N1\"");
        
        drl = drl.substring(drl.indexOf("rule \"N1\""));

        assertThat(drl).contains("no-loop true");
        assertThat(drl).contains("duration 100");
        assertThat(drl).contains("salience 1");
        assertThat(drl).contains("ruleflow-group \"RFG1\"");
        assertThat(drl).contains("agenda-group \"AG1\"");
        assertThat(drl).contains("timer (T1)");
        assertThat(drl).contains("lock-on-active true");
        assertThat(drl).contains("activation-group \"g1\"");
        assertThat(drl).contains("auto-focus true");
        assertThat(drl).contains("calendars \"CAL1\"");
        assertThat(drl).contains("date-effective \"01-Jan-2007\"");
        assertThat(drl).contains("date-expires \"31-Dec-2007\"");

        assertThat(drl).contains("rule \"N2\"");
        
        drl = drl.substring(drl.indexOf("rule \"N2\""));

        assertThat(drl).contains("no-loop false");
        assertThat(drl).contains("duration 200");
        assertThat(drl).contains("salience 2");
        assertThat(drl).contains("ruleflow-group \"RFG2\"");
        assertThat(drl).contains("agenda-group \"AG2\"");
        assertThat(drl).contains("timer (T2)");
        assertThat(drl).contains("lock-on-active false");
        assertThat(drl).contains("activation-group \"g2\"");
        assertThat(drl).contains("auto-focus false");
        assertThat(drl).contains("calendars \"CAL2\"");
        assertThat(drl).contains("date-effective \"01-Jan-2012\"");
        assertThat(drl).contains("date-expires \"31-Dec-2015\"");
    }

    @Test
    public void testPropertiesXLS() {

        final List<DataListener> listeners = new ArrayList<>();
        final DefaultRuleSheetListener listener = new DefaultRuleSheetListener();
        listeners.add(listener);

        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("Properties.drl.xls");

        parser.parseFile(is);

        listener.getProperties();

        final String rulesetName = listener.getProperties().getSingleProperty(DefaultRuleSheetListener.RULESET_TAG);
        assertThat(rulesetName).isNotNull().isEqualTo("Properties");

        final List<Import> importList = RuleSheetParserUtil.getImportList(listener.getProperties().getProperty(DefaultRuleSheetListener.IMPORT_TAG));
        assertThat(importList).isNotNull().hasSize(1);
        assertThat(importList.get(0).getClassName()).isEqualTo("java.util.List");

        final List<Global> variableList = RuleSheetParserUtil.getVariableList(listener.getProperties().getProperty(DefaultRuleSheetListener.VARIABLES_TAG));
        assertThat(variableList).isNotNull().hasSize(1);
        assertThat(variableList.get(0).getClassName()).isEqualTo("java.util.List");
        assertThat(variableList.get(0).getIdentifier()).isEqualTo("list");

        final List<String> functions = listener.getProperties().getProperty(DefaultRuleSheetListener.FUNCTIONS_TAG);
        assertThat(functions).isNotNull().hasSize(1).containsExactly("A function");

        final List<String> queries = listener.getProperties().getProperty(DefaultRuleSheetListener.QUERIES_TAG);
        assertThat(queries).isNotNull().hasSize(1).containsExactly("A query");

        final List<String> declarations = listener.getProperties().getProperty(DefaultRuleSheetListener.DECLARES_TAG);
        assertThat(declarations).isNotNull().hasSize(1).containsExactly("A declared type");

        final String sequentialFlag = listener.getProperties().getSingleProperty(DefaultRuleSheetListener.SEQUENTIAL_FLAG);
        assertThat(sequentialFlag).isNotNull().isEqualTo("false");

        final String escapeQuotesFlag = listener.getProperties().getSingleProperty(DefaultRuleSheetListener.ESCAPE_QUOTES_FLAG);
        assertThat(escapeQuotesFlag).isNotNull().isEqualTo("false");

    }

    @Test
    public void testPropertiesWithWhiteSpaceXLS() {
        final List<DataListener> listeners = new ArrayList<>();
        final DefaultRuleSheetListener listener = new DefaultRuleSheetListener();
        listeners.add(listener);

        final ExcelParser parser = new ExcelParser(listeners);
        final InputStream is = this.getClass().getResourceAsStream("PropertiesWithWhiteSpace.drl.xls");

        parser.parseFile(is);

        listener.getProperties();

        final String rulesetName = listener.getProperties().getSingleProperty(DefaultRuleSheetListener.RULESET_TAG);
        assertThat(rulesetName).isNotNull().isEqualTo("Properties");

        final List<Import> importList = RuleSheetParserUtil.getImportList(listener.getProperties().getProperty(DefaultRuleSheetListener.IMPORT_TAG));
        assertThat(importList).isNotNull().hasSize(1);
        assertThat(importList.get(0).getClassName()).isEqualTo("java.util.List");

        final List<Global> variableList = RuleSheetParserUtil.getVariableList(listener.getProperties().getProperty(DefaultRuleSheetListener.VARIABLES_TAG));
        assertThat(variableList).isNotNull().hasSize(1);
        assertThat(variableList.get(0).getClassName()).isEqualTo("java.util.List");
        assertThat(variableList.get(0).getIdentifier()).isEqualTo("list");

        final List<String> functions = listener.getProperties().getProperty(DefaultRuleSheetListener.FUNCTIONS_TAG);
        assertThat(functions).isNotNull().hasSize(1).containsExactly("A function");

        final List<String> queries = listener.getProperties().getProperty(DefaultRuleSheetListener.QUERIES_TAG);
        assertThat(queries).isNotNull().hasSize(1).containsExactly("A query");

        final List<String> declarations = listener.getProperties().getProperty(DefaultRuleSheetListener.DECLARES_TAG);
        assertThat(declarations).isNotNull().hasSize(1).containsExactly("A declared type");

        final String sequentialFlag = listener.getProperties().getSingleProperty(DefaultRuleSheetListener.SEQUENTIAL_FLAG);
        assertThat(sequentialFlag).isNotNull().isEqualTo("false");

        final String escapeQuotesFlag = listener.getProperties().getSingleProperty(DefaultRuleSheetListener.ESCAPE_QUOTES_FLAG);
        assertThat(escapeQuotesFlag).isNotNull().isEqualTo("false");

    }

    @Test
    public void testProcessSheetForExtremeLowNumbers() {
      
        final InputStream stream = this.getClass().getResourceAsStream("/data/BasicWorkbook_with_low_values.drl.xls");
        final String drl = converter.compile(stream, XLS);
        assertThat(drl).isNotNull();
        System.out.println(drl);

        // Should parse the correct number
        assertThat(drl).doesNotContain("myObject.size() < 0");

        assertThat(drl).contains("myObject.size() < 8.0E-11");
        assertThat(drl).contains("myObject.size() < 9.0E-7");
        assertThat(drl).contains("myObject.size() < 3.0E-4");

    }

    @Test
    public void testNegativeNumbers() throws Exception {
        KieBase kbase = readKnowledgeBase("/data/DT_WithNegativeNumbers.drl.xls");
        KieSession ksession = kbase.newKieSession();
        try {
            IntHolder i1 = new IntHolder(1);
            IntHolder i2 = new IntHolder(-1);
            ksession.insert(i1);
            ksession.insert(i2);
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testOOXMLParseCellValue() {
      
        String drl = converter.compile("/data/BZ963584.drl.xls", XLS);

        assertThat(drl).isNotNull();
    }

    @Test
    public void testNoConstraintsEmptyCells() {
      
        /**
         * +--------------+--------------+
         * | CONDITION    | CONDITION    |
         * +--------------+--------------+
         * |           Person            |
         * +--------------+--------------+
         * |    name      |    age       |
         * +--------------+--------------+
         * | <empty>      | 55           |
         * | Fred         | <empty>      |
         * | Fred         | 55           |
         * | <empty>      | <empty>      |
         * +--------------+--------------+
         */
        String drl = converter.compile("/data/NoConstraintsEmptyCells.drl.xls", XLS);

        assertThat(drl).isNotNull();

        final String expected = "package data;\n" +
                "//generated from Decision Table\n" +
                "import org.drools.decisiontable.Person;\n" +
                "// rule values at C10, header at C5\n" +
                "rule \"Cheese fans_10\"\n" +
                "  when\n" +
                "    Person(age == \"55\")\n" +
                "  then\n" +
                "end\n" +
                "// rule values at C11, header at C5\n" +
                "rule \"Cheese fans_11\"\n" +
                "  when\n" +
                "    Person(name == \"Fred\")\n" +
                "  then\n" +
                "end\n" +
                "// rule values at C12, header at C5\n" +
                "rule \"Cheese fans_12\"\n" +
                "  when\n" +
                "    Person(name == \"Fred\", age == \"55\")\n" +
                "  then\n" +
                "end";
        //No Pattern generated when all constraints have empty values

        assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    @Test
    public void testNoConstraintsSpacesInCells() {
      
        /**
         * +--------------+--------------+
         * | CONDITION    | CONDITION    |
         * +--------------+--------------+
         * |           Person            |
         * +--------------+--------------+
         * |    name      |    age       |
         * +--------------+--------------+
         * | <spaces>     | 55           |
         * | Fred         | <spaces>     |
         * | Fred         | 55           |
         * | <spaces>     | <spaces>     |
         * +--------------+--------------+
         */
        String drl = converter.compile("/data/NoConstraintsSpacesInCells.drl.xls", XLS);

        assertThat(drl).isNotNull();

        final String expected = "package data;\n" +
                "//generated from Decision Table\n" +
                "import org.drools.decisiontable.Person;\n" +
                "// rule values at C10, header at C5\n" +
                "rule \"Cheese fans_10\"\n" +
                "  when\n" +
                "    Person(age == \"55\")\n" +
                "  then\n" +
                "end\n" +
                "// rule values at C11, header at C5\n" +
                "rule \"Cheese fans_11\"\n" +
                "  when\n" +
                "    Person(name == \"Fred\")\n" +
                "  then\n" +
                "end\n" +
                "// rule values at C12, header at C5\n" +
                "rule \"Cheese fans_12\"\n" +
                "  when\n" +
                "    Person(name == \"Fred\", age == \"55\")\n" +
                "  then\n" +
                "end";
        //No Pattern generated when all constraints have empty values

        assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    @Test
    public void testNoConstraintsDelimitedSpacesInCells() {
      
        /**
         * +--------------+--------------+
         * | CONDITION    | CONDITION    |
         * +--------------+--------------+
         * |           Person            |
         * +--------------+--------------+
         * |    name      |    age       |
         * +--------------+--------------+
         * | "     "      | 55           |
         * | Fred         | "     "      |
         * | Fred         | 55           |
         * | "     "      | "     "      |
         * | ""           | 55           |
         * | Fred         | ""           |
         * +--------------+--------------+
         */
        String drl = converter.compile("/data/NoConstraintsDelimitedSpacesInCells.drl.xls", XLS);

        assertThat(drl).isNotNull();

        final String expected = "package data;\n" +
                "//generated from Decision Table\n" +
                "import org.drools.decisiontable.Person;\n" +
                "// rule values at C10, header at C5\n" +
                "rule \"Cheese fans_10\"\n" +
                "  when\n" +
                "    Person(name == \"     \", age == \"55\")\n" +
                "  then\n" +
                "end\n" +
                "// rule values at C11, header at C5\n" +
                "rule \"Cheese fans_11\"\n" +
                "  when\n" +
                "    Person(name == \"Fred\", age == \"\")\n" +
                "  then\n" +
                "end\n" +
                "// rule values at C12, header at C5\n" +
                "rule \"Cheese fans_12\"\n" +
                "  when\n" +
                "    Person(name == \"Fred\", age == \"55\")\n" +
                "  then\n" +
                "end\n" +
                "// rule values at C13, header at C5\n" +
                "rule \"Cheese fans_13\"\n" +
                "  when\n" +
                "    Person(name == \"     \", age == \"\")\n" +
                "  then\n" +
                "end\n" +
                "// rule values at C14, header at C5\n" +
                "rule \"Cheese fans_14\"\n" +
                "  when\n" +
                "    Person(name == \"\", age == \"55\")\n" +
                "  then\n" +
                "end\n" +
                "// rule values at C15, header at C5\n" +
                "rule \"Cheese fans_15\"\n" +
                "  when\n" +
                "    Person(name == \"Fred\", age == \"\")\n" +
                "  then\n" +
                "end";

        assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    @Test
    public void testForAllConstraintQuoteRemoval() {
        String drl = converter.compile("/data/ForAllConstraintQuoteRemoval.drl.xls", XLS);

        assertThat(drl).isNotNull();

        final String expected = "package data;\n" +
                "//generated from Decision Table\n" +
                "import com.brms.dto.fact.*;\n" +
                "dialect \"mvel\";\n" +
                "// rule values at C21, header at C16\n" +
                "rule \"enrichment_21\"\n" +
                "  when\n" +
                "    p:Payment(fileFormat == \"TOTO\")\n" +
                "  then\n" +
                "    System.out.println(true);\n" +
                "end\n" +
                "// rule values at C22, header at C16\n" +
                "rule \"enrichment_22\"\n" +
                "  when\n" +
                "    p:Payment(fileFormat == \"TOTO\")\n" +
                "  then\n" +
                "    System.out.println(true);\n" +
                "end\n" +
                "// rule values at C23, header at C16\n" +
                "rule \"enrichment_23\"\n" +
                "  when\n" +
                "    p:Payment(fileFormat == \"TOTO\" || fileFormat == \"TITI\" || fileFormat == \"TOR\")\n" +
                "  then\n" +
                "    System.out.println(true);\n" +
                "end\n" +
                "// rule values at C24, header at C16\n" +
                "rule \"enrichment_24\"\n" +
                "  when\n" +
                "    p:Payment(fileFormat == \"TOTO\" || fileFormat == \"TOR\")\n" +
                "  then\n" +
                "    System.out.println(true);\n" +
                "end\n" +
                "// rule values at C25, header at C16\n" +
                "rule \"enrichment_25\"\n" +
                "  when\n" +
                "    p:Payment(fileFormat == \"TITI\", isConsistencyCheckEnabled == \"true\")\n" +
                "  then\n" +
                "    System.out.println(true);\n" +
                "end\n" +
                "// rule values at C26, header at C16\n" +
                "rule \"enrichment_26\"\n" +
                "  when\n" +
                "  then\n" +
                "    System.out.println(false);\n" +
                "end\n";

        assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    public static class IntHolder {

        private int value;

        public IntHolder(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "IntHolder [value=" + value + "]";
        }
    }

    private KieBase readKnowledgeBase(String resource) throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        DecisionTableConfiguration config = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        config.setInputType(DecisionTableInputType.XLS);
        kbuilder.add(ResourceFactory.newClassPathResource(resource, getClass()), ResourceType.DTABLE,
                      config);
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if (errors.size() > 0) {
            for (KnowledgeBuilderError error : errors) {
                System.err.println(error);
            }
            throw new IllegalArgumentException("Could not parse knowledge.");
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    @Test
    public void testFunctionCellMerged() {
        // BZ-1147402
      
        String drl = converter.compile("/data/FunctionCellMerged.drl.xls", XLS);

        assertThat(drl).isNotNull().contains("function void test(){");
    }

    @Test
    public void testMoreThan9InputParamSubstitution() throws Exception {
        //https://issues.jboss.org/browse/DROOLS-836
        final String EXPECTED_CONDITION = "eval ($objects: Object (id == a ||  == b ||  == c ||  == d ||  == e ||  == f ||  == g ||  == h ||  == i  ||  == j  ) )";
        final String EXPECTED_ACTION = "System.out.println(“test” + a  + b   + c  + d  + e  + f  + g + h  + i + j);";

      
        String drl = converter.compile("/data/DROOLS-836.drl.xls", XLS);

        assertThat(drl).isNotNull().contains(EXPECTED_CONDITION, EXPECTED_ACTION);
    }

    @Test
    public void testDtableUsingExcelFunction() throws Exception {
        // DROOLS-887
      
        String drl = converter.compile("/data/RuleNameUsingExcelFunction.drl.xls", XLS);

        final String EXPECTED_RULE_NAME = "rule \"RULE_500\"";
        
        assertThat(drl).contains(EXPECTED_RULE_NAME);
    }

    @Test
    public void checkLhsBuilderFixValue() {
        //https://issues.jboss.org/browse/DROOLS-1279
      

        String drl = converter.compile("/data/DROOLS-1279.drl.xls", XLS);

        assertThat(drl).isNotNull();

        final String expected = "package data;\n" +
                "//generated from Decision Table\n" +
                "import com.sample.DecisionTableTest.Message;\n" +
                "dialect \"mvel\"\n" +
                "// rule values at C12, header at C7\n" +
                "rule \"HelloWorld_12\"\n" +
                "  when\n" +
                "    m:Message(checktest in(\"AAA\"), status == \"Message.HELLO\")\n" +
                "  then\n" +
                "    System.out.println(m.getMessage());\n" +
                "    m.setMessage(\"Goodbye cruel world\");update(m);\n" +
                "    m.setChecktest(\"BBB\");update(m);\n" +
                "end\n" +
                "// rule values at C13, header at C7\n" +
                "rule \"HelloWorld_13\"\n" +
                "  when\n" +
                "    m:Message(checktest in(\"BBB\", \"CCC\"), status == \"Message.GOODBYE\")\n" +
                "  then\n" +
                "    System.out.println(m.getMessage());\n" +
                "end\n";

        assertThat(expected).isEqualToIgnoringWhitespace(drl);
    }

    @Test
    public void testLhsOrder() {
        // DROOLS-3080
      
        String drl = converter.compile("/data/LhsOrder.drl.xls", XLS);

        assertThat(Stream.of(drl.split("\n")).map(String::trim).toArray())
                .as("Lhs order is wrong")
                .containsSequence("accumulate(Person(name == \"John\", $a : age); $max:max($a))",
                        "$p:Person(name == \"John\", age == $max)");
    }

    @Test
    public void testNewLineInConstraint() {
        // DROOLS-4788
        String drl = converter.compile("/data/NewLineInConstraint.drl.xls", XLS);

        assertThat(drl).contains("map[\"Key2\"] == var2");
    }

    @Test
    public void testNoUnit() {
      
        final InputStream stream = this.getClass().getResourceAsStream("/data/CanDrink.drl.xls");
        final String drl = converter.compile(stream, XLS);
        
        assertThat(drl).contains("$p: Person(age < 18)");
    }

    @Test
    public void testNoLhsParam() {
        // DROOLS-5782
      
        final InputStream stream = this.getClass().getResourceAsStream("/data/CanDrinkNoParam.drl.xls");
        final String drl = converter.compile(stream, XLS);
        
        assertThat(drl).contains("$p : Person( age < 18 )\n");
    }

    @Test
    public void testChecksOnLhs() {
        // DROOLS-5785
        final InputStream stream = this.getClass().getResourceAsStream("/data/CanDrinkCheckOnLhs.drl.xls");

        final String drl = converter.compile(stream, XLS);

        assertThat(drl).contains("$p : Person(age < 18, name == \"Matteo\")\n");
    }

    @Test
    public void testRuleUnit() {
        final InputStream stream = this.getClass().getResourceAsStream("/data/CanDrinkUnit.drl.xls");

        final String drl = converter.compile(stream, XLS);

        assertThat(drl).contains("unit CanDrinkUnit;");
        assertThat(drl).contains("query Results $r: /results end");
        assertThat(drl).contains("$p: /persons[age < 18]");
    }

    @Test
    public void testUseWatchAnnotation() {
        final InputStream stream = this.getClass().getResourceAsStream("/data/CanDrinkUsingWatch.drl.xls");

        final String drl = converter.compile(stream, XLS);

        assertThat(drl).contains("$p: Person(age < 18) @watch(name)");
    }

    @Test
    public void testZipBomb() {
        // RHDM-1468
        System.setProperty("drools.excelParser.minInflateRatio", "0.001");
        try {
            final InputStream stream = this.getClass().getResourceAsStream("/data/Sample2.drl.xlsx");

            final String drl = converter.compile(stream, XLS);

            assertThat(drl).contains("m:Message(status == Message.HELLO)");
        } finally {
            System.clearProperty("drools.excelParser.minInflateRatio");
        }
    }

    @Test
    public void testForAll() {
        // DROOLS-7350
        String drl = converter.compile("/data/Hal1.drl.xls", XLS);

        assertThat(drl).contains("m : Message(number >5 && number <=10)");
    }
}
