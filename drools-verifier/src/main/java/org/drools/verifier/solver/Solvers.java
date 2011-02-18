/*
 * Copyright 2010 JBoss Inc
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

package org.drools.verifier.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternComponent;
import org.drools.verifier.components.RuleComponent;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierComponent;

public class Solvers {

    private RuleSolver       ruleSolver        = null;
    private PatternSolver    patternSolver     = null;

    private List<SubPattern> subPatterns       = new ArrayList<SubPattern>();
    private List<SubRule>    rulePossibilities = new ArrayList<SubRule>();

    private int              subRuleIndex      = 0;
    private int              subPatternIndex   = 0;

    public void startRuleSolver(VerifierRule rule) {
        ruleSolver = new RuleSolver( rule );
    }

    public void endRuleSolver() {
        createRulePossibilities();
        ruleSolver = null;
    }

    public void startPatternSolver(Pattern pattern) {
        patternSolver = new PatternSolver( pattern );

        patternSolver.getPattern().setPatternNot( ruleSolver.isChildNot() );
    }

    public void endPatternSolver() {
        createPatternPossibilities();
        patternSolver = null;
    }

    public void startForall() {
        if ( patternSolver != null ) {
            patternSolver.setChildForall( true );
        } else if ( ruleSolver != null ) {
            ruleSolver.setChildForall( true );
        }
    }

    public void endForall() {
        if ( patternSolver != null ) {
            patternSolver.setChildForall( false );
        } else if ( ruleSolver != null ) {
            ruleSolver.setChildForall( false );
        }
    }

    public void startExists() {
        if ( patternSolver != null ) {
            patternSolver.setChildExists( true );
        } else if ( ruleSolver != null ) {
            ruleSolver.setChildExists( true );
        }
    }

    public void endExists() {
        if ( patternSolver != null ) {
            patternSolver.setChildExists( false );
        } else if ( ruleSolver != null ) {
            ruleSolver.setChildExists( false );
        }
    }

    public void startNot() {
        if ( patternSolver != null ) {
            patternSolver.setChildNot( true );
        } else if ( ruleSolver != null ) {
            ruleSolver.setChildNot( true );
        }
    }

    public void endNot() {
        if ( patternSolver != null ) {
            patternSolver.setChildNot( false );
        } else if ( ruleSolver != null ) {
            ruleSolver.setChildNot( false );
        }
    }

    public void startOperator(OperatorDescrType type) {
        if ( patternSolver != null ) {
            patternSolver.addOperator( type );
        } else if ( ruleSolver != null ) {
            ruleSolver.addOperator( type );
        }
    }

    public void endOperator() {
        if ( patternSolver != null ) {
            patternSolver.end();
        } else if ( ruleSolver != null ) {
            ruleSolver.end();
        }
    }

    public void addRuleComponent(RuleComponent ruleComponent) {
        ruleSolver.add( ruleComponent );
    }

    public void addPatternComponent(PatternComponent patternComponent) {
        patternSolver.add( patternComponent );
    }

    private void createPatternPossibilities() {
        List<Set<VerifierComponent>> lists = patternSolver.getPossibilityLists();
        if ( lists.size() == 0 ) {
            SubPattern subPattern = newSubPattern();

            ruleSolver.add( subPattern );
            subPatterns.add( subPattern );
        } else {

            for ( Set<VerifierComponent> list : lists ) {
                SubPattern subPattern = newSubPattern();

                for ( VerifierComponent descr : list ) {
                    subPattern.add( (PatternComponent) descr );
                }

                ruleSolver.add( subPattern );
                subPatterns.add( subPattern );
            }
        }
    }

    private SubPattern newSubPattern() {
        SubPattern subPattern = new SubPattern( patternSolver.getPattern(),
                                                subPatternIndex++ );

        return subPattern;
    }

    private void createRulePossibilities() {
        for ( Set<VerifierComponent> list : ruleSolver.getPossibilityLists() ) {
            SubRule possibility = new SubRule( ruleSolver.getRule(),
                                               subRuleIndex++ );

            for ( VerifierComponent descr : list ) {
                possibility.add( (RuleComponent) descr );
            }

            rulePossibilities.add( possibility );
        }
    }

    public List<SubPattern> getPatternPossibilities() {
        return subPatterns;
    }

    public void setPatternPossibilities(List<SubPattern> patternPossibilities) {
        this.subPatterns = patternPossibilities;
    }

    public List<SubRule> getRulePossibilities() {
        return rulePossibilities;
    }

    public void setRulePossibilities(List<SubRule> rulePossibilities) {
        this.rulePossibilities = rulePossibilities;
    }

    public PatternSolver getPatternSolver() {
        return patternSolver;
    }

    public RuleSolver getRuleSolver() {
        return ruleSolver;
    }
}
