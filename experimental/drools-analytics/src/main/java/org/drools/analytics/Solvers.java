package org.drools.analytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.drools.analytics.components.AnalyticsComponent;
import org.drools.analytics.components.AnalyticsRule;
import org.drools.analytics.components.OperatorDescr;
import org.drools.analytics.components.Pattern;
import org.drools.analytics.components.PatternPossibility;
import org.drools.analytics.components.Restriction;
import org.drools.analytics.components.RulePossibility;

/**
 * 
 * @author Toni Rikkola
 */
public class Solvers {

	private RuleSolver ruleSolver = null;
	private PatternSolver patternSolver = null;

	private List<PatternPossibility> patternPossibilities = new ArrayList<PatternPossibility>();
	private List<RulePossibility> rulePossibilities = new ArrayList<RulePossibility>();

	public void startRuleSolver(AnalyticsRule rule) {
		ruleSolver = new RuleSolver(rule);
	}

	public void endRuleSolver() {
		createRulePossibilities();
		ruleSolver = null;
	}

	public void startPatternSolver(Pattern pattern) {
		patternSolver = new PatternSolver(pattern);

		patternSolver.getPattern().setPatternNot(ruleSolver.isChildNot());
	}

	public void endPatternSolver() {
		createPatternPossibilities();
		patternSolver = null;
	}

	public void startForall() {
		if (patternSolver != null) {
			patternSolver.setChildForall(true);
		} else if (ruleSolver != null) {
			ruleSolver.setChildForall(true);
		}
	}

	public void endForall() {
		if (patternSolver != null) {
			patternSolver.setChildForall(false);
		} else if (ruleSolver != null) {
			ruleSolver.setChildForall(false);
		}
	}

	public void startExists() {
		if (patternSolver != null) {
			patternSolver.setChildExists(true);
		} else if (ruleSolver != null) {
			ruleSolver.setChildExists(true);
		}
	}

	public void endExists() {
		if (patternSolver != null) {
			patternSolver.setChildExists(false);
		} else if (ruleSolver != null) {
			ruleSolver.setChildExists(false);
		}
	}

	public void startNot() {
		if (patternSolver != null) {
			patternSolver.setChildNot(true);
		} else if (ruleSolver != null) {
			ruleSolver.setChildNot(true);
		}
	}

	public void endNot() {
		if (patternSolver != null) {
			patternSolver.setChildNot(false);
		} else if (ruleSolver != null) {
			ruleSolver.setChildNot(false);
		}
	}

	public void startOperator(OperatorDescr operatorDescr) {
		if (patternSolver != null) {
			patternSolver.add(operatorDescr);
		} else if (ruleSolver != null) {
			ruleSolver.add(operatorDescr);
		}
	}

	public void endOperator() {
		if (patternSolver != null) {
			patternSolver.end();
		} else if (ruleSolver != null) {
			ruleSolver.end();
		}
	}

	public void addRestriction(Restriction restriction) {
		patternSolver.add(restriction);
	}

	private void createPatternPossibilities() {
		for (Set<AnalyticsComponent> list : patternSolver.getPossibilityLists()) {
			PatternPossibility possibility = new PatternPossibility();

			possibility.setRuleId(ruleSolver.getRule().getId());
			possibility.setPatternId(patternSolver.getPattern().getId());

			for (AnalyticsComponent descr : list) {
				possibility.add((Restriction) descr);
			}

			ruleSolver.add(possibility);
			patternPossibilities.add(possibility);
		}
	}

	private void createRulePossibilities() {
		for (Set<AnalyticsComponent> list : ruleSolver.getPossibilityLists()) {
			RulePossibility possibility = new RulePossibility();

			possibility.setRuleId(ruleSolver.getRule().getId());
			possibility.setRuleName(ruleSolver.getRule().getRuleName());

			for (AnalyticsComponent descr : list) {
				PatternPossibility patternPossibility = (PatternPossibility) descr;
				possibility.add(patternPossibility);
			}

			rulePossibilities.add(possibility);
		}
	}

	public List<PatternPossibility> getPatternPossibilities() {
		return patternPossibilities;
	}

	public void setPatternPossibilities(
			List<PatternPossibility> patternPossibilities) {
		this.patternPossibilities = patternPossibilities;
	}

	public List<RulePossibility> getRulePossibilities() {
		return rulePossibilities;
	}

	public void setRulePossibilities(List<RulePossibility> rulePossibilities) {
		this.rulePossibilities = rulePossibilities;
	}

	public PatternSolver getPatternSolver() {
		return patternSolver;
	}

	public RuleSolver getRuleSolver() {
		return ruleSolver;
	}
}