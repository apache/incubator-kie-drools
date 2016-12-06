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

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.RuleConditionElement;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.builder.Message;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.MBeansOption;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class AnnotationsTest  extends CommonTestMethodBase {

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
        assertEquals( 4,
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

    @Test
    public void testAnnotationNameClash() {
        String drl = "package org.drools.test\n" +
                     "" +
                     "declare Annot\n" +
                     " id : int " +
                     " @org.drools.compiler.integrationtests.AnnotationsTest.Annot( intProp = 3, typeProp = String.class, typeArrProp = {} ) \n" +
                     " " +
                     "end\n" +
                     "" +
                     "rule X\n" +
                     "when\n"+
                     " \n" +
                     "then\n" +
                     " insert( new Annot( 22 ) ); " +
                     "end";

        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBase kbase = loadKnowledgeBase( "kb1", drl, conf );
        FactType ft = kbase.getFactType( "org.drools.test", "Annot" );
        try {
            Object o = ft.newInstance();
            Annot a = o.getClass().getDeclaredField( "id" ).getAnnotation( Annot.class );
            assertEquals( 3, a.intProp() );
            assertEquals( String.class, a.typeProp() );
            assertEquals( 0, a.typeArrProp().length );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }



    public static class Duration { }

    @Test
    public void testAnnotationNameClashWithRegularClass() {
        String drl = "package org.drools.test\n" +
                     "import " + Duration.class.getCanonicalName() + "; " +

                     "declare Annot " +
                     "  @role( event )" +
                     "  @duration( durat ) " +
                     "  durat : long " +
                     "end\n" +
                     "";

        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();

        KnowledgeBase kbase = loadKnowledgeBase( "kb1", drl, conf );
        FactType ft = kbase.getFactType( "org.drools.test", "Annot" );
        assertNotNull( ft );
    }

    public static @interface Inner {
        String text() default "hello";
        String test() default "world";
    }

    public static @interface Outer {
        Inner value();
        Inner[] values() default {};
        Class klass() default Object.class;
        Class[] klasses() default {};
        int test();
    }

    public static @interface Simple {
        int[] numbers();
    }

    @Test
    public void testAnnotationWithUnknownProperty() {
        String drl = "package org.drools.test; " +
                     "import " + Outer.class.getName().replace( "$", "." ) + "; " +
                     "import " + Inner.class.getName().replace( "$", "." ) + "; " +

                     "rule Foo " +
                     "when " +
                     "  String() @Outer( missing = 3 ) " +
                     "then " +
                     "end ";
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );

        assertEquals( 1, helper.verify().getMessages( Message.Level.ERROR ).size() );
    }

    @Test
    public void testAnnotationWithUnknownClass() {
        String drl = "package org.drools.test; " +
                     "import " + Outer.class.getName().replace( "$", "." ) + "; " +

                     "rule Foo " +
                     "when " +
                     "  String() @Outer( klass = Foo.class ) " +
                     "then " +
                     "end ";
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );

        assertEquals( 1, helper.verify().getMessages( Message.Level.ERROR ).size() );
    }

    @Test
    public void testAnnotationWithQualifiandClass() {
        String drl = "package org.drools.test; " +
                     "import " + Outer.class.getName().replace( "$", "." ) + "; " +

                     "rule Foo " +
                     "when " +
                     "  String() @Outer( klass = String.class, klasses = { String.class, Integer.class } ) " +
                     "then " +
                     "end ";
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );

        Pattern p = ((Pattern) (( RuleImpl ) helper.build().getRule( "org.drools.test", "Foo" )).getLhs().getChildren().get( 0 ));
        AnnotationDefinition adef = p.getAnnotations().get( Outer.class.getName().replace( "$", "." ) );

        assertEquals( String.class, adef.getPropertyValue( "klass" ) );
        assertEquals( Arrays.asList( new Class[] { String.class, Integer.class } ),
                      Arrays.asList( (Class[]) adef.getPropertyValue( "klasses" ) ) );

        assertNotNull( adef );

    }

    @Test
    public void testNestedAnnotations() {
        String drl = "package org.drools.test; " +
                     "import " + Outer.class.getName().replace( "$", "." ) + "; " +
                     "import " + Inner.class.getName().replace( "$", "." ) + "; " +

                     "rule Foo " +
                     "when " +
                     "  String() @Outer( value = @Inner( text = \"world\" ) ) " +
                     "then " +
                     "end ";
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );

        Pattern p = ((Pattern) (( RuleImpl ) helper.build().getRule( "org.drools.test", "Foo" )).getLhs().getChildren().get( 0 ));
        Map<String,AnnotationDefinition> defs = p.getAnnotations();
        assertEquals( 1, defs.size() );

        AnnotationDefinition outer = defs.get( Outer.class.getName().replace( "$", "." ) );
        assertNotNull( outer );

        Object val = outer.getPropertyValue( "value" );
        assertNotNull( val );
        assertTrue( val instanceof AnnotationDefinition );

        AnnotationDefinition inner = (AnnotationDefinition) val;
        assertEquals( "world", inner.getPropertyValue( "text" ) );
    }

    @Test
    public void testNestedAnnotationsWithMultiplicity() {
        String drl = "package org.drools.test; " +
                     "import " + Outer.class.getName().replace( "$", "." ) + "; " +
                     "import " + Inner.class.getName().replace( "$", "." ) + "; " +

                     "rule Foo " +
                     "when " +
                     "  String() @Outer( values = { @Inner( text = \"hello\" ), @Inner( text = \"world\" ) } ) " +
                     "then " +
                     "end ";
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        assertEquals( 0, helper.verify().getMessages().size() );

        Pattern p = ((Pattern) (( RuleImpl ) helper.build().getRule( "org.drools.test", "Foo" )).getLhs().getChildren().get( 0 ));
        Map<String,AnnotationDefinition> defs = p.getAnnotations();
        assertEquals( 1, defs.size() );

        AnnotationDefinition outer = defs.get( Outer.class.getName().replace( "$", "." ) );
        assertNotNull( outer );

        Object val = outer.getPropertyValue( "values" );
        assertNotNull( val );
        assertTrue( val instanceof AnnotationDefinition[] );


    }

    @Test
    public void testTypedSimpleArrays() {
        String drl = "package org.drools.test; " +
                     "import " + Simple.class.getName().replace( "$", "." ) + "; " +

                     "rule Foo " +
                     "when " +
                     "  String() @Simple( numbers = { 1, 2, 3 } ) " +
                     "then " +
                     "end ";
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );

        Pattern p = ((Pattern) (( RuleImpl ) helper.build().getRule( "org.drools.test", "Foo" )).getLhs().getChildren().get( 0 ));
        Map<String,AnnotationDefinition> defs = p.getAnnotations();
        assertEquals( 1, defs.size() );

        AnnotationDefinition simple = defs.get( Simple.class.getName().replace( "$", "." ) );
        assertNotNull( simple );

        Object val = simple.getPropertyValue( "numbers" );
        assertTrue( val instanceof int[] );

    }


    @Test
    public void testRuleAnnotations() {
        String drl = "package org.drools.test; " +
                     "import " + Inner.class.getName().replace( "$", "." ) + "; " +

                     "rule Foo " +
                     "@Inner( text=\"a\", test=\"b\" ) " +
                     "when " +
                     "then " +
                     "end ";
        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );

        Rule rule = helper.build().getRule( "org.drools.test", "Foo" );
        assertTrue( rule.getMetaData().containsKey( Inner.class.getName().replace( "$", "." ) ) );

        Object obj = rule.getMetaData().get( Inner.class.getName().replace( "$", "." ) );
        assertNotNull( obj );
        assertTrue( obj instanceof Map );
        assertEquals( "b", ((Map) obj).get( "test" ) );
        assertEquals( "a", ((Map) obj).get( "text" ) );

    }


    @Test
    public void testCollectAnnotationsParsingAndBuilding() throws Exception {
        final DrlParser parser = new DrlParser( LanguageLevelOption.DRL6 );

        final KnowledgeBuilderImpl kBuilder = new KnowledgeBuilderImpl();
        kBuilder.addPackage(new PackageDescr("org.drools"));

        String ruleDrl =
                "package org.drools.compiler; " +
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

        final PackageDescr pkgDescr = parser.parse( new StringReader( ruleDrl ) );

        // just checking there is no parsing errors
        assertFalse(parser.getErrors().toString(),
                    parser.hasErrors());

        kBuilder.addPackage(pkgDescr);

        assertTrue(kBuilder.getErrors().toString(),
                   kBuilder.getErrors().isEmpty());

        final RuleImpl rule = kBuilder.getPackage().getRule("test collect with annotation");

        List<? extends RuleConditionElement> nested = ((Pattern) rule.getLhs().getChildren().get( 0 )).getSource().getNestedElements();

        assertEquals(1, nested.size());

        Map<String, AnnotationDefinition> annotations = ((Pattern) nested.get( 0 )).getAnnotations();

        assertEquals(1, annotations.size());
        assertNotNull(annotations.keySet().iterator().next());
    }


    @Test
    public void testAnnotationOnLHSAndMerging() throws Exception {
        final DrlParser parser = new DrlParser( LanguageLevelOption.DRL6 );

        final KnowledgeBuilderImpl kBuilder = new KnowledgeBuilderImpl();

        String ruleDrl =
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

        final PackageDescr pkgDescr = parser.parse( new StringReader( ruleDrl ) );

        kBuilder.addPackage(pkgDescr);

        assertTrue(kBuilder.getErrors().toString(),
                   kBuilder.getErrors().isEmpty());
    }
}
