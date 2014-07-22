/*
 * Copyright 2011 JBoss Inc
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

package org.drools.reteoo.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.io.impl.ByteArrayResource;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.MBeansOption;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.io.ResourceFactory;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public class AnnotationsTest extends CommonTestMethodBase {

    public static enum AnnPropEnum {
        ONE(
                "one"), TWO(
                "two"), THREE(
                "three");

        private String value;

        AnnPropEnum(String s) {
            this.value = s;
        }

        public String getValue() {
            return value;
        }
    }

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = {ElementType.TYPE, ElementType.FIELD})
    public static @interface Annot {
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

        String drl = "package org.drools.compiler.test;\n " +
                     "" +
                     "import org.kie.api.definition.type.Position; \n " +
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString( drl );

        Class clazz = kbase.getFactType( "org.drools.compiler.test",
                                         "AnnotatedBean" ).getFactClass();
        assertNotNull( clazz );
        try {
            Field fld = clazz.getDeclaredField( "name" );
            assertEquals( 3,
                          fld.getAnnotations().length );
            assertNotNull( fld.getAnnotation( Deprecated.class ) );
            assertNotNull( fld.getAnnotation( Position.class ) );
            assertNotNull( fld.getAnnotation( Key.class ) );

            Position pos = fld.getAnnotation( Position.class );
            assertEquals( 0,
                          pos.value() );
        } catch ( NoSuchFieldException nsfe ) {
            fail( "field name has not been generated correctly : " + nsfe.getMessage() );
        }

        Annotation[] anns = clazz.getAnnotations();
        assertEquals( 3,
                      anns.length );
        assertNotNull( clazz.getAnnotation( Deprecated.class ) );
        assertNotNull( clazz.getAnnotation( Annot.class ) );
        assertNotNull( clazz.getAnnotation( Role.class ) );

        Annot ann = (Annot) clazz.getAnnotation( Annot.class );
        assertEquals( 7,
                      ann.intProp() );
        assertEquals( String.class,
                      ann.typeProp() );
        assertEquals( "hello world",
                      ann.strProp() );
        assertEquals( AnnPropEnum.THREE,
                      ann.enumProp() );
        assertArrayEquals( new double[]{1.0, 2.0},
                           ann.dblArrProp(),
                           1e-16 );
        assertArrayEquals( new Class[]{String.class, AnnotationsTest.class},
                           ann.typeArrProp() );
        assertArrayEquals( new String[]{"x1", "x2"},
                           ann.strArrProp() );
        assertArrayEquals( new AnnPropEnum[]{AnnPropEnum.ONE, AnnPropEnum.THREE},
                           ann.enumArrProp() );

        Class clazz2 = kbase.getFactType( "org.drools.compiler.test",
                                          "SecondBean" ).getFactClass();
        assertNotNull( clazz2 );
        Annotation[] anns2 = clazz2.getAnnotations();
        assertEquals( 0,
                      anns2.length );

        Annot ann2 = null;
        try {
            Field fld2 = clazz2.getDeclaredField( "field" );
            assertEquals( 1,
                          fld2.getAnnotations().length );
            assertNotNull( fld2.getAnnotation( Annot.class ) );
            ann2 = fld2.getAnnotation( Annot.class );
        } catch ( NoSuchFieldException nsfe ) {
            fail( "field name has not been generated correctly : " + nsfe.getMessage() );
        }

        assertNotNull( ann2 );
        assertEquals( 0,
                      ann2.intProp() );
        assertEquals( "foo",
                      ann2.strProp() );
        assertEquals( AnnPropEnum.ONE,
                      ann2.enumProp() );
        assertArrayEquals( new double[]{0.4, 0.5},
                           ann2.dblArrProp(),
                           1e-16 );
        assertArrayEquals( new String[]{"a", "b", "c"},
                           ann2.strArrProp() );
        assertArrayEquals( new AnnPropEnum[]{AnnPropEnum.TWO, AnnPropEnum.THREE},
                           ann2.enumArrProp() );

    }

    @Test
    public void annotationErrorTest() {

        String drl = "package org.drools.compiler.test;\n " +
                     "" +
                     "declare MissingAnnotationBean \n" +
                     " @IgnoreMissingAnnotation1 \n" +
                     "" +
                     " name : String      @IgnoreMissingAnnotation2( noProp = 999 ) \n" +
                     " end \n ";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( drl.getBytes() ),
                      ResourceType.DRL );
        assertEquals( 0,
                      kbuilder.getErrors().size() );

        String drl2 = "package org.drools.compiler.test;\n " +
                      "" +
                      "import " + AnnotationsTest.Annot.class.getCanonicalName() + "; \n" +
                      "" +
                      "" +
                      "declare MissingAnnotationBean \n" +
                      " @Annot( wrongProp1 = 1 ) \n" +
                      "" +
                      " name : String      @Annot( wrongProp2 = 2, wrongProp3 = 3 ) \n" +
                      " end \n ";

        KnowledgeBuilder kbuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder2.add( new ByteArrayResource( drl2.getBytes() ),
                       ResourceType.DRL );
        assertEquals( 3,
                      kbuilder2.getErrors().size() );

    }

    @Test
    public void testRuleAnnotation() {
        String drl = "package org.drools.compiler.integrationtests\n" +
                     "import " + Person.class.getCanonicalName() + "; \n" +
                     "rule X\n" +
                     "    @author(\"John Doe\")\n" +
                     "    @output(Hello World!)\n" + // backward compatibility
                     "    @value( 10 + 10 )\n" +
                     "    @alt( \"Hello \"+\"World!\" )\n" +
                     "when\n" +
                     "    Person()\n" +
                     "then\n" +
                     "end";
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        conf.setOption( MBeansOption.ENABLED );
        conf.setOption( RuleEngineOption.PHREAK );

        KnowledgeBase kbase = loadKnowledgeBase( "kb1",
                                                 drl,
                                                 conf );

        Rule rule = kbase.getRule( "org.drools.compiler.integrationtests",
                                   "X" );

        Assert.assertEquals( "John Doe",
                             rule.getMetaData().get( "author" ) );
        Assert.assertEquals( "Hello World!",
                             rule.getMetaData().get( "output" ) );
        Assert.assertEquals( 20,
                             ((Number)rule.getMetaData().get( "value" )).intValue() );
        Assert.assertEquals( "Hello World!",
                             rule.getMetaData().get( "alt" ) );

    }

    @Test
    public void testRuleAnnotation2() {
        String drl = "package org.drools.compiler.integrationtests\n" +
                     "import " + Person.class.getCanonicalName() + "; \n" +
                     "rule X\n" +
                     "    @alt(\" \\\"<- these are supposed to be the only quotes ->\\\" \")\n" +
                     "when\n"+
                     "    Person()\n" +
                     "then\n" +
                     "end";
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        conf.setOption( MBeansOption.ENABLED );

        KnowledgeBase kbase = loadKnowledgeBase( "kb1",
                                                 drl,
                                                 conf );

        Rule rule = kbase.getRule( "org.drools.compiler.integrationtests",
                                   "X" );

        Assert.assertEquals( " \"<- these are supposed to be the only quotes ->\" ",
                             rule.getMetaData().get( "alt" ) );

    }

    private KnowledgeBase loadKnowledgeBase( String id,
                                             String drl,
                                             KieBaseConfiguration conf ) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newReaderResource( new StringReader( drl ) ),
                      ResourceType.DRL );
        Assert.assertFalse( kbuilder.getErrors().toString(),
                            kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( id,
                                                                     conf );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

}
