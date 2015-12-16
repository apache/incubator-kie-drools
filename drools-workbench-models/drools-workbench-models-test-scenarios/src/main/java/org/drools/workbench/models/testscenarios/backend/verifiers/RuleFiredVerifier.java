/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.backend.verifiers;

import java.util.Map;

import org.drools.workbench.models.testscenarios.shared.VerifyRuleFired;

public class RuleFiredVerifier {

    private Map<String, Integer> firingCounts;

    public void setFireCounter(Map<String, Integer> firingCounts) {
        this.firingCounts = firingCounts;
    }

    public void verifyFiringCounts(VerifyRuleFired verifyRuleFired) {

        setActualResult( verifyRuleFired );

        if ( verifyRuleFired.getExpectedFire() != null ) {
            if ( verifyRuleFired.getExpectedFire() ) {
                verifyFiredAtLeastOnce( verifyRuleFired );
            } else {
                verifyFiredZeroTimes( verifyRuleFired );
            }
        }

        if ( verifyRuleFired.getExpectedCount() != null ) {
            verifyFiredXTimes( verifyRuleFired );
        }
    }

    private void verifyFiredXTimes(VerifyRuleFired verifyRuleFired) {
        if ( verifyRuleFired.getActualResult().equals( verifyRuleFired.getExpectedCount() ) ) {
            verifyRuleFired.setSuccessResult( true );
            verifyRuleFired.setExplanation( "Rule [" + verifyRuleFired.getRuleName() + "] activated " + verifyRuleFired.getActualResult() + " times." );
        } else {
            verifyRuleFired.setSuccessResult( false );
            verifyRuleFired.setExplanation( "Rule [" + verifyRuleFired.getRuleName() + "] activated " + verifyRuleFired.getActualResult() + " times. Expected " + verifyRuleFired.getExpectedCount() + " times." );
        }
    }

    private void verifyFiredZeroTimes(VerifyRuleFired verifyRuleFired) {
        if ( verifyRuleFired.getActualResult() == 0 ) {
            verifyRuleFired.setSuccessResult( true );
            verifyRuleFired.setExplanation( "Rule [" + verifyRuleFired.getRuleName() + "] was not activated." );
        } else {
            verifyRuleFired.setSuccessResult( false );
            verifyRuleFired.setExplanation( "Rule [" + verifyRuleFired.getRuleName() + "] was activated " + verifyRuleFired.getActualResult() + " times, but expected none." );
        }
    }

    private void verifyFiredAtLeastOnce(VerifyRuleFired verifyRuleFired) {
        if ( verifyRuleFired.getActualResult() > 0 ) {
            verifyRuleFired.setSuccessResult( true );
            verifyRuleFired.setExplanation( "Rule [" + verifyRuleFired.getRuleName() + "] was activated " + verifyRuleFired.getActualResult() + " times." );
        } else {
            verifyRuleFired.setSuccessResult( false );
            verifyRuleFired.setExplanation( "Rule [" + verifyRuleFired.getRuleName() + "] was not activated. Expected it to be activated." );
        }
    }

    private void setActualResult(VerifyRuleFired verifyRuleFired) {
        if ( firingCounts.containsKey( verifyRuleFired.getRuleName() ) ) {
            verifyRuleFired.setActualResult( firingCounts.get( verifyRuleFired.getRuleName() ) );
        } else {
            verifyRuleFired.setActualResult( 0 );
        }
    }
}
