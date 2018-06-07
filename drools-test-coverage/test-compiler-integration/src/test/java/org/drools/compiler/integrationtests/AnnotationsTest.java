/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
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
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class AnnotationsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AnnotationsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    public enum AnnPropEnum {
        ONE(
                "one"),
        TWO(
                "two"),
        THREE(
                "three");

        private final String value;

        AnnPropEnum(final String s) {
            this.value = s;
        }

        public String getValue() {
            return value;
        }
    }

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.TYPE, ElementType.FIELD})
    public @interface Annot {

        int intProp() default 0;

        Class typeProp();

        String strProp() default "foo";

        AnnPropEnum enumProp() default AnnPropEnum.ONE;

        double[] dblArrProp() default {0.4, 0.5};

        Class[] typeArrProp();

        String[] strArrProp() default {"a", "b", "c"};

        AnnPropEnum[] enumArrProp() default {AnnPropEnum.TWO, AnnPropEnum.THREE};
    }

    @Test
    public void annotationTest() {

        final String drl = "package org.drools.compiler.test;\n " +
                "" +
                "import " + Position.class.getCanonicalName() + "; \n " +
                "import " + AnnotationsTest.class.getCanonicalName() + "; \n" +
                "import " + AnnotationsTest.Annot.class.getCanonicalName() + "; \n" +
                "" +
                "declare AnnotatedBean \n" +
                " @Deprecated \n" +
                "" +
                " @Annot( intProp=7 " +
                "         ,typeProp=String.class " +
                "         ,strProp=\"hello world\" " +
                "         ,enumProp=AnnPropEnum.THREE " +
                "         ,dblArrProp={1.0,2.0} " +
                "         ,typeArrProp={String.class, AnnotationsTest.class} " +
                "         ,strArrProp={\"x1\",\"x2\"} " +
                "         ,enumArrProp={AnnPropEnum.ONE, AnnPropEnum.THREE} " +
                "         ) \n " +
                " \n " +
                " @role(event) \n " +
                " " +
                " age : int \n" +
                " name : String      @key    @Position(0)    @Deprecated \n" +
                " end \n " +
                " " +
                " \n\n" +
                " " +
                "declare SecondBean \n " +
                " @NonexistingAnnotation" +
                "  \n" +
                " field : String @Annot \n" +
                "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);

        final Class clazz = kbase.getFactType("org.drools.compiler.test",
                                              "AnnotatedBean").getFactClass();
        assertNotNull(clazz);
        try {
            final Field fld = clazz.getDeclaredField("name");
            assertEquals(3,
                         fld.getAnnotations().length);
            assertNotNull(fld.getAnnotation(Deprecated.class));
            assertNotNull(fld.getAnnotation(Position.class));
            assertNotNull(fld.getAnnotation(Key.class));

            final Position pos = fld.getAnnotation(Position.class);
            assertEquals(0,
                         pos.value());
        } catch (final NoSuchFieldException nsfe) {
            fail("field name has not been generated correctly : " + nsfe.getMessage());
        }

        final Annotation[] anns = clazz.getAnnotations();
        assertEquals(3,
                     anns.length);
        assertNotNull(clazz.getAnnotation(Deprecated.class));
        assertNotNull(clazz.getAnnotation(Annot.class));
        assertNotNull(clazz.getAnnotation(Role.class));

        final Annot ann = (Annot) clazz.getAnnotation(Annot.class);
        assertEquals(7,
                     ann.intProp());
        assertEquals(String.class,
                     ann.typeProp());
        assertEquals("hello world",
                     ann.strProp());
        assertEquals(AnnPropEnum.THREE,
                     ann.enumProp());
        assertArrayEquals(new double[]{1.0, 2.0},
                          ann.dblArrProp(),
                          1e-16);
        assertArrayEquals(new Class[]{String.class, AnnotationsTest.class},
                          ann.typeArrProp());
        assertArrayEquals(new String[]{"x1", "x2"},
                          ann.strArrProp());
        assertArrayEquals(new AnnPropEnum[]{AnnPropEnum.ONE, AnnPropEnum.THREE},
                          ann.enumArrProp());

        final Class clazz2 = kbase.getFactType("org.drools.compiler.test",
                                               "SecondBean").getFactClass();
        assertNotNull(clazz2);
        final Annotation[] anns2 = clazz2.getAnnotations();
        assertEquals(0,
                     anns2.length);

        Annot ann2 = null;
        try {
            final Field fld2 = clazz2.getDeclaredField("field");
            assertEquals(1,
                         fld2.getAnnotations().length);
            assertNotNull(fld2.getAnnotation(Annot.class));
            ann2 = fld2.getAnnotation(Annot.class);
        } catch (final NoSuchFieldException nsfe) {
            fail("field name has not been generated correctly : " + nsfe.getMessage());
        }

        assertNotNull(ann2);
        assertEquals(0,
                     ann2.intProp());
        assertEquals("foo",
                     ann2.strProp());
        assertEquals(AnnPropEnum.ONE,
                     ann2.enumProp());
        assertArrayEquals(new double[]{0.4, 0.5},
                          ann2.dblArrProp(),
                          1e-16);
        assertArrayEquals(new String[]{"a", "b", "c"},
                          ann2.strArrProp());
        assertArrayEquals(new AnnPropEnum[]{AnnPropEnum.TWO, AnnPropEnum.THREE},
                          ann2.enumArrProp());
    }

    @Test
    public void annotationErrorTest() {

        final String drl = "package org.drools.compiler.test;\n " +
                "" +
                "declare MissingAnnotationBean \n" +
                " @IgnoreMissingAnnotation1 \n" +
                "" +
                " name : String      @IgnoreMissingAnnotation2( noProp = 999 ) \n" +
                " end \n ";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isEmpty();

        final String drl2 = "package org.drools.compiler.test;\n " +
                "" +
                "import " + AnnotationsTest.Annot.class.getCanonicalName() + "; \n" +
                "" +
                "" +
                "declare MissingAnnotationBean \n" +
                " @Annot( wrongProp1 = 1 ) \n" +
                "" +
                " name : String      @Annot( wrongProp2 = 2, wrongProp3 = 3 ) \n" +
                " end \n ";

        final KieBuilder kieBuilder2 = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl2);
        Assertions.assertThat(kieBuilder2.getResults().getMessages()).hasSize(3);
    }

    @Test
    public void testAnnotationNameClash() {
        final String drl = "package org.drools.test\n" +
                "" +
                "declare Annot\n" +
                " id : int " +
                " @" + AnnotationsTest.Annot.class.getCanonicalName() + "( intProp = 3, typeProp = String.class, typeArrProp = {} ) \n" +
                " " +
                "end\n" +
                "" +
                "rule X\n" +
                "when\n" +
                " \n" +
                "then\n" +
                " insert( new Annot( 22 ) ); " +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);
        final FactType ft = kbase.getFactType("org.drools.test", "Annot");
        try {
            final Object o = ft.newInstance();
            final Annot a = o.getClass().getDeclaredField("id").getAnnotation(Annot.class);
            assertEquals(3, a.intProp());
            assertEquals(String.class, a.typeProp());
            assertEquals(0, a.typeArrProp().length);
        } catch (final Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public static class Duration {

    }

    @Test
    public void testAnnotationNameClashWithRegularClass() {
        final String drl = "package org.drools.test\n" +
                "import " + Duration.class.getCanonicalName() + "; " +

                "declare Annot " +
                "  @role( event )" +
                "  @duration( durat ) " +
                "  durat : long " +
                "end\n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);
        final FactType ft = kbase.getFactType("org.drools.test", "Annot");
        assertNotNull(ft);
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

    public @interface Simple {

        int[] numbers();
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

        final Pattern p = ((Pattern) ((RuleImpl) kbase.getRule("org.drools.test", "Foo")).getLhs().getChildren().get(0));
        final AnnotationDefinition adef = p.getAnnotations().get(Outer.class.getName().replace("$", "."));

        assertEquals(String.class, adef.getPropertyValue("klass"));
        assertEquals(Arrays.asList(String.class, Integer.class),
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
    public void testTypedSimpleArrays() {
        final String drl = "package org.drools.test; " +
                "import " + Simple.class.getName().replace("$", ".") + "; " +

                "rule Foo " +
                "when " +
                "  String() @Simple( numbers = { 1, 2, 3 } ) " +
                "then " +
                "end ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("annotations-test", kieBaseTestConfiguration, drl);

        final Pattern p = ((Pattern) ((RuleImpl) kbase.getRule("org.drools.test", "Foo")).getLhs().getChildren().get(0));
        final Map<String, AnnotationDefinition> defs = p.getAnnotations();
        assertEquals(1, defs.size());

        final AnnotationDefinition simple = defs.get(Simple.class.getName().replace("$", "."));
        assertNotNull(simple);

        final Object val = simple.getPropertyValue("numbers");
        assertTrue(val instanceof int[]);
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
    public void testCollectAnnotationsParsingAndBuilding() {

        final String packageName = "org.drools.compiler.integrationtests";

        final String drl =
                "package " + packageName + "; " +
                        " " +
                        "dialect 'mvel' " +
                        " " +
                        "import java.util.Collection; " +
                        "import " + Annot.class.getCanonicalName() + "; " +
                        " " +
                        "rule \"test collect with annotation\" " +
                        "    when " +
                        "        Collection() from collect ( " +
                        "            String() @Annot " +
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

    @Test
    public void testAnnotationOnLHSAndMerging() {
        final String drl =
                "package org.drools.compiler; " +
                        " " +
                        "import " + Annot.class.getCanonicalName() + "; " +
                        " " +
                        "rule \"test collect with annotation\" " +
                        "    when " +
                        "       ( and @Annot " +
                        "         String() " +
                        "         Integer() ) " +
                        "    then " +
                        "end " +
                        "";

        KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, drl);
    }
}
