package org.drools.verifier.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.Restriction;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierComponent;

/**
 * 
 * @author Toni Rikkola
 */
public class Solvers {

    private RuleSolver               ruleSolver           = null;
    private PatternSolver            patternSolver        = null;

    private List<SubPattern> patternPossibilities = new ArrayList<SubPattern>();
    private List<SubRule>    rulePossibilities    = new ArrayList<SubRule>();

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

    public void startOperator(OperatorDescr.Type type) {
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

    public void addRestriction(Restriction restriction) {
        patternSolver.add( restriction );
    }

    private void createPatternPossibilities() {
        for ( Set<VerifierComponent> list : patternSolver.getPossibilityLists() ) {
            SubPattern possibility = new SubPattern();

            possibility.setRuleGuid( ruleSolver.getRule().getGuid() );
            possibility.setRuleName( ruleSolver.getRule().getRuleName() );
            possibility.setRuleGuid( ruleSolver.getRule().getGuid() );
            possibility.setPatternGuid( patternSolver.getPattern().getGuid() );

            for ( VerifierComponent descr : list ) {
                possibility.add( (Restriction) descr );
            }

            ruleSolver.add( possibility );
            patternPossibilities.add( possibility );
        }
    }

    private void createRulePossibilities() {
        for ( Set<VerifierComponent> list : ruleSolver.getPossibilityLists() ) {
            SubRule possibility = new SubRule();

            possibility.setRuleGuid( ruleSolver.getRule().getGuid() );
            possibility.setRuleName( ruleSolver.getRule().getRuleName() );
            possibility.setRuleGuid( ruleSolver.getRule().getGuid() );

            for ( VerifierComponent descr : list ) {
                SubPattern patternPossibility = (SubPattern) descr;
                possibility.add( patternPossibility );
            }

            rulePossibilities.add( possibility );
        }
    }

    public List<SubPattern> getPatternPossibilities() {
        return patternPossibilities;
    }

    public void setPatternPossibilities(List<SubPattern> patternPossibilities) {
        this.patternPossibilities = patternPossibilities;
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