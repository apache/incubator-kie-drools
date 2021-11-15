/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.gwt.functions.rebind;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.kie.dmn.feel.runtime.functions.extended.KieExtendedDMNFunctions;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MethodTemplatesTest {

    private static final List<FEELProfile> profiles = new ArrayList<>();

    static {
        profiles.add(new KieExtendedFEELProfile());
    }

    @Test
    public void testTemplate() {

        final String template = MethodTemplates.getTemplate();
        final String[] templateLines = template.split("\n");

        assertEquals("private ArrayList definitions = null;", templateLines[0].trim());
        assertEquals("public List<FunctionOverrideVariation> getDefinitions() {", templateLines[1].trim());
        assertEquals("if(definitions == null) {", templateLines[2].trim());
        assertEquals("definitions = new ArrayList();", templateLines[3].trim());
        assertTemplateBody(templateLines);
        assertEquals("return definitions;", templateLines[templateLines.length - 2].trim());
        assertEquals("}", templateLines[templateLines.length - 1].trim());
    }

    @Test
    public void testTemplatedFunctionsCanBeEvaluated() {
        final FEEL feel = FEEL.newInstance(profiles);

        final List<FEELFunction> templatedFunctions = MethodTemplates.getFeelFunctions();
        assertTrue(templatedFunctions.stream().allMatch(fn -> feel.evaluate(fn.getName()) instanceof FEELFunction));
    }

    @Test
    public void testTemplatedFunctionsIncludeBuiltInAndKieExtendedFunctions() {
        final List<FEELFunction> templatedFunctions = MethodTemplates.getFeelFunctions();

        assertTrue(templatedFunctions.containsAll(asList(BuiltInFunctions.getFunctions())));
        assertTrue(templatedFunctions.containsAll(asList(KieExtendedDMNFunctions.getFunctions())));
    }

    private void assertTemplateBody(final String[] templateLines) {

        final List<String> lines = asList(templateLines);

        assertLine(lines, "BuiltInType.DATE, \"org.kie.dmn.feel.runtime.functions.DateFunction\", \"date\", new Parameter( \"year\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"month\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"day\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.DATE, \"org.kie.dmn.feel.runtime.functions.DateFunction\", \"date\", new Parameter( \"from\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.DATE, \"org.kie.dmn.feel.runtime.functions.DateFunction\", \"date\", new Parameter( \"from\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.TIME, \"org.kie.dmn.feel.runtime.functions.TimeFunction\", \"time\", new Parameter( \"from\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.TIME, \"org.kie.dmn.feel.runtime.functions.TimeFunction\", \"time\", new Parameter( \"hour\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"minute\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"second\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.TIME, \"org.kie.dmn.feel.runtime.functions.TimeFunction\", \"time\", new Parameter( \"from\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.TIME, \"org.kie.dmn.feel.runtime.functions.TimeFunction\", \"time\", new Parameter( \"hour\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"minute\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"second\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"offset\", java.time.Duration.class, BuiltInType.DURATION ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.DateAndTimeFunction\", \"date and time\", new Parameter( \"year\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"month\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"day\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"hour\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"minute\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"second\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"hour offset\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.DateAndTimeFunction\", \"date and time\", new Parameter( \"year\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"month\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"day\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"hour\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"minute\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"second\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"timezone\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.DateAndTimeFunction\", \"date and time\", new Parameter( \"date\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ), new Parameter( \"time\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.DateAndTimeFunction\", \"date and time\", new Parameter( \"from\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.DateAndTimeFunction\", \"date and time\", new Parameter( \"year\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"month\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"day\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"hour\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"minute\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"second\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.DURATION, \"org.kie.dmn.feel.runtime.functions.DurationFunction\", \"duration\", new Parameter( \"from\", java.time.temporal.TemporalAmount.class, BuiltInType.DURATION ) )");
        assertLine(lines, "BuiltInType.DURATION, \"org.kie.dmn.feel.runtime.functions.DurationFunction\", \"duration\", new Parameter( \"from\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.DURATION, \"org.kie.dmn.feel.runtime.functions.YearsAndMonthsFunction\", \"years and months duration\", new Parameter( \"from\", java.time.temporal.Temporal.class, BuiltInType.DATE_TIME ), new Parameter( \"to\", java.time.temporal.Temporal.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.StringFunction\", \"string\", new Parameter( \"from\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.StringFunction\", \"string\", new Parameter( \"mask\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"p\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.NumberFunction\", \"number\", new Parameter( \"from\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"grouping separator\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"decimal separator\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.SubstringFunction\", \"substring\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"start position\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.SubstringFunction\", \"substring\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"start position\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"length\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.SubstringBeforeFunction\", \"substring before\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"match\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.SubstringAfterFunction\", \"substring after\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"match\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.StringLengthFunction\", \"string length\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.StringUpperCaseFunction\", \"upper case\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.StringLowerCaseFunction\", \"lower case\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.ContainsFunction\", \"contains\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"match\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.StartsWithFunction\", \"starts with\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"match\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.EndsWithFunction\", \"ends with\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"match\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.MatchesFunction\", \"matches\", new Parameter( \"input\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"pattern\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.MatchesFunction\", \"matches\", new Parameter( \"input\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"pattern\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"flags\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.ReplaceFunction\", \"replace\", new Parameter( \"input\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"pattern\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"replacement\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.ReplaceFunction\", \"replace\", new Parameter( \"input\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"pattern\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"replacement\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"flags\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.ListContainsFunction\", \"list contains\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"element\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.CountFunction\", \"count\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.CountFunction\", \"count\", new Parameter( \"c\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.MinFunction\", \"min\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.MinFunction\", \"min\", new Parameter( \"c\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.MaxFunction\", \"max\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.MaxFunction\", \"max\", new Parameter( \"c\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.SumFunction\", \"sum\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.SumFunction\", \"sum\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.SumFunction\", \"sum\", new Parameter( \"list\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.MeanFunction\", \"mean\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.MeanFunction\", \"mean\", new Parameter( \"list\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.MeanFunction\", \"mean\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.SublistFunction\", \"sublist\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"start position\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.SublistFunction\", \"sublist\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"start position\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"length\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.AppendFunction\", \"append\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"item\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.AppendFunction\", \"append\", new Parameter( \"list\", java.lang.Object.class, BuiltInType.UNKNOWN ), new Parameter( \"item\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.ConcatenateFunction\", \"concatenate\", new Parameter( \"list\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.InsertBeforeFunction\", \"insert before\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"position\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"newItem\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.RemoveFunction\", \"remove\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"position\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.ReverseFunction\", \"reverse\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.IndexOfFunction\", \"index of\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"match\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.UnionFunction\", \"union\", new Parameter( \"list\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.DistinctValuesFunction\", \"distinct values\", new Parameter( \"list\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.FlattenFunction\", \"flatten\", new Parameter( \"list\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.DecimalFunction\", \"decimal\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"scale\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.FloorFunction\", \"floor\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.CeilingFunction\", \"ceiling\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.DecisionTableFunction\", \"decision table\", new Parameter( \"ctx\", org.kie.dmn.feel.lang.EvaluationContext.class, BuiltInType.UNKNOWN ), new Parameter( \"outputs\", java.lang.Object.class, BuiltInType.UNKNOWN ), new Parameter( \"input expression list\", java.lang.Object.class, BuiltInType.UNKNOWN ), new Parameter( \"input values list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"output values\", java.lang.Object.class, BuiltInType.UNKNOWN ), new Parameter( \"rule list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"hit policy\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"default output value\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.NotFunction\", \"not\", new Parameter( \"negand\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.SortFunction\", \"sort\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.SortFunction\", \"sort\", new Parameter( \"ctx\", org.kie.dmn.feel.lang.EvaluationContext.class, BuiltInType.UNKNOWN ), new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"precedes\", org.kie.dmn.feel.runtime.FEELFunction.class, BuiltInType.FUNCTION ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.GetEntriesFunction\", \"get entries\", new Parameter( \"m\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.GetValueFunction\", \"get value\", new Parameter( \"m\", java.lang.Object.class, BuiltInType.UNKNOWN ), new Parameter( \"key\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.AllFunction\", \"all\", new Parameter( \"b\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.AllFunction\", \"all\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.AllFunction\", \"all\", new Parameter( \"list\", java.lang.Boolean.class, BuiltInType.BOOLEAN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.AnyFunction\", \"any\", new Parameter( \"b\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.AnyFunction\", \"any\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.AnyFunction\", \"any\", new Parameter( \"list\", java.lang.Boolean.class, BuiltInType.BOOLEAN ) )");
        assertLine(lines, "BuiltInType.DURATION, \"org.kie.dmn.feel.runtime.functions.AbsFunction\", \"abs\", new Parameter( \"n\", java.time.Duration.class, BuiltInType.DURATION ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.AbsFunction\", \"abs\", new Parameter( \"n\", java.time.Period.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.AbsFunction\", \"abs\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.DURATION, \"org.kie.dmn.feel.runtime.functions.AbsFunction\", \"abs\", new Parameter( \"n\", org.kie.dmn.feel.lang.types.impl.ComparablePeriod.class, BuiltInType.DURATION ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.ModuloFunction\", \"modulo\", new Parameter( \"dividend\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"divisor\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.ProductFunction\", \"product\", new Parameter( \"list\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.ProductFunction\", \"product\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.ProductFunction\", \"product\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.SplitFunction\", \"split\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"delimiter\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"flags\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.SplitFunction\", \"split\", new Parameter( \"string\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"delimiter\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.StddevFunction\", \"stddev\", new Parameter( \"list\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.StddevFunction\", \"stddev\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.StddevFunction\", \"stddev\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.ModeFunction\", \"mode\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.ModeFunction\", \"mode\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.SqrtFunction\", \"sqrt\", new Parameter( \"number\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.LogFunction\", \"log\", new Parameter( \"number\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.ExpFunction\", \"exp\", new Parameter( \"number\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.EvenFunction\", \"even\", new Parameter( \"number\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.OddFunction\", \"odd\", new Parameter( \"number\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.MedianFunction\", \"median\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.MedianFunction\", \"median\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.DayOfWeekFunction\", \"day of week\", new Parameter( \"date\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.DayOfYearFunction\", \"day of year\", new Parameter( \"date\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.MonthOfYearFunction\", \"month of year\", new Parameter( \"date\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.WeekOfYearFunction\", \"week of year\", new Parameter( \"date\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.IsFunction\", \"is\", new Parameter( \"value1\", java.lang.Object.class, BuiltInType.UNKNOWN ), new Parameter( \"value2\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.AfterFunction\", \"after\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.AfterFunction\", \"after\", new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ), new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.AfterFunction\", \"after\", new Parameter( \"point1\", java.lang.Comparable.class, BuiltInType.UNKNOWN ), new Parameter( \"point2\", java.lang.Comparable.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.AfterFunction\", \"after\", new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.BeforeFunction\", \"before\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.BeforeFunction\", \"before\", new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ), new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.BeforeFunction\", \"before\", new Parameter( \"point1\", java.lang.Comparable.class, BuiltInType.UNKNOWN ), new Parameter( \"point2\", java.lang.Comparable.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.BeforeFunction\", \"before\", new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.CoincidesFunction\", \"coincides\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.CoincidesFunction\", \"coincides\", new Parameter( \"point1\", java.lang.Comparable.class, BuiltInType.UNKNOWN ), new Parameter( \"point2\", java.lang.Comparable.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.StartsFunction\", \"starts\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.StartsFunction\", \"starts\", new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ), new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.StartedByFunction\", \"started by\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.StartedByFunction\", \"started by\", new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.FinishesFunction\", \"finishes\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.FinishesFunction\", \"finishes\", new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ), new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.FinishedByFunction\", \"finished by\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.FinishedByFunction\", \"finished by\", new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.DuringFunction\", \"during\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.DuringFunction\", \"during\", new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ), new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.IncludesFunction\", \"includes\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.IncludesFunction\", \"includes\", new Parameter( \"range\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"point\", java.lang.Comparable.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.OverlapsFunction\", \"overlaps\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.OverlapsBeforeFunction\", \"overlaps before\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.OverlapsAfterFunction\", \"overlaps after\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.MeetsFunction\", \"meets\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.interval.MetByFunction\", \"met by\", new Parameter( \"range1\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ), new Parameter( \"range2\", org.kie.dmn.feel.runtime.Range.class, BuiltInType.RANGE ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.extended.TimeFunction\", \"time\", new Parameter( \"from\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.extended.TimeFunction\", \"time\", new Parameter( \"hour\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"minute\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"second\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"offset\", java.time.Duration.class, BuiltInType.DURATION ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.extended.TimeFunction\", \"time\", new Parameter( \"hour\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"minute\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"second\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.extended.TimeFunction\", \"time\", new Parameter( \"from\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.extended.DateFunction\", \"date\", new Parameter( \"from\", java.time.temporal.TemporalAccessor.class, BuiltInType.DATE_TIME ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.extended.DateFunction\", \"date\", new Parameter( \"year\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"month\", java.lang.Number.class, BuiltInType.NUMBER ), new Parameter( \"day\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.extended.DateFunction\", \"date\", new Parameter( \"from\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.DURATION, \"org.kie.dmn.feel.runtime.functions.extended.DurationFunction\", \"duration\", new Parameter( \"from\", java.time.temporal.TemporalAmount.class, BuiltInType.DURATION ) )");
        assertLine(lines, "BuiltInType.DURATION, \"org.kie.dmn.feel.runtime.functions.extended.DurationFunction\", \"duration\", new Parameter( \"from\", java.lang.String.class, BuiltInType.STRING ) )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.extended.NowFunction\", \"now\" )");
        assertLine(lines, "BuiltInType.DATE_TIME, \"org.kie.dmn.feel.runtime.functions.extended.TodayFunction\", \"today\" )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.extended.CodeFunction\", \"code\", new Parameter( \"value\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.extended.InvokeFunction\", \"invoke\", new Parameter( \"ctx\", org.kie.dmn.feel.lang.EvaluationContext.class, BuiltInType.UNKNOWN ), new Parameter( \"namespace\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"model name\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"decision name\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"parameters\", java.util.Map.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.extended.ContextPutFunction\", \"context put\", new Parameter( \"context\", java.lang.Object.class, BuiltInType.UNKNOWN ), new Parameter( \"keys\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"value\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.extended.ContextPutFunction\", \"context put\", new Parameter( \"context\", java.lang.Object.class, BuiltInType.UNKNOWN ), new Parameter( \"key\", java.lang.String.class, BuiltInType.STRING ), new Parameter( \"value\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.extended.ContextMergeFunction\", \"context merge\", new Parameter( \"contexts\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.extended.ContextFunction\", \"context\", new Parameter( \"entries\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.FloorFunction\", \"floor\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"scale\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.FloorFunction\", \"floor\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.CeilingFunction\", \"ceiling\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"scale\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.CeilingFunction\", \"ceiling\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.RoundUpFunction\", \"round up\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"scale\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.RoundUpFunction\", \"round up\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.RoundDownFunction\", \"round down\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"scale\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.RoundDownFunction\", \"round down\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.RoundHalfUpFunction\", \"round half up\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"scale\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.RoundHalfUpFunction\", \"round half up\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.RoundHalfDownFunction\", \"round half down\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ), new Parameter( \"scale\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.extended.RoundHalfDownFunction\", \"round half down\", new Parameter( \"n\", java.math.BigDecimal.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAnyFunction\", \"nn any\", new Parameter( \"list\", java.lang.Boolean.class, BuiltInType.BOOLEAN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAnyFunction\", \"nn any\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAnyFunction\", \"nn any\", new Parameter( \"b\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAllFunction\", \"nn all\", new Parameter( \"list\", java.lang.Boolean.class, BuiltInType.BOOLEAN ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAllFunction\", \"nn all\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.BOOLEAN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNAllFunction\", \"nn all\", new Parameter( \"b\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNCountFunction\", \"nn count\", new Parameter( \"c\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNCountFunction\", \"nn count\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMaxFunction\", \"nn max\", new Parameter( \"c\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMaxFunction\", \"nn max\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMeanFunction\", \"nn mean\", new Parameter( \"list\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMeanFunction\", \"nn mean\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMeanFunction\", \"nn mean\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMedianFunction\", \"nn median\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMedianFunction\", \"nn median\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMinFunction\", \"nn min\", new Parameter( \"c\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.UNKNOWN, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNMinFunction\", \"nn min\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNModeFunction\", \"nn mode\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.LIST, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNModeFunction\", \"nn mode\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNStddevFunction\", \"nn stddev\", new Parameter( \"list\", java.lang.Object.class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNStddevFunction\", \"nn stddev\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNStddevFunction\", \"nn stddev\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNSumFunction\", \"nn sum\", new Parameter( \"list\", java.lang.Number.class, BuiltInType.NUMBER ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNSumFunction\", \"nn sum\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.NUMBER, \"org.kie.dmn.feel.runtime.functions.twovaluelogic.NNSumFunction\", \"nn sum\", new Parameter( \"n\", Object[].class, BuiltInType.UNKNOWN ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.extended.StringJoinFunction\", \"string join\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ) )");
        assertLine(lines, "BuiltInType.STRING, \"org.kie.dmn.feel.runtime.functions.extended.StringJoinFunction\", \"string join\", new Parameter( \"list\", java.util.List.class, BuiltInType.LIST ), new Parameter( \"delimiter\", java.lang.String.class, BuiltInType.STRING ) )");
    }

    private void assertLine(final List<String> lines,
                            final String line) {
        final String functionAdd = String.format("      definitions.add( new FunctionOverrideVariation( %s );", line);
        assertTrue("Could not find " + functionAdd, lines.contains(functionAdd));
    }
}
