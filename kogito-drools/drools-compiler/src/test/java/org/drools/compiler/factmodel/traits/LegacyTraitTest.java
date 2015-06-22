

package org.drools.compiler.factmodel.traits;

import org.drools.core.common.ProjectClassLoader;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.drools.core.util.StandaloneTraitFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class LegacyTraitTest {

    public VirtualPropertyMode mode;

    @Parameterized.Parameters
    public static Collection modes() {
        return Arrays.asList( new VirtualPropertyMode[][]
                                      {
                                              { VirtualPropertyMode.MAP },
                                              { VirtualPropertyMode.TRIPLES }
                                      } );
    }

    public LegacyTraitTest( VirtualPropertyMode m ) {
        this.mode = m;
    }



    private StatefulKnowledgeSession getSessionFromString( String drl ) {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource( drl.getBytes() ),
                              ResourceType.DRL );
        if (knowledgeBuilder.hasErrors()) {
            throw new RuntimeException( knowledgeBuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );

        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        return session;
    }

    // Getters and setters are both needed. They should refer to an attribute with the same name
    public static class PatientImpl implements Patient {
        private String name;
        public String getName() {
            return this.name;
        }
        public void setName(String name) {
            this.name = name;
        }

    }

    public static interface Pers {
        public String getName();
    }

    public static interface Patient extends Pers {
    }

    public static interface Procedure {
        public Patient getSubject();
        public void setSubject(Patient p);
    }

    public static interface ExtendedProcedure extends Procedure {
        public Pers getPers();
        public void setPers(Pers pers);
    }

    public class ProcedureImpl implements Procedure {

        public ProcedureImpl() {
        }

        private Patient subject;

        @Override
        public Patient getSubject() {
            return this.subject;
        }
        public void setSubject(Patient patient) {
            this.subject = patient;
        }
    }

    public class ExtendedProcedureImpl extends ProcedureImpl implements ExtendedProcedure {

        public ExtendedProcedureImpl() {
        }

        private Pers pers;

        @Override
        public Pers getPers() {
            return this.pers;
        }
        public void setPers(Pers pers) {
            this.pers = pers;
        }
    }


    @Test
    public void traitWithPojoInterface() {
        String source = "package org.drools.compiler.test;" +
                        "import org.drools.compiler.factmodel.traits.LegacyTraitTest.Procedure; " +
                        "import org.drools.compiler.factmodel.traits.LegacyTraitTest; " +
                        "import org.drools.compiler.factmodel.traits.LegacyTraitTest.ExtendedProcedureImpl; " +
                        "import org.drools.compiler.factmodel.traits.LegacyTraitTest.ExtendedProcedure; " +

                        // enhanced so that declaration is not needed
                        // "declare ProcedureImpl end " +
                        "declare trait ExtendedProcedure " +
                        "   @role( event )" +
                        "end " +

                        // Surgery must be declared as trait, since it does not extend Thing
                        "declare trait Surgery extends ExtendedProcedure end " +

                        "declare ExtendedProcedureImpl " +
                        "    @Traitable " +
                        "end " +

                        "rule 'Don Procedure' " +
                        "when " +
                        "    $p : ExtendedProcedure() " +
                        "then " +
                        "    don( $p, Surgery.class ); " +
                        "end " +

                        "rule 'Test 1' " +
                        "dialect 'mvel' " +
                        "when " +
                        "    $s1 : ExtendedProcedure( $subject : subject ) " +
                        "    $s2 : ExtendedProcedure( subject == $subject ) " +
                        "then " +
                        "end " +

                        "rule 'Test 2' " +
                        "dialect 'mvel' " +
                        "when " +
                        "    $s1 : ExtendedProcedure( $subject : subject.name ) " +
                        "    $s2 : ExtendedProcedure( subject.name == $subject ) " +
                        "then " +
                        "end " +

                        "rule 'Test 3' " +
                        "dialect 'mvel' " +
                        "when " +
                        "    $s1 : ExtendedProcedure( ) " +
                        "then " +
                        "    update( $s1 ); " +
                        "end " +
                        "\n";

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );

        ExtendedProcedureImpl procedure1 = new ExtendedProcedureImpl();
        ExtendedProcedureImpl procedure2 = new ExtendedProcedureImpl();

        PatientImpl patient1 = new PatientImpl();
        patient1.setName("John");
        procedure1.setSubject( patient1 );
        procedure1.setPers(new PatientImpl());

        PatientImpl patient2 = new PatientImpl();
        patient2.setName("John");
        procedure2.setSubject( patient2 );
        procedure2.setPers(new PatientImpl());

        ks.insert( procedure1 );
        ks.insert( procedure2 );

        ks.fireAllRules( 500 );
    }


    @Traitable
    @PropertyReactive
    public static class BarImpl implements Foo { }

    public static interface Root { }

    public static interface Trunk extends Root { }

    @PropertyReactive
    @Trait
    public static interface Foo extends Trunk { }

    @Test
    public void traitWithMixedInterfacesExtendingEachOther() {
        String source = "package org.drools.compiler.test;" +
                        "import " + BarImpl.class.getCanonicalName() + "; " +
                        "import " + Foo.class.getCanonicalName() + "; " +
                        "import " + Trunk.class.getCanonicalName() + "; " +
                        "global java.util.List list; " +

                        // We need to redeclare the interfaces as traits, the annotation on the original class is not enough here
                        "declare trait Foo end " +
                        // notice that the declarations do not include supertypes, and are out of order. The engine will figure out what to do
                        "declare trait Root end " +

                        "declare trait Foo2 extends Foo " +
                        "  @propertyReactive " +
                        "end " +

                        "rule 'Bar Don'" +
                        "when " +
                        "   $b : BarImpl( this isA Foo.class, this not isA Foo2.class )\n" +
                        "   String()\n" +
                        "then " +
                        "   list.add( 3 ); " +
                        "   retract( $b ); " +
                        "end " +

                        "rule 'Don Bar' " +
                        "no-loop " +
                        "when " +
                        "    $b : Foo( ) " +
                        "then " +
                        "    list.add( 1 ); " +
                        "    don( $b, Foo2.class ); " +
                        "end " +

                        "rule 'Cant really shed Foo but Foo2' " +
                        "when " +
                        "   $b : Foo2() " +
                        "then " +
                        "   list.add( 2 ); " +
                        "   shed( $b, Foo.class ); " +
                        "   insert( \"done\" );" +
                         "end " +

                        "";

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( mode, ks.getKieBase() );
        ArrayList list = new ArrayList();
        ks.setGlobal( "list", list );

        ks.insert( new BarImpl() );

        int n = ks.fireAllRules();

        System.out.println( list );
        assertEquals( Arrays.asList( 1, 2, 3 ), list );
        assertEquals( 3, n );
    }



    @Test
    public void testTraitWithNonAccessorMethodShadowing() {
        StandaloneTraitFactory factory = new StandaloneTraitFactory( ProjectClassLoader.createProjectClassLoader() );
        try {
            SomeInterface r = (SomeInterface) factory.don( new SomeClass(), SomeInterface.class );
            r.prepare();
            assertEquals( 42, r.getFoo() );
            assertEquals( "I did that", r.doThis( "that" ) );
        } catch ( LogicalTypeInconsistencyException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
    }

}
