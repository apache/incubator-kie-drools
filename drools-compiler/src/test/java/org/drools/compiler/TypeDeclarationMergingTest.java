package org.drools.compiler;

import java.util.Iterator;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.impl.KnowledgeBuilderImpl;
import org.drools.definition.type.Position;
import org.drools.io.ResourceFactory;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.TypeDeclaration.Format;
import org.drools.rule.TypeDeclaration.Role;
import org.junit.Test;


public class TypeDeclarationMergingTest {
    
    @Test
    public void testMask() {
        TypeDeclaration tdeclr = new TypeDeclaration(CImpl.class.getName() );
        assertEquals( 0, tdeclr.getSetMask() );
        
        tdeclr.setRole( Role.EVENT );
        assertEquals( TypeDeclaration.ROLE_BIT, tdeclr.getSetMask() & TypeDeclaration.ROLE_BIT );
        assertFalse( TypeDeclaration.TYPESAFE_BIT == ( tdeclr.getSetMask() & TypeDeclaration.TYPESAFE_BIT ) );
        assertFalse( TypeDeclaration.FORMAT_BIT == ( tdeclr.getSetMask() & TypeDeclaration.FORMAT_BIT ) );
        
        tdeclr.setTypesafe( false );
        assertEquals( TypeDeclaration.ROLE_BIT, tdeclr.getSetMask() & TypeDeclaration.ROLE_BIT );
        assertEquals( TypeDeclaration.TYPESAFE_BIT, tdeclr.getSetMask() & TypeDeclaration.TYPESAFE_BIT );
        assertFalse( TypeDeclaration.FORMAT_BIT == ( tdeclr.getSetMask() & TypeDeclaration.FORMAT_BIT ) );
        
        tdeclr = new TypeDeclaration(CImpl.class.getName() );
        tdeclr.setTypesafe( true );
        assertFalse( TypeDeclaration.ROLE_BIT == ( tdeclr.getSetMask() & TypeDeclaration.ROLE_BIT ) );
        assertEquals( TypeDeclaration.TYPESAFE_BIT, tdeclr.getSetMask() & TypeDeclaration.TYPESAFE_BIT );
        assertFalse( TypeDeclaration.FORMAT_BIT == ( tdeclr.getSetMask() & TypeDeclaration.FORMAT_BIT ) );
        
        tdeclr.setFormat( Format.POJO );
        assertFalse( TypeDeclaration.ROLE_BIT == ( tdeclr.getSetMask() & TypeDeclaration.ROLE_BIT ) );
        assertEquals( TypeDeclaration.TYPESAFE_BIT, tdeclr.getSetMask() & TypeDeclaration.TYPESAFE_BIT );
        assertEquals( TypeDeclaration.FORMAT_BIT, tdeclr.getSetMask() & TypeDeclaration.FORMAT_BIT );
    }
    
    @Test
    public void testOverrideFromParentClass() {
        // inherits role, but not typesafe
        String str = ""+
           "package org.test \n" +
           "global java.util.List list \n" +
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +
           "end\n" +           
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(false)\n" +
           "end\n";
        PackageBuilder builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( false, tdecl.isTypesafe() );
        assertEquals( Role.EVENT, tdecl.getRole() );
    }
    
    @Test
    public void testInheritNoneExitenceFromParentClass() {
        // inherits role and typesafe
        String str = ""+
           "package org.test \n" +
           "global java.util.List list \n" +
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +           
           "end\n";

        PackageBuilder builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.EVENT, tdecl.getRole() );
    }    
    
    @Test
    public void testInheritExitenceFromParentClass() {
        // inherits role and typesafe
        String str = ""+
           "package org.test \n" +
           "global java.util.List list \n" +
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n" +
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "end\n";           

        PackageBuilder builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.EVENT, tdecl.getRole() );        
    }    
    
    @Test
    public void testOverrideFromParentInterface() {
        // inherits role but not typesafe
        String str = ""+
           "package org.test \n" +
           "global java.util.List list \n" +
           "declare " + IB.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n" +           
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(false)\n" +
           "end\n";
        PackageBuilder builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( false, tdecl.isTypesafe() );
        assertEquals( Role.EVENT, tdecl.getRole() );        
    }
    
    @Test
    public void testOverrideFromDeeperParentInterface() {
        // inherits role but not typesafe        
        String str = ""+
           "package org.test \n" +
           "global java.util.List list \n" +
           "declare " + IA.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n" +           
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(false)\n" +
           "end\n";
        PackageBuilder builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( false, tdecl.isTypesafe() );
        assertEquals( Role.EVENT, tdecl.getRole() );        
    }    
    
    @Test
    public void testOverrideFromDeeperHierarchyParentInterface() {
        // inherits role from and typesafe from the other      
        String str = ""+
           "package org.test \n" +
           "global java.util.List list \n" +
           "declare " + IA.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n" +         
           "declare " + IB.class.getCanonicalName() + "\n" +
           "    @role(fact)\n" +              
           "end\n" +             
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "end\n";
        PackageBuilder builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.FACT, tdecl.getRole() );        
    }        
    
    @Test
    public void testInheritNoneExitenceFromParentInterface() {
        // inherits role and typesafe  
        String str = ""+
           "package org.test \n" +
           "global java.util.List list \n" +
           "declare " + IB.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n";

        PackageBuilder builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.EVENT, tdecl.getRole() );
    }    
    
    @Test
    public void testInheritExitenceFromParentInterface() {
        // inherits role and typesafe  
        String str = ""+
           "package org.test \n" +
           "global java.util.List list \n" +
           "declare " + IB.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +           
           "end\n" +
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "end\n";           

        PackageBuilder builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.EVENT, tdecl.getRole() );
    }     
    
    @Test
    public void testOverrideFromMixedHierarchyParentInterface() {
        // inherits role from and typesafe from the other      
        String str = ""+
           "package org.test \n" +
           "global java.util.List list \n" +
           "declare " + IA.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n" +         
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @role(fact)\n" +              
           "end\n" +             
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "end\n";
        PackageBuilder builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.FACT, tdecl.getRole() );        
    }      

    /**
     * Tests adding metadata in DRL to the metadata already declared in a POJO.
     */
    @Test
    public void testNotOverwritePOJOMetadata() {
        final String eventClassName = PositionAnnotatedEvent.class.getCanonicalName();
        // should add metadata to metadata already defined in POJO
        String str =
           "package org.test \n" +
           "declare " + eventClassName + "\n" +
           "    @role(event)\n" +
           "end \n" + 
           "rule 'sample rule' \n" +
           "when \n" +
           "  " + eventClassName + "( 'value1', 'value2'; ) \n" +
           "then \n" +
           "end \n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        try {
            kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );
        } catch (IndexOutOfBoundsException e) {
            final String msg = e.getMessage();
            if ( "Error trying to access field at position 0".equals( msg ) ) {
                fail( "@Position declared in POJO was ignored." );
            } else {
                fail( "Check the test, unexpected error message: " + msg );
            }         
        }
        assertFalse( "Check the test, unexpected error message: "
                     + kbuilder.getErrors(), kbuilder.hasErrors());
    }      

    private PackageBuilder getPackageBuilder(String str) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        PackageBuilder builder = ((KnowledgeBuilderImpl)kbuilder).getPackageBuilder();
        return builder;
        
    }   
}
