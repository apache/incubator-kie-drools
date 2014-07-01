

package org.drools.compiler.factmodel.traits;

import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;


public class LegacyTraitTest {


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
        TraitFactory.setMode( VirtualPropertyMode.MAP, ks.getKieBase() );

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

}

