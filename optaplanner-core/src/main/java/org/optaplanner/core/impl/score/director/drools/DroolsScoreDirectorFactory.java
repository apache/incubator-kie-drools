/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.director.drools;

import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Global;
import org.optaplanner.core.impl.score.director.AbstractScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

/**
 * Drools implementation of {@link ScoreDirectorFactory}.
 * @see DroolsScoreDirector
 * @see ScoreDirectorFactory
 */
public class DroolsScoreDirectorFactory extends AbstractScoreDirectorFactory {

    protected KieBase kieBase;

    public DroolsScoreDirectorFactory(KieBase kieBase) {
        this.kieBase = kieBase;
        boolean hasGlobalScoreHolder = false;
        for (KiePackage kiePackage : kieBase.getKiePackages()) {
            for (Global global : kiePackage.getGlobalVariables()) {
                if (DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY.equals(global.getName())) {
                    hasGlobalScoreHolder = true;
                    break;
                }
            }
        }
        if (!hasGlobalScoreHolder) {
            throw new IllegalArgumentException("The kieBase with kiePackages (" + kieBase.getKiePackages()
                    + ") has no global field called " + DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY + ".\n"
                    + "Check if the rule files are found and if the global field is spelled correctly.");
        }
    }

    public KieBase getKieBase() {
        return kieBase;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public DroolsScoreDirector buildScoreDirector(boolean constraintMatchEnabledPreference) {
        return new DroolsScoreDirector(this, constraintMatchEnabledPreference);
    }

}
