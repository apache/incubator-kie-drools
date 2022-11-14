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
        assertThat(accessordescriptor.getExpression()).isEqualTo("cheesery.getCheeses(i+4)");

        assertThat(patterndescr.getObjectType()).isEqualTo("Cheese");
        assertThat(patterndescr.getIdentifier()).isEqualTo("cheese");

    }

    @Test
    public void testAccumulate() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseAccumulate.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        RuleDescr obj = (RuleDescr) packageDescr.getRules().get(0);

        Object patternobj = obj.getLhs().getDescrs().get(0);
        assertThat(patternobj).isInstanceOf(PatternDescr.class);
        final PatternDescr patterncheese = (PatternDescr) patternobj;
        assertThat(patterncheese.getIdentifier()).isEqualTo("cheese");
        assertThat(patterncheese.getObjectType()).isEqualTo("Cheese");

        AccumulateDescr accumulatedescr = (AccumulateDescr) patterncheese.getSource();
        assertThat(accumulatedescr.getActionCode()).isEqualTo("total += $cheese.getPrice();");
        assertThat(accumulatedescr.getInitCode()).isEqualTo("int total = 0;");
        assertThat(accumulatedescr.getResultCode()).isEqualTo("new Integer( total ) );");

        patternobj = obj.getLhs().getDescrs().get(1);
        assertThat(patternobj).isInstanceOf(PatternDescr.class);

        final PatternDescr patternmax = (PatternDescr) patternobj;
        assertThat(patternmax.getIdentifier()).isEqualTo("max");
        assertThat(patternmax.getObjectType()).isEqualTo("Number");

        accumulatedescr = (AccumulateDescr) patternmax.getSource();

        assertThat(accumulatedescr.isExternalFunction()).isTrue();

        assertThat(accumulatedescr.getFunctions().get(0).getFunction()).isEqualTo("max");

        assertThat(accumulatedescr.getInitCode()).isNull();
        assertThat(accumulatedescr.getActionCode()).isNull();
        assertThat(accumulatedescr.getResultCode()).isNull();
        assertThat(accumulatedescr.getReverseCode()).isNull();

    }

    @Test
    public void testAccumulateMultiPattern() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseAccumulate.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        RuleDescr obj = (RuleDescr) packageDescr.getRules().get(1);

        Object patternobj = obj.getLhs().getDescrs().get(0);
        assertThat(patternobj).isInstanceOf(PatternDescr.class);
        final PatternDescr patterncheese = (PatternDescr) patternobj;
        assertThat(patterncheese.getIdentifier()).isEqualTo("cheese");
        assertThat(patterncheese.getObjectType()).isEqualTo("Cheese");

        AccumulateDescr accumulatedescr = (AccumulateDescr) patterncheese.getSource();
        assertThat(accumulatedescr.getActionCode()).isEqualTo("total += $cheese.getPrice();");
        assertThat(accumulatedescr.getInitCode()).isEqualTo("int total = 0;");
        assertThat(accumulatedescr.getResultCode()).isEqualTo("new Integer( total ) );");

        AndDescr anddescr = (AndDescr) accumulatedescr.getInput();

        List descrlist = anddescr.getDescrs();

        PatternDescr[] listpattern = (PatternDescr[]) descrlist.toArray(new PatternDescr[descrlist.size()]);

        assertThat(listpattern[0].getObjectType()).isEqualTo("Milk");
        assertThat(listpattern[1].getObjectType()).isEqualTo("Cup");
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

        assertThat(pattarnState.getObjectType()).isEqualTo("State");
        assertThat(personState.getObjectType()).isEqualTo("Person");
        assertThat(cheeseState.getObjectType()).isEqualTo("Cheese");
    }

    @Test
    public void testParseExists() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseExists.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();

        RuleDescr obj = (RuleDescr) packageDescr.getRules().get(0);
        Object existdescr = obj.getLhs().getDescrs().get(0);
        assertThat(existdescr).isInstanceOf(ExistsDescr.class);

        Object patternDescriptor = ((ExistsDescr) existdescr).getDescrs().get(0);
        assertThat(patternDescriptor).isInstanceOf(PatternDescr.class);
        assertThat(((PatternDescr) patternDescriptor).getObjectType()).isEqualTo("Person");

        Object notDescr = obj.getLhs().getDescrs().get(1);

        assertThat(NotDescr.class.getName()).isEqualTo(notDescr.getClass().getName());
        existdescr = ((NotDescr) notDescr).getDescrs().get(0);
        patternDescriptor = ((ExistsDescr) existdescr).getDescrs().get(0);
        assertThat(patternDescriptor).isInstanceOf(PatternDescr.class);
        assertThat(((PatternDescr) patternDescriptor).getObjectType()).isEqualTo("Cheese");
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
        assertThat(packageDescr.getName()).isEqualTo("com.sample");
    }

    @Test
    public void testParseImport() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseImport.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertThat(packageDescr.getName()).isEqualTo("com.sample");

        final List imports = packageDescr.getImports();
        assertThat(imports).hasSize(2);
        assertThat(((ImportDescr) imports.get(0)).getTarget()).isEqualTo("java.util.HashMap");
        assertThat(((ImportDescr) imports.get(1)).getTarget()).isEqualTo("org.drools.mvel.compiler.*");

        final List functionImport = packageDescr.getFunctionImports();

        assertThat(((FunctionImportDescr) functionImport.get(0)).getTarget()).isEqualTo("org.drools.function");
    }

    @Test
    public void testParseGlobal() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseGlobal.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertThat(packageDescr.getName()).isEqualTo("com.sample");

        final List imports = packageDescr.getImports();
        assertThat(imports).hasSize(2);
        assertThat(((ImportDescr) imports.get(0)).getTarget()).isEqualTo("java.util.HashMap");
        assertThat(((ImportDescr) imports.get(1)).getTarget()).isEqualTo("org.drools.mvel.compiler.*");

        final List globals = packageDescr.getGlobals();
        assertThat(globals).hasSize(2);
        final GlobalDescr x = (GlobalDescr) globals.get(0);
        final GlobalDescr yada = (GlobalDescr) globals.get(1);
        assertThat(x.getType()).isEqualTo("com.sample.X");
        assertThat(x.getIdentifier()).isEqualTo("x");
        assertThat(yada.getType()).isEqualTo("com.sample.Yada");
        assertThat(yada.getIdentifier()).isEqualTo("yada");
    }

    @Test
    public void testParseFunction() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseFunction.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertThat(packageDescr.getName()).isEqualTo("com.sample");

        final List imports = packageDescr.getImports();
        assertThat(imports).hasSize(2);
        assertThat(((ImportDescr) imports.get(0)).getTarget()).isEqualTo("java.util.HashMap");
        assertThat(((ImportDescr) imports.get(1)).getTarget()).isEqualTo("org.drools.mvel.compiler.*");

        final List globals = packageDescr.getGlobals();
        assertThat(globals).hasSize(2);
        final GlobalDescr x = (GlobalDescr) globals.get(0);
        final GlobalDescr yada = (GlobalDescr) globals.get(1);
        assertThat(x.getType()).isEqualTo("com.sample.X");
        assertThat(x.getIdentifier()).isEqualTo("x");
        assertThat(yada.getType()).isEqualTo("com.sample.Yada");
        assertThat(yada.getIdentifier()).isEqualTo("yada");

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get(0);
        final List names = functionDescr.getParameterNames();
        assertThat(names.get(0)).isEqualTo("foo");
        assertThat(names.get(1)).isEqualTo("bada");

        final List types = functionDescr.getParameterTypes();
        assertThat(types.get(0)).isEqualTo("Bar");
        assertThat(types.get(1)).isEqualTo("Bing");

        assertThat(functionDescr.getText().trim()).isEqualTo("System.out.println(\"hello world\");");
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
        assertThat(packageDescr.getName()).isEqualTo("com.sample");

        final List imports = packageDescr.getImports();
        assertThat(imports).hasSize(2);
        assertThat(((ImportDescr) imports.get(0)).getTarget()).isEqualTo("java.util.List");
        assertThat(((ImportDescr) imports.get(1)).getTarget()).isEqualTo("org.drools.mvel.compiler.Person");

        RuleDescr ruleDescr = (RuleDescr) packageDescr.getRules().get(0);
        assertThat(ruleDescr.getName()).isEqualTo("simple_rule1");
        AndDescr lhs = ruleDescr.getLhs();
        PatternDescr patternDescr = (PatternDescr) lhs.getDescrs().get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo("Person");
        ExprConstraintDescr expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertThat(expr.getExpression()).isEqualTo("name == \"darth\"");

        ruleDescr = (RuleDescr) packageDescr.getRules().get(1);
        assertThat(ruleDescr.getName()).isEqualTo("simple_rule2");
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo("Person");
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertThat(expr.getExpression()).isEqualTo("age == 35 || == -3.5");

        ruleDescr = (RuleDescr) packageDescr.getRules().get(2);
        assertThat(ruleDescr.getName()).isEqualTo("simple_rule3");
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo("Person");
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertThat(expr.getExpression()).isEqualTo("age == 35 || (!= 7.0 && != -70)");

        ruleDescr = (RuleDescr) packageDescr.getRules().get(3);
        assertThat(ruleDescr.getName()).isEqualTo("simple_rule3");
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(1);
        assertThat(patternDescr.getObjectType()).isEqualTo("Person");
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertThat(expr.getExpression()).isEqualTo("name == $s");

        ruleDescr = (RuleDescr) packageDescr.getRules().get(4);
        assertThat(ruleDescr.getName()).isEqualTo("simple_rule4");
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(1);
        assertThat(patternDescr.getObjectType()).isEqualTo("Person");
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertThat(expr.getExpression()).isEqualTo("(name == $s) || (age == 35 || (!= 7.0 && != -70))");

        ruleDescr = (RuleDescr) packageDescr.getRules().get(5);
        assertThat(ruleDescr.getName()).isEqualTo("simple_rule5");
        lhs = ruleDescr.getLhs();
        patternDescr = (PatternDescr) lhs.getDescrs().get(1);
        assertThat(patternDescr.getObjectType()).isEqualTo("Person");
        expr = (ExprConstraintDescr) ((AndDescr) patternDescr.getConstraint()).getDescrs().get(0);
        assertThat(expr.getExpression()).isEqualTo("(name == $s) || ((age != 34) && (age != 37) && (name != \"yoda\"))");

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
        assertThat(packageDescr.getName()).isEqualTo("com.sample");

        final List imports = packageDescr.getImports();
        assertThat(imports).hasSize(2);
        assertThat(((ImportDescr) imports.get(0)).getTarget()).isEqualTo("java.util.HashMap");
        assertThat(((ImportDescr) imports.get(1)).getTarget()).isEqualTo("org.drools.mvel.compiler.*");

        final List globals = packageDescr.getGlobals();
        assertThat(globals).hasSize(2);
        final GlobalDescr x = (GlobalDescr) globals.get(0);
        final GlobalDescr yada = (GlobalDescr) globals.get(1);
        assertThat(x.getType()).isEqualTo("com.sample.X");
        assertThat(x.getIdentifier()).isEqualTo("x");
        assertThat(yada.getType()).isEqualTo("com.sample.Yada");
        assertThat(yada.getIdentifier()).isEqualTo("yada");

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get(0);
        final List names = functionDescr.getParameterNames();
        assertThat(names.get(0)).isEqualTo("foo");
        assertThat(names.get(1)).isEqualTo("bada");

        final List types = functionDescr.getParameterTypes();
        assertThat(types.get(0)).isEqualTo("Bar");
        assertThat(types.get(1)).isEqualTo("Bing");

        assertThat(functionDescr.getText().trim()).isEqualTo("System.out.println(\"hello world\");");

        final RuleDescr ruleDescr = (RuleDescr) packageDescr.getRules().get(0);
        assertThat(ruleDescr.getName()).isEqualTo("my rule");

        final String consequence = (String) ruleDescr.getConsequence();
        assertThat(consequence).isNotNull();
        assertThat(consequence.trim()).isEqualTo("System.out.println( \"hello\" );");
    }

    @Test
    public void testParseQuery() throws Exception {
        final XmlPackageReader xmlPackageReader = getXmReader();
        xmlPackageReader.read(new InputStreamReader(getClass().getResourceAsStream("test_ParseQuery.xml")));
        final PackageDescr packageDescr = xmlPackageReader.getPackageDescr();
        assertThat(packageDescr).isNotNull();
        assertThat(packageDescr.getName()).isEqualTo("com.sample");

        final List imports = packageDescr.getImports();
        assertThat(imports).hasSize(2);
        assertThat(((ImportDescr) imports.get(0)).getTarget()).isEqualTo("java.util.HashMap");
        assertThat(((ImportDescr) imports.get(1)).getTarget()).isEqualTo("org.drools.mvel.compiler.*");

        final List globals = packageDescr.getGlobals();
        assertThat(globals).hasSize(2);
        final GlobalDescr x = (GlobalDescr) globals.get(0);
        final GlobalDescr yada = (GlobalDescr) globals.get(1);
        assertThat(x.getType()).isEqualTo("com.sample.X");
        assertThat(x.getIdentifier()).isEqualTo("x");
        assertThat(yada.getType()).isEqualTo("com.sample.Yada");
        assertThat(yada.getIdentifier()).isEqualTo("yada");

        final FunctionDescr functionDescr = (FunctionDescr) packageDescr.getFunctions().get(0);
        final List names = functionDescr.getParameterNames();
        assertThat(names.get(0)).isEqualTo("foo");
        assertThat(names.get(1)).isEqualTo("bada");

        final List types = functionDescr.getParameterTypes();
        assertThat(types.get(0)).isEqualTo("Bar");
        assertThat(types.get(1)).isEqualTo("Bing");

        assertThat(functionDescr.getText().trim()).isEqualTo("System.out.println(\"hello world\");");

        final QueryDescr queryDescr = (QueryDescr) packageDescr.getRules().get(0);
        assertThat(queryDescr.getName()).isEqualTo("my query");

        final AndDescr lhs = queryDescr.getLhs();
        assertThat(lhs.getDescrs()).hasSize(1);
        final PatternDescr patternDescr = (PatternDescr) lhs.getDescrs().get(0);
        assertThat(patternDescr.getObjectType()).isEqualTo("Foo");

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
