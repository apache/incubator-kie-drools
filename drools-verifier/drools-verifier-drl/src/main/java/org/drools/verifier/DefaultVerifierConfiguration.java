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