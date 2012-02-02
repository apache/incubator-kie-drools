package org.drools.compiler;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.builder.impl.KnowledgeBuilderImpl;
import org.drools.integrationtests.MVELTest.DMap;
import org.drools.integrationtests.MVELTest.Triangle;
import org.drools.io.ResourceFactory;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.TypeDeclaration.Format;
import org.drools.rule.TypeDeclaration.Role;
import org.drools.runtime.StatefulKnowledgeSession;
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
