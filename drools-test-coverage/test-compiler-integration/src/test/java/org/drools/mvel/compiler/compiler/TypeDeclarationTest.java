/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.compiler.compiler;

import java.io.StringReader;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.common.EventFactHandle;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.rule.TypeDeclaration;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.type.Annotation;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Role;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResults;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.io.ResourceFactory;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class TypeDeclarationTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public TypeDeclarationTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testClassNameClashing() {
        String str = "";
        str += "package org.kie \n" +
        		"declare org.kie.Character \n" +
        		"    name : String \n" +
        		"end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    @Test
    public void testAnnotationReDefinition(){
        String str1 = "";
        str1 += "package org.kie \n" +
        		"declare org.kie.EventA \n" +
        		"    name : String \n" +
        		"    duration : Long \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.kie \n" +
        		"declare org.kie.EventA \n" +
        		"    @role (event) \n" +
        		"    @duration (duration) \n" +
        		"end \n";

        // Note: Didn't applied new APIs because KnowledgePackageImpl.getTypeDeclaration() doesn't return an expected TypeDeclaration
        // TODO: File a JIRA to revisit and write a valid test for new APIs and exec-model

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ),
                      ResourceType.DRL );

        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ),
                      ResourceType.DRL );

        if (kbuilder.hasErrors() ) {
           fail( kbuilder.getErrors().toString() );
        }

        //No Warnings
        KnowledgeBuilderResults warnings = kbuilder.getResults(ResultSeverity.WARNING);
        Assert.assertEquals(0, warnings.size());

        //just 1 package was created
        Assert.assertEquals(1, kbuilder.getKnowledgePackages().size());

        //Get the Fact Type for org.kie.EventA
        FactType factType = ((KnowledgePackageImpl)kbuilder.getKnowledgePackages().iterator().next()).getFactType("org.kie.EventA");
        assertNotNull( factType );

        //'name' field must still be there
        FactField field = factType.getField("name");
        assertNotNull( field );

        //'duration' field must still be there
        field = factType.getField("duration");
        assertNotNull( field );

        //New Annotations must be there too
        TypeDeclaration typeDeclaration = ((KnowledgePackageImpl)kbuilder.getKnowledgePackages().iterator().next()).getTypeDeclaration("EventA");

        assertEquals(Role.Type.EVENT, typeDeclaration.getRole());
        assertEquals("duration", typeDeclaration.getDurationAttribute());

    }

    @Test
    public void testNoAnnotationUpdateIfError(){
        String str1 = "";
        str1 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.EventA \n" +
        		"    name : String \n" +
        		"    duration : Long \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.EventA \n" +
        		"    @role (event) \n" +
        		"    @duration (duration) \n" +
        		"    anotherField : String \n" +
        		"end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource(str1.getBytes()),
                      ResourceType.DRL );

        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ),
                      ResourceType.DRL );

        if (!kbuilder.hasErrors() ) {
           fail("Errors Expected");
        }

        //No Warnings
        KnowledgeBuilderResults warnings = kbuilder.getResults(ResultSeverity.WARNING);
        assertEquals(0, warnings.size());

        //just 1 package was created
        assertEquals(0, kbuilder.getKnowledgePackages().size());

        // TODO: Odd behavior with new APIs (an error found for single test method. But no error for running the test class or parameterized)
        // File a JIRA to revisit and write a valid test for new APIs and exec-model

//        final Resource drlResource1 = KieServices.Factory.get().getResources().newReaderResource(new StringReader(str1));
//        drlResource1.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "rule1x.drl");
//        final Resource drlResource2 = KieServices.Factory.get().getResources().newReaderResource(new StringReader(str2));
//        drlResource2.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "rule2x.drl");
//
//        KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false, drlResource1, drlResource2);
//        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
//        System.out.println(errors);
//        assertFalse("Errors Expected", errors.isEmpty());
    }

    /**
     * The same resource (containing a type declaration) is added twice in the
     * kbuilder.
     */
    @Test
    public void testDuplicatedTypeDeclarationWith2FieldsInSameResource() {
        //same package, different resources
        String str1 = "";
        str1 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    lastName : String \n" +
        		"end \n";

        final Resource drlResource = KieServices.Factory.get().getResources().newReaderResource(new StringReader(str1));
        drlResource.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "rule1.drl");

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false, drlResource, drlResource); // same resource
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    /**
     * 2 resources (containing a the same type declaration) are added to the
     * kbuilder.
     * The expectation here is to silently discard the second type declaration.
     */
    @Test
    public void testDuplicatedTypeDeclarationInDifferentResources() {
        //same package, different resources
        String str1 = "";
        str1 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"end \n";

        final Resource drlResource1 = KieServices.Factory.get().getResources().newReaderResource(new StringReader(str1));
        drlResource1.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "rule1.drl");
        final Resource drlResource2 = KieServices.Factory.get().getResources().newReaderResource(new StringReader(str1));
        drlResource2.setSourcePath(TestConstants.TEST_RESOURCES_FOLDER + "rule2.drl");

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, false, drlResource1, drlResource2); // different resources
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }


    /**
     * 2 resources (containing different declarations of the same type ) are added
     * to the kbuilder.
     * The expectation here is that compilation fails because we are changing
     * the type of a field
     */
    @Test
    public void testClashingTypeDeclarationInDifferentResources() {
        // Note: Didn't applied new APIs because KieUtil.getKieBuilderFromResources() didn't reproduce the issue.
        // TODO: File a JIRA to revisit and write a valid test for new APIs and exec-model

        //same package, different resources
        String str1 = "";
        str1 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    age : Integer \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    age : String \n" +
        		"end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ),
                      ResourceType.DRL );

        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ),
                      ResourceType.DRL );

        if (!kbuilder.hasErrors() ) {
           fail( "An error should have been generated, redefinition of ClassA is not allowed" );
        }
    }

    /**
     * 2 resources (containing different declarations of the same type ) are added
     * to the kbuilder.
     * The expectation here is to silently discard the second type declaration.
     * This is because the new definition has less fields that the original
     * UPDATE : any use of the full-arg constructor in the second DRL will fail,
     * so we generate an error anyway
     */
    @Test
    public void testNotSoHarmlessTypeReDeclaration() {
        // Note: Didn't applied new APIs because KieUtil.getKieBuilderFromResources() didn't reproduce the issue.
        // TODO: File a JIRA to revisit and write a valid test for new APIs and exec-model

        //same package, different resources
        String str1 = "";
        str1 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    age : Integer \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ),
                      ResourceType.DRL );

        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ),
                      ResourceType.DRL );

        if ( ! kbuilder.hasErrors() ) {
           fail( "An error should have been generated, redefinition of ClassA is not allowed" );
        }

        /*
        //1 Warning
        KnowledgeBuilderResults warnings = kbuilder.getResults( ResultSeverity.WARNING );
        Assert.assertEquals(1, warnings.size());
        System.out.println(warnings.iterator().next().getMessage());

        //just 1 package was created
        Assert.assertEquals(1, kbuilder.getKnowledgePackages().size());

        //Get the Fact Type for org.drools.ClassA
        FactType factType = ((KnowledgePackageImp)kbuilder.getKnowledgePackages().iterator().next()).pkg.getFactType("org.drools.ClassA");
        Assert.assertNotNull(factType);

        //'age' field must still be there
        FactField field = factType.getField("age");
        Assert.assertNotNull(field);

        //Assert that the 'name' field must be String and not Long
        Assert.assertEquals(Integer.class, field.getType());
        */
    }

    /**
     * 2 resources (containing different declarations of the same type ) are added
     * to the kbuilder.
     * The expectation here is that the compilation fails because we are
     * adding a new field to the declared Type
     */
    @Test
    public void testTypeReDeclarationWithExtraField() {
        // Note: Didn't applied new APIs because KieUtil.getKieBuilderFromResources() didn't reproduce the issue.
        // TODO: File a JIRA to revisit and write a valid test for new APIs and exec-model

        //same package, different resources
        String str1 = "";
        str1 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    age : Integer \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    lastName : String \n" +
        		"end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ),
                      ResourceType.DRL );

        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ),
                      ResourceType.DRL );

        if ( ! kbuilder.hasErrors() ) {
           fail( kbuilder.getErrors().toString() );
        }
    }

    /**
     * 2 resources (containing different declarations of the same type ) are added
     * to the kbuilder.
     * The expectation here is that the compilation fails because we are
     * trying to add an incompatible re-definition of the declared type:
     * it introduces a new field 'lastName'
     */
    @Test
    public void testTypeReDeclarationWithExtraField2() {
        // Note: Didn't applied new APIs because KieUtil.getKieBuilderFromResources() didn't reproduce the issue.
        // TODO: File a JIRA to revisit and write a valid test for new APIs and exec-model

        //same package, different resources
        String str1 = "";
        str1 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    age : Integer \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools.mvel.compiler \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    lastName : String \n" +
        		"end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ),
                      ResourceType.DRL );

        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ),
                      ResourceType.DRL );

        if (!kbuilder.hasErrors() ) {
           fail( kbuilder.getErrors().toString() );
        }
    }



    @Test
    public void testDuplicateDeclaration() {
        String str = "";
        str += "package org.drools.mvel.compiler \n" +
                "declare Bean \n" +
                "    name : String \n" +
                "end \n" +
                "declare Bean \n" +
                "    age : int \n" +
                "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertFalse("Two definitions with the same name are not allowed, but it was not detected!", errors.isEmpty());
    }



    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = ElementType.TYPE)
    public static @interface KlassAnnotation {
        String value();
    }

    @Retention(value = RetentionPolicy.RUNTIME)
    @Target(value = ElementType.FIELD)
    public static @interface FieldAnnotation {
        String prop();
    }

    @Test
    public void testTypeDeclarationMetadata() {
        String str = "";
        str += "package org.drools.mvel.compiler.test; \n" +
                "import org.drools.mvel.compiler.compiler.TypeDeclarationTest.KlassAnnotation; \n" +
                "import org.drools.mvel.compiler.compiler.TypeDeclarationTest.FieldAnnotation; \n" +
                "import org.drools.mvel.compiler.Person\n" +
                "\n" +
                "declare Bean \n" +
                "@role(event) \n" +
                "@expires( 1s ) \n" +
                "@KlassAnnotation( \"klass\" )" +
                "" +
                "    name : String\n" +
                "end \n" +
                "declare Person @role(event) end";

        KieBase kBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);

        FactType bean = kBase.getFactType( "org.drools.mvel.compiler.test", "Bean" );
        FactType pers = kBase.getFactType( "org.drools", "Person" );
        assertEquals( "org.drools.mvel.compiler.test.Bean", bean.getName() );
        assertEquals( "Bean", bean.getSimpleName() );
        assertEquals( "org.drools.mvel.compiler.test", bean.getPackageName() );

        assertEquals( 3, bean.getClassAnnotations().size() );
        Annotation ann = bean.getClassAnnotations().get( 0 );
        if (!ann.getName().equals("org.drools.mvel.compiler.compiler.TypeDeclarationTest$KlassAnnotation")) {
            ann = bean.getClassAnnotations().get( 1 );
        }
        if (!ann.getName().equals("org.drools.mvel.compiler.compiler.TypeDeclarationTest$KlassAnnotation")) {
            ann = bean.getClassAnnotations().get( 2 );
        }
        assertEquals( "org.drools.mvel.compiler.compiler.TypeDeclarationTest$KlassAnnotation", ann.getName() );
        assertEquals( "klass", ann.getPropertyValue( "value" ) );
        assertEquals( String.class, ann.getPropertyType( "value" ) );

        assertEquals( 2, bean.getMetaData().size() );
        assertEquals( "event", bean.getMetaData().get( "role" ) );

        FactField field = bean.getField( "name" );
        assertNotNull( field );
    }

    public static class EventBar {
        public static class Foo {

        }
    }

    @Test
    public void testTypeDeclarationWithInnerClasses() {
        // DROOLS-150
        String str = "";
        str += "package org.drools.mvel.compiler;\n" +
               "\n" +
               "import org.drools.mvel.compiler.compiler.TypeDeclarationTest.EventBar.*;\n" +
               "" +
               "declare Foo\n" +
               " @role( event )\n" +
               "end\n" +
               "" +
               "rule R when Foo() then end";

        KieBase kBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession knowledgeSession = kBase.newKieSession();
        FactHandle handle = knowledgeSession.insert( new EventBar.Foo() );

        assertTrue( handle instanceof EventFactHandle );

    }

    @Test
    public void testTypeDeclarationWithInnerClassesImport() {
        // DROOLS-150
        String str = "";
        str += "package org.drools.mvel.compiler;\n" +
               "\n" +
               "import org.drools.mvel.compiler.compiler.TypeDeclarationTest.EventBar.Foo;\n" +
               "" +
               "declare Foo\n" +
               " @role( event )\n" +
               "end\n" +
               "" +
               "rule R when Foo() then end";

        KieBase kBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession knowledgeSession = kBase.newKieSession();
        FactHandle handle = knowledgeSession.insert( new EventBar.Foo() );

        assertTrue( handle instanceof EventFactHandle );

    }


    public static class ClassC {
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge( Integer age ) {
            this.age = age;
        }
    }

    @Test
    public void testMultipleTypeReDeclaration() {
        // Note: Didn't applied new APIs because KieUtil.getKieBuilderFromResources() didn't reproduce the issue.
        // TODO: File a JIRA to revisit and write a valid test for new APIs and exec-model

        //same package, different resources
        String str1 = "";
        str1 += "package org.drools \n" +
                "declare org.drools.ClassC \n" +
                "    name : String \n" +
                "    age : Integer \n" +
                "end \n";

        String str2 = "";
        str2 += "package org.drools \n" +
                "declare org.drools.ClassC \n" +
                "    name : String \n" +
                "    age : Integer \n" +
                "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ),
                      ResourceType.DRL );

        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
    }

    @Test
    public void testDeclareFieldArray() {
        String str1 = "" +
                      "package org.drools " +

                      "declare Test end " +

                      "declare Pet " +
                      "    owners : Owner[] " +
                      "    twoDimArray : Foo[][] " +
                      "    friends : Pet[] " +
                      "    ages : int[] " +
                      "end " +

                      "declare Owner " +
                      "     name : String " +
                      "end " +

                      "declare Foo end " +

                      "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());

        InternalKieModule kieModule = (InternalKieModule)kieBuilder.getKieModule();
        KnowledgeBuilderImpl kbuilder = (KnowledgeBuilderImpl)kieModule.getKnowledgeBuilderForKieBase(KieBaseTestConfiguration.KIE_BASE_MODEL_NAME);

        for( KiePackage kp : kbuilder.getKnowledgePackages() ) {
            if ( kp.getName().equals( "org.drools" ) ) {
                Collection<FactType> types = kp.getFactTypes();
                for ( FactType type : types ) {
                    if ( "org.drools.Pet".equals( type.getName() ) ) {
                        assertEquals( 4, type.getFields().size() );
                        FactField owners = type.getField( "owners" );
                        assertTrue( owners != null && owners.getType().getSimpleName().equals( "Owner[]" ) && owners.getType().isArray()  );
                        FactField twoDim = type.getField( "twoDimArray" );
                        assertTrue( twoDim != null && twoDim.getType().getSimpleName().equals( "Foo[][]" ) && twoDim.getType().isArray()  );
                        FactField friends = type.getField( "friends" );
                        assertTrue( friends != null && friends.getType().getSimpleName().equals( "Pet[]" ) && friends.getType().isArray()  );
                        FactField ages = type.getField( "ages" );
                        assertTrue( ages != null && ages.getType().getSimpleName().equals( "int[]" ) && ages.getType().isArray()  );
                    }
                }
            }
        }
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testPreventReflectionAPIsOnJavaClasses() {
        String drl = "package org.test; " +

                     // existing java class
                     "declare org.drools.mvel.compiler.Person " +
                     "  @role(event) " +
                     "end \n" +

                     "";

        KieBuilder kieBuilder = build(drl);

        assertFalse( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) );
        KieBase kieBase = KieServices.Factory.get().newKieContainer( kieBuilder.getKieModule().getReleaseId() ).getKieBase();

        FactType type = kieBase.getFactType( "org.drools.mvel.compiler", "Person" );

    }

    @Test
    public void testCrossPackageDeclares() {
        // TODO: enable exec-model

        String pkg1 =
                "package org.drools.mvel.compiler.test1; " +
                "import org.drools.mvel.compiler.test2.GrandChild; " +
                "import org.drools.mvel.compiler.test2.Child; " +
                "import org.drools.mvel.compiler.test2.BarFuu; " +

                "declare FuBaz foo : String end " +

                "declare Parent " +
                "   unknown : BarFuu " +
                "end " +

                "declare GreatChild extends GrandChild " +
                "   father : Child " +
                "end "
                ;

        String pkg2 =
                "package org.drools.mvel.compiler.test2; " +
                "import org.drools.mvel.compiler.test1.Parent; " +
                "import org.drools.mvel.compiler.test1.FuBaz; " +

                "declare BarFuu " +
                "   baz : FuBaz " +
                "end " +

                "declare Child extends Parent " +
                "end " +

                "declare GrandChild extends Child " +
                "   notknown : FuBaz " +
                "end "
                ;

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.generateAndWritePomXML( ks.newReleaseId( "test", "foo", "1.0" ) );
        KieModuleModel km = ks.newKieModuleModel();
        km.newKieBaseModel( "rules" )
                .addPackage( "org.drools.mvel.compiler.test2" )
                .addPackage( "org.drools.mvel.compiler.test1" );
        kfs.writeKModuleXML( km.toXML() );

        KieResources kr = ks.getResources();
        Resource r1 = kr.newByteArrayResource( pkg1.getBytes() )
                .setResourceType( ResourceType.DRL )
                .setSourcePath( "org/drools/compiler/test1/p1.drl" );
        Resource r2 = kr.newByteArrayResource( pkg2.getBytes() )
                .setResourceType( ResourceType.DRL )
                .setSourcePath( "org/drools/compiler/test2/p2.drl" );

        kfs.write( r1 );
        kfs.write( r2 );

        KieBuilder builder = ks.newKieBuilder( kfs );
        builder.buildAll();

        assertEquals( Collections.emptyList(), builder.getResults().getMessages( Message.Level.ERROR ) );

        KieContainer kc = ks.newKieContainer(builder.getKieModule().getReleaseId());
        FactType ft = kc.getKieBase( "rules" ).getFactType( "org.drools.mvel.compiler.test2", "Child" );

        assertNotNull( ft );
        assertNotNull( ft.getFactClass() );
        assertEquals( "org.drools.mvel.compiler.test1.Parent", ft.getFactClass().getSuperclass().getName() );
    }


    @Test
    public void testUnknownField() throws InstantiationException, IllegalAccessException {
        // DROOLS-546
        String drl = "package org.test; " +
                     "declare Pet" +
                     " " +
                     "end \n";

        KieBuilder kieBuilder = build(drl);

        assertFalse( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) );
        KieBase kieBase = KieServices.Factory.get().newKieContainer( kieBuilder.getKieModule().getReleaseId() ).getKieBase();

        FactType factType = kieBase.getFactType("org.test", "Pet");
        Object instance = factType.newInstance();
        factType.get(instance, "unknownField");
        factType.set(instance, "unknownField", "myValue");
    }

    @Test
    public void testPositionalArguments() throws InstantiationException, IllegalAccessException {
        String drl = "package org.test;\n" +
                     "global java.util.List names;\n" +
                     "declare Person\n" +
                     "    name : String\n" +
                     "    age : int\n" +
                     "end\n" +
                     "rule R when \n" +
                     "    $p : Person( \"Mark\", 37; )\n" +
                     "then\n" +
                     "    names.add( $p.getName() );\n" +
                     "end\n";

        KieBuilder kieBuilder = build(drl);

        assertFalse(kieBuilder.getResults().hasMessages(Message.Level.ERROR));
        KieBase kieBase = KieServices.Factory.get().newKieContainer( kieBuilder.getKieModule().getReleaseId() ).getKieBase();

        FactType factType = kieBase.getFactType( "org.test", "Person" );
        Object instance = factType.newInstance();
        factType.set(instance, "name", "Mark");
        factType.set(instance, "age", 37);

        List<String> names = new ArrayList<String>();
        KieSession ksession = kieBase.newKieSession();
        ksession.setGlobal("names", names);

        ksession.insert(instance);
        ksession.fireAllRules();

        assertEquals( 1, names.size() );
        assertEquals( "Mark", names.get( 0 ) );
    }

    @Test
    public void testExplictPositionalArguments() throws InstantiationException, IllegalAccessException {
        String drl = "package org.test;\n" +
                     "global java.util.List names;\n" +
                     "declare Person\n" +
                     "    name : String @Position(1)\n" +
                     "    age : int @Position(0)\n" +
                     "end\n" +
                     "rule R when \n" +
                     "    $p : Person( 37, \"Mark\"; )\n" +
                     "then\n" +
                     "    names.add( $p.getName() );\n" +
                     "end\n";

        KieBuilder kieBuilder = build(drl);

        assertFalse(kieBuilder.getResults().hasMessages(Message.Level.ERROR));
        KieBase kieBase = KieServices.Factory.get().newKieContainer( kieBuilder.getKieModule().getReleaseId() ).getKieBase();

        FactType factType = kieBase.getFactType("org.test", "Person");
        Object instance = factType.newInstance();
        factType.set(instance, "name", "Mark");
        factType.set(instance, "age", 37);

        List<String> names = new ArrayList<String>();
        KieSession ksession = kieBase.newKieSession();
        ksession.setGlobal("names", names);

        ksession.insert(instance);
        ksession.fireAllRules();

        assertEquals(1, names.size());
        assertEquals("Mark", names.get(0));
    }

    @Test
    public void testTooManyPositionalArguments() throws InstantiationException, IllegalAccessException {
        // DROOLS-559
        String drl = "package org.test;\n" +
                     "global java.util.List names;\n" +
                     "declare Person\n" +
                     "    name : String\n" +
                     "    age : int\n" +
                     "end\n" +
                     "rule R when \n" +
                     "    $p : Person( \"Mark\", 37, 42; )\n" +
                     "then\n" +
                     "    names.add( $p.getName() );\n" +
                     "end\n";

        KieBuilder kieBuilder = build(drl);

        assertTrue( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) );
    }

    @Test
    public void testOutOfRangePositions() throws InstantiationException, IllegalAccessException {
        // DROOLS-559
        String drl = "package org.test;\n" +
                     "global java.util.List names;\n" +
                     "declare Person\n" +
                     "    name : String @Position(3)\n" +
                     "    age : int @Position(1)\n" +
                     "end\n" +
                     "rule R when \n" +
                     "    $p : Person( 37, \"Mark\"; )\n" +
                     "then\n" +
                     "    names.add( $p.getName() );\n" +
                     "end\n";

        KieBuilder kieBuilder = build(drl);

        assertTrue( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) );
    }

    @Test
    public void testDuplicatedPositions() throws InstantiationException, IllegalAccessException {
        // DROOLS-559
        String drl = "package org.test;\n" +
                     "global java.util.List names;\n" +
                     "declare Person\n" +
                     "    name : String @Position(1)\n" +
                     "    age : int @Position(1)\n" +
                     "end\n" +
                     "rule R when \n" +
                     "    $p : Person( 37, \"Mark\"; )\n" +
                     "then\n" +
                     "    names.add( $p.getName() );\n" +
                     "end\n";

        KieBuilder kieBuilder = build(drl);

        assertTrue( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) );
    }

    private KieBuilder build(String drl) {
        return KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
    }

    @Test
    public void testMultipleAnnotationDeclarations() {
        String str1 = "";
        str1 += "package org.kie1 " +
                "" +
               "declare Foo \n" +
                "   @role(event) " +
               "    name : String " +
               "    age : int " +
               "end ";

        String str2 = "" +
               "package org.kie3 " +
               "" +
               "declare org.kie1.Foo " +
               "    @propertyReactive " +
               "end ";

        String str3 = "" +
                      "package org.kie4; " +
                      "import org.kie1.Foo; " +
                      "" +
                      "rule Check " +
                      "when " +
                      " $f : Foo( name == 'bar' ) " +
                      "then " +
                      " modify( $f ) { setAge( 99 ); } " +
                      "end ";

        KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1, str2, str3);

        FactType type = kieBase.getFactType( "org.kie1", "Foo" );
        assertEquals( 2, type.getFields().size() );

        Object foo = null;
        try {
            foo = type.newInstance();
            type.set( foo, "name", "bar" );
            assertEquals( "bar", type.get( foo, "name" ) );
        } catch ( InstantiationException e ) {
            fail( e.getMessage() );
        } catch ( IllegalAccessException e ) {
            fail( e.getMessage() );
        }

        KieSession session = kieBase.newKieSession();
        FactHandle handle = session.insert( foo );
        int n = session.fireAllRules( 5 );

        assertTrue( handle instanceof EventFactHandle );
        assertEquals( 1, n );
        assertEquals( 99, type.get( foo, "age" ) );
    }

    public static interface Base {
        public Object getFld();
        public void setFld( Object x );
    }

    public static interface Ext extends Base {
        public String getFld();
        public void setFld( String s );
    }

    @Test
    public void testDeclareWithExtensionAndOverride() {
        final String s1 = "package test; " +
                          "global java.util.List list; " +

                          "declare Sub extends Sup " +
                          " fld : String " +
                          "end " +

                          "declare Sup " +
                          " fld : Object " +
                          "end " +

                          "rule Init when " +
                          "then insert( new Sub( 'aa' ) ); end " +

                          "rule CheckSup when " +
                          " $s : Sup( $f : fld == 'aa' ) " +
                          "then " +
                          "  list.add( \"Sup\" + $f );  " +
                          "end " +

                          "rule CheckSub when " +
                          " $s : Sub( $f : fld == 'aa' ) " +
                          "then " +
                          "  list.add( \"Sub\" + $f );  " +
                          "end ";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, s1);
        KieSession ks = kbase.newKieSession();

        List list = new ArrayList();
        ks.setGlobal( "list", list );
        ks.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.containsAll( asList("Supaa", "Subaa") ) );

        FactType sup = ks.getKieBase().getFactType( "test", "Sup" );
        FactType sub = ks.getKieBase().getFactType( "test", "Sub" );

        try {
            Method m1 = sup.getFactClass().getMethod( "getFld" );
            assertNotNull( m1 );
            assertEquals( Object.class, m1.getReturnType() );

            Method m2 = sub.getFactClass().getMethod( "getFld" );
            assertNotNull( m2 );
            assertEquals( String.class, m2.getReturnType() );

            assertEquals( 0, sub.getFactClass().getFields().length );
            assertEquals( 0, sub.getFactClass().getDeclaredFields().length );
            assertEquals( 1, sup.getFactClass().getDeclaredFields().length );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

    }

    public static class BeanishClass {
        private int foo;
        public int getFoo() { return foo; }
        public void setFoo( int x ) { foo = x; }
        public void setFooAsString( String x ) { foo = Integer.parseInt( x ); }
    }

    @Test
    public void testDeclarationOfClassWithNonStandardSetter() {
        final String s1 = "package test; " +
                          "import " + BeanishClass.class.getCanonicalName() + "; " +

                          "declare " + BeanishClass.class.getSimpleName() + " @propertyReactive end " +

                          "rule Check when BeanishClass() @Watch( foo ) then end ";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, s1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    @Test
    public void testDeclarationOfClassWithNonStandardSetterAndCanonicalName() {
        // DROOLS-815
        final String s1 = "package test; " +
                          "import " + BeanishClass.class.getCanonicalName() + "; " +

                          "declare " + BeanishClass.class.getCanonicalName() + " @propertyReactive end " +

                          "rule Check when BeanishClass() @Watch( foo ) then end ";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, s1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }

    @Test
    public void testDeclarationOfClassWithNonStandardSetterAndFulllName() {
        final String s1 = "package test; " +
                          "import " + BeanishClass.class.getCanonicalName() + "; " +

                          "declare " + BeanishClass.class.getName() + " @propertyReactive end " +

                          "rule Check when BeanishClass() @watch( foo ) then end ";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, s1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertTrue(errors.toString(), errors.isEmpty());
    }

}
