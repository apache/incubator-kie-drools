package org.drools.verifier.misc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.verifier.Verifier;

public class RuleLoader {

    // public static Collection<KnowledgePackage> loadPackages() {
    // return loadPackages(Collections.<Resource, ResourceType> emptyMap());
    // }

    /**
     * 
     * @param resources
     *            Additional custom rules added by the user.
     * @return
     */
    public static Collection<KnowledgePackage> loadPackages(Map<Resource, ResourceType> resources) {

        KnowledgeBuilderConfiguration kbuilderConfiguration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbuilderConfiguration.setProperty( "drools.dialect.java.compiler",
                                           "JANINO" );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        if ( resources != null ) {
            for ( Resource resource : resources.keySet() ) {
                kbuilder.add( resource,
                              resources.get( resource ) );
            }
        }

        return kbuilder.getKnowledgePackages();
    }

    public static Map<Resource, ResourceType> basicRulesForFullKnowledgeBase() {
        Map<Resource, ResourceType> resources = new HashMap<Resource, ResourceType>();

        // Missing consequence
        resources.put( ResourceFactory.newClassPathResource( "Consequence.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // Always false
        resources.put( ResourceFactory.newClassPathResource( "alwaysFalse/Patterns.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // Incoherence
        resources.put( ResourceFactory.newClassPathResource( "incoherence/Patterns.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "incoherence/Restrictions.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // Incompatibility
        resources.put( ResourceFactory.newClassPathResource( "incompatibility/Patterns.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "incompatibility/Restrictions.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // Missing equality
        resources.put( ResourceFactory.newClassPathResource( "missingEquality/MissingEquality.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // Opposites
        resources.put( ResourceFactory.newClassPathResource( "opposites/Patterns.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "opposites/Restrictions.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "opposites/Rules.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // Optimization
        resources.put( ResourceFactory.newClassPathResource( "optimisation/PatternOrder.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // TODO: The DRL needs more work
        //        resources.put( ResourceFactory.newClassPathResource( "optimisation/RestrictionOrder.drl",
        //                                                             Verifier.class ),
        //                       ResourceType.DRL );
        // Overlaps
        resources.put( ResourceFactory.newClassPathResource( "overlaps/Restrictions.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "overlaps/Restrictions.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // Range checks
        resources.put( ResourceFactory.newClassPathResource( "rangeChecks/Clean.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "rangeChecks/Dates.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "rangeChecks/Doubles.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "rangeChecks/Integers.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
//        resources.put( ResourceFactory.newClassPathResource( "rangeChecks/NumberPatterns.drl",
//                                                             Verifier.class ),
//                       ResourceType.DRL );
//        resources.put( ResourceFactory.newClassPathResource( "rangeChecks/Variables.drl",
//                                                             Verifier.class ),
//                       ResourceType.DRL );
        //         Redundancy
        resources.put( ResourceFactory.newClassPathResource( "redundancy/Redundancy.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "redundancy/Notes.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "redundancy/Warnings.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // Reporting
        resources.put( ResourceFactory.newClassPathResource( "reports/RangeCheckReports.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        // Subsumption
        resources.put( ResourceFactory.newClassPathResource( "subsumption/Consequences.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "subsumption/Restrictions.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "subsumption/SubPatterns.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );
        resources.put( ResourceFactory.newClassPathResource( "subsumption/SubRules.drl",
                                                             Verifier.class ),
                       ResourceType.DRL );

        return resources;
    }
}
