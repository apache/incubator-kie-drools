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
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class AnnotationsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AnnotationsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
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

        Class typeProp() default Object.class;

        String strProp() default "foo";

        AnnPropEnum enumProp() default AnnPropEnum.ONE;

        double[] dblArrProp() default {0.4, 0.5};

        Class[] typeArrProp() default {};

        String[] strArrProp() default {"a", "b", "c"};

        AnnPropEnum[] enumArrProp() default {AnnPropEnum.TWO, AnnPropEnum.THREE};
    }

    @Test
    public void annotationTest() {

        final String drl = "package org.drools.compiler.test;\n " +
                "" +
                "import " + AnnPropEnum.class.getCanonicalName() + "; \n " +
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

    public @interface Simple {

        int[] numbers();
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
