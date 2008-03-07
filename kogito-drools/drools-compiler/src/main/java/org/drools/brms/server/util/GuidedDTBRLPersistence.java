package org.drools.brms.server.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.brms.client.modeldriven.brl.FactPattern;
import org.drools.brms.client.modeldriven.brl.IPattern;
import org.drools.brms.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.brms.client.modeldriven.brl.RuleAttribute;
import org.drools.brms.client.modeldriven.brl.RuleModel;
import org.drools.brms.client.modeldriven.brl.SingleFieldConstraint;
import org.drools.brms.client.modeldriven.dt.AttributeCol;
import org.drools.brms.client.modeldriven.dt.ConditionCol;
import org.drools.brms.client.modeldriven.dt.GuidedDecisionTable;

/**
 * This takes care of converting GuidedDT object to DRL (via the RuleModel).
 * @author Michael Neale
 *
 */
public class GuidedDTBRLPersistence {


	public String marshal(GuidedDecisionTable dt) {

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < dt.data.length; i++) {
			String[] row = dt.data[i];
			String num = row[0];
			String desc = row[1];

			RuleModel rm = new RuleModel();
			rm.name = getName(dt.tableName, num, desc);

			doAttribs(dt.attributeCols, row, rm);
			doConditions(dt.attributeCols.size(), dt.conditionCols, row, rm);
			doActions(dt.attributeCols.size() + dt.conditionCols.size(), dt.actionCols, row, rm);
		}


		return sb.toString();

	}

	void doActions(int condAndAttrs, List actionCols, String[] row, RuleModel rm) {


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

	void doAttribs(List attributeCols, String[] row, RuleModel rm) {
		List<RuleAttribute> attribs = new ArrayList<RuleAttribute>();
		for (int j = 0; j < attributeCols.size(); j++) {
			AttributeCol at = (AttributeCol) attributeCols.get(j);
			String cell = row[j + 2];
			if (validCell(cell)) {
				attribs.add(new RuleAttribute(at.attr, cell));
			}
		}
		if (attribs.size() > 0) {
			rm.attributes = attribs.toArray(new RuleAttribute[attribs.size()]);
		}
	}

	String getName(String tableName, String num, String desc) {
		return (validCell(desc)) ? num + "_" + desc : num + "_" + tableName;
	}

	boolean validCell(String c) {
		return c !=null && !c.trim().equals("");
	}

}
