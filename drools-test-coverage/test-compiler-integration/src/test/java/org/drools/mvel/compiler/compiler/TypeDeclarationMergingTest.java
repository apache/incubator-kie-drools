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
package org.drools.mvel.compiler.compiler;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.base.rule.TypeDeclaration;
import org.drools.base.rule.TypeDeclaration.Format;
import org.junit.Test;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class TypeDeclarationMergingTest {

    // TODO: Fails with standard-drl after changing to new API. See DROOLS-6061

    @Test
    public void testMask() {
        TypeDeclaration tdeclr = new TypeDeclaration(CImpl.class.getName() );
        assertThat(tdeclr.getSetMask()).isEqualTo(0);

        tdeclr.setRole( Role.Type.EVENT );
        assertThat(tdeclr.getSetMask() & TypeDeclaration.ROLE_BIT).isEqualTo(TypeDeclaration.ROLE_BIT);
        assertThat(TypeDeclaration.TYPESAFE_BIT == (tdeclr.getSetMask() & TypeDeclaration.TYPESAFE_BIT)).isFalse();
        assertThat(TypeDeclaration.FORMAT_BIT == (tdeclr.getSetMask() & TypeDeclaration.FORMAT_BIT)).isFalse();
        
        tdeclr.setTypesafe( false );
        assertThat(tdeclr.getSetMask() & TypeDeclaration.ROLE_BIT).isEqualTo(TypeDeclaration.ROLE_BIT);
        assertThat(tdeclr.getSetMask() & TypeDeclaration.TYPESAFE_BIT).isEqualTo(TypeDeclaration.TYPESAFE_BIT);
        assertThat(TypeDeclaration.FORMAT_BIT == (tdeclr.getSetMask() & TypeDeclaration.FORMAT_BIT)).isFalse();
        
        tdeclr = new TypeDeclaration(CImpl.class.getName() );
        tdeclr.setTypesafe( true );
        assertThat(TypeDeclaration.ROLE_BIT == (tdeclr.getSetMask() & TypeDeclaration.ROLE_BIT)).isFalse();
        assertThat(tdeclr.getSetMask() & TypeDeclaration.TYPESAFE_BIT).isEqualTo(TypeDeclaration.TYPESAFE_BIT);
        assertThat(TypeDeclaration.FORMAT_BIT == (tdeclr.getSetMask() & TypeDeclaration.FORMAT_BIT)).isFalse();
        
        tdeclr.setFormat( Format.POJO );
        assertThat(TypeDeclaration.ROLE_BIT == (tdeclr.getSetMask() & TypeDeclaration.ROLE_BIT)).isFalse();
        assertThat(tdeclr.getSetMask() & TypeDeclaration.TYPESAFE_BIT).isEqualTo(TypeDeclaration.TYPESAFE_BIT);
        assertThat(tdeclr.getSetMask() & TypeDeclaration.FORMAT_BIT).isEqualTo(TypeDeclaration.FORMAT_BIT);
    }
    
    @Test
    public void testOverrideFromParentClass() {
        // inherits role, but not typesafe
        String str = ""+
           "package org.drools.mvel.compiler.test \n" +
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
        assertThat(tdecl.isTypesafe()).isEqualTo(false);
        assertThat(tdecl.getRole()).isEqualTo(Role.Type.EVENT);
    }
    
    @Test
    public void testInheritNoneExitenceFromParentClass() {
        // inherits role and typesafe
        String str = ""+
           "package org.drools.mvel.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +           
           "end\n";

        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertThat(tdecl.isTypesafe()).isEqualTo(true);
        assertThat(tdecl.getRole()).isEqualTo(Role.Type.EVENT);
    }    
    
    @Test
    public void testInheritExitenceFromParentClass() {
        // inherits role and typesafe
        String str = ""+
           "package org.drools.mvel.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n" +
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "end\n";

        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertThat(tdecl.isTypesafe()).isEqualTo(true);
        assertThat(tdecl.getRole()).isEqualTo(Role.Type.EVENT);
    }    
    
    @Test
    public void testOverrideFromParentInterface() {
        // inherits role but not typesafe
        String str = ""+
           "package org.drools.mvel.compiler.test \n" +
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
        assertThat(tdecl.isTypesafe()).isEqualTo(false);
        assertThat(tdecl.getRole()).isEqualTo(Role.Type.EVENT);
    }
    
    @Test
    public void testOverrideFromDeeperParentInterface() {
        // inherits role but not typesafe        
        String str = ""+
           "package org.drools.mvel.compiler.test \n" +
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
        assertThat(tdecl.isTypesafe()).isEqualTo(false);
        assertThat(tdecl.getRole()).isEqualTo(Role.Type.EVENT);
    }    
    
    @Test
    public void testOverrideFromDeeperHierarchyParentInterface() {
        // inherits role from and typesafe from the other      
        String str = ""+
           "package org.drools.mvel.compiler.test \n" +
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
        assertThat(tdecl.isTypesafe()).isEqualTo(true);
        assertThat(tdecl.getRole()).isEqualTo(Role.Type.FACT);
    }        
    
    @Test
    public void testInheritNoneExitenceFromParentInterface() {
        // inherits role and typesafe  
        String str = ""+
           "package org.drools.mvel.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + IB.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +              
           "end\n";

        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertThat(tdecl.isTypesafe()).isEqualTo(true);
        assertThat(tdecl.getRole()).isEqualTo(Role.Type.EVENT);
    }    
    
    @Test
    public void testInheritExitenceFromParentInterface() {
        // inherits role and typesafe  
        String str = ""+
           "package org.drools.mvel.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + IB.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +           
           "end\n" +
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "end\n";

        KnowledgeBuilderImpl builder = getPackageBuilder( str );
        TypeDeclaration tdecl = builder.getTypeDeclaration( DImpl.class );
        assertThat(tdecl.isTypesafe()).isEqualTo(true);
        assertThat(tdecl.getRole()).isEqualTo(Role.Type.EVENT);
    }     
    
    @Test
    public void testOverrideFromMixedHierarchyParentInterface() {
        // inherits role from and typesafe from the other      
        String str = ""+
           "package org.drools.mvel.compiler.test \n" +
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
        assertThat(tdecl.isTypesafe()).isEqualTo(true);
        assertThat(tdecl.getRole()).isEqualTo(Role.Type.FACT);
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
                "package org.drools.mvel.compiler.test \n" +
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
        assertThat(kbuilder.hasErrors()).as("Check the test, unexpected error message: "
                + kbuilder.getErrors()).isFalse();
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