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

import java.util.Collection;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.rule.TypeDeclaration.Format;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;


@RunWith(Parameterized.class)
public class TypeDeclarationMergingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public TypeDeclarationMergingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with testInheritExitenceFromParentClass etc. Not fully sure if the test change is valid. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }
    
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
           "package org.drools.mvel.compiler.test \n" +
           "global java.util.List list \n" +
           "declare " + CImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(true)\n" +
           "    @role(event)\n" +
           "end\n" +           
           "declare " + DImpl.class.getCanonicalName() + "\n" +
           "    @typesafe(false)\n" +
           "end\n" +
           "rule r1\n" +
           "when\n " +
           "  DImpl()\n" +
           "then\n" +
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
           "package org.drools.mvel.compiler.test \n" +
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
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
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
        assertEquals( false, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
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
        assertEquals( false, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
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
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.FACT, tdecl.getRole() );
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
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
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
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.EVENT, tdecl.getRole() );
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
        assertEquals( true, tdecl.isTypesafe() );
        assertEquals( Role.Type.FACT, tdecl.getRole() );
    }      

    private KnowledgeBuilderImpl getPackageBuilder(String str) {
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, str);
        InternalKieModule kieModule = (InternalKieModule)kieBuilder.getKieModule();
        return (KnowledgeBuilderImpl)kieModule.getKnowledgeBuilderForKieBase("defaultKieBase");
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

        KieBuilder kieBuilder = null;
        try {
            kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        } catch (IndexOutOfBoundsException e) {
            final String msg = e.getMessage();
            if ( "Error trying to access field at position 0".equals( msg ) ) {
                fail( "@Position declared in POJO was ignored." );
            } else {
                fail( "Check the test, unexpected error message: " + msg );
            }
        }
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertFalse( "Check the test, unexpected error message: "
                + errors, !errors.isEmpty());
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
