package org.drools.examples.traits;

import java.util.Collection;

import org.drools.io.ClassPathResource;
import org.drools.traits.core.base.evaluators.IsAEvaluatorDefinition;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.utils.KieHelper;


public class TraitExample {


    private static KieSession getSession( String drl ) {
        KieHelper kieHelper = new KieHelper();
        kieHelper.kfs.write( new ClassPathResource( "org/drools/examples/traits/" + drl ) );
        return kieHelper.build().newKieSession();
    }

    public static void run( String demo ) {
        System.setProperty(EvaluatorOption.PROPERTY_NAME + "isA", IsAEvaluatorDefinition.class.getName());
        KieSession kSession = getSession( demo );
        kSession.fireAllRules();

        Collection c =  kSession.getObjects();
        System.out.println( "------------------------- " + c.size() + " ----------------------" );
        for ( Object o : c ) {
            System.out.println( " \t --- " + o );
        }
        System.out.println( "-----------------------------------------------------------------" );

        kSession.dispose();
    }

    public static void main( String[] args ) {

        run( "noTraits.drl" );

        run( "traitsDon.drl" );

        run( "multipleTraits.drl" );

        run( "traitsMixins.drl" );

        run( "traitsShed.drl" );
    }
}
