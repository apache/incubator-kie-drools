package org.drools.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.TypeDeclaration.Format;
import org.junit.Test;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.*;


public class TypeDeclarationMergingTest {
    
    @Test
    public void testMask() {
        TypeDeclaration tdeclr = new TypeDeclaration(CImpl.class.getName() );
        assertEquals( 0, tdeclr.getSetMask() );

        tdeclr.setRole( Role.Type.EVENT );
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
           "package org.drools.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +
           "end\n" +           
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(false)\n" +
           "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( false, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
    }
    
    @Test
    public void testInheritNoneExitenceFromParentClass() {
        // inherits role and typesafe
        String str = ""+
           "package org.drools.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +           
           "end\n";

        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
    }    
    
    @Test
    public void testInheritExitenceFromParentClass() {
        // inherits role and typesafe
        String str = ""+
           "package org.drools.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n" +
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "end\n";

        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
    }    
    
    @Test
    public void testOverrideFromParentInterface() {
        // inherits role but not typesafe
        String str = ""+
           "package org.drools.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + IB.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n" +           
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(false)\n" +
           "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( false, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
    }
    
    @Test
    public void testOverrideFromDeeperParentInterface() {
        // inherits role but not typesafe        
        String str = ""+
           "package org.drools.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + IA.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n" +           
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(false)\n" +
           "end\n";
        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( false, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
    }    
    
    @Test
    public void testOverrideFromDeeperHierarchyParentInterface() {
        // inherits role from and typesafe from the other      
        String str = ""+
           "package org.drools.compiler.test \n" +
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
        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.FACT, tdecl.getRole() );
    }        
    
    @Test
    public void testInheritNoneExitenceFromParentInterface() {
        // inherits role and typesafe  
        String str = ""+
           "package org.drools.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + IB.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n";

        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
    }    
    
    @Test
    public void testInheritExitenceFromParentInterface() {
        // inherits role and typesafe  
        String str = ""+
           "package org.drools.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + IB.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +           
           "end\n" +
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "end\n";

        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
    }     
    
    @Test
    public void testOverrideFromMixedHierarchyParentInterface() {
        // inherits role from and typesafe from the other      
        String str = ""+
           "package org.drools.compiler.test \n" +
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
        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.FACT, tdecl.getRole() );
    }      
    
    private KnowledgeBuilderImpl getPackageBuilder(String str) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        return (KnowledgeBuilderImpl)kbuilder;
    }

    @Test
    public void testNotOverwritePOJOMetadata() {
        final String eventClassName = PositionAnnotatedEvent.class.getCanonicalName();
        // should add metadata to metadata already defined in POJO
        String str =
                "package org.drools.compiler.test \n" +
                "declare " + eventClassName + "\n" +
                " @role(event)\n" +
                "end \n" +
                "rule 'sample rule' \n" +
                "when \n" +
                " " + eventClassName + "( 'value1', 'value2'; ) \n" +
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

    public static class PositionAnnotatedEvent {

        @Position(1)
        private String arg1;

        @Position(0)
        private String arg0;

        public String getArg1() {
            return arg1;
        }

        public String getArg0() {
            return arg0;
        }

        public void setArg1( String arg1 ) {
            this.arg1 = arg1;
        }

        public void setArg0( String arg0 ) {
            this.arg0 = arg0;
        }
    }
}
