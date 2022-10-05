/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.xml;

import java.io.InputStreamReader;
import java.util.List;

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.ForallDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.MVELExprDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.mvel.DrlDumper;
import org.drools.util.StringUtils;
import org.jbpm.compiler.xml.compiler.SemanticKnowledgeBuilderConfigurationImpl;
import org.jbpm.compiler.xml.compiler.XmlPackageReader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XmlPackageReaderTest {

    @Test
    public void testParseFrom() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseFrom.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        RuleDescr obj = packageDescr.getRules().get(0);
        PatternDescr patterndescr = (PatternDescr) obj.getLhs().getDescrs().get(0);

        FromDescr from = (FromDescr) patterndescr.getSource();

        MVELExprDescr accessordescriptor = (MVELExprDescr) from.getDataSource();
        assertEquals("cheesery.getCheeses(i+4)",
                accessordescriptor.getExpression());

        assertEquals("Cheese", patterndescr.getObjectType());
        assertEquals("cheese", patterndescr.getIdentifier());

    }

    @Test
    public void testAccumulate() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseAccumulate.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        RuleDescr obj = (RuleDescr) packageDescr.getRules().get(0);

        Object patternobj = obj.getLhs().getDescrs().get(0);
        assertTrue(patternobj instanceof PatternDescr);
        final PatternDescr patterncheese = (PatternDescr) patternobj;
        assertEquals("cheese", patterncheese.getIdentifier());
        assertEquals("Cheese", patterncheese.getObjectType());

        AccumulateDescr accumulatedescr = (AccumulateDescr) patterncheese.getSource();
        assertEquals("total += $cheese.getPrice();",
                accumulatedescr.getActionCode());
        assertEquals("int total = 0;",
                accumulatedescr.getInitCode());
        assertEquals("new Integer( total ) );",
                accumulatedescr.getResultCode());

        patternobj = obj.getLhs().getDescrs().get(1);
        assertTrue(patternobj instanceof PatternDescr);

        final PatternDescr patternmax = (PatternDescr) patternobj;
        assertEquals("max", patternmax.getIdentifier());
        assertEquals("Number", patternmax.getObjectType());

        accumulatedescr = (AccumulateDescr) patternmax.getSource();

        assertTrue(accumulatedescr.isExternalFunction());

        assertEquals("max",
                accumulatedescr.getFunctions().get(0).getFunction());

        assertNull(accumulatedescr.getInitCode());
        assertNull(accumulatedescr.getActionCode());
        assertNull(accumulatedescr.getResultCode());
        assertNull(accumulatedescr.getReverseCode());

    }

    @Test
    public void testAccumulateMultiPattern() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseAccumulate.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        RuleDescr obj = (RuleDescr) packageDescr.getRules().get(1);

        Object patternobj = obj.getLhs().getDescrs().get(0);
        assertTrue(patternobj instanceof PatternDescr);
        final PatternDescr patterncheese = (PatternDescr) patternobj;
        assertEquals("cheese", patterncheese.getIdentifier());
        assertEquals("Cheese", patterncheese.getObjectType());

        AccumulateDescr accumulatedescr = (AccumulateDescr) patterncheese.getSource();
        assertEquals("total += $cheese.getPrice();",
                accumulatedescr.getActionCode());
        assertEquals("int total = 0;",
                accumulatedescr.getInitCode());
        assertEquals("new Integer( total ) );",
                accumulatedescr.getResultCode());

        AndDescr anddescr = (AndDescr) accumulatedescr.getInput();

        List descrlist = anddescr.getDescrs();

        PatternDescr[] listpattern = (PatternDescr[]) descrlist.toArray(new PatternDescr[descrlist.size()]);

        assertEquals("Milk", listpattern[0].getObjectType());
        assertEquals("Cup", listpattern[1].getObjectType());
    }

    @Test
    public void testParseForall() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseForall.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();

        RuleDescr obj = (RuleDescr) packageDescr.getRules().get(0);
        ForallDescr forall = (ForallDescr) obj.getLhs().getDescrs().get(0);
        List forallPaterns = forall.getDescrs();

        PatternDescr pattarnState = (PatternDescr) forallPaterns.get(0);
        PatternDescr personState = (PatternDescr) forallPaterns.get(1);
        PatternDescr cheeseState = (PatternDescr) forallPaterns.get(2);

        assertEquals("State", pattarnState.getObjectType());
        assertEquals("Person", personState.getObjectType());
        assertEquals("Cheese", cheeseState.getObjectType());
    }

    @Test
    public void testParseExists() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseExists.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();

        RuleDescr obj = (RuleDescr) packageDescr.getRules().get(0);
        Object existdescr = obj.getLhs().getDescrs().get(0);
        assertTrue(existdescr instanceof ExistsDescr);

        Object patternDescriptor = ((ExistsDescr) existdescr).getDescrs().get(0);
        assertTrue(patternDescriptor instanceof PatternDescr);
        assertEquals("Person", ((PatternDescr) patternDescriptor).getObjectType());

        Object notDescr = obj.getLhs().getDescrs().get(1);

        assertEquals(notDescr.getClass().getName(),
                NotDescr.class.getName());
        existdescr = ((NotDescr) notDescr).getDescrs().get(0);
        patternDescriptor = ((ExistsDescr) existdescr).getDescrs().get(0);
        assertTrue(patternDescriptor instanceof PatternDescr);
        assertEquals("Cheese", ((PatternDescr) patternDescriptor).getObjectType());
    }

    @Test
    public void testParseCollect() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseCollect.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();

        String expected = StringUtils.readFileAsString(new InputStreamReader(getClass().getResourceAsStream("test_ParseCollect.drl")));
        String expectedWithoutHeader = removeLicenseHeader(expected);
        String actual = new DrlDumper().dump(packageDescr);

        assertThat(expectedWithoutHeader).isEqualToIgnoringWhitespace(actual);
    }

    @Test
    public void testParsePackageName() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParsePackageName.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertEquals("com.sample",
                packageDescr.getName());
    }

    @Test
    public void testParseImport() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseImport.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertEquals("com.sample",
                packageDescr.getName());

        final List imports = packageDescr.getImports();
        assertEquals(2,
                imports.size());
        assertEquals("java.util.HashMap",
                ((ImportDescr) imports.get(0)).getTarget());
        assertEquals("org.drools.mvel.compiler.*",
                ((ImportDescr) imports.get(1)).getTarget());

        final List functionImport = packageDescr.getFunctionImports();

        assertEquals("org.drools.function",
                ((FunctionImportDescr) functionImport.get(0)).getTarget());
    }

    @Test
    public void testParseGlobal() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseGlobal.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertEquals("com.sample",
                packageDescr.getName());

        final List imports = packageDescr.getImports();
        assertEquals(2,
                imports.size());
        assertEquals("java.util.HashMap",
                ((ImportDescr) imports.get(0)).getTarget());
        assertEquals("org.drools.mvel.compiler.*",
                ((ImportDescr) imports.get(1)).getTarget());

        final List globals = packageDescr.getGlobals();
        assertEquals(2,
                globals.size());
        final GlobalDescr x = (GlobalDescr) globals.get(0);
        final GlobalDescr yada = (GlobalDescr) globals.get(1);
        assertEquals("com.sample.X",
                x.getType());
        assertEquals("x",
                x.getIdentifier());
        assertEquals("com.sample.Yada",
                yada.getType());
        assertEquals("yada",
                yada.getIdentifier());
    }

    @Test
    public void testParseFunction() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseFunction.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertEquals("com.sample",
                packageDescr.getName());

        final List imports = packageDescr.getImports();
        assertEquals(2,
                imports.size());
        assertEquals("java.util.HashMap",
                ((ImportDescr) imports.get(0)).getTarget());
        assertEquals("org.drools.mvel.compiler.*",
                ((ImportDescr) imports.get(1)).getTarget());

        final List globals = packageDescr.getGlobals();
        assertEquals(2,
                globals.size());
        final GlobalDescr x = (GlobalDescr) globals.get(0);
        final GlobalDescr yada = (GlobalDescr) globals.get(1);
        assertEquals("com.sample.X",
                x.getType());
        assertEquals("x",
                x.getIdentifier());
        assertEquals("com.sample.Yada",
                yada.getType());
        assertEquals("yada",
                yada.getIdentifier());

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get(0);
        final List names = functionDescr.getParameterNames();
        assertEquals("foo",
                names.get(0));
        assertEquals("bada",
                names.get(1));

        final List types = functionDescr.getParameterTypes();
        assertEquals("Bar",
                types.get(0));
        assertEquals("Bing",
                types.get(1));

        assertEquals("System.out.println(\"hello world\");",
                functionDescr.getText().trim());
    }

    @Test
    public void testParseRule() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseRule.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();

        String expected = StringUtils.readFileAsString(new InputStreamReader(getClass().getResourceAsStream("test_ParseRule.drl")));
        // remove license header as that one is not stored in the XML
        String expectedWithoutHeader = removeLicenseHeader(expected);
        System.out.println(expectedWithoutHeader);
        String actual = new DrlDumper().dump(packageDescr);

        assertThat(expectedWithoutHeader).isEqualToIgnoringWhitespace(actual);
    }

    @Test
    public void testParseSimpleRule() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_SimpleRule1.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertEquals("com.sample",
                packageDescr.getName());

        final List imports = packageDescr.getImports();
        assertEquals(2,
                imports.size());
        assertEquals("java.util.List",
                ((ImportDescr) imports.get(0)).getTarget());
        assertEquals("org.drools.mvel.compiler.Person",
                ((ImportDescr) imports.get(1)).getTarget());

        RuleDescr ruleDescr = (RuleDescr) packageDescr.getRules().get(0);
        assertEquals("simple_rule1",
                ruleDescr.getName());
        AndDescr lhs = ruleDescr.getLhs();
        PatternDescr patternDescr = (PatternDescr) lhs.getDescrs().get(0);
        assertEquals("Person",
                patternDescr.getObjectType());
        ExprConstraintDescr expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertEquals("name == \"darth\"", expr.getExpression());

        ruleDescr = (RuleDescr) packageDescr.getRules().get(1);
        assertEquals("simple_rule2",
                ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(0);
        assertEquals("Person",
                patternDescr.getObjectType());
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertEquals("age == 35 || == -3.5", expr.getExpression());

        ruleDescr = (RuleDescr) packageDescr.getRules().get(2);
        assertEquals("simple_rule3",
                ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(0);
        assertEquals("Person",
                patternDescr.getObjectType());
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertEquals("age == 35 || (!= 7.0 && != -70)", expr.getExpression());

        ruleDescr = (RuleDescr) packageDescr.getRules().get(3);
        assertEquals("simple_rule3",
                ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(1);
        assertEquals("Person",
                patternDescr.getObjectType());
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertEquals("name == $s", expr.getExpression());

        ruleDescr = (RuleDescr) packageDescr.getRules().get(4);
        assertEquals("simple_rule4",
                ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(1);
        assertEquals("Person",
                patternDescr.getObjectType());
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertEquals("(name == $s) || (age == 35 || (!= 7.0 && != -70))", expr.getExpression());

        ruleDescr = (RuleDescr) packageDescr.getRules().get(5);
        assertEquals("simple_rule5",
                ruleDescr.getName());
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(1);
        assertEquals("Person",
                patternDescr.getObjectType());
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertEquals("(name == $s) || ((age != 34) && (age != 37) && (name != \"yoda\"))", expr.getExpression());

    }

    @Test
    public void testParseLhs() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseLhs.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();

        String expected = StringUtils.readFileAsString(new InputStreamReader(getClass().getResourceAsStream("test_ParseLhs.drl")));
        String expectedWithoutHeader = removeLicenseHeader(expected);
        String actual = new DrlDumper().dump(packageDescr);

        assertThat(expectedWithoutHeader).isEqualToIgnoringWhitespace(actual);
    }

    @Test
    public void testParseRhs() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseRhs.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertEquals("com.sample",
                packageDescr.getName());

        final List imports = packageDescr.getImports();
        assertEquals(2,
                imports.size());
        assertEquals("java.util.HashMap",
                ((ImportDescr) imports.get(0)).getTarget());
        assertEquals("org.drools.mvel.compiler.*",
                ((ImportDescr) imports.get(1)).getTarget());

        final List globals = packageDescr.getGlobals();
        assertEquals(2,
                globals.size());
        final GlobalDescr x = (GlobalDescr) globals.get(0);
        final GlobalDescr yada = (GlobalDescr) globals.get(1);
        assertEquals("com.sample.X",
                x.getType());
        assertEquals("x",
                x.getIdentifier());
        assertEquals("com.sample.Yada",
                yada.getType());
        assertEquals("yada",
                yada.getIdentifier());

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get(0);
        final List names = functionDescr.getParameterNames();
        assertEquals("foo",
                names.get(0));
        assertEquals("bada",
                names.get(1));

        final List types = functionDescr.getParameterTypes();
        assertEquals("Bar",
                types.get(0));
        assertEquals("Bing",
                types.get(1));

        assertEquals("System.out.println(\"hello world\");",
                functionDescr.getText().trim());

        final RuleDescr ruleDescr = (RuleDescr) packageDescr.getRules().get(0);
        assertEquals("my rule",
                ruleDescr.getName());

        final String consequence = (String) ruleDescr.getConsequence();
        assertThat(consequence).isNotNull();
        assertEquals("System.out.println( \"hello\" );",
                consequence.trim());
    }

    @Test
    public void testParseQuery() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseQuery.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertEquals("com.sample",
                packageDescr.getName());

        final List imports = packageDescr.getImports();
        assertEquals(2,
                imports.size());
        assertEquals("java.util.HashMap",
                ((ImportDescr) imports.get(0)).getTarget());
        assertEquals("org.drools.mvel.compiler.*",
                ((ImportDescr) imports.get(1)).getTarget());

        final List globals = packageDescr.getGlobals();
        assertEquals(2,
                globals.size());
        final GlobalDescr x = (GlobalDescr) globals.get(0);
        final GlobalDescr yada = (GlobalDescr) globals.get(1);
        assertEquals("com.sample.X",
                x.getType());
        assertEquals("x",
                x.getIdentifier());
        assertEquals("com.sample.Yada",
                yada.getType());
        assertEquals("yada",
                yada.getIdentifier());

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get(0);
        final List names = functionDescr.getParameterNames();
        assertEquals("foo",
                names.get(0));
        assertEquals("bada",
                names.get(1));

        final List types = functionDescr.getParameterTypes();
        assertEquals("Bar",
                types.get(0));
        assertEquals("Bing",
                types.get(1));

        assertEquals("System.out.println(\"hello world\");",
                functionDescr.getText().trim());

        final QueryDescr queryDescr = (QueryDescr) packageDescr.getRules().get(0);
        assertEquals("my query",
                queryDescr.getName());

        final AndDescr lhs = queryDescr.getLhs();
        assertEquals(1,
                lhs.getDescrs().size());
        final PatternDescr patternDescr = (PatternDescr) lhs.getDescrs().get(0);
        assertEquals("Foo",
                patternDescr.getObjectType());

    }

    private XmlPackageReader getXmReader() {
        SemanticKnowledgeBuilderConfigurationImpl conf = new SemanticKnowledgeBuilderConfigurationImpl();
        XmlPackageReader xmlReader = new XmlPackageReader(conf.getSemanticModules());
        xmlReader.getParser().setClassLoader(XmlPackageReaderTest.class.getClassLoader());

        return xmlReader;
    }

    private String removeLicenseHeader(String content) {
        String[] lines = content.trim().split("\n");
        StringBuilder result = new StringBuilder();
        if (lines.length > 1 && lines[0].startsWith("/*")) {
            boolean inHeader = true;
            for (String line : lines) {
                if (line.trim().startsWith("package")) {
                    inHeader = false;
                }
                if (!inHeader) {
                    result.append(line);
                    result.append("\n");
                }
            }
            return result.toString();
        } else {
            return content;
        }
    }

}
