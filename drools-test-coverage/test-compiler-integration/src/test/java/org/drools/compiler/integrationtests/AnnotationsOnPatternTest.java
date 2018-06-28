/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.definition.rule.Rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class AnnotationsOnPatternTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AnnotationsOnPatternTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testAnnotationWithUnknownProperty() {
        final String drl = "package org.drools.test; " +
                "import " + Outer.class.getName().replace("$", ".") + "; " +
                "import " + Inner.class.getName().replace("$", ".") + "; " +

                "rule Foo " +
                "when " +
                "  String() @Outer( missing = 3 ) " +
                "then " +
                "end ";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).hasSize(1);
    }

    @Test
    public void testAnnotationWithUnknownClass() {
        final String drl = "package org.drools.test; " +
                "import " + Outer.class.getName().replace("$", ".") + "; " +

                "rule Foo " +
                "when " +
                "  String() @Outer( klass = Foo.class ) " +
                "then " +
                "end ";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).hasSize(1);
    }

    @Test
    public void testAnnotationWithQualifiandClass() {
        final String drl = "package org.drools.test; " +
                "import " + Outer.class.getName().replace("$", ".") + "; " +

                "rule Foo " +
                "when " +
                "  String() @Outer( klass = String.class, klasses = { String.class, Integer.class } ) " +
                "then " +
                "end ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);

        final Pattern p = ((Pattern) ((RuleImpl ) kbase.getRule("org.drools.test", "Foo")).getLhs().getChildren().get(0));
        final AnnotationDefinition adef = p.getAnnotations().get(Outer.class.getName().replace("$", "."));

        assertEquals(String.class, adef.getPropertyValue("klass"));
        assertEquals( Arrays.asList(String.class, Integer.class),
                Arrays.asList((Class[]) adef.getPropertyValue("klasses")));

        assertNotNull(adef);
    }

    @Test
    public void testNestedAnnotations() {
        final String drl = "package org.drools.test; " +
                "import " + Outer.class.getName().replace("$", ".") + "; " +
                "import " + Inner.class.getName().replace("$", ".") + "; " +

                "rule Foo " +
                "when " +
                "  String() @Outer( value = @Inner( text = \"world\" ) ) " +
                "then " +
                "end ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);

        final Pattern p = ((Pattern) ((RuleImpl) kbase.getRule("org.drools.test", "Foo")).getLhs().getChildren().get(0));
        final Map<String, AnnotationDefinition> defs = p.getAnnotations();
        assertEquals(1, defs.size());

        final AnnotationDefinition outer = defs.get(Outer.class.getName().replace("$", "."));
        assertNotNull(outer);

        final Object val = outer.getPropertyValue("value");
        assertNotNull(val);
        assertTrue(val instanceof AnnotationDefinition);

        final AnnotationDefinition inner = (AnnotationDefinition) val;
        assertEquals("world", inner.getPropertyValue("text"));
    }

    @Test
    public void testNestedAnnotationsWithMultiplicity() {
        final String drl = "package org.drools.test; " +
                "import " + Outer.class.getName().replace("$", ".") + "; " +
                "import " + Inner.class.getName().replace("$", ".") + "; " +

                "rule Foo " +
                "when " +
                "  String() @Outer( values = { @Inner( text = \"hello\" ), @Inner( text = \"world\" ) } ) " +
                "then " +
                "end ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);

        final Pattern p = ((Pattern) ((RuleImpl) kbase.getRule("org.drools.test", "Foo")).getLhs().getChildren().get(0));
        final Map<String, AnnotationDefinition> defs = p.getAnnotations();
        assertEquals(1, defs.size());

        final AnnotationDefinition outer = defs.get(Outer.class.getName().replace("$", "."));
        assertNotNull(outer);

        final Object val = outer.getPropertyValue("values");
        assertNotNull(val);
        assertTrue(val instanceof AnnotationDefinition[]);
    }

    @Test
    public void testRuleAnnotations() {
        final String drl = "package org.drools.test; " +
                "import " + Inner.class.getName().replace("$", ".") + "; " +

                "rule Foo " +
                "@Inner( text=\"a\", test=\"b\" ) " +
                "when " +
                "then " +
                "end ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);

        final Rule rule = kbase.getRule("org.drools.test", "Foo");
        assertTrue(rule.getMetaData().containsKey(Inner.class.getName().replace("$", ".")));

        final Object obj = rule.getMetaData().get(Inner.class.getName().replace("$", "."));
        assertNotNull(obj);
        assertTrue(obj instanceof Map);
        assertEquals("b", ((Map) obj).get("test"));
        assertEquals("a", ((Map) obj).get("text"));
    }

    @Test
    public void testTypedSimpleArrays() {
        final String drl = "package org.drools.test; " +
                "import " + AnnotationsTest.Simple.class.getName().replace("$", ".") + "; " +

                "rule Foo " +
                "when " +
                "  String() @Simple( numbers = { 1, 2, 3 } ) " +
                "then " +
                "end ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);

        final Pattern p = ((Pattern) ((RuleImpl) kbase.getRule("org.drools.test", "Foo")).getLhs().getChildren().get(0));
        final Map<String, AnnotationDefinition> defs = p.getAnnotations();
        assertEquals(1, defs.size());

        final AnnotationDefinition simple = defs.get(AnnotationsTest.Simple.class.getName().replace("$", "."));
        assertNotNull(simple);

        final Object val = simple.getPropertyValue("numbers");
        assertTrue(val instanceof int[]);
    }

    @Test
    public void testCollectAnnotationsParsingAndBuilding() {

        final String packageName = "org.drools.compiler.integrationtests";

        final String drl =
                "package " + packageName + "; " +
                        " " +
                        "dialect 'mvel' " +
                        " " +
                        "import java.util.Collection; " +
                        "import " + Inner.class.getCanonicalName() + "; " +
                        " " +
                        "rule \"test collect with annotation\" " +
                        "    when " +
                        "        Collection() from collect ( " +
                        "            String() @Inner " +
                        "        ) " +
                        "    then " +
                        "end " +
                        "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);

        final RuleImpl rule = (RuleImpl) kbase.getRule(packageName, "test collect with annotation");

        final List<? extends RuleConditionElement> nested = ((Pattern) rule.getLhs().getChildren().get(0)).getSource().getNestedElements();

        assertEquals(1, nested.size());

        final Map<String, AnnotationDefinition> annotations = ((Pattern) nested.get(0)).getAnnotations();

        assertEquals(1, annotations.size());
        assertNotNull(annotations.keySet().iterator().next());
    }

    public @interface Inner {

        String text() default "hello";

        String test() default "world";
    }

    public @interface Outer {

        Inner value();

        Inner[] values() default {};

        Class klass() default Object.class;

        Class[] klasses() default {};

        int test();
    }
}
