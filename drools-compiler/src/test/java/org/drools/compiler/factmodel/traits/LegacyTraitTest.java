package org.drools.compiler.factmodel.traits;

import org.drools.core.factmodel.traits.Trait;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.Traitable;
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

    public static interface Pers {
        public String getName();
    }

    public static interface Patient extends Pers {
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

    public static interface Procedure {
        public Patient getSubject();
        public void setSubject(Patient p);
    }

    @Traitable()
    // Getters and setters are both needed. They should refer to an attribute with the same name
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

    // @Trait not necessary, since will need "declare trait" in DRL anyway.
    // To be a full trait, the interface should also extend Thing.
    public interface Surgery extends Procedure {

    }

    @Test
    public void traitWithPojoInterface() {
        String source = "package org.drools.compiler.test;" +
                        "import org.drools.compiler.factmodel.traits.LegacyTraitTest.Procedure; " +
                        "import org.drools.compiler.factmodel.traits.LegacyTraitTest; " +
                        "import org.drools.compiler.factmodel.traits.LegacyTraitTest.ProcedureImpl; " +
                        "import org.drools.compiler.factmodel.traits.LegacyTraitTest.Surgery; " +

                        // enhanced so that declaration is not needed
                        // "declare ProcedureImpl end " +

                        // Surgery must be declared as trait, since it does not extend Thing
                        "declare trait Surgery end " +

                        "rule 'Don Procedure' " +
                        "when " +
                        "    $p : Procedure() " +
                        "then " +
                        "    don( $p, Surgery.class ); " +
                        "end " +

                        "rule 'Print Provider' " +
                        "dialect 'mvel' " +
                        "when " +
                        "    $s : Surgery() " +
                        "then " +
                        "    System.out.println( $s.subject.name ); " +
                        "end " +
                        "\n";

        StatefulKnowledgeSession ks = getSessionFromString( source );
        TraitFactory.setMode( VirtualPropertyMode.MAP, ks.getKieBase() );

        ProcedureImpl procedure = new ProcedureImpl();

        PatientImpl patient = new PatientImpl();
        patient.setName("John");
        procedure.setSubject( patient );

        ks.insert( procedure );

        ks.fireAllRules();
    }

}
