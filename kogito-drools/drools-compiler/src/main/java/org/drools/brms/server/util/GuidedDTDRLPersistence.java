package org.drools.brms.server.util;

import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.modeldriven.brl.ActionFieldValue;
import org.drools.brms.client.modeldriven.brl.ActionInsertFact;
import org.drools.brms.client.modeldriven.brl.ActionRetractFact;
import org.drools.brms.client.modeldriven.brl.ActionSetField;
import org.drools.brms.client.modeldriven.brl.FactPattern;
import org.drools.brms.client.modeldriven.brl.IAction;
import org.drools.brms.client.modeldriven.brl.IPattern;
import org.drools.brms.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.brms.client.modeldriven.brl.RuleAttribute;
import org.drools.brms.client.modeldriven.brl.RuleModel;
import org.drools.brms.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.brms.client.modeldriven.dt.ActionCol;
import org.drools.brms.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.brms.client.modeldriven.dt.ActionRetractFactCol;
import org.drools.brms.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.brms.client.modeldriven.dt.AttributeCol;
import org.drools.brms.client.modeldriven.dt.ConditionCol;
import org.drools.brms.client.modeldriven.dt.GuidedDecisionTable;

/**
 * This takes care of converting GuidedDT object to DRL (via the RuleModel).
 * @author Michael Neale
 *
 */
public class GuidedDTDRLPersistence {

	public static GuidedDTDRLPersistence getInstance() {
		return new GuidedDTDRLPersistence();
	}

	public String marshal(GuidedDecisionTable dt) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < dt.data.length; i++) {
			String[] row = dt.data[i];
			String num = row[0];
			String desc = row[1];

			RuleModel rm = new RuleModel();
			rm.name = getName(dt.tableName, num);

			doAttribs(dt.attributeCols, row, rm);
			doConditions(dt.attributeCols.size(), dt.conditionCols, row, rm);
			doActions(dt.attributeCols.size() + dt.conditionCols.size(), dt.actionCols, row, rm);

			sb.append("#from row number: " + (i + 1) + "\n");
			String rule = BRDRLPersistence.getInstance().marshal(rm);
			sb.append(rule);
			sb.append("\n");
		}


		return sb.toString();

	}

	void doActions(int condAndAttrs, List<ActionCol> actionCols, String[] row, RuleModel rm) {
		List<LabelledAction> actions = new ArrayList<LabelledAction>();
		for (int i = 0; i < actionCols.size(); i++) {
			ActionCol c = actionCols.get(i);
			String cell = row[condAndAttrs + i + 2];
			if (validCell(cell)) {
				if (c instanceof ActionInsertFactCol) {
					ActionInsertFactCol ac = (ActionInsertFactCol)c;
					LabelledAction a = find(actions, ac.boundName);
					if (a == null) {
						a = new LabelledAction();
						a.boundName  = ac.boundName;
						ActionInsertFact ins = new ActionInsertFact(ac.factType);
						a.action = ins;
						actions.add(a);
					}
					ActionInsertFact ins = (ActionInsertFact) a.action;
					ActionFieldValue val = new ActionFieldValue(ac.factField, cell, ac.type);
					ins.addFieldValue(val);
				} else if (c instanceof ActionRetractFactCol) {
					ActionRetractFactCol rf = (ActionRetractFactCol)c;
					LabelledAction a = find(actions, rf.boundName);
					if (a == null) {
						a = new LabelledAction();
						a.action = new ActionRetractFact(rf.boundName);
						a.boundName = rf.boundName;
						actions.add(a);
					}
				} else if (c instanceof ActionSetFieldCol) {
					ActionSetFieldCol sf = (ActionSetFieldCol)c;
					LabelledAction a = find(actions, sf.boundName);
					if (a == null) {
						a = new LabelledAction();
						a.boundName = sf.boundName;
						a.action = new ActionSetField(sf.boundName);
						actions.add(a);
					}
					ActionSetField asf = (ActionSetField) a.action;
					ActionFieldValue val = new ActionFieldValue(sf.factField, cell, sf.type);
					asf.addFieldValue(val);
				}
			}
		}

		rm.rhs = new IAction[actions.size()];
		for (int i = 0; i < rm.rhs.length; i++) {
			rm.rhs[i] = actions.get(i).action;
		}
	}

	private LabelledAction find(List<LabelledAction> actions, String boundName) {
		for (LabelledAction labelledAction : actions) {
			if (labelledAction.boundName.equals(boundName)) {
				return labelledAction;
			}
		}
		return null;
	}

	void doConditions(int numOfAttributes, List<ConditionCol> conditionCols, String[] row, RuleModel rm) {

		List<FactPattern> patterns = new ArrayList<FactPattern>();

		for (int i = 0; i < conditionCols.size(); i++) {
			ConditionCol c = (ConditionCol) conditionCols.get(i);
			String cell = row[i + 2 + numOfAttributes];
			if (validCell(cell)) {

				//get or create the pattern it belongs too
				FactPattern fp = find(patterns, c.boundName);
				if (fp == null) {
					fp = new FactPattern(c.factType);
					fp.boundName = c.boundName;
					patterns.add(fp);
				}



				//now add the constraint from this cell
				switch (c.constraintValueType) {
					case ISingleFieldConstraint.TYPE_LITERAL:
					case ISingleFieldConstraint.TYPE_RET_VALUE:
						SingleFieldConstraint sfc = new SingleFieldConstraint(c.factField);
						sfc.operator = c.operator;
						sfc.constraintValueType = c.constraintValueType;
						sfc.value = cell;
						fp.addConstraint(sfc);
						break;
					case ISingleFieldConstraint.TYPE_PREDICATE:
						SingleFieldConstraint pred = new SingleFieldConstraint();
						pred.constraintValueType = c.constraintValueType;
						pred.value = cell;
						fp.addConstraint(pred);
						break;
				default:
					throw new IllegalArgumentException("Unknown constraintValueType: " + c.constraintValueType);
				}
			}
		}
		rm.lhs = patterns.toArray(new IPattern[patterns.size()]);
	}

	private FactPattern find(List<FactPattern> patterns, String boundName) {
		for (FactPattern factPattern : patterns) {
			if (factPattern.boundName.equals(boundName)) {
				return factPattern;
			}
		}
		return null;
	}

	void doAttribs(List<AttributeCol> attributeCols, String[] row, RuleModel rm) {
		List<RuleAttribute> attribs = new ArrayList<RuleAttribute>();
		for (int j = 0; j < attributeCols.size(); j++) {
			AttributeCol at = attributeCols.get(j);
			String cell = row[j + 2];
			if (validCell(cell)) {
				attribs.add(new RuleAttribute(at.attr, cell));
			}
		}
		if (attribs.size() > 0) {
			rm.attributes = attribs.toArray(new RuleAttribute[attribs.size()]);
		}
	}

	String getName(String tableName, String num) {
		return "Row " + num + " " + tableName;
	}

	boolean validCell(String c) {
		return c !=null && !c.trim().equals("");
	}

	private class LabelledAction {
		String boundName;
		IAction action;
	}

}
