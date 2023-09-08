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
package org.drools.compiler.integrationtests;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.factmodel.AnnotationDefinition;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
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

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(kieBuilder.getResults().getMessages()).hasSize(1);
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
        assertThat(kieBuilder.getResults().getMessages()).hasSize(1);
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

        assertThat(adef.getPropertyValue("klass")).isEqualTo(String.class);
        assertThat(Arrays.asList((Class[]) adef.getPropertyValue("klasses"))).isEqualTo(Arrays.asList(String.class, Integer.class));

        assertThat(adef).isNotNull();
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
        assertThat(defs.size()).isEqualTo(1);

        final AnnotationDefinition outer = defs.get(Outer.class.getName().replace("$", "."));
        assertThat(outer).isNotNull();

        final Object val = outer.getPropertyValue("value");
        assertThat(val).isNotNull();
        assertThat(val instanceof AnnotationDefinition).isTrue();

        final AnnotationDefinition inner = (AnnotationDefinition) val;
        assertThat(inner.getPropertyValue("text")).isEqualTo("world");
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
        assertThat(defs.size()).isEqualTo(1);

        final AnnotationDefinition outer = defs.get(Outer.class.getName().replace("$", "."));
        assertThat(outer).isNotNull();

        final Object val = outer.getPropertyValue("values");
        assertThat(val).isNotNull();
        assertThat(val instanceof AnnotationDefinition[]).isTrue();
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
        assertThat(rule.getMetaData().containsKey(Inner.class.getName().replace("$", "."))).isTrue();

        final Object obj = rule.getMetaData().get(Inner.class.getName().replace("$", "."));
        assertThat(obj).isNotNull();
        assertThat(obj instanceof Map).isTrue();
        assertThat(((Map) obj).get("test")).isEqualTo("b");
        assertThat(((Map) obj).get("text")).isEqualTo("a");
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
        assertThat(defs.size()).isEqualTo(1);

        final AnnotationDefinition simple = defs.get(AnnotationsTest.Simple.class.getName().replace("$", "."));
        assertThat(simple).isNotNull();

        final Object val = simple.getPropertyValue("numbers");
        assertThat(val instanceof int[]).isTrue();
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

        assertThat(nested.size()).isEqualTo(1);

        final Map<String, AnnotationDefinition> annotations = ((Pattern) nested.get(0)).getAnnotations();

        assertThat(annotations.size()).isEqualTo(1);
        assertThat(annotations.keySet().iterator().next()).isNotNull();
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
