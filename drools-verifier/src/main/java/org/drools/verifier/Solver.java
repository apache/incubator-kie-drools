package org.drools.verifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drools.verifier.components.AnalyticsComponent;
import org.drools.verifier.components.OperatorDescr;



/**
 * Takes a list of Constraints and makes possibilities from them.
 * 
 * @author Toni Rikkola
 */
class Solver {

	private List<Set<AnalyticsComponent>> possibilityLists = new ArrayList<Set<AnalyticsComponent>>();
	private Solver subSolver = null;
	private boolean isChildExists = false;
	private boolean isChildForall = false;
	private boolean isChildNot = false;

	private OperatorDescr.Type type;

	protected Solver(OperatorDescr.Type type) {
		this.type = type;
	}

	public void add(AnalyticsComponent descr) {
		if (subSolver != null) {
			subSolver.add(descr);
		} else if (descr instanceof OperatorDescr) {
			OperatorDescr operatorDescr = (OperatorDescr) descr;
			subSolver = new Solver(operatorDescr.getType());
		} else {
			if (type == OperatorDescr.Type.AND) {
				if (possibilityLists.isEmpty()) {
					possibilityLists.add(new HashSet<AnalyticsComponent>());
				}
				for (Set<AnalyticsComponent> set : possibilityLists) {
					set.add(descr);
				}
			} else if (type == OperatorDescr.Type.OR) {
				Set<AnalyticsComponent> set = new HashSet<AnalyticsComponent>();
				set.add(descr);
				possibilityLists.add(set);
			}
		}
	}

	/**
	 * Ends subSolvers data collection.
	 * 
	 */
	protected void end() {
		if (subSolver != null && subSolver.subSolver == null) {
			if (type == OperatorDescr.Type.AND) {
				if (possibilityLists.isEmpty()) {
					possibilityLists.add(new HashSet<AnalyticsComponent>());
				}

				List<Set<AnalyticsComponent>> newPossibilities = new ArrayList<Set<AnalyticsComponent>>();

				List<Set<AnalyticsComponent>> sets = subSolver
						.getPossibilityLists();
				for (Set<AnalyticsComponent> possibilityList : possibilityLists) {

					for (Set<AnalyticsComponent> set : sets) {
						Set<AnalyticsComponent> newSet = new HashSet<AnalyticsComponent>();
						newSet.addAll(possibilityList);
						newSet.addAll(set);
						newPossibilities.add(newSet);
					}
				}
				possibilityLists = newPossibilities;

			} else if (type == OperatorDescr.Type.OR) {

				possibilityLists.addAll(subSolver.getPossibilityLists());

			}

			subSolver = null;

		} else if (subSolver != null && subSolver.subSolver != null) {

			subSolver.end();
		}

	}

	public void setChildForall(boolean b) {
		if (subSolver != null) {
			subSolver.setChildForall(b);
		} else {
			isChildForall = b;
		}
	}

	public void setChildExists(boolean b) {
		if (subSolver != null) {
			subSolver.setChildExists(b);
		} else {
			isChildExists = b;
		}
	}

	public void setChildNot(boolean b) {
		if (subSolver != null) {
			subSolver.setChildNot(b);
		} else {
			isChildNot = b;
		}
	}

	public boolean isForall() {
		if (subSolver != null) {
			return subSolver.isForall();
		} else {
			return isChildForall;
		}
	}

	public boolean isExists() {
		if (subSolver != null) {
			return subSolver.isExists();
		} else {
			return isChildExists;
		}
	}

	public boolean isChildNot() {
		if (subSolver != null) {
			return subSolver.isChildNot();
		} else {
			return isChildNot;
		}
	}

	public List<Set<AnalyticsComponent>> getPossibilityLists() {
		return possibilityLists;
	}
}
