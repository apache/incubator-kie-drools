package org.drools.compiler;

import junit.framework.Assert;
import static org.junit.Assert.*;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeBuilderResults;
import org.drools.builder.ResourceType;
import org.drools.builder.ResultSeverity;
import org.drools.definition.type.FactField;
import org.drools.definition.type.FactType;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.rule.TypeDeclaration;
import org.junit.Test;

public class TypeDeclarationTest {

    @Test
    public void testClassNameClashing() {
        String str = "";
        str += "package org.drools \n" +
        		"declare org.drools.Character \n" +
        		"    name : String \n" +
        		"end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
           fail( kbuilder.getErrors().toString() );
        }
    }

    public void testAnnotationReDefinition(){
        String str1 = "";
        str1 += "package org.drools \n" +
        		"declare org.drools.EventA \n" +
        		"    name : String \n" +
        		"    duration : Long \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools \n" +
        		"declare org.drools.ClassA \n" +
        		"    @Role (event) \n" +
        		"    @duration (duration) \n" +
        		"end \n";

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

        //Get the Fact Type for org.drools.EventA
        FactType factType = ((KnowledgePackageImp)kbuilder.getKnowledgePackages().iterator().next()).pkg.getFactType("org.drools.EventA");
        Assert.assertNotNull(factType);

        //'name' field must still be there
        FactField field = factType.getField("name");
        Assert.assertNotNull(field);

        //'duration' field must still be there
        field = factType.getField("duration");
        Assert.assertNotNull(field);

        //New Annotations must be there too
        TypeDeclaration typeDeclaration = ((KnowledgePackageImp)kbuilder.getKnowledgePackages().iterator().next()).pkg.getTypeDeclaration("org.drools.EventA");

        assertEquals(TypeDeclaration.Role.EVENT, typeDeclaration.getRole());
        assertEquals("duration", typeDeclaration.getDurationAttribute());

    }

    public void testNoAnnotationUpdateIfError(){
        String str1 = "";
        str1 += "package org.drools \n" +
        		"declare org.drools.EventA \n" +
        		"    name : String \n" +
        		"    duration : Long \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools \n" +
        		"declare org.drools.ClassA \n" +
        		"    @Role (event) \n" +
        		"    @duration (duration) \n" +
        		"    anotherField : String \n" +
        		"end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ),
                      ResourceType.DRL );

        kbuilder.add( ResourceFactory.newByteArrayResource( str2.getBytes() ),
                      ResourceType.DRL );

        if (!kbuilder.hasErrors() ) {
           fail("Errors Expected");
        }

        //No Warnings
        KnowledgeBuilderResults warnings = kbuilder.getResults(ResultSeverity.WARNING);
        Assert.assertEquals(0, warnings.size());

        //just 1 package was created
        Assert.assertEquals(1, kbuilder.getKnowledgePackages().size());

        //Get the Fact Type for org.drools.EventA
        FactType factType = ((KnowledgePackageImp)kbuilder.getKnowledgePackages().iterator().next()).pkg.getFactType("org.drools.EventA");
        Assert.assertNotNull(factType);

        //'name' field must still be there
        FactField field = factType.getField("name");
        Assert.assertNotNull(field);

        //'duration' field must still be there
        field = factType.getField("duration");
        Assert.assertNotNull(field);

        //@Role annotations shouldn't have any effect
        TypeDeclaration typeDeclaration = ((KnowledgePackageImp)kbuilder.getKnowledgePackages().iterator().next()).pkg.getTypeDeclaration("org.drools.EventA");

        assertEquals(TypeDeclaration.Role.FACT, typeDeclaration.getRole());
        assertNull(typeDeclaration.getDurationAttribute());

    }

    /**
     * The same resource (containing a type declaration) is added twice in the
     * kbuilder.
     */
    @Test
    public void testDuplicatedTypeDeclarationWith2FieldsInSameResource() {
        //same package, different resources
        String str1 = "";
        str1 += "package org.drools \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    lastName : String \n" +
        		"end \n";

        Resource resource = ResourceFactory.newByteArrayResource( str1.getBytes());

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( resource, ResourceType.DRL );

        kbuilder.add( resource, ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
           fail( kbuilder.getErrors().toString() );
        }

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
        str1 += "package org.drools \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ),
                      ResourceType.DRL );

        kbuilder.add( ResourceFactory.newByteArrayResource( str1.getBytes() ),
                      ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
           fail( kbuilder.getErrors().toString() );
        }

    }


    /**
     * 2 resources (containing different declarations of the same type ) are added
     * to the kbuilder.
     * The expectation here is that compilation fails because we are changing
     * the type of a field
     */
    @Test
    public void testClashingTypeDeclarationInDifferentResources() {
        //same package, different resources
        String str1 = "";
        str1 += "package org.drools \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    age : Integer \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools \n" +
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
           fail( kbuilder.getErrors().toString() );
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
        //same package, different resources
        String str1 = "";
        str1 += "package org.drools \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    age : Integer \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools \n" +
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
        //same package, different resources
        String str1 = "";
        str1 += "package org.drools \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    age : Integer \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools \n" +
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
        //same package, different resources
        String str1 = "";
        str1 += "package org.drools \n" +
        		"declare org.drools.ClassA \n" +
        		"    name : String \n" +
        		"    age : Integer \n" +
        		"end \n";

        String str2 = "";
        str2 += "package org.drools \n" +
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
        str += "package org.drools \n" +
                "declare Bean \n" +
                "    name : String \n" +
                "end \n" +
                "declare Bean \n" +
                "    age : int \n" +
                "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                ResourceType.DRL );

        if ( ! kbuilder.hasErrors() ) {
            fail( "Two definitions with the same name are not allowed, but it was not detected! " );
        }
    }

}