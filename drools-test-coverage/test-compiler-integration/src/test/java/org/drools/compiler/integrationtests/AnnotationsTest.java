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

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

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
        assertThat(clazz).isNotNull();
        try {
            final Field fld = clazz.getDeclaredField("name");
            assertThat(fld.getAnnotations().length).isEqualTo(3);
            assertThat(fld.getAnnotation(Deprecated.class)).isNotNull();
            assertThat(fld.getAnnotation(Position.class)).isNotNull();
            assertThat(fld.getAnnotation(Key.class)).isNotNull();

            final Position pos = fld.getAnnotation(Position.class);
            assertThat(pos.value()).isEqualTo(0);
        } catch (final NoSuchFieldException nsfe) {
            fail("field name has not been generated correctly : " + nsfe.getMessage());
        }

        final Annotation[] anns = clazz.getAnnotations();
        assertThat(anns.length).isEqualTo(3);
        assertThat(clazz.getAnnotation(Deprecated.class)).isNotNull();
        assertThat(clazz.getAnnotation(Annot.class)).isNotNull();
        assertThat(clazz.getAnnotation(Role.class)).isNotNull();

        final Annot ann = (Annot) clazz.getAnnotation(Annot.class);
        assertThat(ann.intProp()).isEqualTo(7);
        assertThat(ann.typeProp()).isEqualTo(String.class);
        assertThat(ann.strProp()).isEqualTo("hello world");
        assertThat(ann.enumProp()).isEqualTo(AnnPropEnum.THREE);
        assertThat(ann.dblArrProp()).isEqualTo(new double[]{1.0, 2.0});
        assertThat(ann.typeArrProp()).isEqualTo(new Class[]{String.class, AnnotationsTest.class});
        assertThat(ann.strArrProp()).isEqualTo(new String[]{"x1", "x2"});
        assertThat(ann.enumArrProp()).isEqualTo(new AnnPropEnum[]{AnnPropEnum.ONE, AnnPropEnum.THREE});

        final Class clazz2 = kbase.getFactType("org.drools.compiler.test",
                                               "SecondBean").getFactClass();
        assertThat(clazz2).isNotNull();
        final Annotation[] anns2 = clazz2.getAnnotations();
        assertThat(anns2.length).isEqualTo(0);

        Annot ann2 = null;
        try {
            final Field fld2 = clazz2.getDeclaredField("field");
            assertThat(fld2.getAnnotation(Annot.class)).isNotNull();
            ann2 = fld2.getAnnotation(Annot.class);
        } catch (final NoSuchFieldException nsfe) {
            fail("field name has not been generated correctly : " + nsfe.getMessage());
        }

        assertThat(ann2).isNotNull();
        assertThat(ann2.intProp()).isEqualTo(0);
        assertThat(ann2.strProp()).isEqualTo("foo");
        assertThat(ann2.enumProp()).isEqualTo(AnnPropEnum.ONE);
        assertThat(ann2.dblArrProp()).isEqualTo(new double[]{0.4, 0.5});
        assertThat(ann2.strArrProp()).isEqualTo(new String[]{"a", "b", "c"});
        assertThat(ann2.enumArrProp()).isEqualTo(new AnnPropEnum[]{AnnPropEnum.TWO, AnnPropEnum.THREE});
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
        assertThat(kieBuilder.getResults().getMessages()).isEmpty();

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
        assertThat(kieBuilder2.getResults().getMessages()).hasSize(3);
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
            assertThat(a.intProp()).isEqualTo(3);
            assertThat(a.typeProp()).isEqualTo(String.class);
            assertThat(a.typeArrProp().length).isEqualTo(0);
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
        assertThat(ft).isNotNull();
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
