package org.drools.verifier;

import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;

public class DefaultVerifierConfiguration extends VerifierConfigurationImpl {

    public DefaultVerifierConfiguration() {
        verifyingResources.put( ResourceFactory.newClassPathResource( "bootstrap-essentials.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "consequence/Consequence.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "alwaysFalse/Patterns.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "incoherence/Patterns.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "incoherence/Restrictions.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "incompatibility/Patterns.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "incompatibility/Restrictions.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "missingEquality/MissingEquality.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "opposites/Patterns.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "opposites/Restrictions.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "opposites/Rules.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "optimisation/PatternOrder.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "rangeChecks/Clean.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "rangeChecks/Dates.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "rangeChecks/Doubles.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "rangeChecks/Integers.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "redundancy/Redundancy.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "redundancy/Notes.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "redundancy/Warnings.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "subsumption/Consequences.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "subsumption/Restrictions.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "subsumption/SubPatterns.drl", getClass() ), ResourceType.DRL );
        verifyingResources.put( ResourceFactory.newClassPathResource( "subsumption/SubRules.drl", getClass() ), ResourceType.DRL );
    }

}